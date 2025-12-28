package controller.teams;

import model.dto.response.TeamResponseDTO;
import model.dto.response.UtenteResponseDTO;
import service.TeamsService;
import view.teams.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.List;
import java.util.logging.*;

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
            LOGGER.info("Richiesta aggiornamento manuale lista team.");
            loadTeams();
        });
    }

    private void loadTeams()
    {
        new Thread(() ->
        {
            try
            {
                List<TeamResponseDTO> teams = teamsService.getTeamsManagedByAdmin();

                SwingUtilities.invokeLater(() ->
                {
                    DefaultTableModel model = view.getModel();
                    model.setRowCount(0);

                    if(teams != null)
                    {
                        for(TeamResponseDTO team : teams)
                        {
                            model.addRow(new Object[]{
                                    team.getId(),
                                    team.getNome(),
                                    team
                            });
                        }
                    }
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore durante il caricamento dei team", e);
            }
        }).start();
    }

    private void onViewMembers(TeamResponseDTO team)
    {
        LOGGER.info(() -> "Recupero membri per il team ID: " + team.getId());

        new Thread(() ->
        {
            try
            {
                List<UtenteResponseDTO> membri = teamsService.getTeamMembers(team.getId());

                SwingUtilities.invokeLater(() ->
                {
                    if(membri.isEmpty())
                    {
                        JOptionPane.showMessageDialog(mainFrame, "Nessun membro presente nel team.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    ShowMembriDialog dialog = new ShowMembriDialog(mainFrame, membri);
                    dialog.setVisible(true);
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore fetch membri", e);
                showError("Errore di rete durante il recupero dei membri.");
            }
        }).start();
    }

    private void onAddMember(TeamResponseDTO team)
    {
        LOGGER.info(() -> "Apertura dialog Aggiungi Membro per Team ID: " + team.getId());

        new Thread(() ->
        {
            try
            {
                List<UtenteResponseDTO> utentiDisponibili = teamsService.getTeamsNotMembers(team.getId());

                SwingUtilities.invokeLater(() ->
                {
                    if(utentiDisponibili.isEmpty())
                    {
                        JOptionPane.showMessageDialog(mainFrame, "Nessun utente disponibile da aggiungere.");
                        return;
                    }

                    AddMembroDialog dialog = new AddMembroDialog(mainFrame, utentiDisponibili);
                    dialog.setVisible(true);

                    List<UtenteResponseDTO> selected = dialog.getSelectedUsers();
                    if(!selected.isEmpty())
                    {
                        List<Long> ids = selected.stream().map(UtenteResponseDTO::getId).toList();
                        performAddMembri(team.getId(), ids);
                    }
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore fetch utenti disponibili", e);
            }
        }).start();
    }

    private void onRemoveMember(TeamResponseDTO team)
    {
        LOGGER.info(() -> "Apertura dialog Rimuovi Membro per Team ID: " + team.getId());

        new Thread(() ->
        {
            try
            {
                List<UtenteResponseDTO> membriAttuali = teamsService.getTeamMembers(team.getId());

                SwingUtilities.invokeLater(() ->
                {
                    if(membriAttuali.isEmpty())
                    {
                        JOptionPane.showMessageDialog(mainFrame, "Nessun membro da rimuovere.");
                        return;
                    }

                    DeleteMembroDialog dialog = new DeleteMembroDialog(mainFrame, membriAttuali);
                    dialog.setVisible(true);

                    List<UtenteResponseDTO> selected = dialog.getSelectedUsers();
                    if (!selected.isEmpty())
                    {
                        List<Long> ids = selected.stream().map(UtenteResponseDTO::getId).toList();
                        performRemoveMembri(team.getId(), ids);
                    }
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Errore fetch membri per rimozione", e);
            }
        }).start();
    }

    private void performAddMembri(Long teamId, List<Long> userIds)
    {
        new Thread(() ->
        {
            boolean success = teamsService.aggiungiMembri(teamId, userIds);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Membri aggiunti con successo!");
                    loadTeams();
                }
                else
                    showError("Errore durante l'aggiunta dei membri.");
            });
        }).start();
    }

    private void performRemoveMembri(Long teamId, List<Long> userIds)
    {
        new Thread(() ->
        {
            boolean success = teamsService.rimuoviMembri(teamId, userIds);
            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    JOptionPane.showMessageDialog(mainFrame, "Membri rimossi con successo!");
                    loadTeams();
                }
                else
                    showError("Errore durante la rimozione dei membri.");
            });
        }).start();
    }

    private void openCreateDialog()
    {
        CreateTeamDialog dialog = new CreateTeamDialog(mainFrame);
        new CreateTeamController(dialog, teamsService, this::loadTeams);
        dialog.setVisible(true);
    }

    private void showError(String msg)
    {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, msg, "Errore", JOptionPane.ERROR_MESSAGE));
    }
}