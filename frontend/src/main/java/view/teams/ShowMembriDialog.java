package view.teams;

import com.formdev.flatlaf.FlatClientProperties;
import model.dto.response.UtenteResponseDTO;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ShowMembriDialog extends JDialog
{
    private final JList<UtenteResponseDTO> userList;

    public ShowMembriDialog(Frame owner, java.util.List<UtenteResponseDTO> members)
    {
        super(owner, "Membri del team", true);

        setLayout(new MigLayout("fill, insets 20", "[400!]", "[]10[grow]20[]"));
        setResizable(false);
        setModal(true);

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

        add(scrollPane, "grow, h 200!, wrap");

        pack();
        setLocationRelativeTo(owner);
    }
}
