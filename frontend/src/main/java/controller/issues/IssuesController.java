package controller.issues;

import model.dto.response.IssueResponseDTO;
import service.AuthService;
import service.IssueService;
import view.MainFrame;
import view.issues.IssuesView;

import javax.swing.*;
import java.util.List;
import java.util.logging.*;

public class IssuesController
{
    private static final Logger LOGGER = Logger.getLogger(IssuesController.class.getName());
    private static final String MSG_ERROR = "Errore";

    private final IssuesView view;
    private final IssueService issueService;
    private final AuthService authService;
    private final MainFrame mainFrame;

    public IssuesController(IssuesView view,
                            IssueService issueService,
                            AuthService authService,
                            MainFrame mainFrame)
    {
        this.view = view;
        this.issueService = issueService;
        this.authService = authService;
        this.mainFrame = mainFrame;

        initController();
    }

    private void initController()
    {
        LOGGER.info("Inizializzazione IssuesController");

        view.setRefreshAction(e -> loadData());
        view.setNuovaIssueAction(e -> openCreateDialog());

        view.setOnEditIssue(id -> JOptionPane.showMessageDialog(mainFrame, "Funzionalità Modifica in sviluppo. ID: " + id));

        view.setOnResolveIssue(id ->
        {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Vuoi marcare questa issue come RISOLTA?",
                    "Conferma Risoluzione", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION)
                resolveIssue(id);
        });

        view.setOnArchiveIssue(id ->
        {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Sei sicuro di voler archiviare questo BUG?",
                    "Conferma Archiviazione", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION)
                archiveIssue(id);
        });

        loadData();
    }

    private void loadData()
    {
        SwingUtilities.invokeLater(() ->
            new Thread(() ->
            {
                try
                {
                    var currentUser = authService.getCurrentUser();
                    if(currentUser == null) return;

                    LOGGER.info("Caricamento dati issues per utente: " + currentUser.getEmail());

                    List<IssueResponseDTO> mainList;

                    if(Boolean.TRUE.equals(currentUser.getIsAdmin()))
                        mainList = issueService.getAllIssues();
                    else
                        mainList = issueService.getIssuesAssignedTo(currentUser.getId());

                    List<IssueResponseDTO> createdList = issueService.getIssuesCreatedBy(currentUser.getId());

                    SwingUtilities.invokeLater(() ->
                    {
                        view.updateMainTab(mainList);
                        view.updateCreateTab(createdList);
                    });
                }
                catch(Exception e)
                {
                    LOGGER.log(Level.SEVERE, "Errore caricamento issues", e);
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(mainFrame, "Errore nel caricamento dati", MSG_ERROR, JOptionPane.ERROR_MESSAGE)
                    );
                }
            }).start()
        );
    }

    private void resolveIssue(Long id)
    {
        LOGGER.info(() -> "Tentativo risoluzione issue ID: " + id);
        new Thread(() ->
        {
            boolean success = issueService.resolveIssue(id);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Issue risolta con successo!");
                    loadData();
                }
                else
                    JOptionPane.showMessageDialog(mainFrame, "Errore durante l'operazione.", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
            });
        }).start();
    }

    private void archiveIssue(Long id)
    {
        LOGGER.info(() -> "Tentativo archiviazione issue ID: " + id);
        new Thread(() ->
        {
            boolean success = issueService.archiveIssue(id);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Bug archiviato con successo.");
                    loadData();
                }
                else
                    JOptionPane.showMessageDialog(mainFrame, "Errore durante l'archiviazione.", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
            });
        }).start();
    }

    private void openCreateDialog()
    {
        JOptionPane.showMessageDialog(mainFrame, "Apertura dialog creazione...");

    }
}