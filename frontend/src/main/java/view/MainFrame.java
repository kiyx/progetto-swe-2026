package view;

import org.jdesktop.swingx.*;
import javax.swing.*;
import java.awt.*;
import java.util.logging.*;


public class MainFrame extends JXFrame
{
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());
    private static final Dimension MIN_DIMENSION = new Dimension(405, 510);

    private final CardLayout cardLayout;
    private final JXPanel mainPanel;

    public MainFrame()
    {
        super("BugBoard26");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(MIN_DIMENSION);
        //setLocationRelativeTo(null);

        try
        {
            Image iconImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/bug.png"));
            this.setIconImage(iconImage);
        }
        catch(Exception e)
        {
            LOGGER.log(Level.WARNING, e, ()-> "Impossibile caricare l'immagine della applicazione");
        }

        this.cardLayout = new CardLayout();
        this.mainPanel = new JXPanel(cardLayout);

        add(mainPanel);
    }

    public void addView(JXPanel view, String key)
    {
        mainPanel.add(view, key);
    }

    public void showView(String key)
    {
        cardLayout.show(mainPanel, key);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void clearViews()
    {
        mainPanel.removeAll();
    }
}