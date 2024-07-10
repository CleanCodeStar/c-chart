package com.chart.code.thread;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextArea;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChatWindow {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setTitle("Chat Window");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(600, 400);

        JXPanel panel = new JXPanel();
        jFrame.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JXTextArea textArea = new JXTextArea();
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        textArea.setText("收到了反馈" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "收到了开发商" +
                "sdlkf数量的看法" +
                "sdlkfjs");
        textArea.setLineWrap(true);

        panel.add(textArea);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SwingUtilities.invokeLater(() -> {
                    int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();
                    int lines = Math.max(textArea.getLineCount(), textArea.getRows()); // 取行数的最大值
                    int height = lineHeight * lines;

                    // 设置 JTextArea 的高度
                    Dimension size = textArea.getSize();
                    size.height = height;
                    textArea.setSize(size);
                });
            }
        });

    }
}
