package com.epam.client.gui.swing.tabs.fleet;

import com.epam.client.gui.swing.async.Processor;
import com.epam.client.gui.swing.table.model.Column;
import com.epam.client.gui.swing.table.model.ColumnImpl;
import com.epam.client.gui.swing.table.model.TableModel;
import com.epam.client.gui.swing.table.model.TableRow;
import com.epam.client.util.SwingUtil;
import com.epam.common.dto.CarDto;
import com.epam.common.dto.RentalClassDto;
import com.epam.common.service.FleetService;
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
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
@org.springframework.stereotype.Component
public class FleetTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(FleetTabPanel.class);

    private Type carTableRowType = new TypeToken<java.util.List<CarTableRow>>() {
    }.getType();
    private JToolBar toolBar;
    private JPanel bottomPanel;
    private JTable table;
    private TableModel<CarTableRow> tableModel;
    private JButton addButton, saveButton, cancelButton, refreshButton;
    private Processor processor;
    private FleetService fleetService;
    private RentalClassService rentalClassService;
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
    @Value("${rentalclass.all.load}")
    private String rentalClassLoadingFailedMsg;

    @Autowired
    public FleetTabPanel(Processor processor, FleetService fleetService, RentalClassService rentalClassService, ModelMapper modelMapper) {
        this.processor = processor;
        this.fleetService = fleetService;
        this.modelMapper = modelMapper;
        this.rentalClassService = rentalClassService;
    }

    @PostConstruct
    public void postConstruct() {
        SwingUtil.loadLookAndFeel(logger);

        toolBar = new JToolBar();
        bottomPanel = new JPanel(new FlowLayout());

        bottomPanel.add(getSaveButton());
        bottomPanel.add(getCancelButton());

        java.util.List<Column> columns = new ArrayList<>();

        columns.add(new ColumnImpl("Model", String.class));
        columns.add(new ColumnImpl("Registration", String.class));
        columns.add(new ColumnImpl("Rental Class", JComboBox.class));

        tableModel = new TableModel(new ArrayList<>(), columns, CarTableRow::new);
        table = new JTable(tableModel) {
            @Override
            protected void createDefaultRenderers() {
                super.createDefaultRenderers();
                defaultRenderersByColumnClass.put(JComboBox.class, (UIDefaults.LazyValue) t -> new DefaultTableCellRenderer.UIResource() {
                    public void setValue(Object value) {
                        setText((value == null) ? "" : ((RentalClassDto)value).getName());
                    }

                });
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        toolBar.add(initAddButton());
        toolBar.add(initRefreshButton());
        toolBar.setFloatable(false);
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        if (parentComponent == null) {
            parentComponent = this;
        }

        loadFleetData();

    }

    private Component initAddButton() {
        addButton = new JButton("Add");
        addButton.addActionListener((e -> {
            processor.execute(rentalClassService::findAll,
                    rentalClassDtoList -> initRentalClassDtoCellEditor(rentalClassDtoList),
                    ex -> JOptionPane.showMessageDialog(parentComponent, rentalClassLoadingFailedMsg,
                            errorDialogTitle, JOptionPane.ERROR_MESSAGE));
            tableModel.addRow();
            table.changeSelection(table.getRowCount() - 1, 0, false, false);
            flipButtonStates();
        }));
        return addButton;
    }

    private Component initRefreshButton() {
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadFleetData());
        return refreshButton;
    }

    private void loadFleetData() {
        setButtonStatesOnRefresh(false);
        processor.execute(fleetService::findAll, carDtoList -> {
            initRentalClassDtoCellEditor(rentalClassService.findAll());
            tableModel.setData(modelMapper.map(carDtoList, carTableRowType));
            setButtonStatesOnRefresh(true);
        }, e -> {
            setButtonStatesOnRefresh(true);
            JOptionPane.showMessageDialog(parentComponent, fleetLoadFailureMsg,
                    errorDialogTitle, JOptionPane.ERROR_MESSAGE);
        });
    }

    private Component getSaveButton() {
        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> {
            stopCellEditing();
            setButtonStatesOnSave(false);
            //service call
            processor.execute(() -> {
                        CarDto newCardDto = new CarDto();
                        modelMapper.map(tableModel.getDataAt(tableModel.getRowCount() - 1), newCardDto);
                        fleetService.add(newCardDto);
                        return newCardDto;
                    },
                    customerDto -> {
                        tableModel.setRowEditable(tableModel.getRowCount() - 1, false);
                        setButtonStatesOnSave(true);
                        flipButtonStates();
                        JOptionPane.showMessageDialog(parentComponent, fleetSaveSuccessMsg, "",
                                JOptionPane.INFORMATION_MESSAGE);
                    },
                    exceptionConsumer -> {
                        setButtonStatesOnSave(true);
                        JOptionPane.showMessageDialog(parentComponent, fleetSaveFailureMsg,
                                errorDialogTitle, JOptionPane.ERROR_MESSAGE);
                    }
            );
        });
        return saveButton;
    }

    private Component getCancelButton() {
        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> {
            stopCellEditing();
            tableModel.removeRow();
            flipButtonStates();
        });
        return cancelButton;
    }

    private void stopCellEditing() {
        Optional.ofNullable(table.getCellEditor()).map(optional -> optional.stopCellEditing());
    }

    private void flipButtonStates() {
        addButton.setEnabled(!addButton.isEnabled());
        refreshButton.setEnabled(!refreshButton.isEnabled());
        cancelButton.setEnabled(!cancelButton.isEnabled());
        saveButton.setEnabled(!saveButton.isEnabled());
    }

    private void setButtonStatesOnSave(boolean isEnabled) {
        saveButton.setEnabled(isEnabled);
        cancelButton.setEnabled(isEnabled);
    }

    private void setButtonStatesOnRefresh(boolean isEnabled) {
        addButton.setEnabled(isEnabled);
        refreshButton.setEnabled(isEnabled);
    }

    static class CarTableRow extends CarDto implements TableRow {

        boolean isEditable[];
        Supplier[] getterSupplier;
        Consumer[] setterConsumer;

        {
            getterSupplier = new Supplier[]{this::getModel, this::getRegistration, () -> this.getRentalClassDto()};
            setterConsumer = new Consumer[]{(object) -> this.setModel((String) object), (object) -> this.setRegistration((String) object), (object) -> this.setRentalClassDto((RentalClassDto) object)};
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

    private void initRentalClassDtoCellEditor(java.util.List<RentalClassDto> rentalClassDtoList) {
        final JComboBox<RentalClassDto> jComboBox = new JComboBox<>(rentalClassDtoList.toArray(new RentalClassDto[rentalClassDtoList.size()]));
        ListCellRenderer<? super RentalClassDto> defaultRentalClassDtoListCellRenderer = jComboBox.getRenderer();
        jComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) defaultRentalClassDtoListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value!=null) {
                label.setText(value.getName());
            }
            return label;
        });
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(jComboBox));
    }

}
