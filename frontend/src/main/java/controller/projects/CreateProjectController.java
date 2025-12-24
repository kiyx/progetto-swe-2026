package controller.projects;

import model.dto.enums.StatoProgetto;
import model.dto.request.CreateProgettoRequestDTO;
import model.dto.response.TeamResponseDTO;
import service.AuthService;
import service.ProjectsService;
import view.projects.CreateProjectDialog;

import javax.swing.*;
import java.util.logging.*;

public class CreateProjectController
{
    private static final Logger logger = Logger.getLogger(CreateProjectController.class.getName());

    private final CreateProjectDialog view;
    private final ProjectsService progettoService;
    private final AuthService authService;
    private final Runnable onSuccessCallback;

    public CreateProjectController(CreateProjectDialog view,
                                   ProjectsService projectsService,
                                   AuthService authService,
                                   Runnable onSuccessCallback)
    {
        this.view = view;
        this.progettoService = projectsService;
        this.authService = authService;
        this.onSuccessCallback = onSuccessCallback;

        initController();
    }

    private void initController()
    {
        this.view.setSaveAction(e -> handleSave());
        this.view.setCancelAction(e -> view.dispose());
    }

    private void handleSave()
    {
        String nome = view.getNome();
        StatoProgetto stato = view.getStato();
        TeamResponseDTO team = view.getSelectedTeam();

        if(nome.isBlank() || team == null || stato == null)
        {
            JOptionPane.showMessageDialog(view, "Tutti i campi sono obbligatori.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CreateProgettoRequestDTO request = new CreateProgettoRequestDTO(
                nome,
                stato,
                team.getId(),
                authService.getCurrentUser().getId()
        );

        logger.info(() -> "Invio richiesta creazione progetto: " + nome + " [Stato: " + stato + "]");
        boolean success = progettoService.createProgetto(request);

        if(success)
        {
            JOptionPane.showMessageDialog(view, "Progetto creato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            view.dispose();

            if(onSuccessCallback != null)
                onSuccessCallback.run();
        }
        else
        {
            JOptionPane.showMessageDialog(view,
                    """
                            Impossibile creare il progetto.
                            Verifica che il Team selezionato non abbia già un progetto ATTIVO.
                            Un team può lavorare su un solo progetto attivo alla volta.""",
                    "Errore Creazione",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}