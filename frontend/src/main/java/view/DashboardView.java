package view;

import org.jdesktop.swingx.JXFrame;
import com.formdev.flatlaf.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DashboardView extends JXPanel
{
    public DashboardView()
    {
        setMinimumSize(new Dimension(1230, 960));
        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        // 1. Root Panel con MigLayout
        JXPanel rootPanel = new JXPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        rootPanel.setBackground(Color.WHITE);

        // 2. Pannello per i filtri (Top)
        // Usiamo MigLayout anche qui per coerenza
        JPanel topPanel = new JPanel(new MigLayout("fillx, insets 10", "[][grow]", "[]10[]"));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JTextField searchField = new JTextField();
        topPanel.add(new JLabel("Cerca Issue:"), "split 2");
        topPanel.add(searchField, "growx, wrap");

        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"Tutti i Tipi", "Bug", "Feature", "Task"});
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"Tutti gli Stati", "Aperto", "In Corso", "Chiuso"});
        JComboBox<String> priorityFilter = new JComboBox<>(new String[]{"Tutte le Priorità", "Alta", "Media", "Bassa"});

        topPanel.add(new JLabel("Filtri:"), "split 6");
        topPanel.add(typeFilter);
        topPanel.add(statusFilter);
        topPanel.add(priorityFilter);

        // 3. Tabella
        String[] columns = {"ID", "Titolo", "Progetto", "Tipo", "Stato", "Priorità"};
        Object[][] data = {
                {"#101", "Bug Login", "App Mobile", "Bug", "Aperto", "Alta"},
                {"#102", "Nuova Feature Pagamento", "E-commerce", "Feature", "In Corso", "Media"},
                {"#103", "Refactoring API", "Backend Cloud", "Task", "Chiuso", "Bassa"}
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columns);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // AGGIUNTA AL ROOT PANEL (La parte cruciale)
        // Aggiungiamo i componenti seguendo le righe definite nel MigLayout del rootPanel [][grow]
        rootPanel.add(topPanel, "growx, wrap");
        rootPanel.add(scrollPane, "grow");

        // Refresh finale
        revalidate();
        repaint();
    }

}
