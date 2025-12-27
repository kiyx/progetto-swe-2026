package view.issues;

import com.formdev.flatlaf.*;
import lombok.*;
import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import model.dto.response.IssueResponseDTO;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import utils.ImageUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

public class IssuesView extends JXPanel
{
    public enum TabType
    {
        MAIN,
        CREATED,
        ARCHIVE
    }

    private static final int ROW_HEIGHT = 50;
    private static final int ICON_SIZE = 40;
    private static final int ACTION_COL_WIDTH = 160;
    private static final int IMG_COL_WIDTH = 60;

    private static final int COL_ID = 0;
    private static final int COL_IMG = 1;
    private static final int COL_TIPO = 3;
    private static final int COL_STATO = 4;
    private static final int COL_PRIORITA = 5;
    private static final int COL_AZIONI = 8;

    private final boolean isAdmin;
    private final DefaultTableModel modelMain;
    private final DefaultTableModel modelCreate;
    private final DefaultTableModel modelArchive;

    @Getter
    private JTextField searchField;
    @Getter
    private JComboBox<TipoIssue> filterTipo;
    @Getter
    private JComboBox<StatoIssue> filterStato;
    @Getter
    private JComboBox<TipoPriorita> filterPriorita;

    private JXButton btnReset;
    private JXButton btnNuovaIssue;
    private JXButton btnRefresh;

    @Setter
    private transient LongConsumer onArchiveIssue;
    @Setter
    private transient LongConsumer onEditIssue;
    @Setter
    private transient LongConsumer onResolveIssue;
    @Setter
    private transient LongConsumer onAssignIssue;

    public IssuesView(boolean isAdmin)
    {
        this.isAdmin = isAdmin;

        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[]10[grow]"));
        setBackground(Color.WHITE);

        initHeader();
        initFilterPanel();

        String[] cols = {"ID", "Immagine", "Titolo", "Tipo", "Stato", "Priorità", "Progetto", "Team", "Azioni"};

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "font: bold; tabType: card");

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

    private void initHeader()
    {
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
        btnNuovaIssue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        header.add(title);
        header.add(btnRefresh, "gapright 10");
        header.add(btnNuovaIssue);
        add(header, "wrap, growx");
    }

    private void initFilterPanel()
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
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add(new JLabel("Filtri:"));
        panel.add(searchField);
        panel.add(filterTipo);
        panel.add(filterStato);
        panel.add(filterPriorita);
        panel.add(new JLabel(""), "growx");
        panel.add(btnReset);

        add(panel, "wrap, growx");
    }

    private DefaultTableModel createModel(String[] cols)
    {
        return new DefaultTableModel(cols, 0)
        {
            @Override
            public boolean isCellEditable(int row, int col)
            {
                return col == COL_AZIONI;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                return columnIndex == COL_ID ? Long.class : Object.class;
            }
        };
    }

    private JTable createTable(DefaultTableModel model, TabType tabType)
    {
        JTable table = new JTable(model);
        table.setRowHeight(ROW_HEIGHT);
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true);

        table.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        table.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        table.getColumnModel().getColumn(COL_ID).setPreferredWidth(0);

        table.getColumnModel().getColumn(COL_IMG).setCellRenderer(new ImageCellRenderer());
        table.getColumnModel().getColumn(COL_IMG).setMinWidth(IMG_COL_WIDTH);
        table.getColumnModel().getColumn(COL_IMG).setMaxWidth(IMG_COL_WIDTH);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(COL_TIPO).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(COL_STATO).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(COL_PRIORITA).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(COL_AZIONI).setCellRenderer(new ActionsRenderer(tabType));
        table.getColumnModel().getColumn(COL_AZIONI).setCellEditor(new ActionsEditor(tabType));
        table.getColumnModel().getColumn(COL_AZIONI).setMinWidth(ACTION_COL_WIDTH);
        table.getColumnModel().getColumn(COL_AZIONI).setMaxWidth(ACTION_COL_WIDTH);

        table.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if(row >= 0 && col == COL_IMG)
                {
                    int modelRow = table.convertRowIndexToModel(row);
                    String base64 = (String) table.getModel().getValueAt(modelRow, COL_IMG);
                    showImagePreview(base64);
                }
            }
        });

        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e)
            {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if(row >= 0 && col == COL_IMG)
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                else
                    table.setCursor(Cursor.getDefaultCursor());
            }
        });

        return table;
    }

    private <T> void setupCombo(JComboBox<T> combo, String placeholder)
    {
        combo.insertItemAt(null, 0);
        combo.setSelectedIndex(0);
        combo.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value == null)
                    setText(placeholder + ": Tutti");
                return this;
            }
        });
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

    public void updateMainTab(List<IssueResponseDTO> issues)
    {
        updateModel(modelMain, issues);
    }

    public void updateCreateTab(List<IssueResponseDTO> issues)
    {
        updateModel(modelCreate, issues);
    }

    public void updateArchiveTab(List<IssueResponseDTO> issues)
    {
        updateModel(modelArchive, issues);
    }

    private void updateModel(DefaultTableModel model, List<IssueResponseDTO> issues)
    {
        model.setRowCount(0);
        for (IssueResponseDTO i : issues)
        {
            model.addRow(new Object[]{
                    i.getId(),
                    i.getImmagine(),
                    i.getTitolo(),
                    i.getTipo(),
                    i.getStato(),
                    i.getPriorita(),
                    i.getNomeProgetto(),
                    i.getNomeTeam(),
                    ""
            });
        }
    }

    public void setupFiltersListener(ActionListener l)
    {
        searchField.addActionListener(l);
        filterTipo.addActionListener(l);
        filterStato.addActionListener(l);
        filterPriorita.addActionListener(l);
    }

    public void setNuovaIssueAction(ActionListener l)
    {
        btnNuovaIssue.addActionListener(l);
    }

    public void setRefreshAction(ActionListener l)
    {
        btnRefresh.addActionListener(l);
    }

    public void setResetFilterAction(ActionListener l)
    {
        btnReset.addActionListener(l);
    }

    private static class ImageCellRenderer extends DefaultTableCellRenderer
    {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setIcon(null);

            if(value instanceof String base64 && !base64.isBlank())
            {
                ImageIcon icon = ImageUtils.decodeBase64ToIcon(base64, ICON_SIZE, ICON_SIZE);
                setIcon(icon);
                setToolTipText("Clicca per ingrandire");
            }
            else
            {
                setText("-");
                setToolTipText(null);
            }
            return this;
        }
    }

    private class ActionsPanel extends JPanel
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

            btnEdit = createActionBtn("Modifica");
            btnResolve = createActionBtn("Risolvi / PR");
            btnAssign = createActionBtn("Assegna");
            btnArchive = createActionBtn("Archivia Bug");

            add(btnEdit);
            add(btnResolve);
            add(btnAssign);
            add(btnArchive);
        }

        private JButton createActionBtn(String tooltip)
        {
            JButton btn = new JButton();
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setToolTipText(tooltip);
            return btn;
        }

        public void updateView(boolean isSelected, StatoIssue stato, TipoIssue tipo)
        {
            updateColors(isSelected);
            updateVisibility(stato, tipo);
        }

        private void updateColors(boolean isSelected)
        {
            Color cEdit = isSelected ? Color.WHITE : new Color(25, 118, 210);
            Color cResolve = isSelected ? Color.WHITE : new Color(46, 125, 50);
            Color cArchive = isSelected ? Color.WHITE : new Color(211, 47, 47);
            Color cAssign = isSelected ? Color.WHITE : new Color(255, 152, 0);

            btnEdit.setIcon(FontIcon.of(MaterialDesignP.PENCIL, 20, cEdit));
            btnResolve.setIcon(FontIcon.of(MaterialDesignC.CHECK_CIRCLE, 22, cResolve));
            btnArchive.setIcon(FontIcon.of(MaterialDesignA.ARCHIVE, 20, cArchive));
            btnAssign.setIcon(FontIcon.of(MaterialDesignA.ACCOUNT_PLUS, 22, cAssign));
        }

        private void updateVisibility(StatoIssue stato, TipoIssue tipo)
        {
            boolean isBug = (tipo == TipoIssue.BUG);
            boolean isResolved = (stato == StatoIssue.RISOLTA);

            btnEdit.setVisible(tabType == TabType.CREATED && !isResolved);
            btnResolve.setVisible(tabType == TabType.MAIN && !isResolved);
            btnAssign.setVisible(isAdmin && tabType == TabType.MAIN && !isResolved);
            btnArchive.setVisible(isAdmin && tabType == TabType.MAIN && isBug);
        }
    }

    private class ActionsRenderer extends ActionsPanel implements TableCellRenderer
    {
        public ActionsRenderer(TabType type)
        {
            super(type);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int col)
        {
            setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);

            int modelRow = table.convertRowIndexToModel(row);
            Object t = table.getModel().getValueAt(modelRow, COL_TIPO);
            Object s = table.getModel().getValueAt(modelRow, COL_STATO);

            if(t instanceof TipoIssue tipo && s instanceof StatoIssue stato)
                updateView(isSel, stato, tipo);
            return this;
        }
    }

    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final ActionsPanel panel;
        private Long currentId;

        public ActionsEditor(TabType type)
        {
            panel = new ActionsPanel(type);

            panel.btnEdit.addActionListener(e -> fireAction(onEditIssue));
            panel.btnResolve.addActionListener(e -> fireAction(onResolveIssue));
            panel.btnArchive.addActionListener(e -> fireAction(onArchiveIssue));
            panel.btnAssign.addActionListener(e -> fireAction(onAssignIssue));
        }

        private void fireAction(LongConsumer action)
        {
            fireEditingStopped();
            if(action != null && currentId != null)
                action.accept(currentId);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSel, int row, int col)
        {
            int modelRow = table.convertRowIndexToModel(row);
            this.currentId = (Long) table.getModel().getValueAt(modelRow, COL_ID);

            panel.setBackground(table.getSelectionBackground());

            Object t = table.getModel().getValueAt(modelRow, COL_TIPO);
            Object s = table.getModel().getValueAt(modelRow, COL_STATO);

            if(t instanceof TipoIssue tipo && s instanceof StatoIssue stato)
                panel.updateView(true, stato, tipo);
            return panel;
        }

        @Override
        public Object getCellEditorValue()
        {
            return "";
        }

        @Override
        public boolean isCellEditable(EventObject e)
        {
            return true;
        }
    }

    private void showImagePreview(String base64)
    {
        if(base64 == null || base64.isBlank())
            return;

        ImageIcon icon = ImageUtils.decodeBase64ToIcon(base64, 800, 600);

        if(icon == null)
            return;

        JDialog previewDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Anteprima Immagine", true);
        previewDialog.setLayout(new BorderLayout());
        previewDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JLabel lblImage = new JLabel(icon);
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        previewDialog.add(new JScrollPane(lblImage), BorderLayout.CENTER);

        lblImage.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                previewDialog.dispose();
            }
        });

        previewDialog.pack();
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setVisible(true);
    }
}