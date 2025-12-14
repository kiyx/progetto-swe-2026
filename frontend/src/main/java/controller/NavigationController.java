package controller;

import view.*;
import view.component.HeaderPanel;
import view.component.SidebarPanel;
import service.AuthService;
import service.NavigationService;
import javax.swing.*;

public class NavigationController implements NavigationService
{
    private static final String VIEW_LOGIN = "LOGIN";
    private static final String VIEW_APP_SHELL = "APP_SHELL";

    private static final String INNER_DASHBOARD = "DASHBOARD";
    private static final String INNER_ISSUES = "ISSUES";
    private static final String INNER_TEAMS = "TEAMS";
    private static final String INNER_PROJECTS = "PROJECTS";

    private MainLayoutView mainLayoutView;
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

    private void initMainLayoutIfNeeded()
    {
        if(mainLayoutView == null)
        {
            var user = authService.getCurrentUser();
            String username = (user != null) ? user.getNome() + " " + user.getCognome() : "Utente";
            boolean isAdmin = (user != null) && user.getIsAdmin();

            HeaderPanel header = new HeaderPanel(username, isAdmin);
            SidebarPanel sidebar = new SidebarPanel();
            mainLayoutView = new MainLayoutView(header, sidebar);

            header.setToggleAction(e -> mainLayoutView.toggleSidebar());
            header.setProfileEditAction(e -> showProfileDialog());
            header.setLogoutAction(e -> logout());

            sidebar.setDashboardAction(e -> goToDashboard());
            sidebar.setIssuesAction(e -> goToIssues());
            sidebar.setTeamsAction(e -> goToTeams());
            sidebar.setProjectsAction(e -> goToProjects());


            mainFrame.addView(mainLayoutView, VIEW_APP_SHELL);

            mainLayoutView.addContentView(new DashboardView(), INNER_DASHBOARD);
            // mainLayoutView.addContentView(new IssuesView(), INNER_ISSUES);
            // mainLayoutView.addContentView(new ProjectsView(), INNER_PROJECTS);
            mainLayoutView.addContentView(new TeamsView(), INNER_TEAMS);
        }
    }

    @Override
    public void goToLogin()
    {
        SwingUtilities.invokeLater(() ->
        {
            mainFrame.clearViews();
            mainLayoutView = null;

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

    @Override
    public void goToDashboard()
    {
        SwingUtilities.invokeLater(() ->
        {
            initMainLayoutIfNeeded();

            mainFrame.showView(VIEW_APP_SHELL);
            mainLayoutView.showContentView(INNER_DASHBOARD);
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
        });
    }

    @Override
    public void goToIssues()
    {
        SwingUtilities.invokeLater(() ->
        {
            initMainLayoutIfNeeded();
            mainFrame.showView(VIEW_APP_SHELL);


            mainLayoutView.showContentView(INNER_ISSUES);
        });
    }
    
    @Override
    public void goToTeams()
    {
        SwingUtilities.invokeLater(() ->
        {
            initMainLayoutIfNeeded();
            mainFrame.showView(VIEW_APP_SHELL);


            mainLayoutView.showContentView(INNER_TEAMS);
        });
    }

    @Override
    public void goToProjects()
    {
        SwingUtilities.invokeLater(() ->
        {
            initMainLayoutIfNeeded();
            mainFrame.showView(VIEW_APP_SHELL);


            mainLayoutView.showContentView(INNER_PROJECTS);
        });
    }

    private void showProfileDialog()
    {
        JOptionPane.showMessageDialog(mainFrame, "Qui aprirei il JDialog del profilo!");
    }

    @Override
    public void logout()
    {
        authService.logout();
        goToLogin();
    }
}