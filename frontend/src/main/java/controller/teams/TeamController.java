package controller.teams;

import controller.projects.CreateProjectController;
import model.dto.response.TeamResponseDTO;
import model.dto.response.UtenteResponseDTO;
import service.TeamsService;
import view.projects.CreateProjectDialog;
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
        {
            try
            {
                List<UtenteResponseDTO> utenti = teamsService.getTeamsNotMembers(team.getId());

                SwingUtilities.invokeLater(() ->
                {
                    if(utenti.isEmpty())
                    {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Non esistono utenti disponibili per l'aggiunta.\n",
                                "Nessun utente Disponibile da aggiungere al team",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    AddMembroDialog dialog = new AddMembroDialog(mainFrame, utenti);
                    //new UpdateTeamController(dialog, teamsService);
                    dialog.setVisible(true);
                });
            }
            catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore recupero utenti per dialog addmembri", e);
            }
        }).start();
    }

    private void onRemoveMember(TeamResponseDTO team) {
        LOGGER.info(() -> "Preparazione apertura dialog rimozione membro dal team...");

        new Thread(() ->
        {
            try
            {
                List<UtenteResponseDTO> utenti = teamsService.getTeamMembers(team.getId());

                SwingUtilities.invokeLater(() ->
                {
                    if(utenti.isEmpty())
                    {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Non esistono utenti disponibili per la rimozione.\nDevi aggiungere almeno un utente al server e al team.",
                                "Nessun utente Disponibile",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    DeleteMembroDialog dialog = new DeleteMembroDialog(mainFrame, utenti);
                    //new UpdateTeamController(dialog, teamsService);
                    dialog.setVisible(true);
                });
            }
            catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore recupero utenti per dialog deletemembri", e);
            }
        }).start();
    }

    private void onViewMembers(TeamResponseDTO team) {
        new Thread(() ->
        {
            try
            {
                List<UtenteResponseDTO> utenti = teamsService.getTeamMembers(team.getId());

                SwingUtilities.invokeLater(() ->
                {
                    if(utenti.isEmpty())
                    {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Non esistono utenti disponibili.\nDevi aggiungere almeno un utente al server.",
                                "Nessun utente Disponibile",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    ShowMembriDialog dialog = new ShowMembriDialog(mainFrame, utenti);
                    dialog.setVisible(true);
                });
            }
            catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore recupero utenti per dialog ShowMembri", e);
            }
        }).start();
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
