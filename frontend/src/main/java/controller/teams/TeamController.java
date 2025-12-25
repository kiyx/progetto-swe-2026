package controller.teams;

import model.dto.response.TeamResponseDTO;
import service.AuthService;
import service.TeamsService;
import view.teams.CreateTeamDialog;
import view.teams.TeamsView;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamController
{
    private static final Logger LOGGER = Logger.getLogger(TeamController.class.getName());

    private final TeamsView view;
    private final TeamsService teamsService;
    private final AuthService authService;
    private final JFrame mainFrame;

    public TeamController(TeamsView view, TeamsService teamsService, AuthService authService, JFrame mainFrame)
    {
        this.view = view;
        this.teamsService = teamsService;
        this.authService = authService;
        this.mainFrame = mainFrame;

        initController();
        loadTeams();
    }
    private void initController()
    {

        view.addCreateListener(e -> openCreateDialog());
        view.addRefreshListener(e ->
        {
            LOGGER.info("Aggiornamento manuale lista teams...");
            loadTeams();
        });

        loadTeams();

    }

    private void loadTeams()
    {
        try{
            List<TeamResponseDTO> teams = teamsService.getTeamsManagedByAdmin();
            view.getModel().setData(teams);
        }
        catch(Exception e)
        {
            LOGGER.log(Level.SEVERE, "Errore nel load Team", e);
        }

    }

    private void openCreateDialog()
    {
        LOGGER.info(() -> "Preparazione apertura dialog creazione...");

        new Thread(() ->
                SwingUtilities.invokeLater(() ->
                {

                    CreateTeamDialog dialog = new CreateTeamDialog(mainFrame);
                    new CreateTeamController(dialog, teamsService, authService);

                    dialog.setVisible(true);
                })).start();
    }

}
