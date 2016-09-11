package com.epam.client.gui.swing.tabs.rentcar;

import com.epam.common.dto.RentCarDto;
import com.epam.client.gui.swing.async.Processor;
import com.epam.client.gui.swing.table.model.Column;
import com.epam.client.gui.swing.table.model.ColumnImpl;
import com.epam.client.gui.swing.table.model.TableModel;
import com.epam.client.gui.swing.table.model.TableRow;
import com.epam.common.service.RentCarService;
import com.epam.client.util.SwingUtil;
import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.impl.JDatePickerImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
@org.springframework.stereotype.Component
public class RentalHistoryTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(RentalHistoryTabPanel.class);

    private Type carTableRowType = new TypeToken<java.util.List<RentCarTableRow>>() {
    }.getType();
    //    private JToolBar toolBar;
    private JTable table;
    private TableModel<RentCarTableRow> tableModel;
    private JDatePickerImpl jDatePickerFrom;
    private JDatePickerImpl jDatePickerTo;
    private JButton searchButton;
    private Processor processor;
    private RentCarService rentCarService;
    private ModelMapper modelMapper;

    @Resource(name = "mainWindow")
    private Component parentComponent;
    @Value("${fleet.save.mgs.success}")
    private String fleetSaveSuccessMsg;
    @Value("${fleet.save.mgs.failure}")
    private String fleetSaveFailureMsg;
    @Value("${fleet.all.load}")
    private String fleetLoadFailureMsg;
    @Value("${testconnection.errordialog.title}")
    private String errorDialogTitle;

    @Autowired
    public RentalHistoryTabPanel(Processor processor, RentCarService rentCarService, ModelMapper modelMapper) {
        this.processor = processor;
        this.rentCarService = rentCarService;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        SwingUtil.loadLookAndFeel(logger);
        List<Column> columns = new ArrayList<>();

        columns.add(new ColumnImpl("Registration", String.class));
        columns.add(new ColumnImpl("Name", String.class));
        columns.add(new ColumnImpl("Email", String.class));
        columns.add(new ColumnImpl("Rented On", LocalDateTime.class));
        columns.add(new ColumnImpl("Returned On", LocalDateTime.class));

        tableModel = new TableModel(new ArrayList<>(), columns, RentCarTableRow::new);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        JLabel fromDateLabel = new JLabel("From");
        jDatePickerFrom = (JDatePickerImpl) new JDateComponentFactory().createJDatePicker();
        JLabel toDateLabel = new JLabel("To");
        jDatePickerTo = (JDatePickerImpl) new JDateComponentFactory().createJDatePicker();
        BorderLayout borderLayout = new BorderLayout();
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(fromDateLabel);
        searchPanel.add(jDatePickerFrom);
        searchPanel.add(toDateLabel);
        searchPanel.add(jDatePickerTo);
        searchPanel.add(getSearchButton());
        setLayout(borderLayout);
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }


    private Component getSearchButton() {
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
        processor.execute(() -> rentCarService.getRentHistoryDto(fromLocalDate, toLocalDate), carDtoList -> {
            tableModel.setData(modelMapper.map(carDtoList, carTableRowType));
            searchButton.setEnabled(true);
        }, e -> {
            searchButton.setEnabled(true);
            JOptionPane.showMessageDialog(parentComponent, fleetLoadFailureMsg,
                    errorDialogTitle, JOptionPane.ERROR_MESSAGE);
        });
    }

    static class RentCarTableRow extends RentCarDto implements TableRow {

        boolean isEditable[];
        Supplier[] getterSupplier;
        Consumer[] setterConsumer;

        {
            getterSupplier = new Supplier[]{() -> getCarDto().getRegistration(), () -> getCustomerDto().getName(), () -> getCustomerDto().getEmail(), this::getStartDateTime, this::getEndDateTime};
            setterConsumer = new Consumer[]{(o) -> {
            }, o -> {
            }, o -> {
            }, o -> {
            }, o -> {
            }};
            isEditable = new boolean[getterSupplier.length];
        }

        @Override
        public boolean[] getEditable() {
            return isEditable;
        }

        @Override
        public Supplier[] getGetterSuppliers() {
            return getterSupplier;
        }

        @Override
        public Consumer[] getSetterConsumers() {
            return setterConsumer;
        }

    }

}
