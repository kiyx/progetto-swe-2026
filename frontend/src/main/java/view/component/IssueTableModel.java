package view.component;

import model.dto.enums.StatoIssue;
import model.dto.enums.TipoPriorita;
import model.dto.response.IssueResponseDTO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IssueTableModel extends AbstractTableModel {

    private final List<IssueResponseDTO> issues = new ArrayList<>();

    private final String[] columns = {"ID", "Titolo", "Progetto", "Team", "Tipo", "Stato", "Priorità", "Img", "Azioni"};

    public static final int COL_ID = 0;
    public static final int COL_TITOLO = 1;
    public static final int COL_PROGETTO = 2;
    public static final int COL_TEAM = 3;
    public static final int COL_TIPO = 4;
    public static final int COL_STATO = 5;
    public static final int COL_PRIORITA = 6;
    public static final int COL_IMG = 7;
    public static final int COL_ACTION = 8;

    public void setData(List<IssueResponseDTO> newIssues) {
        this.issues.clear();
        if (newIssues != null) {
            this.issues.addAll(newIssues);
        }
        fireTableDataChanged();
    }

    public IssueResponseDTO getIssueAt(int row) {
        if (row >= 0 && row < issues.size()) {
            return issues.get(row);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return issues.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_ID) return Long.class;
        if (columnIndex == COL_STATO) return StatoIssue.class;
        if (columnIndex == COL_PRIORITA) return TipoPriorita.class;
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Le celle cliccabili sono solo Immagine e Azioni
        return columnIndex == COL_ACTION || columnIndex == COL_IMG;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IssueResponseDTO issue = issues.get(rowIndex);
        return switch (columnIndex) {
            case COL_ID -> issue.getId();
            case COL_TITOLO -> issue.getTitolo();
            case COL_PROGETTO -> issue.getNomeProgetto();
            case COL_TEAM -> issue.getNomeTeam() != null ? issue.getNomeTeam() : "N/A";
            case COL_TIPO -> issue.getTipo();
            case COL_STATO -> issue.getStato();
            case COL_PRIORITA -> issue.getPriorita();
            case COL_IMG -> issue.getImmagine(); // Ritorna la stringa dell'immagine (se c'è)
            case COL_ACTION -> "Risolvi";
            default -> null;
        };
    }
}