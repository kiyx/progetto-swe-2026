package view.issues;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import lombok.Setter;
import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import model.dto.response.IssueResponseDTO;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;
import java.util.function.Consumer;

public class IssuesView extends JXPanel
{
    public enum TabType { MAIN, CREATED, ARCHIVE }

    private final boolean isAdmin;
    private final DefaultTableModel modelMain;
    private final DefaultTableModel modelCreate;
    private final DefaultTableModel modelArchive;

    @Getter private JTextField searchField;
    @Getter private JComboBox<TipoIssue> filterTipo;
    @Getter private JComboBox<StatoIssue> filterStato;
    @Getter private JComboBox<TipoPriorita> filterPriorita;

    private JXButton btnReset;
    private final JXButton btnNuovaIssue;
    private final JXButton btnRefresh;

    @Setter private transient Consumer<Long> onArchiveIssue;
    @Setter private transient Consumer<Long> onEditIssue;
    @Setter private transient Consumer<Long> onResolveIssue;
    @Setter private transient Consumer<Long> onAssignIssue;

    public IssuesView(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[]10[grow]"));
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[grow][][]", "[]"));
        header.setBackground(Color.WHITE);

        String mainTitleText = isAdmin ? "Gestione Issue (Admin)" : "Le mie Issue";
        JLabel title = new JLabel(mainTitleText);
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");

        btnRefresh = createIconButton();
        btnNuovaIssue = new JXButton("Nuova Issue");
        btnNuovaIssue.setIcon(FontIcon.of(MaterialDesignP.PLUS, 16, Color.WHITE));
        btnNuovaIssue.setBackground(new Color(25, 118, 210));
        btnNuovaIssue.setForeground(Color.WHITE);
        btnNuovaIssue.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,15,8,15");

        header.add(title);
        header.add(btnRefresh, "gapright 10");
        header.add(btnNuovaIssue);
        add(header, "wrap, growx");

        add(createFilterPanel(), "wrap, growx");

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "font: bold; tabType: card");

        String[] cols = {"ID", "Titolo", "Tipo", "Stato", "Priorità", "Progetto", "Team", "Azioni"};

        String tab1Title = isAdmin ? "Issue Segnalate" : "Assegnate a Me";
        modelMain = createModel(cols);
        tabbedPane.addTab(tab1Title, new JScrollPane(createTable(modelMain, TabType.MAIN)));

        modelCreate = createModel(cols);
        tabbedPane.addTab("Create da Me", new JScrollPane(createTable(modelCreate, TabType.CREATED)));

        modelArchive = createModel(cols);
        if(isAdmin)
            tabbedPane.addTab("Archivio", new JScrollPane(createTable(modelArchive, TabType.ARCHIVE)));

        add(tabbedPane, "grow");
    }

    private DefaultTableModel createModel(String[] cols)
    {
        return new DefaultTableModel(cols, 0)
        {
            @Override public boolean isCellEditable(int row, int col) { return col == 7; }
            @Override public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Long.class : Object.class; }
        };
    }

    private JTable createTable(DefaultTableModel model, TabType tabType)
    {
        JTable t = new JTable(model);
        t.setRowHeight(45);
        t.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setShowVerticalLines(false);
        t.setAutoCreateRowSorter(true);

        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        t.getColumnModel().getColumn(0).setPreferredWidth(0);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(2).setCellRenderer(center);
        t.getColumnModel().getColumn(3).setCellRenderer(center);
        t.getColumnModel().getColumn(4).setCellRenderer(center);

        t.getColumnModel().getColumn(7).setCellRenderer(new ActionsRenderer(tabType));
        t.getColumnModel().getColumn(7).setCellEditor(new ActionsEditor(tabType));
        t.getColumnModel().getColumn(7).setMinWidth(160);
        t.getColumnModel().getColumn(7).setMaxWidth(160);

        return t;
    }

    private JPanel createFilterPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 10, fillx, hidemode 3", "[]10[]10[]10[]10[grow]10[]", "[]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #F5F5F5");

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cerca per titolo...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        searchField.setColumns(20);

        filterTipo = new JComboBox<>(TipoIssue.values());
        setupCombo(filterTipo, "Tipo");

        filterStato = new JComboBox<>(StatoIssue.values());
        setupCombo(filterStato, "Stato");

        filterPriorita = new JComboBox<>(TipoPriorita.values());
        setupCombo(filterPriorita, "Priorità");

        btnReset = new JXButton("Reset");
        btnReset.setIcon(FontIcon.of(MaterialDesignC.CLOSE, 16, Color.BLACK));

        panel.add(new JLabel("Filtri:"));
        panel.add(searchField);
        panel.add(filterTipo);
        panel.add(filterStato);
        panel.add(filterPriorita);
        panel.add(new JLabel(""), "growx");
        panel.add(btnReset);

        return panel;
    }

    private <T> void setupCombo(JComboBox<T> combo, String placeholder)
    {
        combo.insertItemAt(null, 0);
        combo.setSelectedIndex(0);
        combo.setRenderer(new DefaultListCellRenderer()
        {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value == null)
                    setText(placeholder + ": Tutti");
                return this;
            }
        });
    }

    public void setupFiltersListener(ActionListener l)
    {
        searchField.addActionListener(l);
        filterTipo.addActionListener(l);
        filterStato.addActionListener(l);
        filterPriorita.addActionListener(l);
    }

    private JXButton createIconButton()
    {
        JXButton btn = new JXButton();
        btn.setIcon(FontIcon.of(MaterialDesignR.REFRESH, 18, new Color(100, 100, 100)));
        btn.setToolTipText("Aggiorna");
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void updateMainTab(List<IssueResponseDTO> issues) { updateModel(modelMain, issues); }
    public void updateCreateTab(List<IssueResponseDTO> issues) { updateModel(modelCreate, issues); }
    public void updateArchiveTab(List<IssueResponseDTO> issues) { updateModel(modelArchive, issues); }

    private void updateModel(DefaultTableModel model, List<IssueResponseDTO> issues)
    {
        model.setRowCount(0);
        for(IssueResponseDTO i : issues)
            model.addRow(new Object[]{ i.getId(), i.getTitolo(), i.getTipo(), i.getStato(), i.getPriorita(), i.getNomeProgetto(), i.getNomeTeam(), "" });
    }

    public void setNuovaIssueAction(ActionListener l) { btnNuovaIssue.addActionListener(l); }
    public void setRefreshAction(ActionListener l) { btnRefresh.addActionListener(l); }
    public void setResetFilterAction(ActionListener l) { btnReset.addActionListener(l); }

    class ActionsPanel extends JPanel
    {
        public final JButton btnEdit;
        public final JButton btnResolve;
        public final JButton btnArchive;
        public final JButton btnAssign;
        private final TabType tabType;

        public ActionsPanel(TabType tabType)
        {
            super(new FlowLayout(FlowLayout.CENTER, 5, 0));
            this.tabType = tabType;
            setOpaque(true);

            btnEdit = new JButton(); styleBtn(btnEdit, "Modifica"); add(btnEdit);
            btnResolve = new JButton(); styleBtn(btnResolve, "Risolvi / PR"); add(btnResolve);
            btnAssign = new JButton(); styleBtn(btnAssign, "Assegna a Membro"); add(btnAssign);
            btnArchive = new JButton(); styleBtn(btnArchive, "Archivia Bug"); add(btnArchive);
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
            Color cAssign  = isSelected ? Color.WHITE : new Color(255, 152, 0);

            btnEdit.setIcon(FontIcon.of(MaterialDesignP.PENCIL, 20, cEdit));
            btnResolve.setIcon(FontIcon.of(MaterialDesignC.CHECK_CIRCLE, 22, cResolve));
            btnArchive.setIcon(FontIcon.of(MaterialDesignA.ARCHIVE, 20, cArchive));
            btnAssign.setIcon(FontIcon.of(MaterialDesignA.ACCOUNT_PLUS, 22, cAssign));

            boolean isBug = (tipo == TipoIssue.BUG);
            boolean isResolved = (stato == StatoIssue.RISOLTA);

            btnEdit.setVisible(tabType == TabType.CREATED && !isResolved);
            btnResolve.setVisible(!isResolved && (tabType == TabType.CREATED || tabType == TabType.MAIN));
            btnAssign.setVisible(!isResolved && isAdmin && tabType == TabType.MAIN);
            btnArchive.setVisible(isAdmin && tabType == TabType.MAIN && isBug);
        }
    }

    class ActionsRenderer extends ActionsPanel implements TableCellRenderer
    {
        public ActionsRenderer(TabType type) { super(type); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int col)
        {
            setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
            int modelRow = table.convertRowIndexToModel(row);
            Object t = table.getModel().getValueAt(modelRow, 2);
            Object s = table.getModel().getValueAt(modelRow, 3);
            if(t instanceof TipoIssue tipo && s instanceof StatoIssue stato) updateView(isSel, stato, tipo);
            return this;
        }
    }

    class ActionsEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final ActionsPanel panel;
        private Long currentId;

        public ActionsEditor(TabType type)
        {
            panel = new ActionsPanel(type);
            panel.btnEdit.addActionListener(e -> { fireEditingStopped(); if(onEditIssue!=null) onEditIssue.accept(currentId); });
            panel.btnResolve.addActionListener(e -> { fireEditingStopped(); if(onResolveIssue!=null) onResolveIssue.accept(currentId); });
            panel.btnArchive.addActionListener(e -> { fireEditingStopped(); if(onArchiveIssue!=null) onArchiveIssue.accept(currentId); });
            panel.btnAssign.addActionListener(e -> { fireEditingStopped(); if(onAssignIssue!=null) onAssignIssue.accept(currentId); });
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSel, int row, int col)
        {
            int modelRow = table.convertRowIndexToModel(row);
            currentId = (Long) table.getModel().getValueAt(modelRow, 0);
            panel.setBackground(table.getSelectionBackground());
            Object t = table.getModel().getValueAt(modelRow, 2);
            Object s = table.getModel().getValueAt(modelRow, 3);
            if(t instanceof TipoIssue tipo && s instanceof StatoIssue stato)
                panel.updateView(true, stato, tipo);
            return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
        @Override public boolean isCellEditable(EventObject e) { return true; }
    }
}