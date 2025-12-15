package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterUserDialog extends JDialog
{
    private final JTextField emailField;
    private final JTextField nomeField;
    private final JTextField cognomeField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final JCheckBox isAdminCheck;
    private final JButton saveButton;
    private final JButton cancelButton;

    public RegisterUserDialog(Frame owner)
    {
        super(owner, "Registra Nuovo Utente", true);

        emailField = new JTextField(20);
        nomeField = new JTextField(20);
        cognomeField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        isAdminCheck = new JCheckBox("Concedi privilegi Amministratore");
        saveButton = new JButton("Crea Utente");
        cancelButton = new JButton("Annulla");

        initLayout();

        styleButton(saveButton, new Color(63, 81, 181), Color.WHITE);
        styleButton(cancelButton, new Color(224, 224, 224), Color.BLACK);

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initLayout() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Helper per aggiungere righe
        int row = 0;
        addFormRow(contentPanel, gbc, "Email:", emailField, row++);
        addFormRow(contentPanel, gbc, "Nome:", nomeField, row++);
        addFormRow(contentPanel, gbc, "Cognome:", cognomeField, row++);
        addFormRow(contentPanel, gbc, "Password:", passwordField, row++);
        addFormRow(contentPanel, gbc, "Conferma Password:", confirmPasswordField, row++);

        gbc.gridx = 1;
        gbc.gridy = row++;
        contentPanel.add(isAdminCheck, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row)
    {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void styleButton(JButton btn, Color bg, Color fg)
    {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setOpaque(true);
    }

    public String getEmail() { return emailField.getText().trim(); }
    public String getNome() { return nomeField.getText().trim(); }
    public String getCognome() { return cognomeField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getConfirmPassword() { return new String(confirmPasswordField.getPassword()); }
    public boolean getIsAdmin() { return isAdminCheck.isSelected(); }

    public void setSaveAction(ActionListener action) { saveButton.addActionListener(action); }
    public void setCancelAction(ActionListener action) { cancelButton.addActionListener(action); }
}