/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import meetingmanager.control.EmployeeControl;
import meetingmanager.entity.Employee;
import static meetingmanager.userinterface.UIUtils.*;

/**
 *
 * @author Matthew
 */
public class AddMeetingPage extends javax.swing.JPanel {

    public int LOGIN_ID = 0;
    public String DATABASE_ERROR_MESSAGE = "Something went terribly wrong with the database. Whoops.";
    
    protected EmployeePage parent;
    protected Employee owner;
    private Map<String, Employee> employeeMap;

    public AddMeetingPage(Employee owner, EmployeePage parent) {
        initComponents();
        clearTable(jTable1);
        clearTable(jTable2);
        this.owner = owner;
        this.parent = parent;
        loadEmployees();
    }
    
    public final void hideTextFields() {
        jTextField1.setVisible(false);
        jTextField2.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        
    }
    
    public AddMeetingPage setParent(EmployeePage parent) {
        this.parent = parent;
        return this;
    }
    
    protected void addToSelected(Employee employee) {
        addRow(jTable2, vectorizeEmployee(employee));
    }
    
    protected void addToBench(Employee employee) {
        addRow(jTable1, vectorizeEmployee(employee));
    }
    
    protected void clearBench() {
        clearTable(jTable1);
    }
    
    protected void clearSelected() {
        clearTable(jTable2);
    }
    
    public String getTitle() {
        return jTextField1.getText();
    }
    
    public Employee getOwner() {
        return owner;
    }
    
    protected void setDuration(long millis) {
        Double hours = (double) millis / (double) 3600000;
        jTextField2.setText(hours.toString());
    }
    
    protected void setTitle(String title) {
        jTextField1.setText(title);
    }
    
    public double getDuration() {
        try {
            return Double.parseDouble(jTextField2.getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void loadEmployees() {
        try {
            List<Employee> employees = EmployeeControl.getAllEmployees();
            employeeMap = new HashMap<>();
            employees.remove(owner);
            
            for(Employee employee : employees) {
                employeeMap.put(employee.getLoginId(), employee);
                Object[] row = vectorizeEmployee(employee);
                addRow(jTable1, row);
            }
        } catch (SQLException e) {
            showMessage(DATABASE_ERROR_MESSAGE);
        }
    }
    
    private Object[] vectorizeEmployee(Employee employee) {
        return new Object[] { employee.getLoginId() };
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(602, 356));

        jLabel1.setText("Create Meeting");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Select Employees"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Add ->");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Selected"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jButton2.setText("<- Remove");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("OK");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Enter meeting title");

        jLabel3.setText("Enter duration");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 15, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addGap(9, 9, 9)))
                                .addGap(18, 18, 18)))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(88, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel1)
                        .addGap(50, 50, 50)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jButton3)
                    .addComponent(jLabel3))
                .addContainerGap(37, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // AFTER USERS ARE SELECTED, MOVE TO NEXT WINDOW TO SELECT ROOM
        moveToNextWindow();
    }//GEN-LAST:event_jButton3ActionPerformed

    public Employee[] getSelectedEmployees() {
        String[] selectedIds = getSelectedEmployeeIds();
        Employee[] selected = new Employee[selectedIds.length];
        
        for(int i = 0; i < selectedIds.length; i++) {
            selected[i] = employeeMap.get(selectedIds[i]);
        }
        
        return selected;
    }
    protected void moveToNextWindow() {
        if (noDuration() || noTitle()) {
            return;
        }
        String[] empArr =  getSelectedEmployeeIds();
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(AddMeetingPage.this);
        //topFrame.add(new AddMeetingPage2(empArr, this).setGrandParent(parent));
        try {
            topFrame.add(new SelectTimePage(this));
            this.setVisible(false);
        } catch (SQLException e) {
            showMessage("SQL error while loading times");
        }
    }
    
    public int getHeadCount() {
        return jTable2.getRowCount();
    }
    
    private boolean noTitle() {
        if(jTextField1.getText().isEmpty()) {
            showMessage("Please enter a title for the meeting before continuing");
            return true;            
        }
        return false;
    }
    
    private boolean noDuration() {
        if(jTextField2.getText().isEmpty()) {            
            showMessage("Please enter a duration for the meeting in hours.");
            return true;
        }
        
        try {
            Double.valueOf(jTextField2.getText());
            return false;
        } catch (NumberFormatException e) {
            showMessage("Duration must be a valid number.");
            return true;
        }
    }
    
    protected JFrame getMainWindow() {
        return (JFrame) SwingUtilities.getWindowAncestor(AddMeetingPage.this);
    }
    
    protected String[] getSelectedEmployeeIds() {
        int rowCount = jTable2.getRowCount();
        String[] empArr = new String[rowCount];
        
        for(int i = 0; i < rowCount; i++){
            String empS = (String)jTable2.getValueAt(i, LOGIN_ID);
            empArr[i] = empS;
        }
        
        return empArr;
    }
    
    public void refreshMainPage() {
        parent.refreshMeetings();
        parent.refreshSchedule();
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // ADD USER TO SELECTED LIST
        int rowSelected = jTable1.getSelectedRow();
        if(rowSelected < 0) {
            showMessage("Please select a user first");
            return;
        }
                
        String loginId = (String) jTable1.getValueAt(rowSelected, LOGIN_ID);
            
        addRow(jTable2, new Object[] { loginId } );
        deleteRow(jTable1, rowSelected);
                
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // REMOVE SELECTED EMPLOYEES FROM LIST
        int rowSelected = jTable2.getSelectedRow();
        if(rowSelected < 0) {
            showMessage("Please select a user first");
            return;
        }
        String loginId = (String) jTable2.getValueAt(rowSelected, LOGIN_ID);
            
        addRow(jTable1, new Object[] { loginId } );
        deleteRow(jTable2, rowSelected);
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
