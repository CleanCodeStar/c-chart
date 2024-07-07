package com.chart.code;

import com.chart.code.component.LoginFrame;

import javax.swing.*;

/**
 * 启动入口
 *
 * @author CleanCode
 */
public class StartApp {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 使用系统风格
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Storage.loginFrame = new LoginFrame();

    }
}
