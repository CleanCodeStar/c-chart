package org.citrsw;


import com.chart.code.common.Constant;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class HtmlDisplayExample1 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("HTML Display Example");
            frame.setSize(900, 600);
            JFXPanel jfxPanel = new JFXPanel();
            String s = new String("".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            frame.getContentPane().add(jfxPanel, BorderLayout.CENTER);
            // 设置HTML内容

            // 创建 JavaFX WebView
            Platform.runLater(() -> {
                WebView webView = new WebView();
                WebEngine engine = webView.getEngine();
                engine.loadContent(Constant.DIALOGUE_HTML);
                // 将 WebView 放入 JFXPanel 中
                jfxPanel.setScene(new Scene(webView));
                JButton button = new JButton("显示HTML内容");
                frame.getContentPane().add(button, BorderLayout.SOUTH);
                button.addActionListener(e -> {
                    // 显示HTML内容
                    Platform.runLater(() -> {
                        // 自己
                        engine.executeScript(String.format(Constant.DIALOGUE_OWN_MESSAGE_JS, "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT", "你好啊，我是小明"));
                        // 时间
                        engine.executeScript(String.format(Constant.DIALOGUE_TIME_JS, "2024年"));
                        // 朋友
                        engine.executeScript(String.format(Constant.DIALOGUE_FRIEND_MESSAGE_JS, "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT", "你好啊，我是小红"));
                        // 自动滚动到底部
                        engine.executeScript(Constant.DIALOGUE_SCROLL_JS);
                    });
                });
            });
            // 显示框架
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.revalidate();
            frame.repaint();
        });
    }
}
