package controller.teams;

import model.dto.request.CreateTeamRequestDTO;
import service.TeamsService;
import view.teams.CreateTeamDialog;
import javax.swing.*;
import java.util.logging.*;

public class CreateTeamController
{
    private static final Logger LOGGER = Logger.getLogger(CreateTeamController.class.getName());

    private final CreateTeamDialog view;
    private final TeamsService teamsService;
    private final Runnable onSuccessCallback;

    public CreateTeamController(CreateTeamDialog view, TeamsService teamsService, Runnable onSuccessCallback)
    {
        this.view = view;
        this.teamsService = teamsService;
        this.onSuccessCallback = onSuccessCallback;

        this.view.setSaveAction(e -> handleCreation());
    }

    private void handleCreation()
    {
        String nome = view.getTeamName();

        if(nome == null || nome.isBlank())
        {
            showWarning("Il nome del team è obbligatorio.");
            return;
        }

        if(nome.length() < 2)
        {
            showWarning("Il nome deve contenere almeno 2 caratteri.");
            return;
        }

        LOGGER.info(() -> "Tentativo creazione team: " + nome);

        new Thread(() ->
        {
            try
            {
                CreateTeamRequestDTO request = new CreateTeamRequestDTO(nome);
                boolean success = teamsService.create(request);

                SwingUtilities.invokeLater(() ->
                {
                    if(success)
                    {
                        LOGGER.info("Team creato con successo.");
                        JOptionPane.showMessageDialog(view, "Team creato con successo!", "Completato", JOptionPane.INFORMATION_MESSAGE);
                        view.dispose();

                        if(onSuccessCallback != null)
                            onSuccessCallback.run();
                    }
                    else
                    {
                        LOGGER.warning("Creazione fallita.");
                        showError("Errore durante la creazione.");
                    }
                });
            }
            catch(Exception e)
            {
                LOGGER.log(Level.SEVERE, "Eccezione creazione team", e);
                SwingUtilities.invokeLater(() -> showError("Errore di comunicazione."));
            }
        }).start();
    }

    private void showWarning(String msg)
    {
        JOptionPane.showMessageDialog(view, msg, "Attenzione", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg)
    {
        JOptionPane.showMessageDialog(view, msg, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}