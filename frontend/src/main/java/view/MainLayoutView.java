package view;

import org.jdesktop.swingx.*;
import view.component.HeaderPanel;
import view.component.SidebarPanel;
import java.awt.*;

public class MainLayoutView extends JXPanel
{
    private final SidebarPanel sidebar;
    private final JXPanel contentPanel;
    private final CardLayout contentCardLayout;

    public MainLayoutView(HeaderPanel header, SidebarPanel sidebar)
    {
        this.sidebar = sidebar;
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);

        contentCardLayout = new CardLayout();
        contentPanel = new JXPanel(contentCardLayout);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        toggleSidebar();
    }

    public void toggleSidebar()
    {
        boolean isVisible = sidebar.isVisible();
        sidebar.setVisible(!isVisible);
        revalidate();
        repaint();
    }

    public void addContentView(JXPanel view, String key)
    {
        contentPanel.add(view, key);
    }

    public void showContentView(String key)
    {
        contentCardLayout.show(contentPanel, key);
    }
}