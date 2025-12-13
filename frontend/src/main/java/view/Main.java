package view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import controller.NavigationController;
import service.AuthService;

import javax.swing.*;
import java.net.http.*;
import java.time.*;
import java.util.logging.Logger; // Importa il logger

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
            MainFrame mainFrame = new MainFrame();
            NavigationController navController = new NavigationController(mainFrame, authService);

            navController.start();
        });
    }
}