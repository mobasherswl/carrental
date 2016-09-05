package com.epam.client.gui.swing;

import com.epam.client.util.SwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;

/**
 * Created by Ahmed_Khan on 4/22/2016.
 */
@org.springframework.stereotype.Component
public class MainWindow extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    @Value("${mainwindow.title}")
    private String title;
    @Value("${mainwindow.width}")
    private int width;
    @Value("${mainwindow.height}")
    private int height;
    @Resource(name = "customerTabPanel")
    private JPanel customerTabPanel;
    @Resource(name = "fleetTabPanel")
    private JPanel fleetTabPanel;
    @Resource(name = "rentCarTabPanel")
    private JPanel rentCarTabPanel;
    @Resource(name = "currentRentalsTabPanel")
    private JPanel currentRentalsTabPanel;
    @Resource(name = "rentalHistoryTabPanel")
    private JPanel rentalHistoryTabPanel;
    @Resource(name = "testConnectionTabPanel")
    private JPanel testConnectionTabPanel;
    @Resource(name = "rentalClassTabPanel")
    private JPanel rentalClassTabPanel;

    @PostConstruct
    public void postConstruct() {
        SwingUtilities.invokeLater(() -> {
            SwingUtil.loadLookAndFeel(logger);
            setTitle(title);
            add(getTabbedPane());
            setResizable(true);
            pack();
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
        });
    }

    private JTabbedPane getTabbedPane() {
        JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.VERTICAL);

        jTabbedPane.addTab("Customers", customerTabPanel);
        jTabbedPane.addTab("Rental Class", rentalClassTabPanel);
        jTabbedPane.add("Fleet", fleetTabPanel);
        jTabbedPane.add("Available Cars", rentCarTabPanel);
        jTabbedPane.add("Current Rentals", currentRentalsTabPanel);
        jTabbedPane.add("Rental History", rentalHistoryTabPanel);
        jTabbedPane.addTab("Other", testConnectionTabPanel);

        return jTabbedPane;
    }
}
