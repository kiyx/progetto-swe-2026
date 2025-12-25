package controller.user;

import service.AuthService;
import service.NavigationService;
import view.user.LoginView;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.logging.*;
import java.util.regex.*;

public class LoginController
{
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private static final int EMAIL_MIN_LENGTH = 4;
    private static final int EMAIL_MAX_LENGTH = 50;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final Pattern USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final LoginView view;
    private final AuthService authService;
    private final NavigationService navigationService;

    public LoginController(LoginView view, AuthService authService, NavigationService navigationService)
    {
        this.view = view;
        this.authService = authService;
        this.navigationService = navigationService;

        initController();
    }

    private void initController()
    {
        view.setLoginButtonEnabled(false);

        view.addInputListener(new DocumentListener()
        {
            @Override public void insertUpdate(DocumentEvent e) {validateInput();}
            @Override public void removeUpdate(DocumentEvent e) {validateInput();}
            @Override public void changedUpdate(DocumentEvent e) {validateInput();}
        });

        FocusAdapter selectAllListener = new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if(e.getSource() instanceof JTextField field && !field.getText().isEmpty())
                    field.selectAll();
            }
        };
        view.addFocusListener(selectAllListener);

        view.addLoginActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e)
    {
        String username = view.getEmail();
        String password = view.getPassword();
        String fullEmail = username + view.getEmailDomain();

        LOGGER.info(() -> "Richiesta login avviata dall'interfaccia per: " + fullEmail);

        view.showLoadingState(true);
        view.clearErrorMessage();

        new Thread(() ->
        {
            boolean success = authService.login(fullEmail, password);

            SwingUtilities.invokeLater(() ->
            {
                if(success)
                {
                    LOGGER.info("Login confermato. Reindirizzamento alla Home.");
                    navigationService.goToDashboard();
                }
                else
                {
                    LOGGER.warning("Login negato. Mostro errore all'utente.");
                    view.showLoadingState(false);
                    view.showErrorMessage("Credenziali non valide");
            }
            });
        }).start();
    }

    private void validateInput()
    {
        view.clearErrorMessage();
        boolean checkEmail = checkEmail();
        boolean checkPassword = checkPassword();

        view.setLoginButtonEnabled(checkEmail && checkPassword);
    }

    private boolean checkEmail()
    {
        String email = view.getEmail();
        boolean isValid = true;
        String errorMsg = "";

        if(email.contains(" ") || email.contains("\t") || email.contains("\n"))
        {
            errorMsg = "L'email non può contenere spazi!";
            isValid = false;
        }

        else if (email.length() < EMAIL_MIN_LENGTH || email.length() > EMAIL_MAX_LENGTH)
        {
            if(!email.isEmpty())
                errorMsg = "L'username deve essere tra " + EMAIL_MIN_LENGTH + " e " + EMAIL_MAX_LENGTH + " caratteri.";

            isValid = false;
        }
        else if (!USERNAME_REGEX.matcher(email).matches())
        {
            errorMsg = "Formato email non valido.";
            isValid = false;
        }

        view.setEmailError(!isValid && !email.isEmpty(), errorMsg);
        return isValid;
    }

    private boolean checkPassword()
    {
        String password = view.getPassword();
        boolean isValid = true;
        String errorMsg = "";

        if(password.length() < PASSWORD_MIN_LENGTH)
        {
            if(!password.isEmpty())
                errorMsg = "La password deve avere almeno " + PASSWORD_MIN_LENGTH + " caratteri.";
            isValid = false;
        }

        view.setPasswordError(!isValid && !password.isEmpty(), errorMsg);
        return isValid;
    }
}