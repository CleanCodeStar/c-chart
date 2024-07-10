package com.chart.code.thread;

import javax.swing.*;
import java.awt.*;
import java.security.Provider;
import java.security.Security;
import java.util.Set;
import java.util.TreeSet;

public class CustomDialog extends JDialog {
    public CustomDialog(Frame owner) {
        super(owner, "Custom Dialog", true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JTextField(20), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(new JPasswordField(20), gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("OK"));
        buttonPanel.add(new JButton("Cancel"));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(owner);
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        //     JFrame frame = new JFrame();
        //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //     frame.setSize(400, 300);
        //     frame.setVisible(true);
        //
        //     JButton showDialogButton = new JButton("Show Dialog");
        //     showDialogButton.addActionListener(e -> {
        //         CustomDialog dialog = new CustomDialog(frame);
        //         dialog.setVisible(true);
        //     });
        //
        //     frame.add(showDialogButton, BorderLayout.CENTER);
        // });
        Provider[] providers = Security.getProviders();
        Set<String> algorithms = new TreeSet<>();

        for (Provider provider : providers) {
            provider.forEach((key, value) -> {
                if (key.toString().startsWith("Alg.Alias.")) {
                    // Skip aliases
                    return;
                }
                if (key.toString().startsWith("MessageDigest.")) {
                    algorithms.add(key.toString());
                }
            });
        }

        System.out.println("Supported Digest Algorithms:");
        algorithms.forEach(System.out::println);
    }
}
