package com.epam.client.gui.swing.tabs.customer;

import com.epam.common.dto.CustomerDto;
import com.epam.client.gui.swing.async.Processor;
import com.epam.client.gui.swing.table.model.Column;
import com.epam.client.gui.swing.table.model.ColumnImpl;
import com.epam.client.gui.swing.table.model.TableModel;
import com.epam.client.gui.swing.table.model.TableRow;
import com.epam.common.service.CustomerService;
import com.epam.client.util.SwingUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
@org.springframework.stereotype.Component
@Scope("prototype")
public class CustomerTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(CustomerTabPanel.class);

    private Type customerTableRowType = new TypeToken<List<CustomerTableRow>>() {
    }.getType();
    private JToolBar toolBar;
    private JPanel bottomPanel;
    private JTable table;
    private TableModel<CustomerTableRow> tableModel;
    private JButton addButton, saveButton, cancelButton, refreshButton;
    private Processor processor;
    private CustomerService customerService;
    private ModelMapper modelMapper;

    @Resource(name = "mainWindow")
    private Component parentComponent;
    @Value("${customer.save.mgs.success}")
    private String customerSaveSuccessMsg;
    @Value("${customer.save.mgs.failure}")
    private String customerSaveFailureMsg;
    @Value("${customer.all.load}")
    private String customersLoadFailureMsg;
    @Value("${testconnection.errordialog.title}")
    private String errorDialogTitle;

    @Autowired
    public CustomerTabPanel(Processor processor, CustomerService customerService, ModelMapper modelMapper) {
        this.processor = processor;
        this.customerService = customerService;
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
        columns.add(new ColumnImpl("Email", String.class));

        tableModel = new TableModel(new ArrayList<>(), columns, CustomerTableRow::new);
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

        loadCustomerData();

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
        refreshButton.addActionListener(e -> loadCustomerData());
        return refreshButton;
    }

    public void loadCustomerData() {
        setButtonStatesOnRefresh(false);
        processor.execute(customerService::findAll, customerDtoList -> {
            tableModel.setData(modelMapper.map(customerDtoList, customerTableRowType));
            setButtonStatesOnRefresh(true);
        }, e -> {
            setButtonStatesOnRefresh(true);
            JOptionPane.showMessageDialog(parentComponent, customersLoadFailureMsg,
                    errorDialogTitle, JOptionPane.ERROR_MESSAGE);
        });
    }

    public CustomerDto getSelectedCustomer() {
        CustomerDto newCustomerDto = null;
        if (table.getSelectedRow() != -1) {
            newCustomerDto = new CustomerDto();
            modelMapper.map(tableModel.getDataAt(table.getSelectedRow()), newCustomerDto);
        }
        return newCustomerDto;
    }

    private Component getSaveButton() {
        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> {
            stopCellEditing();
            setButtonStatesOnSave(false);
            //service call
            processor.execute(() -> {
                        CustomerDto newCustomerDto = new CustomerDto();
                        modelMapper.map(tableModel.getDataAt(tableModel.getRowCount() - 1), newCustomerDto);
                        customerService.add(newCustomerDto);
                        return newCustomerDto;
                    },
                    customerDto -> {
                        tableModel.setRowEditable(tableModel.getRowCount() - 1, false);
                        setButtonStatesOnSave(true);
                        flipButtonStates();
                        JOptionPane.showMessageDialog(parentComponent, customerSaveSuccessMsg, "",
                                JOptionPane.INFORMATION_MESSAGE);
                    },
                    exceptionConsumer -> {
                        setButtonStatesOnSave(true);
                        JOptionPane.showMessageDialog(parentComponent, customerSaveFailureMsg,
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

    public void setBottomPanelVisible(boolean bottomPanelVisible) {
        bottomPanel.setVisible(bottomPanelVisible);
    }

    public void setToolbarVisible(boolean toolbarVisible) {
        toolBar.setVisible(toolbarVisible);
    }

    static class CustomerTableRow extends CustomerDto implements TableRow {

        boolean isEditable[];
        Supplier[] getterSupplier;
        Consumer[] setterConsumer;

        {
            getterSupplier = new Supplier[]{this::getName, this::getEmail};
            setterConsumer = new Consumer[]{(object) -> this.setName((String) object), (object) -> this.setEmail((String) object)};
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
