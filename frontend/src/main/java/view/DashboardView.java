package view;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import model.dto.enums.StatoIssue;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import view.component.IssueTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

public class DashboardView extends JXPanel {

    private final IssueTableModel tableModel;
    @Getter
    private JXTable table;
    @Getter
    private transient TableRowSorter<IssueTableModel> sorter;

    private JTextField searchField;
    private JComboBox<Object> typeFilter;
    private JComboBox<Object> statusFilter;
    private JComboBox<Object> priorityFilter;
    private JButton refreshButton;

    public DashboardView()
    {
        this.tableModel = new IssueTableModel();
        initComponents();
    }

    private void initComponents()
    {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]10[]10[]10[grow][]", "[]"));
        topPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cerca...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        typeFilter = createCombo("Tutti i Tipi", TipoIssue.values());
        statusFilter = createCombo("Tutti gli Stati", StatoIssue.values());
        priorityFilter = createCombo("Tutte le Priorità", TipoPriorita.values());
        refreshButton = new JButton("Aggiorna");

        topPanel.add(new JLabel("Filtra:"));
        topPanel.add(typeFilter);
        topPanel.add(statusFilter);
        topPanel.add(priorityFilter);
        topPanel.add(searchField, "growx");
        topPanel.add(refreshButton);

        table = new JXTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setRowHeight(35);
        table.setShowGrid(false, true);
        table.setColumnControlVisible(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(IssueTableModel.COL_ID).setMaxWidth(50);
        table.getColumnModel().getColumn(IssueTableModel.COL_IMG).setMaxWidth(50); // Icona occhio piccola
        table.getColumnModel().getColumn(IssueTableModel.COL_ACTION).setMaxWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(topPanel, "growx, wrap");
        add(scrollPane, "grow");
    }

    private JComboBox<Object> createCombo(String primaVoce, Object[] values) {
        JComboBox<Object> cb = new JComboBox<>();
        cb.addItem(primaVoce);
        for (Object o : values) cb.addItem(o);
        return cb;
    }

    public IssueTableModel getModel() { return tableModel; }
    public String getSearchText() { return searchField.getText(); }
    public Object getSelectedType() { return typeFilter.getSelectedItem(); }
    public Object getSelectedStatus() { return statusFilter.getSelectedItem(); }
    public Object getSelectedPriority() { return priorityFilter.getSelectedItem(); }

    public void addFilterListener(ActionListener l) {
        typeFilter.addActionListener(l);
        statusFilter.addActionListener(l);
        priorityFilter.addActionListener(l);
    }

    public void addSearchListener(KeyListener k) { searchField.addKeyListener(k); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }
}