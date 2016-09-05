package com.epam.client.gui.swing.table.model;

/**
 * Created by Ahmed_Khan on 5/18/2016.
 */
public class ColumnImpl implements Column {

    private final String name;
    private final Class<?> clazz;

    public ColumnImpl(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getColumnClass() {
        return clazz;
    }
}
