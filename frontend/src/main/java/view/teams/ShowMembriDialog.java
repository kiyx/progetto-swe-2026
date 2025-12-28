package view.teams;

import com.formdev.flatlaf.*;
import model.dto.response.UtenteResponseDTO;
import net.miginfocom.swing.*;
import utils.UtenteListRenderer;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ShowMembriDialog extends JDialog
{
    public ShowMembriDialog(Frame owner, List<UtenteResponseDTO> members)
    {
        super(owner, "Membri del Team", true);

        setLayout(new MigLayout("fill, insets 20", "[400!]", "[]10[grow]10[]"));
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("Elenco Membri Attuali");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");
        add(title, "wrap");

        DefaultListModel<UtenteResponseDTO> listModel = new DefaultListModel<>();
        if(members != null)
            members.forEach(listModel::addElement);

        JList<UtenteResponseDTO> userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new UtenteListRenderer());

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        add(scrollPane, "grow, h 250!, wrap");

        JButton btnClose = new JButton("Chiudi");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, "right");

        pack();
        setLocationRelativeTo(owner);
    }
}