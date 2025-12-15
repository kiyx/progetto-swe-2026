package controller;

import model.dto.request.RegisterRequestDTO;
import service.UtenteService;
import view.RegisterUserDialog;

import javax.swing.*;

public class RegisterUserController
{

    private final RegisterUserDialog view;
    private final UtenteService utenteService;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public RegisterUserController(RegisterUserDialog view, UtenteService utenteService)
    {
        this.view = view;
        this.utenteService = utenteService;

        this.view.setCancelAction(e -> view.dispose());
        this.view.setSaveAction(e -> handleRegistration());
    }

    private void handleRegistration()
    {
        String email = view.getEmail();
        String nome = view.getNome();
        String cognome = view.getCognome();
        String pwd = view.getPassword();
        String confirmPwd = view.getConfirmPassword();
        boolean isAdmin = view.getIsAdmin();

        if(email.isBlank() || nome.isBlank() || cognome.isBlank() || pwd.isBlank())
        {
            showError("Tutti i campi (eccetto Admin) sono obbligatori.");
            return;
        }

        if(!email.matches(EMAIL_REGEX))
        {
            showError("Formato email non valido.");
            return;
        }

        if(!pwd.equals(confirmPwd))
        {
            showError("Le password non coincidono.");
            return;
        }

        if(!pwd.matches(PASSWORD_REGEX))
        {
            JOptionPane.showMessageDialog(view, "La password deve contenere almeno 8 caratteri, inclusi lettere e numeri.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RegisterRequestDTO request = new RegisterRequestDTO(email, nome, cognome, pwd, isAdmin);

        try
        {
            boolean success = utenteService.register(request);
            if(success)
            {
                JOptionPane.showMessageDialog(view, "Utente creato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                view.dispose();
            }
            else
                showError("Impossibile creare l'utente. L'email potrebbe essere già in uso.");
        } catch (Exception ex) {
            showError("Errore Server: " + ex.getMessage());
        }
    }

    private void showError(String message)
    {
        JOptionPane.showMessageDialog(view, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

}