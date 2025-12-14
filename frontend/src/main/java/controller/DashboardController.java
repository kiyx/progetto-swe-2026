package controller;

import model.dto.enums.StatoIssue;
import model.dto.response.IssueResponseDTO;
import service.IssueService;
import view.DashboardView;
import view.component.IssueTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardController {

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    private final DashboardView view;
    private final IssueService issueService;

    public DashboardController(DashboardView view, IssueService issueService) {
        this.view = view;
        this.issueService = issueService;
        initController();
    }

    private void initController() {
        //setupFilterListeners();
        view.addRefreshListener(e -> loadData());

        // Setup Renderers: Bottone Risolvi e Bottone Immagine
        //setupTableRenderers();

        loadData();
    }

    private void loadData() {
        SwingWorker<List<IssueResponseDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<IssueResponseDTO> doInBackground() {
                try {
                    return issueService.getIssues();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Errore fetch issues", e);
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    view.getModel().setData(get());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Errore worker", e);
                }
            }
        };
        worker.execute();
    }

   /* // --- Logica Bottoni Tabella ---

    private void resolveIssue(Long idIssue) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Vuoi segnare la issue #" + idIssue + " come RISOLTA?",
                "Conferma Risoluzione", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return issueService.updateStato(idIssue, StatoIssue.RISOLTA);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) { loadData(); JOptionPane.showMessageDialog(view, "Issue risolta!"); }
                    } catch (Exception e) { LOGGER.log(Level.SEVERE, "Errore risoluzione", e); }
                }
            };
            worker.execute();
        }
    }

    private void showImagePreview(String imageString) {
        if (imageString == null || imageString.isBlank()) return;

        JDialog previewDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Anteprima", true);
        previewDialog.setLayout(new BorderLayout());

        JLabel imageLabel = new JLabel("Caricamento...", SwingConstants.CENTER);

        try {
            // Tentativo di decodifica Base64 (assumendo che le immagini siano salvate così o come URL)
            // Se fosse un URL, useremmo ImageIO.read(new URL(imageString));
            // Qui assumiamo una stringa base64 pura per semplicità dell'esempio
            byte[] imageBytes = Base64.getDecoder().decode(imageString);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

            if (img != null) {
                // Resize se troppo grande
                if (img.getWidth() > 800 || img.getHeight() > 600) {
                    Image scaled = img.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                } else {
                    imageLabel.setIcon(new ImageIcon(img));
                }
                imageLabel.setText("");
            } else {
                imageLabel.setText("Formato immagine non valido");
            }
        } catch (IllegalArgumentException | IOException e) {
            imageLabel.setText("Impossibile caricare l'immagine");
            LOGGER.warning("Errore decodifica immagine: " + e.getMessage());
        }

        previewDialog.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        previewDialog.setSize(600, 500);
        previewDialog.setLocationRelativeTo(view);
        previewDialog.setVisible(true);
    }

    private void setupTableRenderers() {
        JTable table = view.getTable();

        // 1. Renderer Colonna Azioni (Risolvi)
        table.getColumnModel().getColumn(IssueTableModel.COL_ACTION).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(IssueTableModel.COL_ACTION).setCellEditor(
                new ActionButtonEditor(new JCheckBox(), this::resolveIssue)
        );

        // 2. Renderer Colonna Immagine (Occhio)
        table.getColumnModel().getColumn(IssueTableModel.COL_IMG).setCellRenderer(new ImageButtonRenderer());
        table.getColumnModel().getColumn(IssueTableModel.COL_IMG).setCellEditor(
                new ImageButtonEditor(new JCheckBox(), this::showImagePreview)
        );
    }

    // --- Filtri ---
    private void setupFilterListeners() {
        ActionListener filterAction = e -> applyFilters();
        view.addFilterListener(filterAction);
        view.addSearchListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { applyFilters(); }
        });
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        String text = view.getSearchText();

        if (text != null && !text.isBlank()) filters.add(RowFilter.regexFilter("(?i)" + text));

        if (view.getSelectedType() instanceof Enum<?> e)
            filters.add(RowFilter.regexFilter(e.toString(), IssueTableModel.COL_TIPO));
        if (view.getSelectedStatus() instanceof Enum<?> e)
            filters.add(RowFilter.regexFilter(e.toString(), IssueTableModel.COL_STATO));
        if (view.getSelectedPriority() instanceof Enum<?> e)
            filters.add(RowFilter.regexFilter(e.toString(), IssueTableModel.COL_PRIORITA));

        // NOTA: Non aggiungiamo filtri specifici per il Team, ma la ricerca testuale (regex)
        // cercherà comunque anche nella colonna Team perché è una stringa nella tabella.

        if (filters.isEmpty()) view.getSorter().setRowFilter(null);
        else view.getSorter().setRowFilter(RowFilter.andFilter(filters));
    }

    // --- Classi Inner per Renderer ed Editor (Action) ---

    static class ActionButtonRenderer extends JButton implements TableCellRenderer {
        public ActionButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Object stato = t.getValueAt(r, IssueTableModel.COL_STATO);
            boolean risolta = (stato == StatoIssue.RISOLTA);
            setText(risolta ? "✓ Fatto" : "Risolvi");
            setEnabled(!risolta);
            return this;
        }
    }

    static class ActionButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private Long currentId;
        private final Consumer<Long> action;

        public ActionButtonEditor(JCheckBox cb, Consumer<Long> action) {
            super(cb);
            this.action = action;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            int modelRow = t.convertRowIndexToModel(r);
            currentId = (Long) ((IssueTableModel)t.getModel()).getValueAt(modelRow, IssueTableModel.COL_ID);
            Object stato = t.getValueAt(r, IssueTableModel.COL_STATO);
            boolean risolta = (stato == StatoIssue.RISOLTA);
            button.setText(risolta ? "✓ Fatto" : "Risolvi");
            button.setEnabled(!risolta);
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            if (button.isEnabled() && currentId != null) action.accept(currentId);
            return "Risolvi";
        }
    }

    // --- Classi Inner per Renderer ed Editor (Image) ---

    static class ImageButtonRenderer extends JButton implements TableCellRenderer {
        public ImageButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean s, boolean f, int r, int c) {
            String imgVal = (String) value;
            if (imgVal != null && !imgVal.isBlank()) {
                setText("👁"); // Icona Unicode Occhio
                setToolTipText("Visualizza immagine");
                setEnabled(true);
            } else {
                setText("");
                setToolTipText(null);
                setEnabled(false);
            }
            return this;
        }
    }

    static class ImageButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String currentImg;
        private final Consumer<String> action;

        public ImageButtonEditor(JCheckBox cb, Consumer<String> action) {
            super(cb);
            this.action = action;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object value, boolean s, int r, int c) {
            currentImg = (String) value;
            if (currentImg != null && !currentImg.isBlank()) {
                button.setText("👁");
                button.setEnabled(true);
            } else {
                button.setText("");
                button.setEnabled(false);
            }
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            if (button.isEnabled() && currentImg != null) action.accept(currentImg);
            return currentImg; // Ritorna il valore originale per non cancellarlo
        }
    }*/
}