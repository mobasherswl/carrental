package com.epam.client.gui.swing.table.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Ahmed_Khan on 5/5/2016.
 */
public class TableModel<T extends TableRow> extends AbstractTableModel {

    private Supplier<T> newTObjectSupplier;
    private List<T> rows;
    private List<Column> columns;

    public TableModel(List<T> list, List<Column> columns, Supplier<T> newTObjectSupplier) {
        this.columns = columns;
        this.newTObjectSupplier = newTObjectSupplier;
        rows = new ArrayList<>(list);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).getValueAt(columnIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        rows.get(rowIndex).setValueAt(columnIndex, value);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).getEditable()[columnIndex];
    }

    public void setRowEditable(int rowIndex, boolean isEditable) {
        T row = rows.get(rowIndex);
        for (int i = 0; i < columns.size(); i++) {
            row.setEditable(i, isEditable);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns.get(columnIndex).getColumnClass();
    }

    public void addRow() {
        rows.add(newTObjectSupplier.get());
        int row = rows.size() - 1;
        setRowEditable(row, true);
        fireTableRowsInserted(row, row);
    }

    public void removeRow() {
        removeRow(rows.size() - 1);
    }

    public void removeRow(int rowIndex) {
        rows.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public T getDataAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    public void setData(List<T> list) {
        rows = new ArrayList<>(list);
        fireTableDataChanged();
    }

}
