package utils;

import model.dto.response.UtenteResponseDTO;

import javax.swing.*;
import java.awt.*;

public class UtenteListRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if(value instanceof UtenteResponseDTO u)
        {
            setText(String.format("<html><b>%s %s</b> <span style='color:gray'>(%s)</span></html>",
                    u.getNome(), u.getCognome(), u.getEmail()));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        return this;
    }
}