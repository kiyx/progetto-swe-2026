package view.teams;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import view.component.TeamsTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

public class TeamsView extends JXPanel
{

    private final TeamsTableModel tableModel;

    @Getter
    private transient TableRowSorter<TeamsTableModel> sorter;

    private JTextField searchField;
    private JButton createTeamButton;
    private JButton refreshButton;

    public TeamsView()
    {
        this.tableModel = new TeamsTableModel();
        initComponents();
    }

    private void initComponents()
    {
        JXTable table;
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[grow][]", "[]"));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestione Team");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cerca Team...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        createTeamButton = new JButton("Nuovo Team");
        createTeamButton.setBackground(new Color(0, 100, 255));
        createTeamButton.setForeground(Color.WHITE);
        createTeamButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; arc: 10");

        refreshButton = new JButton("Aggiorna");

        topPanel.add(titleLabel);
        topPanel.add(searchField, "growx");
        topPanel.add(createTeamButton);
        topPanel.add(refreshButton);

        table = new JXTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setRowHeight(40);
        table.setShowGrid(false, true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if(table.getColumnCount() >= 4)
        {
            table.getColumnModel().getColumn(TeamsTableModel.COL_ID).setMaxWidth(50);
            table.getColumnModel().getColumn(TeamsTableModel.COL_ADD_MEMBER).setMaxWidth(110);
            table.getColumnModel().getColumn(TeamsTableModel.COL_REMOVE_MEMBER).setMaxWidth(110);
        }

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(0).setResizable(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(topPanel, "growx, wrap");
        add(scrollPane, "grow");
    }

    public TeamsTableModel getModel() { return tableModel; }
    public String getSearchText() { return searchField.getText(); }
    public void addSearchListener(KeyListener k) { searchField.addKeyListener(k); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }
    public void addCreateListener(ActionListener l) { createTeamButton.addActionListener(l); }
}