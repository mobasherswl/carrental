package com.epam.client.gui.swing.table.model;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Ahmed_Khan on 5/12/2016.
 */
public interface TableRow {

    boolean[] getEditable();

    Supplier[] getGetterSuppliers();

    Consumer[] getSetterConsumers();

    default void setEditable(int columnIndex, boolean isEditable) {
        getEditable()[columnIndex] = isEditable;
    }

    default Object getValueAt(int columnIndex) {
        return getGetterSuppliers()[columnIndex].get();
    }

    default void setValueAt(int columnIndex, Object value) {
        getSetterConsumers()[columnIndex].accept(value);
    }

}
