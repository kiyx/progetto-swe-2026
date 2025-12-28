package controller.teams;

import model.dto.request.CreateTeamRequestDTO;
import service.TeamsService;
import view.teams.CreateTeamDialog;

import javax.swing.*;

public class CreateTeamController
{
    private final CreateTeamDialog view;
    private final TeamsService teamsService;
    private final Runnable onSuccessCallback;

    public CreateTeamController(CreateTeamDialog view, TeamsService teamsService, Runnable onSuccessCallback)
    {
        this.view = view;
        this.teamsService = teamsService;
        this.onSuccessCallback = onSuccessCallback;

        this.view.setSaveAction(e -> handleRegistration());
    }

    private void handleRegistration()
    {
        String nome = view.getTeamName();

        if(nome == null || nome.isBlank())
        {
            showWarning("Il nome del team è obbligatorio.");
            return;
        }

        CreateTeamRequestDTO request = new CreateTeamRequestDTO(nome);
        boolean success = teamsService.create(request);

        if(success)
        {
            JOptionPane.showMessageDialog(view, "Team creato con successo!", "Operazione Completata", JOptionPane.INFORMATION_MESSAGE);
            view.dispose();

            if(onSuccessCallback != null)
                onSuccessCallback.run();
        }
        else
        {
            showError("Errore durante la creazione.");
        }
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
