package squidpony.epigon.tool;

/**
 * Panel with the basic data available for any EpiData object.
 *
 * @author Eben Howard
 */
public class EpiDataPanel extends javax.swing.JPanel {

    public EpiDataPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        nameField = new javax.swing.JTextField();
        textEntryPanel = new javax.swing.JPanel();
        tabPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setAlignmentY(0.0F);

        nameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nameField.setText("Data Object Name");
        nameField.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        add(jPanel1, java.awt.BorderLayout.NORTH);

        description.setColumns(20);
        description.setRows(2);
        jScrollPane1.setViewportView(description);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        tabPane.addTab("Description", jPanel2);

        notes.setColumns(20);
        notes.setRows(2);
        jScrollPane2.setViewportView(notes);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        tabPane.addTab("Notes", jPanel3);

        javax.swing.GroupLayout textEntryPanelLayout = new javax.swing.GroupLayout(textEntryPanel);
        textEntryPanel.setLayout(textEntryPanelLayout);
        textEntryPanelLayout.setHorizontalGroup(
            textEntryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
        );
        textEntryPanelLayout.setVerticalGroup(
            textEntryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
        );

        add(textEntryPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea description;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextArea notes;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JPanel textEntryPanel;
    // End of variables declaration//GEN-END:variables
}