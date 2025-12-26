package controller.teams;

import model.dto.response.TeamResponseDTO;
import service.TeamsService;
import view.teams.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamController
{
    private static final Logger LOGGER = Logger.getLogger(TeamController.class.getName());

    private final TeamsView view;
    private final TeamsService teamsService;
    private final JFrame mainFrame;

    public TeamController(TeamsView view, TeamsService teamsService, JFrame mainFrame)
    {
        this.view = view;
        this.teamsService = teamsService;
        this.mainFrame = mainFrame;

        initController();
        loadTeams();
    }
    private void initController()
    {

        view.setTableActions(
                this::onAddMember,
                this::onRemoveMember,
                this::onViewMembers
        );

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
            DefaultTableModel model = view.getModel();

            model.setRowCount(0);

            if (teams != null) {
                for (TeamResponseDTO team : teams) {
                    model.addRow(new Object[]{
                            team.getId(),
                            team.getNome(),
                            team
                    });
                }
            }
        }
        catch(Exception e)
        {
            LOGGER.log(Level.SEVERE, "Errore nel load Team", e);
        }

    }

    private void onAddMember(TeamResponseDTO team) {
        LOGGER.info(() -> "Preparazione apertura dialog aggiunta membro al team...");

        new Thread(() ->
                SwingUtilities.invokeLater(() ->
                {

                    //AddMembroDialog dialog = new AddMembroDialog(mainFrame);
                    //new UpdateTeamController(dialog, teamsService);

                    //dialog.setVisible(true);
                })).start();
    }

    private void onRemoveMember(TeamResponseDTO team) {
        LOGGER.info(() -> "Preparazione apertura dialog rimozione membro dal team...");

        new Thread(() ->
                SwingUtilities.invokeLater(() ->
                {

                    //DeleteMembroDialog dialog = new DeleteMembroDialog(mainFrame);
                    //new UpdateTeamController(dialog, teamsService);

                    //dialog.setVisible(true);
                })).start();
    }

    private void onViewMembers(TeamResponseDTO team) {
        LOGGER.info(() -> "Visualizzazione lista membri team " + team.getId());
        new Thread(() ->
                SwingUtilities.invokeLater(() ->
                {

                    //ShowMembriDialog dialog = new ShowMembriDialog(mainFrame);
                    //new UpdateTeamController(dialog, teamsService);

                    //dialog.setVisible(true);
                })).start();
    }

    private void openCreateDialog()
    {
        LOGGER.info(() -> "Preparazione apertura dialog creazione...");

        new Thread(() ->
                SwingUtilities.invokeLater(() ->
                {

                    CreateTeamDialog dialog = new CreateTeamDialog(mainFrame);
                    new CreateTeamController(dialog, teamsService);

                    dialog.setVisible(true);
                })).start();
    }

}
