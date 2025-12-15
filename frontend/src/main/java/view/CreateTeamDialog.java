package view;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CreateTeamDialog extends JDialog {

    private final JTextField nameField;
    private boolean confirmed = false;

    public CreateTeamDialog(MainFrame owner) {
        super(owner, "Nuovo Team", true);

        nameField = new JTextField();
        initComponents();

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[300!]", "[]10[]20[]"));

        JLabel nameLabel = new JLabel("Nome del Team");
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold");

        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Es. Frontend Devs...");
        nameField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        nameField.addActionListener(this::handleConfirm);

        add(nameLabel, "wrap");
        add(nameField, "growx, h 35!, wrap");

        // --- Bottoni ---
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0, align right"));

        JButton cancelButton = new JButton("Annulla");
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = new JButton("Crea Team");
        confirmButton.setBackground(new Color(0, 100, 255));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; arc: 10");
        confirmButton.addActionListener(this::handleConfirm);

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        add(buttonPanel, "growx");
    }

    private void handleConfirm(ActionEvent e) {
        if (nameField.getText().trim().isEmpty()) {
            nameField.putClientProperty(FlatClientProperties.OUTLINE, "error");
            nameField.requestFocus();
            return;
        }

        this.confirmed = true;
        dispose();
    }

    public String getTeamName() {
        return confirmed ? nameField.getText().trim() : null;
    }
}