package controller.utente;

import model.dto.request.UpdateUtenteRequestDTO;
import model.dto.response.UtenteResponseDTO;
import service.AuthService;
import service.UtenteService;
import view.UpdatePasswordDialog;

import javax.swing.*;
import java.util.*;

public class UpdatePasswordController
{
    private final UpdatePasswordDialog view;
    private final AuthService authService;
    private final UtenteService utenteService;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";

    public UpdatePasswordController(UpdatePasswordDialog view, AuthService authService, UtenteService utenteService)
    {
        this.view = view;
        this.authService = authService;
        this.utenteService = utenteService;

        this.view.setCancelAction(e -> view.dispose());
        this.view.setSaveAction(e -> handleSave());
    }

    private void handleSave()
    {
        String oldPwd = view.getOldPassword();
        String newPwd = view.getPassword();
        String confirmPwd = view.getConfirmPassword();

        if(oldPwd.isBlank())
        {
            JOptionPane.showMessageDialog(view, "Devi inserire la vecchia password.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(newPwd.isBlank())
        {
            JOptionPane.showMessageDialog(view, "La nuova password non può essere vuota.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(!newPwd.equals(confirmPwd))
        {
            JOptionPane.showMessageDialog(view, "Le nuove password non coincidono.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!newPwd.matches(PASSWORD_REGEX))
        {
            JOptionPane.showMessageDialog(view,
                    "La password deve contenere almeno 8 caratteri, inclusi lettere e numeri.",
                    "Password Debole",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UpdateUtenteRequestDTO request = new UpdateUtenteRequestDTO();
        request.setOldPassword(oldPwd);
        request.setPassword(newPwd);

        Optional<UtenteResponseDTO> result = utenteService.updateMe(request);

        if(result.isPresent())
        {
            authService.refreshLocalUser(result.get());
            JOptionPane.showMessageDialog(view, "Password aggiornata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            view.dispose();
        }
        else
            JOptionPane.showMessageDialog(view, "Errore: La vecchia password non è corretta o errore server.", "Errore", JOptionPane.ERROR_MESSAGE);
    }

    private void handleNewUserCreation()
    {





    }




}
