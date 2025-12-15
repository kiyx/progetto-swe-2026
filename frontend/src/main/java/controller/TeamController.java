package controller;

import model.dto.response.TeamResponseDTO;
import service.TeamsService;
import view.TeamsView;
import view.CreateTeamDialog;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamController
{
    private static final Logger LOGGER = Logger.getLogger(TeamController.class.getName());

    private final TeamsView view;
    private final TeamsService teamsService;

    public TeamController(TeamsView view, TeamsService teamsService)
    {
        this.view = view;
        this.teamsService = teamsService;

        initController();
        loadTeams();
    }
    private void initController()
    {


    }

    private void loadTeams()
    {
        try{
            List<TeamResponseDTO> teams = teamsService.getTeams();
            view.getModel().setData(teams);
        }
        catch(Exception e)
        {
            LOGGER.log(Level.SEVERE, "Errore nel load Team", e);
        }

    }

}
