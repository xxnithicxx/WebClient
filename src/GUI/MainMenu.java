package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.Socket;

public class MainMenu {
    private Socket connectionSocket;

    public MainMenu() {
        JFrame jFrame = new JFrame("Remote Control");
        jFrame.setMinimumSize(new Dimension(500, 100));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        // Set theme for the JFrame
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         UnsupportedLookAndFeelException e) {
                    break;
                }
                break;
            }
        }

        jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (connectionSocket != null) {
                    try {
                        connectionSocket.close();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "There is error in closing TCP connection", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        JPanel inputLinkPanel = new JPanel();
        JLabel inputLinkLabel = new JLabel("Input link: ");
        JTextField inputLinkText = new JTextField(50);

        inputLinkPanel.setSize(500, 50);
        inputLinkPanel.add(inputLinkLabel);
        inputLinkPanel.add(inputLinkText);

        JButton connectButton = new JButton("Get link");
        connectButton.addActionListener((ActionEvent e) -> {
            String link = inputLinkText.getText();
            if (link.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter link", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        jFrame.add(inputLinkPanel, BorderLayout.CENTER);
        jFrame.add(connectButton, BorderLayout.SOUTH);

        jFrame.setLocation(500, 300);
        jFrame.setVisible(true);
        jFrame.pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
