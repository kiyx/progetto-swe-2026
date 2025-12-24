package view.projects;

import com.formdev.flatlaf.*;
import model.dto.response.ProgettoResponseDTO;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProjectsView extends JXPanel
{
    private final JXButton btnNuovoProgetto;
    private final JXButton btnRefresh;
    private final DefaultTableModel tableModel;

    public ProjectsView()
    {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[grow][][]", "[]"));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("I Miei Progetti");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");

        btnRefresh = new JXButton();
        btnRefresh.setIcon(FontIcon.of(MaterialDesignR.REFRESH, 18, new Color(100, 100, 100)));
        btnRefresh.setToolTipText("Aggiorna Lista");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 100; margin: 6,6,6,6");

        btnNuovoProgetto = new JXButton("Nuovo Progetto");
        btnNuovoProgetto.setIcon(FontIcon.of(MaterialDesignP.PLUS, 16, Color.WHITE));
        btnNuovoProgetto.setBackground(new Color(25, 118, 210));
        btnNuovoProgetto.setForeground(Color.WHITE);
        btnNuovoProgetto.setFocusPainted(false);
        btnNuovoProgetto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNuovoProgetto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuovoProgetto.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,15,8,15");

        header.add(title);
        header.add(btnRefresh, "gapright 10");
        header.add(btnNuovoProgetto);
        add(header, "wrap, growx");

        String[] columns = {"ID", "Nome Progetto", "Team", "Stato", "Issues Totali"};

        tableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(45);
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));
        add(scrollPane, "grow");
    }

    public void updateTable(List<ProgettoResponseDTO> progetti)
    {
        tableModel.setRowCount(0);
        for (ProgettoResponseDTO p : progetti)
        {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getNomeTeam(),
                    p.getStato(),
                    p.getIssuesTotali()
            });
        }
    }

    public void setCreateButtonVisible(boolean visible)
    {
        btnNuovoProgetto.setVisible(visible);
    }

    public void setNuovoProgettoAction(ActionListener l)
    {
        btnNuovoProgetto.addActionListener(l);
    }

    public void setRefreshAction(ActionListener l)
    {
        btnRefresh.addActionListener(l);
    }
}