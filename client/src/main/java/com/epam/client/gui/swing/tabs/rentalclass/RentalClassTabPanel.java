package com.epam.client.gui.swing.tabs.rentalclass;

import com.epam.common.dto.RentalClassDto;
import com.epam.client.gui.swing.async.Processor;
import com.epam.client.gui.swing.table.model.Column;
import com.epam.client.gui.swing.table.model.ColumnImpl;
import com.epam.client.gui.swing.table.model.TableModel;
import com.epam.client.gui.swing.table.model.TableRow;
import com.epam.common.service.RentalClassService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
@org.springframework.stereotype.Component
public class RentalClassTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(RentalClassTabPanel.class);

    private Type rentalClassTableRowType = new TypeToken<java.util.List<RentalClassTableRow>>() {
    }.getType();
    private JToolBar toolBar;
    private JPanel bottomPanel;
    private JTable table;
    private TableModel<RentalClassTableRow> tableModel;
    private JButton addButton, saveButton, cancelButton, refreshButton;
    private Processor processor;
    private RentalClassService rentalClassService;
    private ModelMapper modelMapper;

    @Resource(name = "mainWindow")
    private Component parentComponent;
    @Value("${rentalclass.save.mgs.success}")
    private String rentalClassSaveSuccessMsg;
    @Value("${rentalclass.save.mgs.failure}")
    private String rentalClassSaveFailureMsg;
    @Value("${rentalclass.all.load}")
    private String rentalClassLoadFailureMsg;
    @Value("${testconnection.errordialog.title}")
    private String errorDialogTitle;

    @Autowired
    public RentalClassTabPanel(Processor processor, RentalClassService rentalClassService, ModelMapper modelMapper) {
        this.processor = processor;
        this.rentalClassService = rentalClassService;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void postConstruct() {
        SwingUtil.loadLookAndFeel(logger);

        toolBar = new JToolBar();
        bottomPanel = new JPanel(new FlowLayout());

        bottomPanel.add(getSaveButton());
        bottomPanel.add(getCancelButton());

        java.util.List<Column> columns = new ArrayList<>();

        columns.add(new ColumnImpl("Name", String.class));
        columns.add(new ColumnImpl("Rate", BigDecimal.class));

        tableModel = new TableModel(new ArrayList<>(), columns, RentalClassTableRow::new);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        toolBar.add(getCustomerButton());
        toolBar.add(getRefreshButton());
        toolBar.setFloatable(false);
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        if (parentComponent == null) {
            parentComponent = this;
        }

        loadData();

    }

    private Component getCustomerButton() {
        addButton = new JButton("Add");
        addButton.addActionListener((e -> {
            tableModel.addRow();
            table.changeSelection(table.getRowCount() - 1, 0, false, false);
            flipButtonStates();
        }));
        return addButton;
    }

    private Component getRefreshButton() {
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadData());
        return refreshButton;
    }

    private void loadData() {
        setButtonStatesOnRefresh(false);
        processor.execute(rentalClassService::findAll, rentalClassDtoList -> {
            tableModel.setData(modelMapper.map(rentalClassDtoList, rentalClassTableRowType));
            setButtonStatesOnRefresh(true);
        }, e -> {
            setButtonStatesOnRefresh(true);
            JOptionPane.showMessageDialog(parentComponent, rentalClassLoadFailureMsg,
                    errorDialogTitle, JOptionPane.ERROR_MESSAGE);
        });
    }

    private Component getSaveButton() {
        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> {
            if (!stopCellEditing()) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            } else {
                setButtonStatesOnSave(false);
                RentalClassDto newRentalClassDto = new RentalClassDto();
                modelMapper.map(tableModel.getDataAt(tableModel.getRowCount() - 1), newRentalClassDto);
                //service call
                processor.execute(() -> {
                            rentalClassService.add(newRentalClassDto);
                            return newRentalClassDto;
                        },
                        rentalClassDto -> {
                            tableModel.setRowEditable(tableModel.getRowCount() - 1, false);
                            setButtonStatesOnSave(true);
                            flipButtonStates();
                            JOptionPane.showMessageDialog(parentComponent, rentalClassSaveSuccessMsg, "",
                                    JOptionPane.INFORMATION_MESSAGE);
                        },
                        exception -> {
                            setButtonStatesOnSave(true);
                            JOptionPane.showMessageDialog(parentComponent, rentalClassSaveFailureMsg,
                                    errorDialogTitle, JOptionPane.ERROR_MESSAGE);
                        }
                );
            }
        });
        return saveButton;
    }

    private Component getCancelButton() {
        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> {
            cancelCellEditing();
            tableModel.removeRow();
            flipButtonStates();
        });
        return cancelButton;
    }

    private boolean stopCellEditing() {
        return Optional.ofNullable(table.getCellEditor()).map(optional -> optional.stopCellEditing()).orElse(true);
    }

    private void cancelCellEditing() {
        Optional.ofNullable(table.getCellEditor()).ifPresent(tableCellEditor -> tableCellEditor.cancelCellEditing());
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

    static class RentalClassTableRow extends RentalClassDto implements TableRow {

        boolean isEditable[];
        Supplier[] getterSupplier;
        Consumer[] setterConsumer;

        {
            getterSupplier = new Supplier[]{this::getName, this::getRate};
            isEditable = new boolean[getterSupplier.length];
            setterConsumer = new Consumer[]{(object) -> this.setName((String) object), (object) -> this.setRate((BigDecimal) object)};
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
