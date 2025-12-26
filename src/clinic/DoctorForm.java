package clinic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DoctorForm {

    public static void showDoctorForm(JFrame previousFrame, Connection con) {

        JFrame doctorFrame = new JFrame("Doctor CRUD Form");  // ممكن تغير العنوان لـ "Course CRUD Form" لو عايز
        doctorFrame.setResizable(true);
        doctorFrame.setBounds(100, 100, 600, 800);

        JLabel lblName = new JLabel("Doctor Name:");
        lblName.setBounds(50,50,120,30);
        doctorFrame.add(lblName);

        JTextField txtName = new JTextField();
        txtName.setBounds(180,50,150,30);
        doctorFrame.add(txtName);

        JLabel lblSpecialization = new JLabel("Specialization:");
        lblSpecialization.setBounds(50,100,120,30);
        doctorFrame.add(lblSpecialization);

        JTextField txtSpecialization = new JTextField();
        txtSpecialization.setBounds(180,100,150,30);
        doctorFrame.add(txtSpecialization);

        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setBounds(50,150,120,30);
        doctorFrame.add(lblPhone);

        JTextField txtPhone = new JTextField();
        txtPhone.setBounds(180,150,150,30);
        doctorFrame.add(txtPhone);

        JLabel lblAttendance = new JLabel("Attendance Days:");
        lblAttendance.setBounds(50,200,130,30);
        doctorFrame.add(lblAttendance);

        JTextField txtAttendance = new JTextField();
        txtAttendance.setBounds(180,200,150,30);
        doctorFrame.add(txtAttendance);

        JLabel lblSearch = new JLabel("Search by ID:");
        lblSearch.setBounds(50,300,100,30);
        doctorFrame.add(lblSearch);

        JTextField txtSearch = new JTextField();
        txtSearch.setBounds(180,300,150,30);
        doctorFrame.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(390,300,100,30);
        doctorFrame.add(btnSearch);

        // الجدول
        String[] columns = {"DoctorID", "Name", "Specialization", "PhoneNumber", "AttendanceDays"};
        DefaultTableModel model = new DefaultTableModel(columns,0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50,400,500,200);
        doctorFrame.add(scroll);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(50,620,80,30);
        doctorFrame.add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(140,620,80,30);
        doctorFrame.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(230,620,80,30);
        doctorFrame.add(btnDelete);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(320,620,80,30);
        doctorFrame.add(btnClear);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(440,620,100,30);
        doctorFrame.add(btnBack);

        JButton btnLoad = new JButton("Load Data");
        btnLoad.setBounds(420,350,130,40);
        doctorFrame.add(btnLoad);

        // التعديل الرئيسي: غيرنا كلمة "doctor" إلى "course" في كل الـ Queries
        btnAdd.addActionListener(e -> {
            try {
                PreparedStatement pst = con.prepareStatement("INSERT INTO course(Name,Specialization,PhoneNumber,AttendanceDays) VALUES(?,?,?,?)");
                pst.setString(1, txtName.getText());
                pst.setString(2, txtSpecialization.getText());
                pst.setString(3, txtPhone.getText());
                pst.setString(4, txtAttendance.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null,"Doctor Added");
                txtName.setText(""); txtSpecialization.setText(""); txtPhone.setText(""); txtAttendance.setText("");
                btnLoad.doClick();
            } catch(SQLException ex){
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnLoad.addActionListener(e -> {
            try{
                model.setRowCount(0);
                PreparedStatement pst = con.prepareStatement("SELECT * FROM course");
                ResultSet rs = pst.executeQuery();
                while(rs.next()){
                    model.addRow(new Object[]{
                            rs.getInt("DoctorID"),
                            rs.getString("Name"),
                            rs.getString("Specialization"),
                            rs.getString("PhoneNumber"),
                            rs.getString("AttendanceDays")
                    });
                }
            } catch(SQLException ex){
                JOptionPane.showMessageDialog(null, "Error loading data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnSearch.addActionListener(e -> {
            try{
                int id = Integer.parseInt(txtSearch.getText());
                PreparedStatement pst = con.prepareStatement("SELECT * FROM course WHERE DoctorID=?");
                pst.setInt(1,id);
                ResultSet rs = pst.executeQuery();
                if(rs.next()){
                    txtName.setText(rs.getString("Name"));
                    txtSpecialization.setText(rs.getString("Specialization"));
                    txtPhone.setText(rs.getString("PhoneNumber"));
                    txtAttendance.setText(rs.getString("AttendanceDays"));
                    model.setRowCount(0);
                    model.addRow(new Object[]{
                            rs.getInt("DoctorID"),
                            rs.getString("Name"),
                            rs.getString("Specialization"),
                            rs.getString("PhoneNumber"),
                            rs.getString("AttendanceDays")
                    });
                } else {
                    JOptionPane.showMessageDialog(null,"Doctor not found");
                }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(null,"Invalid ID or Error: " + ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1){
                int id = (int)model.getValueAt(row,0);
                try{
                    PreparedStatement pst = con.prepareStatement("UPDATE course SET Name=?,Specialization=?,PhoneNumber=?,AttendanceDays=? WHERE DoctorID=?");
                    pst.setString(1, txtName.getText());
                    pst.setString(2, txtSpecialization.getText());
                    pst.setString(3, txtPhone.getText());
                    pst.setString(4, txtAttendance.getText());
                    pst.setInt(5,id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Doctor Updated");
                    btnLoad.doClick();
                    txtName.setText(""); txtSpecialization.setText(""); txtPhone.setText(""); txtAttendance.setText(""); txtSearch.setText("");
                } catch(SQLException ex){
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to update");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1){
                int id = (int)model.getValueAt(row,0);
                try{
                    PreparedStatement pst = con.prepareStatement("DELETE FROM course WHERE DoctorID=?");
                    pst.setInt(1,id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Doctor Deleted");
                    btnLoad.doClick();
                    txtName.setText(""); txtSpecialization.setText(""); txtPhone.setText(""); txtAttendance.setText(""); txtSearch.setText("");
                } catch(SQLException ex){
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to delete");
            }
        });

        btnClear.addActionListener(e -> {
            txtName.setText(""); txtSpecialization.setText(""); txtPhone.setText(""); txtAttendance.setText(""); txtSearch.setText("");
        });

        btnBack.addActionListener(e -> {
            doctorFrame.dispose();
            previousFrame.setVisible(true);
        });

        doctorFrame.setLayout(null);
        doctorFrame.setVisible(true);
        doctorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}