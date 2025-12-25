package view.user;

import com.formdev.flatlaf.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterUserDialog extends JDialog
{
    private final JTextField txtNome;
    private final JTextField txtCognome;
    private final JTextField txtEmail;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JCheckBox chkAdmin;
    private final JXButton btnSave;
    private final JXButton btnCancel;

    private static final String EMAIL_DOMAIN = "@bugboard26.it";

    public RegisterUserDialog(Frame owner)
    {
        super(owner, "Nuova Utenza", true);

        setLayout(new MigLayout("wrap 1, insets 25, fillx, width 450", "[grow, fill]", "[]10[]10[]10[]20[]"));
        setResizable(false);

        JPanel headerPanel = new JPanel(new MigLayout("insets 10, fillx", "[][grow]", "[]0[]"));
        headerPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #E3F2FD");

        JLabel lblIcon = new JLabel(FontIcon.of(MaterialDesignA.ACCOUNT_PLUS, 40, new Color(25, 118, 210)));
        JLabel lblTitle = new JLabel("Registra Nuovo Utente");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +2; foreground: #1565C0");
        JLabel lblSubtitle = new JLabel("Inserisci i dettagli per creare un nuovo account");
        lblSubtitle.setForeground(Color.GRAY);

        headerPanel.add(lblIcon, "spany 2, gapright 15");
        headerPanel.add(lblTitle, "wrap");
        headerPanel.add(lblSubtitle);
        add(headerPanel);

        add(new JSeparator(), "gapy 10");

        JLabel lblInfo = new JLabel("Dati Anagrafici");
        lblInfo.putClientProperty(FlatClientProperties.STYLE, "font: bold; foreground: #666666");
        add(lblInfo);

        JPanel namePanel = new JPanel(new MigLayout("insets 0, fillx, novisualpadding", "[grow][grow]", "[]"));

        txtNome = createTextField("Nome", MaterialDesignA.ACCOUNT);
        txtCognome = createTextField("Cognome", MaterialDesignA.ACCOUNT_OUTLINE);

        namePanel.add(txtNome, "growx");
        namePanel.add(txtCognome, "growx");
        add(namePanel);

        txtEmail = createEmailField();
        add(txtEmail);

        add(new JSeparator(), "gapy 10");
        JLabel lblSec = new JLabel("Sicurezza & Ruolo");
        lblSec.putClientProperty(FlatClientProperties.STYLE, "font: bold; foreground: #2576D2");
        add(lblSec);

        txtPassword = createPassField("Password");
        txtConfirmPassword = createPassField("Conferma Password");
        add(txtPassword);
        add(txtConfirmPassword);

        chkAdmin = new JCheckBox("Concedi privilegi di Amministratore");
        chkAdmin.putClientProperty(FlatClientProperties.STYLE, "font: bold; foreground: #424242");
        add(chkAdmin, "gapy 5");

        JPanel btnPanel = new JPanel(new MigLayout("insets 10 0 0 0, fillx", "[grow][grow]", "[]"));
        btnCancel = new JXButton("Annulla");
        btnSave = new JXButton("Crea Utente");

        btnSave.setBackground(new Color(25, 118, 210));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel);

        pack();
        setLocationRelativeTo(owner);
    }

    private JTextField createTextField(String placeholder, Ikon icon)
    {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, FontIcon.of(icon, 16, Color.GRAY));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,10,8,10");
        return field;
    }

    private JTextField createEmailField()
    {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, FontIcon.of(MaterialDesignE.EMAIL, 16, Color.GRAY));

        JLabel suffixLabel = new JLabel(EMAIL_DOMAIN);
        suffixLabel.setForeground(Color.GRAY);
        suffixLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        field.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, suffixLabel);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,10,8,110");
        return field;
    }

    private JPasswordField createPassField(String placeholder)
    {
        JPasswordField field = new JPasswordField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, FontIcon.of(MaterialDesignL.LOCK_OUTLINE, 16, Color.GRAY));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,10,8,10; showRevealButton: true");
        return field;
    }

    public String getNome() { return txtNome.getText().trim(); }
    public String getCognome() { return txtCognome.getText().trim(); }

    public String getEmail()
    {
        String input = txtEmail.getText().trim();
        if(input.endsWith(EMAIL_DOMAIN))
            return input;
        return input + EMAIL_DOMAIN;
    }

    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getConfirmPassword() { return new String(txtConfirmPassword.getPassword()); }
    public boolean isAdmin() { return chkAdmin.isSelected(); }

    public void setSaveAction(ActionListener l) { btnSave.addActionListener(l); }
    public void setCancelAction(ActionListener l) { btnCancel.addActionListener(l); }
}