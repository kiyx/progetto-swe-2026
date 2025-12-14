package view;

import controller.NavigationController;
import com.fasterxml.jackson.databind.*;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.fonts.inter.*;
import service.AuthService;
import service.UtenteService;

import javax.swing.*;
import java.net.http.*;
import java.time.*;
import java.util.logging.*;

public class Main
{
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

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
            UtenteService utenteService = new UtenteService(sharedClient, sharedMapper, authService);
            MainFrame mainFrame = new MainFrame();
            NavigationController navController = new NavigationController(mainFrame, authService, utenteService);

            navController.start();
        });
    }
}