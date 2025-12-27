package controller.issues;

import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import model.dto.request.CreateIssueRequestDTO;
import model.dto.request.UpdateIssueRequestDTO;
import model.dto.response.IssueResponseDTO;
import model.dto.response.ProgettoResponseDTO;
import model.dto.response.UtenteResponseDTO;
import service.AuthService;
import service.IssueService;
import service.ProjectsService;
import service.TeamsService;
import view.MainFrame;
import view.issues.AssignIssueDialog;
import view.issues.IssueFormDialog;
import view.issues.IssuesView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Stream;

public class IssuesController
{
    private static final Logger LOGGER = Logger.getLogger(IssuesController.class.getName());
    private static final String MSG_ERROR = "Errore";
    private static final int REFRESH_INTERVAL_MS = 30000;

    private final IssuesView view;
    private final IssueService issueService;
    private final TeamsService teamService;
    private final ProjectsService projectsService;
    private final AuthService authService;
    private final MainFrame mainFrame;
    private final Timer autoRefreshTimer;

    private List<IssueResponseDTO> cacheMainList = new ArrayList<>();
    private List<IssueResponseDTO> cacheCreatedList = new ArrayList<>();
    private List<IssueResponseDTO> cacheArchivedList = new ArrayList<>();

    public IssuesController(IssuesView view,
                            IssueService issueService,
                            TeamsService teamService,
                            ProjectsService projectsService,
                            AuthService authService,
                            MainFrame mainFrame)
    {
        this.view = view;
        this.issueService = issueService;
        this.teamService = teamService;
        this.projectsService = projectsService;
        this.authService = authService;
        this.mainFrame = mainFrame;

        this.autoRefreshTimer = new Timer(REFRESH_INTERVAL_MS, e ->
        {
            LOGGER.info("Auto-refreshing issues...");
            loadData(true);
        });

        initController();
    }

    private void initController()
    {
        LOGGER.info("Inizializzazione IssuesController");

        view.setRefreshAction(e -> loadData(false));
        view.setNuovaIssueAction(e -> openCreateDialog());
        view.setupFiltersListener(e -> applyFilters());
        view.setResetFilterAction(e -> resetFilters());

        view.setOnEditIssue(this::openEditDialog);

        view.setOnResolveIssue(id ->
        {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Vuoi marcare come RISOLTA?", "Conferma", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION)
                resolveIssue(id);
        });

        view.setOnArchiveIssue(id ->
        {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Vuoi archiviare questo BUG?", "Conferma", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION)
                archiveIssue(id);
        });

        view.setOnAssignIssue(this::handleAssignRequest);

        loadData(false);
        autoRefreshTimer.start();
    }

    private void loadData(boolean silentMode)
    {
        new Thread(() ->
        {
            try
            {
                var currentUser = authService.getCurrentUser();
                if(currentUser == null)
                    return;

                List<IssueResponseDTO> fetchedAllIssues;

                if(Boolean.TRUE.equals(currentUser.getIsAdmin()))
                    fetchedAllIssues = issueService.getAllIssues();
                else
                    fetchedAllIssues = issueService.getIssuesAssignedTo(currentUser.getId());

                List<IssueResponseDTO> fetchedCreated = issueService.getIssuesCreatedBy(currentUser.getId());

                SwingUtilities.invokeLater(() ->
                {
                    cacheMainList = fetchedAllIssues.stream()
                            .filter(i -> !i.isArchiviato())
                            .toList();

                    cacheCreatedList = fetchedCreated.stream()
                            .filter(i -> !i.isArchiviato())
                            .toList();

                    if(Boolean.TRUE.equals(currentUser.getIsAdmin()))
                    {
                        cacheArchivedList = fetchedAllIssues.stream()
                                .filter(IssueResponseDTO::isArchiviato)
                                .toList();
                    }
                    else
                        cacheArchivedList = new ArrayList<>();

                    applyFilters();
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore caricamento issues", e);
                if(!silentMode)
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, "Errore caricamento dati", MSG_ERROR, JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void applyFilters()
    {
        String text = view.getSearchField().getText().trim().toLowerCase();
        TipoIssue tipo = (TipoIssue) view.getFilterTipo().getSelectedItem();
        StatoIssue stato = (StatoIssue) view.getFilterStato().getSelectedItem();
        TipoPriorita priorita = (TipoPriorita) view.getFilterPriorita().getSelectedItem();

        view.updateMainTab(filterList(cacheMainList, text, tipo, stato, priorita));
        view.updateCreateTab(filterList(cacheCreatedList, text, tipo, stato, priorita));
        view.updateArchiveTab(filterList(cacheArchivedList, text, tipo, stato, priorita));
    }

    private List<IssueResponseDTO> filterList(List<IssueResponseDTO> source, String text, TipoIssue tipo, StatoIssue stato, TipoPriorita priorita)
    {
        return source.stream()
                .filter(i -> text.isEmpty() || i.getTitolo().toLowerCase().contains(text))
                .filter(i -> tipo == null || i.getTipo() == tipo)
                .filter(i -> stato == null || i.getStato() == stato)
                .filter(i -> priorita == null || i.getPriorita() == priorita)
                .toList();
    }

    private void resetFilters()
    {
        view.getSearchField().setText("");
        view.getFilterTipo().setSelectedIndex(0);
        view.getFilterStato().setSelectedIndex(0);
        view.getFilterPriorita().setSelectedIndex(0);

        applyFilters();
    }

    private void handleAssignRequest(Long issueId)
    {
        autoRefreshTimer.stop();

        IssueResponseDTO issue = cacheMainList.stream()
                .filter(i -> i.getId().equals(issueId))
                .findFirst()
                .orElse(null);

        if(issue == null)
        {
            autoRefreshTimer.restart();
            return;
        }

        new Thread(() ->
        {
            try
            {
                Long teamId = issue.getIdTeam();
                List<UtenteResponseDTO> members = teamService.getTeamMembers(teamId);

                SwingUtilities.invokeLater(() ->
                {
                    AssignIssueDialog dialog = new AssignIssueDialog(mainFrame, members);
                    dialog.setVisible(true);

                    List<UtenteResponseDTO> selectedUsers = dialog.getSelectedUsers();

                    if(!selectedUsers.isEmpty())
                    {
                        List<Long> ids = selectedUsers.stream()
                                .map(UtenteResponseDTO::getId)
                                .toList();
                        performAssign(issueId, ids);
                    }
                    else
                        autoRefreshTimer.restart();
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore recupero membri team", e);
                SwingUtilities.invokeLater(() ->
                {
                    JOptionPane.showMessageDialog(mainFrame, "Errore recupero membri del team", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
                    autoRefreshTimer.restart();
                });
            }
        }).start();
    }

    private void performAssign(Long issueId, List<Long> userIds)
    {
        new Thread(() ->
        {
            boolean success = issueService.assignIssue(issueId, userIds);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Issue assegnata ai membri selezionati!");
                    loadData(false);
                }
                else
                    JOptionPane.showMessageDialog(mainFrame, "Errore durante l'assegnazione.", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
                autoRefreshTimer.restart();
            });
        }).start();
    }

    private void openCreateDialog()
    {
        autoRefreshTimer.stop();

        new Thread(() ->
        {
            List<ProgettoResponseDTO> progetti = projectsService.getProgettiGestiti();

            SwingUtilities.invokeLater(() ->
            {
                IssueFormDialog dialog = new IssueFormDialog(mainFrame, progetti, null);
                dialog.setVisible(true);

                if(dialog.isConfirmed())
                {
                    CreateIssueRequestDTO request = dialog.getCreateRequest();
                    createIssue(request);
                }
                else
                    autoRefreshTimer.restart();
            });
        }).start();
    }

    private void createIssue(CreateIssueRequestDTO req)
    {
        new Thread(() ->
        {
            boolean success = issueService.createIssue(req);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Issue creata!");
                    loadData(false);
                }
                else
                    JOptionPane.showMessageDialog(mainFrame, "Errore creazione", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
                autoRefreshTimer.restart();
            });
        }).start();
    }

    private void openEditDialog(Long issueId)
    {
        autoRefreshTimer.stop();

        IssueResponseDTO issue = Stream.concat(cacheMainList.stream(), cacheCreatedList.stream())
                .filter(i -> i.getId().equals(issueId))
                .findFirst()
                .orElse(null);

        if(issue == null)
        {
            autoRefreshTimer.restart();
            return;
        }

        new Thread(() ->
        {
            List<ProgettoResponseDTO> progetti = projectsService.getProgettiGestiti();
            SwingUtilities.invokeLater(() ->
            {
                IssueFormDialog dialog = new IssueFormDialog(mainFrame, progetti, issue);
                dialog.setVisible(true);

                if(dialog.isConfirmed())
                {
                    UpdateIssueRequestDTO req = dialog.getUpdateRequest();
                    updateIssue(issueId, req);
                }
                else
                    autoRefreshTimer.restart();
            });
        }).start();
    }

    private void updateIssue(Long issueId, UpdateIssueRequestDTO req)
    {
        new Thread(() ->
        {
            boolean success = issueService.updateIssue(issueId, req);

            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Issue aggiornata!");
                    loadData(false);
                }
                else
                    JOptionPane.showMessageDialog(mainFrame, "Errore aggiornamento", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
                autoRefreshTimer.restart();
            });
        }).start();
    }

    private void resolveIssue(Long id)
    {
        autoRefreshTimer.stop();
        new Thread(() ->
        {
            boolean success = issueService.resolveIssue(id);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Risolta!");
                    loadData(false);
                }
                else
                    JOptionPane.showMessageDialog(mainFrame, "Errore operazione", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
                autoRefreshTimer.restart();
            });
        }).start();
    }

    private void archiveIssue(Long id)
    {
        autoRefreshTimer.stop();
        new Thread(() ->
        {
            boolean success = issueService.archiveIssue(id);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Archiviata!");
                    loadData(false);
                }
                else
                    JOptionPane.showMessageDialog(mainFrame, "Errore archiviazione", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
                autoRefreshTimer.restart();
            });
        }).start();
    }
}