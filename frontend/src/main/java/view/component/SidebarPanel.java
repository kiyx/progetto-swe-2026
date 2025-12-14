package view.component;

import com.formdev.flatlaf.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SidebarPanel extends JXPanel
{
    private final JXButton btnDashboard;
    private final JXButton btnIssues;
    private final JXButton btnProjects;
    private final JXButton btnTeams;

    public SidebarPanel()
    {
        setLayout(new MigLayout("wrap 1, insets 20 10 0 10, fillx", "[grow, fill]", "[]5[]5[]5[]"));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        setPreferredSize(new Dimension(240, 0));

        btnDashboard = createNavButton("Dashboard", MaterialDesignV.VIEW_DASHBOARD);
        btnIssues = createNavButton("Issues", MaterialDesignA.ALERT_CIRCLE_OUTLINE);
        btnTeams = createNavButton("Team", MaterialDesignA.ACCOUNT_GROUP_OUTLINE);
        btnProjects = createNavButton("Progetti", MaterialDesignF.FOLDER_OUTLINE);

        add(btnDashboard);
        add(btnIssues);
        add(btnTeams);
        add(btnProjects);
    }

    private JXButton createNavButton(String text, Ikon icon)
    {
        JXButton btn = new JXButton(text);
        btn.setIcon(FontIcon.of(icon, 20, new Color(90, 90, 90)));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(new Color(50, 50, 50));

        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10;" +
                "margin: 10,15,10,15;" +
                "borderWidth: 0;" +
                "background: null;" +
                "hoverBackground: #E0E0E0");

        return btn;
    }

    public void setDashboardAction(ActionListener l) { btnDashboard.addActionListener(l); }
    public void setIssuesAction(ActionListener l) { btnIssues.addActionListener(l); }
    public void setProjectsAction(ActionListener l) { btnProjects.addActionListener(l); }
    public void setTeamsAction(ActionListener l) { btnTeams.addActionListener(l); }
}