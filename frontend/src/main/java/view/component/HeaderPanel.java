package view.component;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HeaderPanel extends JXPanel
{
    private final JXButton btnToggleSidebar;
    private final JXButton btnProfile;
    private final JPopupMenu profileMenu;
    private final JMenuItem itemLogout;
    private final JMenuItem itemProfile;

    public HeaderPanel(String username, boolean isAdmin)
    {
        setLayout(new MigLayout("fill, insets 10 20 10 20", "[][][grow][]", "center"));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        btnToggleSidebar = new JXButton();
        btnToggleSidebar.setIcon(FontIcon.of(MaterialDesignM.MENU, 20, Color.DARK_GRAY));
        btnToggleSidebar.putClientProperty(FlatClientProperties.STYLE, "buttonType: toolBarButton; arc: 10");
        btnToggleSidebar.setFocusPainted(false);
        btnToggleSidebar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnToggleSidebar);

        JXLabel lblTitle = new JXLabel("Bugboard26");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(25, 118, 210));
        add(lblTitle, "gapleft 10");

        profileMenu = new JPopupMenu();
        itemProfile = new JMenuItem("Il mio Profilo", FontIcon.of(MaterialDesignA.ACCOUNT_CIRCLE, 16, Color.GRAY));
        itemLogout = new JMenuItem("Logout", FontIcon.of(MaterialDesignL.LOGOUT, 16, Color.GRAY));

        profileMenu.add(itemProfile);
        if(isAdmin)
        {
            JMenuItem itemAdmin = new JMenuItem("Gestione Utenze", FontIcon.of(MaterialDesignA.ACCOUNT_MULTIPLE_PLUS, 16, Color.GRAY));
            profileMenu.add(itemAdmin);
        }
        profileMenu.addSeparator();
        profileMenu.add(itemLogout);

        btnProfile = new JXButton(username);
        btnProfile.setIcon(FontIcon.of(MaterialDesignA.ACCOUNT_CIRCLE, 24, new Color(25, 118, 210)));
        btnProfile.setHorizontalTextPosition(SwingConstants.LEFT);
        btnProfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProfile.setFocusPainted(false);
        btnProfile.putClientProperty(FlatClientProperties.STYLE, "arc: 999; margin: 6,14,6,14; background: #F5F7FA");

        btnProfile.addActionListener(e -> profileMenu.show(btnProfile, 0, btnProfile.getHeight() + 5));

        add(btnProfile, "cell 3 0");
    }

    public void setToggleAction(ActionListener action)
    {
        btnToggleSidebar.addActionListener(action);
    }

    public void setLogoutAction(ActionListener action)
    {
        itemLogout.addActionListener(action);
    }

    public void setProfileEditAction(ActionListener action)
    {
        itemProfile.addActionListener(action);
    }
}