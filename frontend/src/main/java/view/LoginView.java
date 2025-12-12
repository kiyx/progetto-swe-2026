package view;

import com.formdev.flatlaf.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serial;

public class LoginView extends JXPanel
{
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final int ICON_SIZE = 18;
    private static final String FONT = "Segoe UI";
    private static final String EMAIL_DOMAIN = "@bugboard26.it";

    private final JXTextField emailField;
    private final JPasswordField passwordField;
    private final JXButton loginButton;
    private final JXLabel errorLabel;

    public LoginView()
    {
        setLayout(new MigLayout("fill, insets 0", "[center]", "[center]"));
        setBackground(BG_COLOR);

        JXPanel card = new JXPanel(new MigLayout("wrap 1, insets 40 50 40 50, fillx, width 380!", "[fill]", "[]10[]30[]15[]20[]"));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: #FFFFFF");
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        JXLabel titleLabel = new JXLabel("BugBoard - Login");
        titleLabel.setFont(new Font(FONT, Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JXLabel subtitleLabel = new JXLabel("Accedi al workspace");
        subtitleLabel.setFont(new Font(FONT, Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JXLabel domainLabel = new JXLabel(EMAIL_DOMAIN);
        domainLabel.setForeground(Color.GRAY);
        domainLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        emailField = new JXTextField();
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        emailField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, FontIcon.of(MaterialDesignA.ACCOUNT, ICON_SIZE, Color.GRAY));
        emailField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, domainLabel);
        emailField.putClientProperty(FlatClientProperties.STYLE, "margin: 0,10,0,0");
        emailField.setFont(new Font(FONT, Font.PLAIN, 15));

        passwordField = new JPasswordField();
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, FontIcon.of(MaterialDesignL.LOCK, ICON_SIZE, Color.GRAY));
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true; margin: 0,10,0,10");
        passwordField.setFont(new Font(FONT, Font.PLAIN, 15));

        loginButton = new JXButton("ACCEDI");
        loginButton.setFont(new Font(FONT, Font.BOLD, 14));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        errorLabel = new JXLabel(" ");
        errorLabel.setForeground(new Color(211, 47, 47));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setFont(new Font(FONT, Font.BOLD, 12));
        errorLabel.setVisible(false);

        card.add(titleLabel, "center");
        card.add(subtitleLabel, "center, gapbottom 10");
        card.add(errorLabel, "h 20!");
        card.add(emailField, "h 45!");
        card.add(passwordField, "h 45!");
        card.add(loginButton, "h 45!, gaptop 10");

        add(card);
    }

    public String getEmail()
    {
        String userPart = emailField.getText().trim();
        if(userPart.endsWith(EMAIL_DOMAIN))
            return userPart;

        return userPart + EMAIL_DOMAIN;
    }

    public String getPassword() { return new String(passwordField.getPassword()); }
    public void addLoginListener(ActionListener l) { loginButton.addActionListener(l); }

    public void showLoading(boolean loading)
    {
        loginButton.setEnabled(!loading);
        emailField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        loginButton.setText(loading ? "AUTENTICAZIONE..." : "ACCEDI");
    }

    public void setError(String msg)
    {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        emailField.putClientProperty(FlatClientProperties.OUTLINE, "error");
        passwordField.putClientProperty(FlatClientProperties.OUTLINE, "error");
    }

    public void clearError()
    {
        errorLabel.setVisible(false);
        errorLabel.setText(" ");
        emailField.putClientProperty(FlatClientProperties.OUTLINE, null);
        passwordField.putClientProperty(FlatClientProperties.OUTLINE, null);
    }
}