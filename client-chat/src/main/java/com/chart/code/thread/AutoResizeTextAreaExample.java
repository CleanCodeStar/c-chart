package com.chart.code.thread;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AutoResizeTextAreaExample extends JFrame {

    private JTextArea textArea;

    public AutoResizeTextAreaExample() {
        setTitle("Auto Resize TextArea Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 创建一个固定宽度的 JPanel 用来放置 JTextArea
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setPreferredSize(new Dimension(300, 0)); // 设置固定宽度

        textArea = new JTextArea(5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textPanel.add(scrollPane);

        // 添加一个组件监听器，用于在窗口大小改变时重新调整 JTextArea 的高度
        getContentPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                adjustTextAreaHeight();
            }
        });

        mainPanel.add(textPanel, BorderLayout.CENTER);
        getContentPane().add(mainPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void adjustTextAreaHeight() {
        // 根据行数和字体信息计算 JTextArea 的理想高度
        int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();
        int lines = Math.max(textArea.getLineCount(), textArea.getRows()); // 取行数的最大值
        int height = lineHeight * lines;

        // 设置 JTextArea 的高度
        Dimension size = textArea.getSize();
        size.height = height;
        textArea.setSize(size);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AutoResizeTextAreaExample());
    }
}
