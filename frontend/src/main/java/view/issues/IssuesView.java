package view.issues;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import lombok.*;
import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import model.dto.response.IssueResponseDTO;
import model.dto.response.UtenteResponseDTO;
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
import java.util.function.*;
import java.util.stream.*;

public class IssuesView extends JXPanel
{
    private final boolean isAdmin;
    private final DefaultTableModel modelMain;
    private final DefaultTableModel modelCreate;

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
        modelMain = new DefaultTableModel(cols, 0)
        {
            @Override public boolean isCellEditable(int row, int col) { return col == 7; }
            @Override public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Long.class : Object.class; }
        };
        tabbedPane.addTab(tab1Title, new JScrollPane(createTable(modelMain, true)));

        modelCreate = new DefaultTableModel(cols, 0)
        {
            @Override public boolean isCellEditable(int row, int col) { return col == 7; }
            @Override public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Long.class : Object.class; }
        };
        tabbedPane.addTab("Create da Me", new JScrollPane(createTable(modelCreate, false)));

        add(tabbedPane, "grow");
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

    private JTable createTable(DefaultTableModel model, boolean isMainTab)
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

        t.getColumnModel().getColumn(7).setCellRenderer(new ActionsRenderer(isMainTab));
        t.getColumnModel().getColumn(7).setCellEditor(new ActionsEditor(isMainTab));
        t.getColumnModel().getColumn(7).setMinWidth(160);
        t.getColumnModel().getColumn(7).setMaxWidth(160);

        return t;
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
        private final boolean isMainTab;

        public ActionsPanel(boolean isMainTab)
        {
            super(new FlowLayout(FlowLayout.CENTER, 5, 0));
            this.isMainTab = isMainTab;
            setOpaque(true);

            btnEdit = new JButton(); styleBtn(btnEdit, "Modifica"); add(btnEdit);
            btnResolve = new JButton(); styleBtn(btnResolve, "Risolvi / PR"); add(btnResolve);
            btnAssign = new JButton(); styleBtn(btnAssign, "Assegna a Membro"); add(btnAssign); // Added
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

            btnEdit.setVisible(!isMainTab);
            btnResolve.setVisible(!isAdmin && isMainTab && !isResolved);
            btnArchive.setVisible(isAdmin && isMainTab && isBug);
            btnAssign.setVisible(isAdmin && isMainTab && !isResolved);
        }
    }

    class ActionsRenderer extends ActionsPanel implements TableCellRenderer
    {
        public ActionsRenderer(boolean main) { super(main); }
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

        public ActionsEditor(boolean main)
        {
            panel = new ActionsPanel(main);
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

    public static void main(String[] args)
    {
        FlatInterFont.install();
        FlatLightLaf.setup();
        UIManager.put("defaultFont", new Font(FlatInterFont.FAMILY, Font.PLAIN, 13));

        SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame("TEST Issues View (Admin Mode)");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);

            // 1. Init View come ADMIN (così vediamo il tasto Assegna e Archivia)
            IssuesView view = new IssuesView(true);

            // 2. MOCK DATA: Issues
            List<IssueResponseDTO> mockIssues = new ArrayList<>();
            mockIssues.add(new IssueResponseDTO(
                    1L, "Bug Login Frontend", "...", TipoIssue.BUG, StatoIssue.ASSEGNATA, false, null, TipoPriorita.ALTA,
                    1L, "User", 1L, "Progetto A",
                    10L, "Team Web" // <--- Aggiunto 10L
            ));

            mockIssues.add(new IssueResponseDTO(
                    2L, "Refactoring API", "...", TipoIssue.FEATURE, StatoIssue.DA_ACCETTARE, false, null, TipoPriorita.MEDIA,
                    1L, "User", 1L, "Progetto A",
                    11L, "Team Backend" // <--- Aggiunto 11L
            ));

            mockIssues.add(new IssueResponseDTO(
                    3L, "Crash App Android", "...", TipoIssue.BUG, StatoIssue.DA_ACCETTARE, false, null, TipoPriorita.ALTA,
                    1L, "User", 2L, "App Mobile",
                    12L, "Team App" // <--- Aggiunto 12L
            ));

            mockIssues.add(new IssueResponseDTO(
                    4L, "Documentazione", "...", TipoIssue.DOCUMENTATION, StatoIssue.RISOLTA, false, null, TipoPriorita.BASSA,
                    1L, "User", 1L, "Progetto A",
                    10L, "Team Web" // <--- Aggiunto 10L
            ));
            // 3. MOCK DATA: Utenti per assegnazione
            List<UtenteResponseDTO> mockMembers = new ArrayList<>();
            mockMembers.add(new UtenteResponseDTO(101L, "Mario", "Rossi", "mario@test.com", false));
            mockMembers.add(new UtenteResponseDTO(102L, "Luigi", "Verdi", "luigi@test.com", false));
            mockMembers.add(new UtenteResponseDTO(103L, "Anna", "Bianchi", "anna@test.com", true));

            // Carica dati iniziali
            view.updateMainTab(mockIssues);
            view.updateCreateTab(mockIssues);

            // 4. MOCK CONTROLLER: Filtri Live
            view.setupFiltersListener(e -> {
                String text = view.getSearchField().getText().trim().toLowerCase();
                TipoIssue tipo = (TipoIssue) view.getFilterTipo().getSelectedItem();
                StatoIssue stato = (StatoIssue) view.getFilterStato().getSelectedItem();
                TipoPriorita priorita = (TipoPriorita) view.getFilterPriorita().getSelectedItem();

                List<IssueResponseDTO> filtered = mockIssues.stream()
                        .filter(i -> text.isEmpty() || i.getTitolo().toLowerCase().contains(text))
                        .filter(i -> tipo == null || i.getTipo() == tipo)
                        .filter(i -> stato == null || i.getStato() == stato)
                        .filter(i -> priorita == null || i.getPriorita() == priorita)
                        .toList();

                view.updateMainTab(filtered);
                view.updateCreateTab(filtered);
            });

            // 5. MOCK CONTROLLER: Reset
            view.setResetFilterAction(e -> {
                view.getSearchField().setText("");
                view.getFilterTipo().setSelectedIndex(0);
                view.getFilterStato().setSelectedIndex(0);
                view.getFilterPriorita().setSelectedIndex(0);
                view.updateMainTab(mockIssues);
                view.updateCreateTab(mockIssues);
            });

            // 6. MOCK CONTROLLER: Assegnazione (Apre la Dialog reale)
            view.setOnAssignIssue(id -> {
                AssignIssueDialog dialog = new AssignIssueDialog(frame, mockMembers);
                dialog.setVisible(true);

                List<UtenteResponseDTO> selected = dialog.getSelectedUsers();
                if (!selected.isEmpty()) {
                    String names = selected.stream().map(u -> u.getNome() + " " + u.getCognome()).collect(Collectors.joining(", "));
                    JOptionPane.showMessageDialog(frame, "Assegnato Issue " + id + " a: " + names);
                }
            });

            // 7. Altre azioni
            view.setOnArchiveIssue(id -> JOptionPane.showMessageDialog(frame, "Archivia ID: " + id));
            view.setOnEditIssue(id -> JOptionPane.showMessageDialog(frame, "Modifica ID: " + id));

            frame.add(view);
            frame.setVisible(true);
        });
    }
}