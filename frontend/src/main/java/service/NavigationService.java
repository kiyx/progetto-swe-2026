package service;

public interface NavigationService
{
    void start();
    void goToLogin();
    void goToDashboard();
    void goToIssues();
    void goToTeams();
    void goToProjects();
    void logout();
}