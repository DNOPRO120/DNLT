package org.example;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Vector;

public class AgencyManagementApp {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/lottery_mana";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable dataTable;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField revenueField; // Thêm trường nhập liệu cho doanh thu
    private JTextField branch_idField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AgencyManagementApp().initialize();
        });
    }

    public void initialize() {
        frame = new JFrame("Agency Management App");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Address");
        tableModel.addColumn("Revenue");
        tableModel.addColumn("Branch");

        dataTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(dataTable);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addAgency());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editAgency());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteAgency());

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchAgency());

        JButton reload = new JButton("Reload");
        reload.addActionListener(e -> loadDataFromDatabase());

        nameField = new JTextField(10);
        addressField = new JTextField(10);
        revenueField = new JTextField(10);
        branch_idField = new JTextField(10);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Revenue:"));
        inputPanel.add(revenueField);
        inputPanel.add(new JLabel("Branch"));
        inputPanel.add(branch_idField);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(reload);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.add(buttonPanel, BorderLayout.NORTH);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                back();
            }
        });

        loadDataFromDatabase();

        frame.setVisible(true);
    }

    private void addAgency() {
        String name = nameField.getText();
        String address = addressField.getText();
        BigDecimal revenue = new BigDecimal(revenueField.getText());// Lấy giá trị doanh thu
        String branch_id = branch_idField.getText();

        if (!name.isEmpty() && !address.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                String query = "INSERT INTO agency (name, address, revenue, branch_id) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, address);
                    preparedStatement.setBigDecimal(3, revenue);
                    preparedStatement.setString(4,branch_id);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            clearFields();
            loadDataFromDatabase();
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter both name and address.");
        }
    }

    private void editAgency() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String name = nameField.getText();
            String address = addressField.getText();
            BigDecimal revenue = new BigDecimal(revenueField.getText()); // Lấy giá trị doanh thu

            if (!name.isEmpty() && !address.isEmpty()) {
                try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                    String query = "UPDATE agency SET name = ?, address = ?, revenue = ?, branch_id=? WHERE id = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        preparedStatement.setString(2, address);
                        preparedStatement.setBigDecimal(3, revenue);
                        preparedStatement.setString(4, id);
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                clearFields();
                loadDataFromDatabase();
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter both name and address.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a row to edit.");
        }
    }
    private void deleteAgency(){
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                String query = "DELETE FROM agency WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, id);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            loadDataFromDatabase();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
        }
    }

    private void searchAgency(){
        String searchName = JOptionPane.showInputDialog(frame, "Enter Name to Search:");
        if (searchName != null) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                String query = "SELECT * FROM agency WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, searchName);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        tableModel.setRowCount(0); // Clear the table
                        while (resultSet.next()) {
                            Vector<Object> rowData = new Vector<>();
                            rowData.add(resultSet.getInt("id"));
                            rowData.add(resultSet.getString("name"));
                            rowData.add(resultSet.getString("address"));
                            rowData.add(resultSet.getString("revenue"));
                            rowData.add(resultSet.getString("branch_id"));
                            tableModel.addRow(rowData);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String query = "SELECT id, name, address, revenue, branch_id FROM agency";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                tableModel.setRowCount(0); // Clear the table
                while (resultSet.next()) {
                    Vector<Object> rowData = new Vector<>();
                    rowData.add(resultSet.getInt("id"));
                    rowData.add(resultSet.getString("name"));
                    rowData.add(resultSet.getString("address"));
                    rowData.add(resultSet.getBigDecimal("revenue"));
                    rowData.add(resultSet.getString("branch_id"));
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void back(){
        frame.dispose();
        new ManagementApp().initialize();
    }

    private void clearFields() {
        nameField.setText("");
        addressField.setText("");
        revenueField.setText(""); // Xóa trường nhập liệu cho doanh thu
    }
}
