package org.example.component;

import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel {
    private Class[] types;
    private boolean[] canEdit;

    public TableModel() {
        super(new String[][]{}, new String[]{"N", "Archivo", "Estado de clave"});
        types = new Class[]{java.lang.Integer.class, java.lang.String.class, java.lang.String.class};
        canEdit = new boolean[]{false, false, false};
    }

    ;

    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit[columnIndex];
    }
}