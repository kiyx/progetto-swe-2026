package view;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import controller.LoginController;
import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        FlatInterFont.install();
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame("BugBoard");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);

            LoginView view = new LoginView();
            new LoginController(view);
            frame.setContentPane(view);
            frame.setVisible(true);
        });
    }
}