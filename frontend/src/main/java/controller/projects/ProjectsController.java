package controller.projects;

import model.dto.response.TeamResponseDTO;
import service.AuthService;
import service.ProjectsService;
import service.TeamsService;
import view.projects.CreateProjectDialog;
import view.projects.ProjectsView;

import javax.swing.*;
import java.util.List;
import java.util.logging.*;

public class ProjectsController
{
    private static final Logger logger = Logger.getLogger(ProjectsController.class.getName());

    private final ProjectsView view;
    private final ProjectsService projectsService;
    private final TeamsService teamsService;
    private final AuthService authService;
    private final JFrame mainFrame;

    public ProjectsController(ProjectsView view,
                              ProjectsService projectsService,
                              TeamsService teamsService,
                              AuthService authService,
                              JFrame mainFrame)
    {
        this.view = view;
        this.projectsService = projectsService;
        this.teamsService = teamsService;
        this.authService = authService;
        this.mainFrame = mainFrame;

        initController();
    }

    private void initController()
    {
        boolean isAdmin = authService.getCurrentUser().getIsAdmin();
        view.setCreateButtonVisible(isAdmin);

        view.setNuovoProgettoAction(e -> openCreateDialog());
        view.setRefreshAction(e ->
        {
            logger.info("Aggiornamento manuale lista progetti...");
            loadData();
        });
        view.setOnCloseProject(id ->
        {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Sicuro di voler concludere?");
            if(confirm == JOptionPane.YES_OPTION)
            {
                new Thread(() ->
                {
                    boolean success = projectsService.concludiProgetto(id);
                    SwingUtilities.invokeLater(() ->
                    {
                        if(success)
                        {
                            JOptionPane.showMessageDialog(mainFrame, "Progetto concluso.");
                            loadData();
                        }
                    });
                }).start();
            }
        });
        view.setOnActivateProject(id -> new Thread(() ->
                                            {
                                                boolean success = projectsService.attivaProgetto(id);
                                                SwingUtilities.invokeLater(() ->
                                                {
                                                    if(success)
                                                    {
                                                        JOptionPane.showMessageDialog(mainFrame, "Progetto attivato!");
                                                        loadData();
                                                    }
                                                    else
                                                        JOptionPane.showMessageDialog(mainFrame,
                                                                "Impossibile attivare: probabilmente esiste già un progetto attivo nel team.",
                                                                "Errore", JOptionPane.ERROR_MESSAGE);
                                                });
                                            }).start());

        loadData();
    }

    private void loadData()
    {
        new Thread(() ->
        {
            logger.info(() -> "Avvio recupero progetti dal Backend...");
            try
            {
                var progetti = projectsService.getProgettiAccessibili();

                SwingUtilities.invokeLater(() ->
                {
                    view.updateTable(progetti);
                    logger.info(() -> "Tabella aggiornata con " + progetti.size() + " progetti.");
                });
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Errore durante il recupero dei progetti", e);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(mainFrame, "Errore di connessione al server.", "Errore", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    private void openCreateDialog()
    {
        logger.info(() -> "Preparazione apertura dialog creazione...");

        new Thread(() ->
        {
            try
            {
                List<TeamResponseDTO> teams = teamsService.getTeamsManagedByAdmin();

                SwingUtilities.invokeLater(() ->
                {
                    if(teams.isEmpty())
                    {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Non amministri nessun Team.\nDevi essere admin di almeno un team per creare un progetto.",
                                "Nessun Team Disponibile",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    CreateProjectDialog dialog = new CreateProjectDialog(mainFrame, teams);
                    new CreateProjectController(dialog, projectsService, authService, this::loadData);

                    dialog.setVisible(true);
                });
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Errore recupero teams per dialog", e);
            }
        }).start();
    }
}