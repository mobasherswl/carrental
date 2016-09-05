package com.epam.client.gui.swing.tabs.rentcar;

import com.epam.client.gui.swing.async.Processor;
import com.epam.client.gui.swing.table.model.ColumnImpl;
import com.epam.client.gui.swing.table.model.TableModel;
import com.epam.client.gui.swing.table.model.TableRow;
import com.epam.client.gui.swing.tabs.customer.CustomerTabPanel;
import com.epam.client.util.SwingUtil;
import com.epam.common.dto.CarDto;
import com.epam.common.dto.CustomerDto;
import com.epam.common.dto.RentCarDto;
import com.epam.common.dto.RentalClassDto;
import com.epam.common.service.RentCarService;
import com.epam.common.service.RentalClassService;
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
import java.awt.event.ActionEvent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
@org.springframework.stereotype.Component
public class RentCarTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(RentCarTabPanel.class);

    private Type carTableRowType = new TypeToken<java.util.List<CarTableRow>>() {
    }.getType();
    private JButton rentButton, refreshButton;
    private Processor processor;
    private RentCarService rentCarService;
    private ModelMapper modelMapper;
    private CustomerTabPanel customerTabPanel;
    private RentalClassService rentalClassService;
    private java.util.List<CarTableRow> cachedCarDtoList;

    @Resource(name = "mainWindow")
    private Component parentComponent;
    @Value("${rentcar.save.mgs.success}")
    private String rentCarSuccessMsg;
    @Value("${rentcar.save.mgs.failure}")
    private String rentOutFailureMsg;
    @Value("${fleet.all.load}")
    private String fleetLoadFailureMsg;
    @Value("${testconnection.errordialog.title}")
    private String errorDialogTitle;
    @Value("${rentalclass.all.load}")
    private String rentalClassLoadingFailedMsg;

    @Autowired
    public RentCarTabPanel(Processor processor, RentCarService rentCarService, ModelMapper modelMapper, CustomerTabPanel customerTabPanel, RentalClassService rentalClassService) {
        this.processor = processor;
        this.rentCarService = rentCarService;
        this.modelMapper = modelMapper;
        this.customerTabPanel = customerTabPanel;
        this.rentalClassService = rentalClassService;
    }

    @PostConstruct
    public void postConstruct() {
        SwingUtil.loadLookAndFeel(logger);

        if (parentComponent == null) {
            parentComponent = this;
        }

        init();
    }

    private void init() {
        TableModel<CarTableRow> tableModel = getTableModel();
        JTable table = getTable(tableModel);
        JComboBox<RentalClassDto> rentalClassDtoJComboBox = getRentalClassComboBox(tableModel);
        setLayout(new BorderLayout());
        add(getToolBar(table, tableModel, rentalClassDtoJComboBox), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadCarData(rentalClassDtoJComboBox);
    }

    private TableModel<CarTableRow> getTableModel() {
        return new TableModel(new ArrayList<>(),
                Arrays.asList(new ColumnImpl("Model", String.class),
                        new ColumnImpl("Registration", String.class))
                , CarTableRow::new);
    }

    private JTable getTable(TableModel<CarTableRow> tableModel) {
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        return table;
    }

    private JToolBar getToolBar(JTable table, TableModel tableModel, JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        JToolBar toolBar = new JToolBar();
        toolBar.add(getRentButton(table, tableModel, rentalClassDtoJComboBox));
        toolBar.add(getRefreshButton(rentalClassDtoJComboBox));
        toolBar.addSeparator();
        toolBar.add(new Label("Filter By", Label.RIGHT));
        toolBar.add(rentalClassDtoJComboBox);
        toolBar.setFloatable(false);
        return toolBar;
    }

    private JComboBox<RentalClassDto> getRentalClassComboBox(TableModel<CarTableRow> tableModel) {
        RentalClassDto rentalClassDto = new RentalClassDto();
        rentalClassDto.setId(-1L);
        rentalClassDto.setName("All");

        JComboBox<RentalClassDto> rentalClassDtoJComboBox = new JComboBox<>();
        rentalClassDtoJComboBox.addItem(rentalClassDto);
        rentalClassDtoJComboBox.addActionListener(actionEvent -> {
            rentalClassComboBoxActionListenerHandler(tableModel, rentalClassDtoJComboBox);
        });
        updateRentalClassComboBoxRenderer(rentalClassDtoJComboBox);
        return rentalClassDtoJComboBox;
    }

    private void rentalClassComboBoxActionListenerHandler(TableModel<CarTableRow> tableModel, JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        RentalClassDto selectedRentalClassDto = ((RentalClassDto) rentalClassDtoJComboBox.getSelectedItem());
        if (selectedRentalClassDto != null) {
            tableModel.setData(
                    cachedCarDtoList.parallelStream()
                            .filter(
                                    carTableRow -> carTableRow.getRentalClassDto().getId()
                                            .equals(selectedRentalClassDto.getId())
                                            || selectedRentalClassDto.getId() == -1)
                            .collect(Collectors.toList()));
        }
    }

    private void updateRentalClassComboBoxRenderer(JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        ListCellRenderer<? super RentalClassDto> defaultRentalClassDtoListCellRenderer = rentalClassDtoJComboBox.getRenderer();
        rentalClassDtoJComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) defaultRentalClassDtoListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                label.setText(value.getName());
            }
            return label;
        });
    }

    private Component getRentButton(JTable table, TableModel tableModel, JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        rentButton = new JButton("Rent");
        rentButton.addActionListener((e -> {
            if (table.getSelectedRow() != -1) {
                handleCustomerSelection(e, table, tableModel, rentalClassDtoJComboBox);
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Please select a car");
            }
        }));
        return rentButton;
    }

    private void handleCustomerSelection(ActionEvent e, JTable table, TableModel tableModel, JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        showCustomerPanelComponents(false);
        customerTabPanel.loadCustomerData();
        int selection = JOptionPane.showConfirmDialog(parentComponent, customerTabPanel, "Select Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        showCustomerPanelComponents(true);
        if (selection == JOptionPane.OK_OPTION) {
            CustomerDto customerDto = customerTabPanel.getSelectedCustomer();
            if (customerDto == null) {
                JOptionPane.showMessageDialog(parentComponent, "No customer selected");
            } else {
                rentOutCar(customerDto, table, tableModel, rentalClassDtoJComboBox);
            }
        }
    }

    private void rentOutCar(CustomerDto customerDto, JTable table, TableModel tableModel, JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        CarDto carDto = new CarDto();
        modelMapper.map(tableModel.getDataAt(table.getSelectedRow()), carDto);
        RentCarDto rentCarDto = new RentCarDto();
        rentCarDto.setCustomerDto(customerDto);
        rentCarDto.setCarDto(carDto);
        processor.execute(() -> rentCarService.rentOut(rentCarDto), rentCarDto1 -> {
                    loadCarData(rentalClassDtoJComboBox);
                }, ex -> {
                    JOptionPane.showMessageDialog(parentComponent, rentOutFailureMsg,
                            errorDialogTitle, JOptionPane.ERROR_MESSAGE);
                    loadCarData(rentalClassDtoJComboBox);
                }
        );
    }

    private void showCustomerPanelComponents(boolean bottomPanelVisible) {
        customerTabPanel.setBottomPanelVisible(bottomPanelVisible);
        customerTabPanel.setToolbarVisible(bottomPanelVisible);
    }

    private Component getRefreshButton(JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadCarData(rentalClassDtoJComboBox));
        return refreshButton;
    }

    private void loadCarData(JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        setButtonStatesOnRefresh(false);
        processor.execute(rentCarService::findAvailableCars, carDtoList -> {
            updateRentalClassComboBox(rentalClassDtoJComboBox);
            cachedCarDtoList = modelMapper.map(carDtoList, carTableRowType);
            setButtonStatesOnRefresh(true);
        }, e -> {
            setButtonStatesOnRefresh(true);
            JOptionPane.showMessageDialog(parentComponent, fleetLoadFailureMsg,
                    errorDialogTitle, JOptionPane.ERROR_MESSAGE);
        });
    }

    private void updateRentalClassComboBox(JComboBox<RentalClassDto> rentalClassDtoJComboBox) {
        processor.execute(rentalClassService::findAll,
                rentalClassDtoList -> {
                    RentalClassDto rentalClassDto = rentalClassDtoJComboBox.getItemAt(0);
                    RentalClassDto selectedRentalClassDto = (RentalClassDto) rentalClassDtoJComboBox.getSelectedItem();
                    rentalClassDtoJComboBox.removeAllItems();
                    rentalClassDtoJComboBox.addItem(rentalClassDto);
                    rentalClassDtoList.forEach(rentalClassDtoJComboBox::addItem);
                    rentalClassDtoJComboBox.setSelectedItem(selectedRentalClassDto);
                },
                e -> JOptionPane.showMessageDialog(parentComponent, rentalClassLoadingFailedMsg,
                        errorDialogTitle, JOptionPane.ERROR_MESSAGE));
    }

    private void setButtonStatesOnRefresh(boolean isEnabled) {
        rentButton.setEnabled(isEnabled);
        refreshButton.setEnabled(isEnabled);
    }

    static class CarTableRow extends CarDto implements TableRow {

        boolean isEditable[];
        Supplier[] getterSupplier;
        Consumer[] setterConsumer;

        {
            getterSupplier = new Supplier[]{this::getModel, this::getRegistration};
            setterConsumer = new Consumer[]{(object) -> this.setModel((String) object), (object) -> this.setRegistration((String) object)};
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
