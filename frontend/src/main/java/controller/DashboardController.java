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

        view.addFilterListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        view.addRefreshListener(e -> loadData());
        view.addSearchListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
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


}