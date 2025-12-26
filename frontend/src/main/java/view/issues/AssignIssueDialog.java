package view.issues;

import com.formdev.flatlaf.*;
import model.dto.response.UtenteResponseDTO;
import net.miginfocom.swing.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AssignIssueDialog extends JDialog
{
    private final JList<UtenteResponseDTO> userList;
    private boolean confirmed = false;

    public AssignIssueDialog(Frame owner, List<UtenteResponseDTO> members)
    {
        super(owner, "Assegna Issue a Membri", true);

        setLayout(new MigLayout("fill, insets 20", "[400!]", "[]10[grow]20[]"));
        setResizable(false);
        setModal(true);

        JLabel lblTitle = new JLabel("Seleziona i Membri del Team");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");
        JLabel lblSubtitle = new JLabel("Tieni premuto CTRL per selezioni multiple");
        lblSubtitle.putClientProperty(FlatClientProperties.STYLE, "font: small; foreground: $Label.disabledForeground");

        userList = new JList<>();
        DefaultListModel<UtenteResponseDTO> listModel = new DefaultListModel<>();
        if(members != null)
            members.forEach(listModel::addElement);

        userList.setModel(listModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        userList.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof UtenteResponseDTO u)
                {
                    setText(u.getNome() + " " + u.getCognome() + " (" + u.getEmail() + ")");
                    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        JButton btnConfirm = new JButton("Conferma Assegnazione");
        btnConfirm.setBackground(new Color(0, 100, 255));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.putClientProperty(FlatClientProperties.STYLE, "font: bold; arc: 10");

        btnConfirm.addActionListener(e ->
        {
            confirmed = true;
            dispose();
        });

        add(lblTitle, "wrap");
        add(lblSubtitle, "wrap");
        add(scrollPane, "grow, h 200!, wrap");
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