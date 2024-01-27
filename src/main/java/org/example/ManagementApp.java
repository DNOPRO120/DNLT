package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ManagementApp {
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ManagementApp().initialize();
        });
    }

    public void initialize() {
        frame = new JFrame("Management App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        // GridLayout với một cột và ba hàng
        GridLayout gridLayout = new GridLayout(3, 1);
        frame.setLayout(gridLayout);

        JButton agencyButton = new JButton("Agency");
        JButton branchButton = new JButton("Branch");
        JButton lotteryButton = new JButton("Lottery");

//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                back();
//            }
//        });

        agencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToPage("Agency");
                new AgencyManagementApp().initialize();
                frame.dispose();
            }
        });

        branchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToPage("Branch");
                new BranchManagementApp().initialize();
                frame.dispose();
            }
        });

        lotteryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToPage("Lottery");
                new LotteryManagementApp().initialize();
                frame.dispose();
            }
        });

        frame.add(agencyButton);
        frame.add(branchButton);
        frame.add(lotteryButton);

        // Gọi phương thức hiển thị trang mặc định (ví dụ: Agency)
//        switchToPage("Manager");

        frame.setVisible(true);
    }

    private void switchToPage(String page) {
        // Hiển thị trang tương ứng với lựa chọn
        if ("Agency".equals(page)) {
            JOptionPane.showMessageDialog(frame, "Switched to Agency Page");
        } else if ("Branch".equals(page)) {
            JOptionPane.showMessageDialog(frame, "Switched to Branch Page");
        } else if ("Lottery".equals(page)) {
            JOptionPane.showMessageDialog(frame, "Switched to Lottery Page");
        } else if ("Manager".equals(page)) {
            JOptionPane.showMessageDialog(frame, "Choose");
        }
    }

//    private void back(){
//        new Login().initialize();
//        frame.revalidate();
//        frame.repaint();
//    }

}


