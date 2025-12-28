package view.teams;

import com.formdev.flatlaf.*;
import model.dto.response.UtenteResponseDTO;
import net.miginfocom.swing.*;
import utils.UtenteListRenderer;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AddMembroDialog extends JDialog
{
    private final JList<UtenteResponseDTO> userList;
    private boolean confirmed = false;

    public AddMembroDialog(Frame owner, List<UtenteResponseDTO> availableUsers)
    {
        super(owner, "Aggiungi Membri al Team", true);

        setLayout(new MigLayout("fill, insets 20", "[400!]", "[]5[]10[grow]20[]"));
        setResizable(false);
        setModal(true);

        JLabel lblTitle = new JLabel("Seleziona Utenti");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        JLabel lblSubtitle = new JLabel("Tieni premuto CTRL per selezioni multiple");
        lblSubtitle.putClientProperty(FlatClientProperties.STYLE, "font: small; foreground: $Label.disabledForeground");

        DefaultListModel<UtenteResponseDTO> listModel = new DefaultListModel<>();
        if(availableUsers != null)
            availableUsers.forEach(listModel::addElement);

        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userList.setCellRenderer(new UtenteListRenderer());

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        JButton btnConfirm = new JButton("Aggiungi Selezionati");
        btnConfirm.setBackground(new Color(40, 167, 69));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.putClientProperty(FlatClientProperties.STYLE, "font: bold; arc: 10");
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnConfirm.addActionListener(e ->
        {
            confirmed = true;
            dispose();
        });

        add(lblTitle, "wrap");
        add(lblSubtitle, "wrap");
        add(scrollPane, "grow, h 250!, wrap");
        add(btnConfirm, "growx");

        pack();
        setLocationRelativeTo(owner);
    }

    public List<UtenteResponseDTO> getSelectedUsers()
    {
        if(!confirmed || userList.getSelectedValuesList().isEmpty())
            return Collections.emptyList();
        return userList.getSelectedValuesList();
    }
}