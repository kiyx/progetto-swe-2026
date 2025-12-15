package view;

import com.formdev.flatlaf.*;
import model.dto.response.UtenteResponseDTO;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UpdatePasswordDialog extends JDialog
{
    private final JPasswordField txtOldPassword;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JXButton btnSave;
    private final JXButton btnCancel;

    public UpdatePasswordDialog(Frame owner, UtenteResponseDTO currentUser)
    {
        super(owner, "Modifica Password", true);

        setLayout(new MigLayout("wrap 1, insets 25, fillx, width 400", "[grow, fill]", "[]20[]10[]10[]20[]"));
        setResizable(false);

        JLabel lblIdentity = new JLabel("Account");
        lblIdentity.putClientProperty(FlatClientProperties.STYLE, "font: bold; foreground: #666666");
        add(lblIdentity);

        JPanel identityPanel = new JPanel(new MigLayout("insets 10, fillx", "[][grow]", "[]5[]"));
        identityPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #F5F7FA");
        JLabel lblAvatar = new JLabel(FontIcon.of(MaterialDesignA.ACCOUNT, 32, new Color(100, 100, 100)));
        JLabel lblName = new JLabel(currentUser.getNome() + " " + currentUser.getCognome());
        lblName.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");
        JLabel lblEmail = new JLabel(currentUser.getEmail());
        lblEmail.setForeground(Color.GRAY);
        identityPanel.add(lblAvatar, "spany 2, gapright 10");
        identityPanel.add(lblName, "wrap");
        identityPanel.add(lblEmail);

        add(identityPanel);
        add(new JSeparator(), "gapy 10");

        JLabel lblSecurity = new JLabel("Modifica Password");
        lblSecurity.putClientProperty(FlatClientProperties.STYLE, "font: bold; foreground: #2576D2");
        add(lblSecurity);

        txtOldPassword = createPassField("Vecchia Password");
        add(txtOldPassword);

        txtPassword = createPassField("Inserisci la nuova password");
        txtConfirmPassword = createPassField("Conferma la nuova password");

        add(txtPassword);
        add(txtConfirmPassword);

        JPanel btnPanel = new JPanel(new MigLayout("insets 0, fillx", "[grow][grow]", "[]"));
        btnCancel = new JXButton("Annulla");
        btnSave = new JXButton("Aggiorna Password");

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

    private JPasswordField createPassField(String placeholder)
    {
        JPasswordField field = new JPasswordField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, FontIcon.of(MaterialDesignL.LOCK_OUTLINE, 16, Color.GRAY));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,10,8,10; showRevealButton: true");
        return field;
    }

    public String getOldPassword() { return new String(txtOldPassword.getPassword()); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getConfirmPassword() { return new String(txtConfirmPassword.getPassword()); }

    public void setSaveAction(ActionListener l) { btnSave.addActionListener(l); }
    public void setCancelAction(ActionListener l) { btnCancel.addActionListener(l); }
}