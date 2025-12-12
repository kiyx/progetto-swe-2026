package view;

import org.jdesktop.swingx.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JXFrame
{
    private final CardLayout cardLayout;
    private final JXPanel mainPanel;

    public MainFrame()
    {
        super("BugBoard26");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.mainPanel = new JXPanel(cardLayout);

        add(mainPanel);
    }

    public void addView(JXPanel view, String key)
    {
        mainPanel.add(view, key);
    }

    public void showView(String key)
    {
        cardLayout.show(mainPanel, key);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void clearViews()
    {
        mainPanel.removeAll();
    }
}