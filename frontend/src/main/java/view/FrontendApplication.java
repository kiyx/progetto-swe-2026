package view;

import com.fasterxml.jackson.databind.*;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.fonts.inter.*;
import controller.NavigationController;
import service.*;
import javax.swing.*;
import java.net.http.*;
import java.time.*;
import java.util.logging.*;

public class FrontendApplication
{
    private static final Logger LOGGER = Logger.getLogger(FrontendApplication.class.getName());

    public static void main(String[] args)
    {
        FlatInterFont.install();
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() ->
        {
            LOGGER.info("Avvio dell'applicazione BugBoard Frontend...");

            HttpClient sharedClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            ObjectMapper sharedMapper = new ObjectMapper();
            AuthService authService = new AuthService(sharedClient, sharedMapper);

            NavigationController navController = getNavigationController(sharedClient, sharedMapper, authService);
            navController.start();
        });
    }

    private static NavigationController getNavigationController(HttpClient sharedClient, ObjectMapper sharedMapper, AuthService authService)
    {
        UtenteService utenteService = new UtenteService(sharedClient, sharedMapper, authService);
        ProjectsService projectsService = new ProjectsService(sharedClient, sharedMapper, authService);
        TeamsService teamsService = new TeamsService(sharedClient, sharedMapper, authService);
        IssueService issueService = new IssueService(sharedClient, sharedMapper, authService);

        MainFrame mainFrame = new MainFrame();

        return new NavigationController(
                mainFrame,
                authService,
                utenteService,
                issueService,
                projectsService,
                teamsService
        );
    }
}