package controller;

import controller.projects.*;
import controller.teams.TeamController;
import controller.utente.*;
import service.*;
import view.*;
import view.component.*;
import view.projects.*;
import view.teams.TeamsView;

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
    private final UtenteService utenteService;
    private final ProjectsService projectsService;
    private final TeamsService teamsService;

    public NavigationController(MainFrame mainFrame,
                                AuthService authService,
                                UtenteService utenteService,
                                ProjectsService projectsService,
                                TeamsService teamsService)
    {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.utenteService = utenteService;
        this.projectsService = projectsService;
        this.teamsService = teamsService;
    }

    @Override
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
            SidebarPanel sidebar = new SidebarPanel(isAdmin);
            mainLayoutView = new MainLayoutView(header, sidebar);

            header.setToggleAction(e -> mainLayoutView.toggleSidebar());
            header.setEditPasswordAction(e -> showEditPasswordDialog());
            header.setLogoutAction(e -> logout());

            if(isAdmin)
                header.setCreateUserAction(e -> showRegisterUserDialog());

            sidebar.setDashboardAction(e -> goToDashboard());
            sidebar.setIssuesAction(e -> goToIssues());
            sidebar.setTeamsAction(e -> goToTeams());
            sidebar.setProjectsAction(e -> goToProjects());

            mainFrame.addView(mainLayoutView, VIEW_APP_SHELL);

            mainLayoutView.addContentView(new DashboardView(), INNER_DASHBOARD);
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

            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
        });
    }

    @Override
    public void goToTeams()
    {
        SwingUtilities.invokeLater(() ->
        {
            initMainLayoutIfNeeded();

            TeamsView teamsView = new TeamsView();
            new TeamController(teamsView, teamsService, authService, mainFrame);

            mainLayoutView.addContentView(teamsView, INNER_TEAMS);
            mainFrame.showView(VIEW_APP_SHELL);
            mainLayoutView.showContentView(INNER_TEAMS);

            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
        });
    }

    @Override
    public void goToProjects()
    {
        SwingUtilities.invokeLater(() ->
        {
            initMainLayoutIfNeeded();

            ProjectsView projectsView = new ProjectsView();
            new ProjectsController(projectsView, projectsService, teamsService, authService, mainFrame);

            mainLayoutView.addContentView(projectsView, INNER_PROJECTS);
            mainFrame.showView(VIEW_APP_SHELL);
            mainLayoutView.showContentView(INNER_PROJECTS);

            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
        });
    }

    private void showEditPasswordDialog()
    {
        var currentUser = authService.getCurrentUser();

        if(currentUser != null)
        {
            UpdatePasswordDialog dialog = new UpdatePasswordDialog(mainFrame, currentUser);
            new UpdatePasswordController(dialog, authService, utenteService);
            dialog.setVisible(true);
        }
    }

    public void showRegisterUserDialog()
    {
        var currentUser = authService.getCurrentUser();
        if(currentUser == null || !currentUser.getIsAdmin())
        {
            JOptionPane.showMessageDialog(mainFrame, "Azione non autorizzata", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RegisterUserDialog dialog = new RegisterUserDialog(mainFrame);
        new RegisterUserController(dialog, utenteService);
        dialog.setVisible(true);
    }

    @Override
    public void logout()
    {
        authService.logout();
        goToLogin();
    }
}