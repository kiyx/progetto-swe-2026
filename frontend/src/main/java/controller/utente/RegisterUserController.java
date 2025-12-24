package controller.utente;

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
        String nome = view.getNome();
        String cognome = view.getCognome();
        String email = view.getEmail();
        String pwd = view.getPassword();
        String confirmPwd = view.getConfirmPassword();
        boolean isAdmin = view.isAdmin();

        if(nome.isBlank() || cognome.isBlank() || email.isBlank() || pwd.isBlank())
        {
            showWarning("Tutti i campi sono obbligatori.");
            return;
        }

        if(!email.matches(EMAIL_REGEX))
        {
            showWarning("Inserisci un indirizzo email valido.");
            return;
        }

        if(!pwd.equals(confirmPwd))
        {
            showError("Le password non coincidono.");
            return;
        }

        if(!pwd.matches(PASSWORD_REGEX))
        {
            showWarning("La password deve contenere almeno 8 caratteri, inclusi lettere e numeri.");
            return;
        }

        RegisterRequestDTO request = new RegisterRequestDTO(email, nome, cognome, pwd, isAdmin);
        boolean success = utenteService.register(request);

        if(success)
        {
            JOptionPane.showMessageDialog(view, "Utente creato con successo!", "Operazione Completata", JOptionPane.INFORMATION_MESSAGE);
            view.dispose();
        }
        else
        {
            showError("Errore durante la creazione. L'email potrebbe essere già in uso.");
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