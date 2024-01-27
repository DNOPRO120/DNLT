package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.*;
import java.util.Vector;
public class LotteryManagementApp {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/lottery_mana";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable dataTable;
    private JTextField numberField;
    private JTextField prizeField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LotteryManagementApp().initialize();
        });
    }

    public void initialize() {
        frame = new JFrame("Lottery Management App");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Number");
        tableModel.addColumn("Prize");

        dataTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(dataTable);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addLottery());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editLottery());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteLottery());

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchLottery());

        numberField = new JTextField(10);
        prizeField = new JTextField(10);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Number:"));
        inputPanel.add(numberField);
        inputPanel.add(new JLabel("Prize:"));
        inputPanel.add(prizeField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.add(buttonPanel, BorderLayout.NORTH);

        loadDataFromDatabase();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                back();
            }
        });

        frame.setVisible(true);
    }

    private void addLottery() {
        String number = numberField.getText();
        String prize = prizeField.getText();

        if (!number.isEmpty() && !prize.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                String query = "INSERT INTO lottery (number, prize) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, number);
                    preparedStatement.setString(2, prize);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            clearFields();
            loadDataFromDatabase();
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter both number and prize.");
        }
    }

    private void editLottery() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String number = numberField.getText();
            String prize = prizeField.getText();

            if (!number.isEmpty() && !prize.isEmpty()) {
                try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                    String query = "UPDATE lottery SET number = ?, prize = ? WHERE id = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, number);
                        preparedStatement.setString(2, prize);
                        preparedStatement.setString(3, id);
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                clearFields();
                loadDataFromDatabase();
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter both number and prize.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a row to edit.");
        }
    }

    private void deleteLottery() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                String query = "DELETE FROM lottery WHERE id = ?";
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

    private void searchLottery() {
        String searchNumber = JOptionPane.showInputDialog(frame, "Enter Number to Search:");
        if (searchNumber != null) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                String query = "SELECT * FROM lottery WHERE number = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, searchNumber);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        tableModel.setRowCount(0); // Clear the table
                        while (resultSet.next()) {
                            Vector<Object> rowData = new Vector<>();
                            rowData.add(resultSet.getInt("id"));
                            rowData.add(resultSet.getString("number"));
                            rowData.add(resultSet.getString("prize"));
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
            String query = "SELECT * FROM lottery";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                tableModel.setRowCount(0); // Clear the table
                while (resultSet.next()) {
                    Vector<Object> rowData = new Vector<>();
                    rowData.add(resultSet.getInt("id"));
                    rowData.add(resultSet.getString("number"));
                    rowData.add(resultSet.getString("prize"));
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
        numberField.setText("");
        prizeField.setText("");
    }
}