package view;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import controller.NavigationController;
import service.AuthService;

import javax.swing.*;

public class Main {
    public static void main(String[] args)
    {
        FlatInterFont.install();
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            AuthService mockAuth = new MockAuthService();
            NavigationController navController = new NavigationController(mainFrame, mockAuth);
            navController.start();
        });
    }

    static class MockAuthService extends AuthService
    {

        @Override
        public boolean login(String email, String password)
        {
            System.out.println("[MOCK] Tentativo login: " + email);
            if("test@bugboard26.it".equals(email) && "password1234".equals(password)) {
                return true;
            }
            return false;
        }

        @Override
        public void clearSession() {
            System.out.println("[MOCK] Sessione pulita (Logout).");
        }
    }
}