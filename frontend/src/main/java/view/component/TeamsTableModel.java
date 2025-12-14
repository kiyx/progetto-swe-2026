package view.component;

import model.dto.response.TeamResponseDTO;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TeamsTableModel extends AbstractTableModel {

    private final List<TeamResponseDTO> teams = new ArrayList<>();

    // Colonne: ID, Nome, Aggiungi (Bottone), Rimuovi (Bottone)
    private final String[] columns = {"ID", "Nome Team", "Aggiungi", "Rimuovi"};

    public static final int COL_ID = 0;
    public static final int COL_NOME = 1;
    public static final int COL_ADD_MEMBER = 2;
    public static final int COL_REMOVE_MEMBER = 3;

    public void setData(List<TeamResponseDTO> newTeams) {
        this.teams.clear();
        if (newTeams != null) {
            this.teams.addAll(newTeams);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return teams.size(); }
    @Override
    public int getColumnCount() { return columns.length; }
    @Override
    public String getColumnName(int column) { return columns[column]; }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_ID) return Long.class;
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COL_ADD_MEMBER || columnIndex == COL_REMOVE_MEMBER;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TeamResponseDTO team = teams.get(rowIndex);
        return switch (columnIndex) {
            case COL_ID -> team.getId();
            case COL_NOME -> team.getNome();
            case COL_ADD_MEMBER -> "+ Membro";
            case COL_REMOVE_MEMBER -> "- Membro";
            default -> null;
        };
    }
}