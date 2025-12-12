package controller;

import service.AuthService;
import service.NavigationService;
import view.LoginView;
import view.MainFrame;


public class NavigationController implements NavigationService
{
    private static final String VIEW_LOGIN = "LOGIN";
    private static final String VIEW_HOMEPAGE = "HOMEPAGE";

    private final MainFrame mainFrame;
    private final AuthService authService;

    public NavigationController(MainFrame mainFrame, AuthService authService)
    {
        this.mainFrame = mainFrame;
        this.authService = authService;
    }

    public void start()
    {
        goToLogin();
        mainFrame.setVisible(true);
    }

    public void goToLogin()
    {
        mainFrame.clearViews();
        LoginView loginView = new LoginView();

        new LoginController(loginView, this.authService, this);
        mainFrame.addView(loginView, VIEW_LOGIN);
        mainFrame.showView(VIEW_LOGIN);
    }

    public void goToHome()
    {
        mainFrame.clearViews();

        
    }

    public void logout()
    {

    }
}
