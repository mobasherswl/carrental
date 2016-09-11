package com.epam.client.gui.swing.tabs.rentcar;

import com.epam.client.gui.swing.async.Processor;
import com.epam.client.util.SwingUtil;
import com.epam.common.service.RentCarService;
import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.impl.JDatePickerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;

@Component
public class RentalHistoryStatisticsTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(RentalHistoryStatisticsTabPanel.class);

    private JTextArea textAreaForRentStatistics = new JTextArea();
    private JDatePickerImpl jDatePickerFrom;
    private JDatePickerImpl jDatePickerTo;
    private JButton searchButton;
    private Processor processor;
    private RentCarService rentCarService;

    @Resource(name = "mainWindow")
    private java.awt.Component parentComponent;
    @Value("${renthistorystatistics.load.failure}")
    private String rentStatisticLoadFailureMsg;
    @Value("${testconnection.errordialog.title}")
    private String errorDialogTitle;

    @Autowired
    public RentalHistoryStatisticsTabPanel(Processor processor, RentCarService rentCarService) {
        this.processor = processor;
        this.rentCarService = rentCarService;
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        SwingUtil.loadLookAndFeel(logger);

        JLabel fromDateLabel = new JLabel("From");
        jDatePickerFrom = (JDatePickerImpl) new JDateComponentFactory().createJDatePicker();
        JLabel toDateLabel = new JLabel("To");
        jDatePickerTo = (JDatePickerImpl) new JDateComponentFactory().createJDatePicker();
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(fromDateLabel);
        searchPanel.add(jDatePickerFrom);
        searchPanel.add(toDateLabel);
        searchPanel.add(jDatePickerTo);
        searchPanel.add(getSearchButton());
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        textAreaForRentStatistics.setEditable(false);
        add(textAreaForRentStatistics, BorderLayout.CENTER);
    }

    private java.awt.Component getSearchButton() {
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            Calendar fromCalendar = (Calendar) jDatePickerFrom.getModel().getValue();
            Calendar toCalendar = (Calendar) jDatePickerTo.getModel().getValue();
            Calendar today = Calendar.getInstance();
            if (fromCalendar == null || toCalendar == null) {
                JOptionPane.showMessageDialog(this, "Select From & To dates");
            } else if (fromCalendar.after(today) || toCalendar.after(today)) {
                JOptionPane.showMessageDialog(this, "From or To dates cannot be in future");
            } else if (fromCalendar.after(toCalendar)) {
                JOptionPane.showMessageDialog(this, "From date cannot be later than To date");
            } else {
                loadCarData(fromCalendar, toCalendar);
            }
        });
        return searchButton;
    }

    private void loadCarData(Calendar fromCalendar, Calendar toCalendar) {
        searchButton.setEnabled(false);
        LocalDate fromLocalDate = LocalDate.of(fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH) + 1, fromCalendar.get(Calendar.DATE));
        LocalDate toLocalDate = LocalDate.of(toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH) + 1, toCalendar.get(Calendar.DATE));
        processor.execute(
                () -> rentCarService.getRentHistoryStatistics(fromLocalDate, toLocalDate),
                rentHistoryStatisticsText -> {
                    textAreaForRentStatistics.setText(rentHistoryStatisticsText);
                    searchButton.setEnabled(true);
                },
                e -> {
                    searchButton.setEnabled(true);
                    JOptionPane.showMessageDialog(parentComponent, rentStatisticLoadFailureMsg,
                            errorDialogTitle, JOptionPane.ERROR_MESSAGE);
                });
    }

}

