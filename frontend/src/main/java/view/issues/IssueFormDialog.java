package view.issues;

import com.formdev.flatlaf.*;
import lombok.*;
import model.dto.enums.TipoIssue;
import model.dto.enums.TipoPriorita;
import model.dto.request.CreateIssueRequestDTO;
import model.dto.request.UpdateIssueRequestDTO;
import model.dto.response.IssueResponseDTO;
import model.dto.response.ProgettoResponseDTO;
import net.miginfocom.swing.*;
import utils.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Objects;

public class IssueFormDialog extends JDialog
{
    public static final String MSG_ERROR = "Errore";
    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JComboBox<TipoIssue> cmbTipo;
    private JComboBox<TipoPriorita> cmbPriorita;
    private JComboBox<ProgettoResponseDTO> cmbProgetto;
    private JLabel lblImagePreview;

    private String currentBase64Image = null;

    @Getter
    private boolean confirmed = false;

    public IssueFormDialog(Frame owner, List<ProgettoResponseDTO> progettiDisponibili, IssueResponseDTO issueToEdit)
    {
        super(owner, issueToEdit == null ? "Nuova Issue" : "Modifica Issue", true);

        setupWindowProperties();
        initComponents(progettiDisponibili);
        buildLayout(issueToEdit == null ? "Crea Nuova Segnalazione" : "Modifica Segnalazione");

        if(issueToEdit != null)
        {
            loadIssueData(issueToEdit);
            cmbProgetto.setEnabled(false);
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void setupWindowProperties()
    {
        setLayout(new MigLayout("fill, insets 20", "[label]10[grow, fill]20[150!]", "[]10[]10[]10[]10[]20[]"));
        setResizable(false);
    }

    private void initComponents(List<ProgettoResponseDTO> progettiDisponibili)
    {
        txtTitolo = new JTextField();

        txtDescrizione = new JTextArea(5, 30);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);

        cmbTipo = new JComboBox<>(TipoIssue.values());

        initPrioritaCombo();
        initProgettoCombo(progettiDisponibili);
        initImageComponents();
    }

    private void initPrioritaCombo()
    {
        cmbPriorita = new JComboBox<>();
        cmbPriorita.addItem(null);
        for(TipoPriorita p : TipoPriorita.values())
            cmbPriorita.addItem(p);

        cmbPriorita.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value == null)
                    setText("Nessuna");
                return this;
            }
        });
    }

    private void initProgettoCombo(List<ProgettoResponseDTO> progettiDisponibili)
    {
        cmbProgetto = new JComboBox<>();
        if(progettiDisponibili != null)
            progettiDisponibili.forEach(cmbProgetto::addItem);

        cmbProgetto.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof ProgettoResponseDTO p)
                    setText(p.getNome());
                return this;
            }
        });
    }

    private void initImageComponents()
    {
        lblImagePreview = new JLabel();
        lblImagePreview.setPreferredSize(new Dimension(150, 150));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagePreview.setText("Nessuna Immagine");
    }

    private void buildLayout(String title)
    {
        JLabel lblTitleHeader = new JLabel(title);
        lblTitleHeader.putClientProperty(FlatClientProperties.STYLE, "font: bold +4");
        add(lblTitleHeader, "span, wrap, gapbottom 10");

        add(new JLabel("Titolo:*"));
        add(txtTitolo);

        add(lblImagePreview, "spany 4, top, wrap");

        add(new JLabel("Descrizione:*"), "top");
        add(new JScrollPane(txtDescrizione), "wrap");

        add(new JLabel("Tipo:*"));
        add(cmbTipo, "wrap");

        add(new JLabel("Priorità:"));
        add(cmbPriorita, "wrap");

        add(new JLabel("Progetto:*"));
        add(cmbProgetto);

        JPanel imgBtnPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        JButton btnUpload = new JButton("Carica");
        JButton btnRemove = new JButton("Rimuovi");

        btnUpload.addActionListener(e -> chooseImage());
        btnRemove.addActionListener(e -> removeImage());

        imgBtnPanel.add(btnUpload, "growx");
        imgBtnPanel.add(btnRemove, "growx");
        add(imgBtnPanel, "wrap");

        add(createFooterButtons(), "span, growx, pushy, bottom");
    }

    private JPanel createFooterButtons()
    {
        JButton btnSave = new JButton("Salva");
        btnSave.setBackground(new Color(0, 100, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.putClientProperty(FlatClientProperties.STYLE, "font: bold");

        JButton btnCancel = new JButton("Annulla");

        btnSave.addActionListener(e ->
        {
            if(validateFields())
            {
                confirmed = true;
                dispose();
            }
        });
        btnCancel.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0, right"));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        return buttonPanel;
    }

    private void loadIssueData(IssueResponseDTO issue)
    {
        txtTitolo.setText(issue.getTitolo());
        txtDescrizione.setText(issue.getDescrizione());
        cmbTipo.setSelectedItem(issue.getTipo());
        cmbPriorita.setSelectedItem(issue.getPriorita());

        selectProjectById(issue.getIdProgetto());
        loadImageIfExists(issue.getImmagine());
    }

    private void selectProjectById(Long idProgetto)
    {
        for(int i = 0; i < cmbProgetto.getItemCount(); i++)
        {
            ProgettoResponseDTO p = cmbProgetto.getItemAt(i);
            if(Objects.equals(p.getId(), idProgetto))
            {
                cmbProgetto.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadImageIfExists(String base64Image)
    {
        if(base64Image != null && !base64Image.isBlank())
        {
            currentBase64Image = base64Image;
            ImageIcon icon = ImageUtils.decodeBase64ToIcon(currentBase64Image, 150, 150);
            if(icon != null)
            {
                lblImagePreview.setText("");
                lblImagePreview.setIcon(icon);
            }
        }
    }

    private void chooseImage()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Immagini (JPG, PNG)", "jpg", "png", "jpeg"));

        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            String base64 = ImageUtils.encodeFileToBase64(file);

            if(base64 != null)
            {
                this.currentBase64Image = base64;
                lblImagePreview.setText("");
                lblImagePreview.setIcon(ImageUtils.loadResizedIcon(file, 150, 150));
            }
            else
                JOptionPane.showMessageDialog(this, "Errore caricamento file", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeImage()
    {
        this.currentBase64Image = "";
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Nessuna Immagine");
    }

    private boolean validateFields()
    {
        if(txtTitolo.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Il titolo è obbligatorio.", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(txtDescrizione.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "La descrizione è obbligatoria.", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(cmbProgetto.getSelectedItem() == null)
        {
            JOptionPane.showMessageDialog(this, "Seleziona un progetto.", MSG_ERROR, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public CreateIssueRequestDTO getCreateRequest()
    {
        ProgettoResponseDTO proj = (ProgettoResponseDTO) cmbProgetto.getSelectedItem();
        TipoPriorita priorita = (TipoPriorita) cmbPriorita.getSelectedItem();

        return CreateIssueRequestDTO.builder()
                .titolo(txtTitolo.getText().trim())
                .descrizione(txtDescrizione.getText().trim())
                .tipo((TipoIssue) cmbTipo.getSelectedItem())
                .priorita(priorita)
                .immagine(currentBase64Image)
                .idProgetto(proj != null ? proj.getId() : null)
                .build();
    }

    public UpdateIssueRequestDTO getUpdateRequest()
    {
        TipoPriorita priorita = (TipoPriorita) cmbPriorita.getSelectedItem();

        return UpdateIssueRequestDTO.builder()
                .titolo(txtTitolo.getText().trim())
                .descrizione(txtDescrizione.getText().trim())
                .tipo((TipoIssue) cmbTipo.getSelectedItem())
                .priorita(priorita)
                .immagine(currentBase64Image)
                .build();
    }
}