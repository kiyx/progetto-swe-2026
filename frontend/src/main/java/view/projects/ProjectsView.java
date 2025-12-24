package view.projects;

import com.formdev.flatlaf.*;
import lombok.*;
import model.dto.enums.StatoProgetto;
import model.dto.response.ProgettoResponseDTO;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class ProjectsView extends JXPanel
{
    private final JXButton btnNuovoProgetto;
    private final JXButton btnRefresh;
    private final DefaultTableModel tableModel;

    @Setter private transient Consumer<Long> onCloseProject;
    @Setter private transient Consumer<Long> onActivateProject;

    public ProjectsView()
    {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[grow][][]", "[]"));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("I Miei Progetti");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");

        btnRefresh = createIconButton();
        btnNuovoProgetto = new JXButton("Nuovo Progetto");
        btnNuovoProgetto.setIcon(FontIcon.of(MaterialDesignP.PLUS, 16, Color.WHITE));
        btnNuovoProgetto.setBackground(new Color(25, 118, 210));
        btnNuovoProgetto.setForeground(Color.WHITE);
        btnNuovoProgetto.setFocusPainted(false);
        btnNuovoProgetto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNuovoProgetto.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,15,8,15");

        header.add(title);
        header.add(btnRefresh, "gapright 10");
        header.add(btnNuovoProgetto);
        add(header, "wrap, growx");

        String[] columns = {"ID", "Nome Progetto", "Team", "Stato", "Issues", "Azioni"};

        tableModel = new DefaultTableModel(columns, 0)
        {
            @Override public boolean isCellEditable(int row, int column) { return column == 5; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(45);
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionsEditor());
        table.getColumnModel().getColumn(5).setMinWidth(110);
        table.getColumnModel().getColumn(5).setMaxWidth(110);

        add(new JScrollPane(table), "grow");
    }

    private JXButton createIconButton()
    {
        JXButton btn = new JXButton();
        btn.setIcon(FontIcon.of(MaterialDesignR.REFRESH, 18, new Color(100, 100, 100)));
        btn.setToolTipText("Aggiorna");
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void updateTable(List<ProgettoResponseDTO> progetti)
    {
        tableModel.setRowCount(0);
        for(ProgettoResponseDTO p : progetti)
            tableModel.addRow(new Object[]{ p.getId(), p.getNome(), p.getNomeTeam(), p.getStato(), p.getIssuesTotali(), "" });
    }

    public void setNuovoProgettoAction(ActionListener l) { btnNuovoProgetto.addActionListener(l); }
    public void setRefreshAction(ActionListener l) { btnRefresh.addActionListener(l); }
    public void setCreateButtonVisible(boolean visible) { btnNuovoProgetto.setVisible(visible); }

    static class ActionsPanel extends JPanel
    {
        public final JButton btnActivate;
        public final JButton btnClose;
        private static final Color COLOR_ACTIVATE = new Color(30, 136, 229);
        private static final Color COLOR_CLOSE    = new Color(229, 57, 53);
        private static final Color COLOR_SELECTED = Color.WHITE;

        public ActionsPanel()
        {
            super(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);
            btnActivate = new JButton();
            styleBtn(btnActivate, "Attiva Progetto");
            btnClose = new JButton();
            styleBtn(btnClose, "Concludi Progetto");
            add(btnActivate);
            add(btnClose);
        }

        private void styleBtn(JButton btn, String tip)
        {
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setToolTipText(tip);
        }

        public void updateView(StatoProgetto stato, boolean isSelected)
        {
            Color iconColorActivate = isSelected ? COLOR_SELECTED : COLOR_ACTIVATE;
            Color iconColorClose    = isSelected ? COLOR_SELECTED : COLOR_CLOSE;
            btnActivate.setIcon(FontIcon.of(MaterialDesignR.ROCKET, 22, iconColorActivate));
            btnClose.setIcon(FontIcon.of(MaterialDesignS.STOP_CIRCLE, 22, iconColorClose));

            switch (stato)
            {
                case FUTURO ->
                {
                    btnActivate.setVisible(true);
                    btnClose.setVisible(true);
                }
                case ATTIVO ->
                {
                    btnActivate.setVisible(false);
                    btnClose.setVisible(true);
                }
                case null, default ->
                {
                    btnActivate.setVisible(false);
                    btnClose.setVisible(false);
                }
            }
        }
    }

    static class ActionsRenderer extends ActionsPanel implements TableCellRenderer
    {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if(isSelected)
                setBackground(table.getSelectionBackground());
            else
                setBackground(Color.WHITE);

            Object statoObj = table.getModel().getValueAt(row, 3);
            if(statoObj instanceof StatoProgetto statoProgetto)
                updateView(statoProgetto, isSelected);

            return this;
        }
    }

    class ActionsEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final ActionsPanel panel;
        private Long currentId;

        public ActionsEditor()
        {
            panel = new ActionsPanel();
            panel.btnActivate.addActionListener(e ->
            {
                fireEditingStopped();
                if(onActivateProject != null && currentId != null)
                    onActivateProject.accept(currentId);
            });
            panel.btnClose.addActionListener(e ->
            {
                fireEditingStopped();
                if(onCloseProject != null && currentId != null)
                    onCloseProject.accept(currentId);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            currentId = (Long) table.getValueAt(row, 0);
            Object statoObj = table.getValueAt(row, 3);
            panel.setBackground(table.getSelectionBackground());

            if(statoObj instanceof StatoProgetto statoProgetto)
                panel.updateView(statoProgetto, true);

            return panel;
        }

        @Override public Object getCellEditorValue() { return ""; }
        @Override public boolean isCellEditable(EventObject e) { return true; }
    }
}