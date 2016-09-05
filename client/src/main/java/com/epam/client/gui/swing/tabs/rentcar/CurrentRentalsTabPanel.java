package com.epam.client.gui.swing.tabs.rentcar;

import com.epam.common.dto.RentCarDto;
import com.epam.client.gui.swing.async.Processor;
import com.epam.client.gui.swing.table.model.Column;
import com.epam.client.gui.swing.table.model.ColumnImpl;
import com.epam.client.gui.swing.table.model.TableModel;
import com.epam.client.gui.swing.table.model.TableRow;
import com.epam.common.service.RentCarService;
import com.epam.client.util.SwingUtil;
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
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
@org.springframework.stereotype.Component
public class CurrentRentalsTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(CurrentRentalsTabPanel.class);

    private Type carTableRowType = new TypeToken<java.util.List<RentCarTableRow>>() {
    }.getType();

    private TableModel<RentCarTableRow> tableModel;
    private Processor processor;
    private RentCarService rentCarService;
    private ModelMapper modelMapper;

    private JButton returnCar;

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
    public CurrentRentalsTabPanel(Processor processor, RentCarService rentCarService, ModelMapper modelMapper) {
        this.processor = processor;
        this.rentCarService = rentCarService;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void postConstruct() {
        SwingUtil.loadLookAndFeel(logger);

        JToolBar toolBar = new JToolBar();

        java.util.List<Column> columns = new ArrayList<>();

        columns.add(new ColumnImpl("Registration", String.class));
        columns.add(new ColumnImpl("Name", String.class));
        columns.add(new ColumnImpl("Email", String.class));
        columns.add(new ColumnImpl("Rented On", LocalDateTime.class));

        tableModel = new TableModel(new ArrayList<>(), columns, RentCarTableRow::new);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JButton refreshButton = getRefreshButton();
        toolBar.add(refreshButton);
        toolBar.add(getReturnCarButton(table, refreshButton));
        toolBar.setFloatable(false);
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        if (parentComponent == null) {
            parentComponent = this;
        }

        loadCarData(refreshButton);

    }

    private JButton getRefreshButton() {
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadCarData(refreshButton));
        return refreshButton;
    }

    private Component getReturnCarButton(JTable table, JButton refreshButton) {
        returnCar = new JButton("Return");
        returnCar.addActionListener(e -> {
            int carSelectedIndex = table.getSelectedRow();
            if (carSelectedIndex != -1) {
                RentCarDto rentCarDto = new RentCarDto();
                modelMapper.map(tableModel.getDataAt(carSelectedIndex), rentCarDto);
                processor.execute(() -> rentCarService.rentCarReturn(rentCarDto), t -> this.loadCarData(refreshButton), ex -> {
                    if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("optimistic")) {
                        JOptionPane.showMessageDialog(parentComponent, "Car already returned");
                    } else {
                        JOptionPane.showMessageDialog(parentComponent, "Error occurred. Unable to return car.");
                    }
                    loadCarData(refreshButton);
                });
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Please select a car to return");
            }
        });
        return returnCar;
    }

    private void loadCarData(JButton refreshButton) {
        refreshButton.setEnabled(false);
        processor.execute(rentCarService::findRentedOutCars, carDtoList -> {
            tableModel.setData(modelMapper.map(carDtoList, carTableRowType));
            refreshButton.setEnabled(true);
            returnCar.setEnabled(!carDtoList.isEmpty());
        }, e -> {
            refreshButton.setEnabled(true);
            returnCar.setEnabled(false);
            JOptionPane.showMessageDialog(parentComponent, fleetLoadFailureMsg,
                    errorDialogTitle, JOptionPane.ERROR_MESSAGE);
        });
    }

    static class RentCarTableRow extends RentCarDto implements TableRow {

        boolean isEditable[];
        Supplier[] getterSupplier;
        Consumer[] setterConsumer;

        {
            getterSupplier = new Supplier[]{() -> getCarDto().getRegistration(), () -> getCustomerDto().getName(), () -> getCustomerDto().getEmail(), this::getStartDateTime};
            setterConsumer = new Consumer[]{(o) -> {
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
