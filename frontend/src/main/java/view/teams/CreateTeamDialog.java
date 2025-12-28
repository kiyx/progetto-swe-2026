package view.teams;

import com.formdev.flatlaf.*;
import net.miginfocom.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreateTeamDialog extends JDialog
{
    private final JTextField nameField;
    private final JButton confirmButton;

    public CreateTeamDialog(Frame owner)
    {
        super(owner, "Nuovo Team", true);

        nameField = new JTextField();
        confirmButton = new JButton("Crea Team");

        initComponents();

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents()
    {
        setLayout(new MigLayout("fill, insets 20", "[300!]", "[]10[]20[]"));

        JLabel nameLabel = new JLabel("Nome del Team");
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold");

        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Es. Frontend Devs...");
        nameField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0, align right"));

        confirmButton.setBackground(new Color(0, 100, 255));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; arc: 10");
        confirmButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        getRootPane().setDefaultButton(confirmButton);
        buttonPanel.add(confirmButton);

        add(nameLabel, "wrap");
        add(nameField, "growx, h 35!, wrap");
        add(buttonPanel, "growx");
    }

    public void setSaveAction(ActionListener l)
    {
        confirmButton.addActionListener(l);
    }

    public String getTeamName()
    {
        return nameField.getText().trim();
    }
}