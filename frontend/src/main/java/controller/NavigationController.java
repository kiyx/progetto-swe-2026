package controller;

import service.AuthService;
import service.NavigationService;
import view.LoginView;
import view.MainFrame;

import javax.swing.*;

public class NavigationController implements NavigationService
{
    private static final String VIEW_LOGIN = "LOGIN";
    private static final String VIEW_DASHBOARD = "DASHBOARD";
    private static final String VIEW_TEAMS = "TEAMS";
    private static final String VIEW_PROJECTS = "PROJECTS";

    private final MainFrame mainFrame;
    private final AuthService authService;

    public NavigationController(MainFrame mainFrame, AuthService authService)
    {
        this.mainFrame = mainFrame;
        this.authService = authService;
    }

    public void start()
    {
        SwingUtilities.invokeLater(this::goToLogin);
    }

    public void goToLogin()
    {
        SwingUtilities.invokeLater(() ->
        {
            mainFrame.clearViews();
            LoginView loginView = new LoginView();
            new LoginController(loginView, this.authService, this);

            mainFrame.addView(loginView, VIEW_LOGIN);
            mainFrame.showView(VIEW_LOGIN);
            loginView.setAsDefaultFocus();

            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);

            if(!mainFrame.isVisible())
                mainFrame.setVisible(true);
        });
    }

    public void goToDashboard()
    {
        SwingUtilities.invokeLater(() ->
        {
            mainFrame.clearViews();

            //mainFrame.addView(dashboardView, VIEW_DASHBOARD);
            mainFrame.showView(VIEW_DASHBOARD);
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
        });
    }

    public void logout()
    {
        authService.logout();
        goToLogin();
    }
}