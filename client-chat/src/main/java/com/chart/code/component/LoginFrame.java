package com.chart.code.component;

import com.alibaba.fastjson.JSON;
import com.chart.code.Client;
import com.chart.code.define.ByteData;
import com.chart.code.dto.UserDTO;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 登录
 * <br/>
 * Created in 2019-08-10 23:30
 *
 * @author Zhenfeng Li
 */
public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("登录");
        setSize(400, 550);
        setLayout(new TableLayout(new double[][]{{20, 60, 10, TableLayout.FILL, 20}, {TableLayout.FILL, 30, 10, 30, 10, 30, 10, 30, TableLayout.FILL}}));

        JLabel userNameLabel = new JLabel("用户名");
        add(userNameLabel, "1, 1");

        JTextField userNameTextField = new JTextField();
        userNameTextField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        userNameTextField.setToolTipText("用户名");
        userNameTextField.setText("admin");
        userNameTextField.setFocusable(true);
        add(userNameTextField, "3, 1");

        JLabel passwordLabel = new JLabel("密码");
        add(passwordLabel, "1, 3");

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passwordField.setToolTipText("密码");
        passwordField.setText("123456");
        passwordField.setFocusable(true);
        add(passwordField, "3, 3");

        // 记住密码
        JCheckBox rememberPasswordCheckBox = new JCheckBox("记住密码");
        rememberPasswordCheckBox.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        rememberPasswordCheckBox.setFocusable(false);
        add(rememberPasswordCheckBox, "3, 5");


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}, {TableLayout.FILL}}));
        add(buttonPanel, "1, 7, 3, 7");
        JButton loginButton = new JButton("登录");
        buttonPanel.add(loginButton, "1, 0");
        loginButton.addActionListener(e -> {
            try {
                Client client = Client.getInstance();
                String username = userNameTextField.getText();
                String password = passwordField.getText();
                UserDTO user = new UserDTO();
                user.setUsername(username).setPassword(password).setRemember(rememberPasswordCheckBox.isSelected());
                ByteData byteData = ByteData.buildLogin(JSON.toJSONString(user).getBytes(StandardCharsets.UTF_8));
                client.send(byteData);
            } catch (IOException ex) {
                try {
                    Client.getInstance().disconnect();
                } catch (IOException ignored) {
                }
                JOptionPane.showMessageDialog(this, "服务连接失败");
            }
        });

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
