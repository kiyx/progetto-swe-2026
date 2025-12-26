package controller.issues;

import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import model.dto.response.IssueResponseDTO;
import model.dto.response.UtenteResponseDTO;
import service.AuthService;
import service.IssueService;
import service.TeamsService;
import view.MainFrame;
import view.issues.AssignIssueDialog;
import view.issues.IssuesView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class IssuesController
{
    private static final Logger LOGGER = Logger.getLogger(IssuesController.class.getName());
    private static final String MSG_ERROR = "Errore";
    private static final int REFRESH_INTERVAL_MS = 30000;

    private final IssuesView view;
    private final IssueService issueService;
    private final TeamsService teamService;
    private final AuthService authService;
    private final MainFrame mainFrame;
    private final Timer autoRefreshTimer;

    private List<IssueResponseDTO> cacheMainList = new ArrayList<>();
    private List<IssueResponseDTO> cacheCreatedList = new ArrayList<>();

    public IssuesController(IssuesView view,
                            IssueService issueService,
                            TeamsService teamService,
                            AuthService authService,
                            MainFrame mainFrame)
    {
        this.view = view;
        this.issueService = issueService;
        this.teamService = teamService;
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

        view.setOnEditIssue(id -> JOptionPane.showMessageDialog(mainFrame, "Funzionalità Modifica in sviluppo. ID: " + id));

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
                if(currentUser == null) return;

                List<IssueResponseDTO> fetchedMainList;
                if(Boolean.TRUE.equals(currentUser.getIsAdmin()))
                    fetchedMainList = issueService.getAllIssues();
                else
                    fetchedMainList = issueService.getIssuesAssignedTo(currentUser.getId());

                List<IssueResponseDTO> fetchedCreatedList = issueService.getIssuesCreatedBy(currentUser.getId());

                SwingUtilities.invokeLater(() ->
                {
                    cacheMainList = fetchedMainList;
                    cacheCreatedList = fetchedCreatedList;
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

    private void handleAssignRequest(Long issueId)
    {
        autoRefreshTimer.stop();

        IssueResponseDTO issue = cacheMainList.stream().filter(i -> i.getId().equals(issueId)).findFirst().orElse(null);
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
              //  List<UtenteResponseDTO> members = teamService.getTeamMembers(teamId);

                SwingUtilities.invokeLater(() ->
                {
                   // AssignIssueDialog dialog = new AssignIssueDialog(mainFrame, members);
                    //dialog.setVisible(true);

                    //List<UtenteResponseDTO> selectedUsers = dialog.getSelectedUsers();

                    //if(!selectedUsers.isEmpty())
                    {
                        //List<Long> ids = selectedUsers.stream().map(UtenteResponseDTO::getId).toList();
                        //performAssign(issueId, ids);
                    }
                    //else
                        autoRefreshTimer.restart();
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore membri team", e);
                SwingUtilities.invokeLater(() ->
                {
                    JOptionPane.showMessageDialog(mainFrame, "Errore recupero membri", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
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

    private void applyFilters()
    {
        String text = view.getSearchField().getText().trim().toLowerCase();
        TipoIssue tipo = (TipoIssue) view.getFilterTipo().getSelectedItem();
        StatoIssue stato = (StatoIssue) view.getFilterStato().getSelectedItem();
        TipoPriorita priorita = (TipoPriorita) view.getFilterPriorita().getSelectedItem();

        view.updateMainTab(filterList(cacheMainList, text, tipo, stato, priorita));
        view.updateCreateTab(filterList(cacheCreatedList, text, tipo, stato, priorita));
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

    private void resolveIssue(Long id)
    {
        autoRefreshTimer.stop();
        new Thread(() -> {
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

    private void openCreateDialog()
    {
        JOptionPane.showMessageDialog(mainFrame, "Apertura creazione...");
    }
}