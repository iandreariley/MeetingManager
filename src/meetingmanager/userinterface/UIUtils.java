/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meetingmanager.userinterface;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class UIUtils {
    
    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
    
    public static void clearTable(JTable table) {
        tableData(table).getDataVector().removeAllElements();
    }
    
    public static void addRow(JTable table, Object[] row) {
        tableData(table).addRow(row);
    }
    
    public static void deleteRow(JTable table, int row) {
        tableData(table).removeRow(row);
    }
    
    private static DefaultTableModel tableData(JTable table) {
        return (DefaultTableModel) table.getModel();
    }
    
    public static int getRowWithValueInColumn(JTable table, String value, int column) {
        for(int i = 0; i < table.getRowCount(); i++) {
            String rowValue = (String) table.getValueAt(i, column);
            if(rowValue.equals(value))
                return i;
        }
        return -1;
    }
}
