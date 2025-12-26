package view.issues;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.Setter;
import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.response.IssueResponseDTO;
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

public class IssuesView extends JXPanel
{
    private final boolean isAdmin;
    private final DefaultTableModel modelMain;
    private final DefaultTableModel modelCreate;

    private final JXButton btnNuovaIssue;
    private final JXButton btnRefresh;

    @Setter private transient Consumer<Long> onArchiveIssue;
    @Setter private transient Consumer<Long> onEditIssue;
    @Setter private transient Consumer<Long> onResolveIssue;

    public IssuesView(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]20[grow]"));
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[grow][][]", "[]"));
        header.setBackground(Color.WHITE);

        String mainTitleText = isAdmin ? "Gestione Issue (Admin)" : "Le mie Issue";
        JLabel title = new JLabel(mainTitleText);
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");

        btnRefresh = createIconButton(MaterialDesignR.REFRESH, "Aggiorna");
        btnNuovaIssue = new JXButton("Nuova Issue");
        btnNuovaIssue.setIcon(FontIcon.of(MaterialDesignP.PLUS, 16, Color.WHITE));
        btnNuovaIssue.setBackground(new Color(25, 118, 210));
        btnNuovaIssue.setForeground(Color.WHITE);
        btnNuovaIssue.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,15,8,15");

        header.add(title);
        header.add(btnRefresh, "gapright 10");
        header.add(btnNuovaIssue);
        add(header, "wrap, growx");

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "font: bold; tabType: card");

        String[] cols = {"ID", "Titolo", "Tipo", "Stato", "Priorità", "Progetto", "Team", "Azioni"};

        String tab1Title = isAdmin ? "Issue Segnalate" : "Assegnate a Me";
        modelMain = new DefaultTableModel(cols, 0)
        {
            @Override public boolean isCellEditable(int row, int col) { return col == 7; }
        };
        tabbedPane.addTab(tab1Title, new JScrollPane(createTable(modelMain, true)));

        modelCreate = new DefaultTableModel(cols, 0)
        {
            @Override public boolean isCellEditable(int row, int col) { return col == 7; }
        };
        tabbedPane.addTab("Create da Me", new JScrollPane(createTable(modelCreate, false)));

        add(tabbedPane, "grow");
    }

    private JTable createTable(DefaultTableModel model, boolean isMainTab)
    {
        JTable t = new JTable(model);
        t.setRowHeight(45);
        t.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setShowVerticalLines(false);

        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        t.getColumnModel().getColumn(0).setPreferredWidth(0);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(2).setCellRenderer(center);
        t.getColumnModel().getColumn(3).setCellRenderer(center);
        t.getColumnModel().getColumn(4).setCellRenderer(center);

        t.getColumnModel().getColumn(7).setCellRenderer(new ActionsRenderer(isMainTab));
        t.getColumnModel().getColumn(7).setCellEditor(new ActionsEditor(isMainTab));
        t.getColumnModel().getColumn(7).setMinWidth(140);
        t.getColumnModel().getColumn(7).setMaxWidth(140);

        return t;
    }

    private JXButton createIconButton(org.kordamp.ikonli.Ikon icon, String tooltip)
    {
        JXButton btn = new JXButton();
        btn.setIcon(FontIcon.of(icon, 18, new Color(100, 100, 100)));
        btn.setToolTipText(tooltip);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void updateMainTab(List<IssueResponseDTO> issues) { updateModel(modelMain, issues); }
    public void updateCreateTab(List<IssueResponseDTO> issues) { updateModel(modelCreate, issues); }

    private void updateModel(DefaultTableModel model, List<IssueResponseDTO> issues)
    {
        model.setRowCount(0);
        for(IssueResponseDTO i : issues)
        {
            model.addRow(new Object[]{ i.getId(), i.getTitolo(), i.getTipo(), i.getStato(), i.getPriorita(), i.getNomeProgetto(), i.getNomeTeam(), "" });
        }
    }

    public void setNuovaIssueAction(ActionListener l) { btnNuovaIssue.addActionListener(l); }
    public void setRefreshAction(ActionListener l) { btnRefresh.addActionListener(l); }

    class ActionsPanel extends JPanel
    {
        public final JButton btnEdit;
        public final JButton btnResolve;
        public final JButton btnArchive;
        private final boolean isMainTab;

        public ActionsPanel(boolean isMainTab)
        {
            super(new FlowLayout(FlowLayout.CENTER, 5, 0));
            this.isMainTab = isMainTab;
            setOpaque(true);

            btnEdit = new JButton();
            styleBtn(btnEdit, "Modifica");
            add(btnEdit);

            btnResolve = new JButton();
            styleBtn(btnResolve, "Risolvi / PR");
            add(btnResolve);

            btnArchive = new JButton();
            styleBtn(btnArchive, "Archivia Bug");
            add(btnArchive);
        }

        private void styleBtn(JButton btn, String tip)
        {
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setToolTipText(tip);
        }

        public void updateView(boolean isSelected, StatoIssue stato, TipoIssue tipo)
        {
            Color cEdit    = isSelected ? Color.WHITE : new Color(25, 118, 210);
            Color cResolve = isSelected ? Color.WHITE : new Color(46, 125, 50);
            Color cArchive = isSelected ? Color.WHITE : new Color(211, 47, 47);

            btnEdit.setIcon(FontIcon.of(MaterialDesignP.PENCIL, 20, cEdit));
            btnResolve.setIcon(FontIcon.of(MaterialDesignC.CHECK_CIRCLE, 22, cResolve));
            btnArchive.setIcon(FontIcon.of(MaterialDesignA.ARCHIVE, 20, cArchive));

            btnEdit.setVisible(!isMainTab);

            boolean isBug = (tipo == TipoIssue.BUG);
            btnArchive.setVisible(isAdmin && isMainTab && isBug);

            boolean isResolved = (stato == StatoIssue.RISOLTA);
            btnResolve.setVisible(!isAdmin && isMainTab && !isResolved);
        }
    }

    class ActionsRenderer extends ActionsPanel implements TableCellRenderer
    {
        public ActionsRenderer(boolean main) { super(main); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int col)
        {
            setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
            Object t = table.getModel().getValueAt(table.convertRowIndexToModel(row), 2);
            Object s = table.getModel().getValueAt(table.convertRowIndexToModel(row), 3);
            if(t instanceof TipoIssue tipoIssue && s instanceof StatoIssue statoIssue)
                updateView(isSel, statoIssue, tipoIssue);
            return this;
        }
    }

    class ActionsEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final ActionsPanel panel;
        private Long currentId;

        public ActionsEditor(boolean main)
        {
            panel = new ActionsPanel(main);
            panel.btnEdit.addActionListener(e -> { fireEditingStopped(); if(onEditIssue!=null) onEditIssue.accept(currentId); });
            panel.btnResolve.addActionListener(e -> { fireEditingStopped(); if(onResolveIssue!=null) onResolveIssue.accept(currentId); });
            panel.btnArchive.addActionListener(e -> { fireEditingStopped(); if(onArchiveIssue!=null) onArchiveIssue.accept(currentId); });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSel, int row, int col)
        {
            currentId = (Long) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0);
            panel.setBackground(table.getSelectionBackground());
            Object t = table.getModel().getValueAt(table.convertRowIndexToModel(row), 2);
            Object s = table.getModel().getValueAt(table.convertRowIndexToModel(row), 3);
            if(t instanceof TipoIssue tipoIssue && s instanceof StatoIssue statoIssue)
                panel.updateView(true, statoIssue, tipoIssue);
            return panel;
        }

        @Override public Object getCellEditorValue() { return ""; }
        @Override public boolean isCellEditable(EventObject e) { return true; }
    }
}