package clinic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PatientForm extends JFrame {

    static Connection con;
    private static JFrame currentFrame;

    public static void main(String[] args) {
        // اتصال بالداتا بيز
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic","root","");
            JOptionPane.showMessageDialog(null, "Connection Success");
        } catch(Exception e) {
            e.printStackTrace();
        }

        // GUI
        JFrame CRUD = new JFrame("Patient CRUD Form");
        currentFrame = CRUD;  // نحفظ المرجع عشان نرجعله بعدين
        CRUD.setResizable(true);
        CRUD.setBounds(100, 100, 600, 800);

        JLabel lblName = new JLabel("Patient Name:");
        lblName.setBounds(50,50,120,30);
        CRUD.add(lblName);

        JTextField txtName = new JTextField();
        txtName.setBounds(180,50,150,30);
        CRUD.add(txtName);

        JLabel lblDOB = new JLabel("Date of Birth (YYYY-MM-DD):");
        lblDOB.setBounds(50,100,180,30);
        CRUD.add(lblDOB);

        JTextField txtDOB = new JTextField();
        txtDOB.setBounds(230,100,100,30);
        CRUD.add(txtDOB);

        JLabel lblGender = new JLabel("Gender:");
        lblGender.setBounds(50,150,100,30);
        CRUD.add(lblGender);

        JTextField txtGender = new JTextField();
        txtGender.setBounds(180,150,100,30);
        CRUD.add(txtGender);

        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setBounds(50,200,120,30);
        CRUD.add(lblPhone);

        JTextField txtPhone = new JTextField();
        txtPhone.setBounds(180,200,150,30);
        CRUD.add(txtPhone);

        JLabel lblHistory = new JLabel("History:");
        lblHistory.setBounds(50,250,100,30);
        CRUD.add(lblHistory);

        JTextField txtHistory = new JTextField();
        txtHistory.setBounds(180,250,150,30);
        CRUD.add(txtHistory);

        JLabel lblSearch = new JLabel("Search by ID:");
        lblSearch.setBounds(50,300,100,30);
        CRUD.add(lblSearch);

        JTextField txtSearch = new JTextField();
        txtSearch.setBounds(180,300,150,30);
        CRUD.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(390,300,100,30);
        CRUD.add(btnSearch);

        // الجدول
        String[] columns = {"PatientID","PatientName","DOB","Gender","Phone","History"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50,400,500,200);
        CRUD.add(scroll);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(50,620,80,30);
        CRUD.add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(140,620,80,30);
        CRUD.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(230,620,80,30);
        CRUD.add(btnDelete);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(320,620,80,30);
        CRUD.add(btnClear);

        JButton btnClose = new JButton("Close");
        btnClose.setBounds(440,620,100,30);
        CRUD.add(btnClose);

        JButton btnLoad = new JButton("Load Data");
        btnLoad.setBounds(420,350,130,40);
        CRUD.add(btnLoad);

        JButton btnNext = new JButton("Next");
        btnNext.setBounds(420,50,130,40);
        CRUD.add(btnNext);

        // Actions
        btnAdd.addActionListener(e -> {
            try (PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO patient(PatientName,DateOfBirth,Gender,PhoneNumber,History) VALUES(?,?,?,?,?)")) {
                pst.setString(1, txtName.getText());
                pst.setString(2, txtDOB.getText());
                pst.setString(3, txtGender.getText());
                pst.setString(4, txtPhone.getText());
                pst.setString(5, txtHistory.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null,"Patient Added");
                txtName.setText(""); txtDOB.setText(""); txtGender.setText(""); txtPhone.setText(""); txtHistory.setText("");
                btnLoad.doClick();
            } catch(SQLException ex){ ex.printStackTrace(); }
        });

        btnLoad.addActionListener(e -> {
            try {
                model.setRowCount(0);
                try (PreparedStatement pst = con.prepareStatement("SELECT * FROM patient");
                     ResultSet rs = pst.executeQuery()) {
                    while(rs.next()){
                        model.addRow(new Object[]{
                                rs.getInt("PatientID"),
                                rs.getString("PatientName"),
                                rs.getString("DateOfBirth"),
                                rs.getString("Gender"),
                                rs.getString("PhoneNumber"),
                                rs.getString("History")
                        });
                    }
                }
            } catch(SQLException ex){ ex.printStackTrace(); }
        });

        btnSearch.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtSearch.getText());
                try (PreparedStatement pst = con.prepareStatement("SELECT * FROM patient WHERE PatientID=?")) {
                    pst.setInt(1,id);
                    try (ResultSet rs = pst.executeQuery()) {
                        if(rs.next()){
                            txtName.setText(rs.getString("PatientName"));
                            txtDOB.setText(rs.getString("DateOfBirth"));
                            txtGender.setText(rs.getString("Gender"));
                            txtPhone.setText(rs.getString("PhoneNumber"));
                            txtHistory.setText(rs.getString("History"));
                            model.setRowCount(0);
                            model.addRow(new Object[]{
                                    rs.getInt("PatientID"),
                                    rs.getString("PatientName"),
                                    rs.getString("DateOfBirth"),
                                    rs.getString("Gender"),
                                    rs.getString("PhoneNumber"),
                                    rs.getString("History")
                            });
                        } else {
                            JOptionPane.showMessageDialog(null,"Patient not found");
                        }
                    }
                }
            } catch(Exception ex){ JOptionPane.showMessageDialog(null,"Invalid ID"); }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1){
                int id = (int)model.getValueAt(row,0);
                try (PreparedStatement pst = con.prepareStatement(
                        "UPDATE patient SET PatientName=?,DateOfBirth=?,Gender=?,PhoneNumber=?,History=? WHERE PatientID=?")) {
                    pst.setString(1, txtName.getText());
                    pst.setString(2, txtDOB.getText());
                    pst.setString(3, txtGender.getText());
                    pst.setString(4, txtPhone.getText());
                    pst.setString(5, txtHistory.getText());
                    pst.setInt(6,id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Patient Updated");
                    btnLoad.doClick();
                    txtName.setText(""); txtDOB.setText(""); txtGender.setText(""); txtPhone.setText(""); txtHistory.setText(""); txtSearch.setText("");
                } catch(SQLException ex){ ex.printStackTrace(); }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1){
                int id = (int)model.getValueAt(row,0);
                try (PreparedStatement pst = con.prepareStatement("DELETE FROM patient WHERE PatientID=?")) {
                    pst.setInt(1,id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Patient Deleted");
                    btnLoad.doClick();
                    txtName.setText(""); txtDOB.setText(""); txtGender.setText(""); txtPhone.setText(""); txtHistory.setText(""); txtSearch.setText("");
                } catch(SQLException ex){ ex.printStackTrace(); }
            }
        });

        btnClear.addActionListener(e -> {
            txtName.setText(""); txtDOB.setText(""); txtGender.setText(""); txtPhone.setText(""); txtHistory.setText(""); txtSearch.setText("");
        });

        btnClose.addActionListener(e -> System.exit(0));

        // Next يفتح DoctorForm مع تمرير نفس الاتصال
        btnNext.addActionListener(e -> {
            CRUD.setVisible(false);
            DoctorForm.showDoctorForm(currentFrame, con);
        });

        CRUD.setLayout(null);
        CRUD.setVisible(true);
        CRUD.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
