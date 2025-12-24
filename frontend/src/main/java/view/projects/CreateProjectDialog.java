package view.projects;

import model.dto.enums.StatoProgetto;
import model.dto.response.TeamResponseDTO;
import com.formdev.flatlaf.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CreateProjectDialog extends JDialog
{
    private final JTextField txtNome;
    private final JComboBox<StatoProgetto> cmbStato;
    private final JComboBox<TeamResponseDTO> cmbTeam;
    private final JXButton btnSave;
    private final JXButton btnCancel;

    public CreateProjectDialog(Frame owner, List<TeamResponseDTO> teams)
    {
        super(owner, "Nuovo Progetto", true);

        setLayout(new MigLayout("wrap 1, insets 25, fillx, width 400", "[grow, fill]", "[]10[]10[]20[]"));
        setResizable(false);

        JLabel lblTitle = new JLabel("Crea Nuovo Progetto");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +4; foreground: #263238");
        add(lblTitle, "gapbottom 10");

        add(new JLabel("Nome Progetto"));
        txtNome = new JTextField();
        txtNome.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,10,5,10");
        txtNome.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Es. Refactoring Backend");
        add(txtNome);

        add(new JLabel("Assegna al Team"));
        cmbTeam = new JComboBox<>(teams.toArray(new TeamResponseDTO[0]));
        cmbTeam.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        cmbTeam.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof TeamResponseDTO team)
                    setText(team.getNome());
                return this;
            }
        });
        add(cmbTeam);

        add(new JLabel("Stato Iniziale"));
        cmbStato = new JComboBox<>(StatoProgetto.values());
        cmbStato.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        add(cmbStato);

        JPanel btnPanel = new JPanel(new MigLayout("insets 0, fillx", "[grow][grow]", "[]"));
        btnCancel = new JXButton("Annulla");
        btnSave = new JXButton("Salva");

        btnSave.setBackground(new Color(25, 118, 210));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");

        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, "gaptop 10");

        pack();
        setLocationRelativeTo(owner);
    }

    public String getNome() { return txtNome.getText().trim(); }
    public TeamResponseDTO getSelectedTeam() { return (TeamResponseDTO) cmbTeam.getSelectedItem(); }
    public StatoProgetto getStato() { return (StatoProgetto) cmbStato.getSelectedItem(); }

    public void setSaveAction(ActionListener l) { btnSave.addActionListener(l); }
    public void setCancelAction(ActionListener l) { btnCancel.addActionListener(l); }
}