package view.teams;

import com.formdev.flatlaf.*;
import model.dto.response.TeamResponseDTO;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.decorator.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;

public class TeamsView extends JXPanel
{
    private final DefaultTableModel tableModel;
    private final JXTable table;
    private final JButton btnNuovoTeam;
    private final JButton btnRefresh;

    public static final int COL_ID = 0;
    public static final int COL_ACTIONS = 2;

    public TeamsView()
    {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new MigLayout("insets 0, fillx", "[grow][][]", "[]"));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestione Team");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");

        btnRefresh = new JButton();
        btnRefresh.setIcon(FontIcon.of(MaterialDesignR.REFRESH, 18, new Color(100, 100, 100)));
        btnRefresh.setToolTipText("Aggiorna");
        btnRefresh.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnNuovoTeam = new JButton("Nuovo Team");
        btnNuovoTeam.setIcon(FontIcon.of(MaterialDesignP.PLUS, 16, Color.WHITE));
        btnNuovoTeam.setBackground(new Color(25, 118, 210));
        btnNuovoTeam.setForeground(Color.WHITE);
        btnNuovoTeam.setFocusPainted(false);
        btnNuovoTeam.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,15,8,15");
        btnNuovoTeam.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        headerPanel.add(titleLabel);
        headerPanel.add(btnRefresh, "gapright 10");
        headerPanel.add(btnNuovoTeam);

        add(headerPanel, "wrap, growx");

        String[] columns = {"ID", "Nome Team", "Azioni"};
        tableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return column == COL_ACTIONS;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                return columnIndex == COL_ACTIONS ? TeamResponseDTO.class : String.class;
            }
        };

        table = new JXTable(tableModel);
        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");
        table.setFocusable(false);

        table.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        table.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        table.getColumnModel().getColumn(COL_ID).setPreferredWidth(0);

        table.getColumnModel().getColumn(COL_ACTIONS).setMinWidth(180);
        table.getColumnModel().getColumn(COL_ACTIONS).setMaxWidth(220);

        table.getColumnModel().getColumn(COL_ACTIONS).setCellRenderer(new ActionsRenderer(new ActionsPanel(null, null, null)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, "grow");
    }

    public DefaultTableModel getModel()
    {
        return tableModel;
    }

    public void addRefreshListener(ActionListener l)
    {
        btnRefresh.addActionListener(l);
    }

    public void addCreateListener(ActionListener l)
    {
        btnNuovoTeam.addActionListener(l);
    }

    public void setTableActions(Consumer<TeamResponseDTO> onAdd, Consumer<TeamResponseDTO> onRemove, Consumer<TeamResponseDTO> onList)
    {
        ActionsPanel editorPanel = new ActionsPanel(onAdd, onRemove, onList);
        table.getColumnModel().getColumn(COL_ACTIONS).setCellEditor(new ActionsEditor(editorPanel));

        ActionsPanel rendererPanel = new ActionsPanel(null, null, null);
        table.getColumnModel().getColumn(COL_ACTIONS).setCellRenderer(new ActionsRenderer(rendererPanel));
    }

    private class ActionsPanel extends JPanel
    {
        private transient TeamResponseDTO currentTeam;

        public ActionsPanel(Consumer<TeamResponseDTO> onAdd, Consumer<TeamResponseDTO> onRemove, Consumer<TeamResponseDTO> onList)
        {
            super(new MigLayout("insets 0, align center", "[]10[]10[]", "[]"));
            setOpaque(true);
            setBackground(Color.WHITE);

            JButton btnAdd = createBtn(MaterialDesignA.ACCOUNT_PLUS, new Color(40, 167, 69), "Aggiungi");
            JButton btnRemove = createBtn(MaterialDesignA.ACCOUNT_REMOVE, new Color(220, 53, 69), "Rimuovi");
            JButton btnList = createBtn(MaterialDesignF.FORMAT_LIST_BULLETED, new Color(108, 117, 125), "Lista");

            add(btnAdd);
            add(btnRemove);
            add(btnList);

            if(onAdd != null)
            {
                btnAdd.addActionListener(e -> fireAction(onAdd));
                btnRemove.addActionListener(e -> fireAction(onRemove));
                btnList.addActionListener(e -> fireAction(onList));
            }
        }

        private void fireAction(Consumer<TeamResponseDTO> action)
        {
            if(table.getCellEditor() != null)
                table.getCellEditor().stopCellEditing();

            if(currentTeam != null)
                action.accept(currentTeam);
        }

        public void updateData(TeamResponseDTO team, boolean isSelected, JTable table)
        {
            this.currentTeam = team;
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        }

        private JButton createBtn(org.kordamp.ikonli.Ikon icon, Color color, String tooltip)
        {
            JButton btn = new JButton(FontIcon.of(icon, 20, color));
            btn.setToolTipText(tooltip);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(32, 32));
            return btn;
        }
    }

    private class ActionsRenderer implements TableCellRenderer
    {
        private final ActionsPanel panel;

        public ActionsRenderer(ActionsPanel panel)
        {
            this.panel = panel;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if(value instanceof TeamResponseDTO dto)
                panel.updateData(dto, isSelected, table);
            return panel;
        }
    }

    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final ActionsPanel panel;

        public ActionsEditor(ActionsPanel panel)
        {
            this.panel = panel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            if(value instanceof TeamResponseDTO dto)
                panel.updateData(dto, true, table);
            return panel;
        }

        @Override
        public Object getCellEditorValue()
        {
            return panel.currentTeam;
        }

        @Override
        public boolean isCellEditable(EventObject e)
        {
            return true;
        }
    }
}