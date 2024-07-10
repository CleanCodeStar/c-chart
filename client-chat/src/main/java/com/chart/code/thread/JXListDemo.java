package com.chart.code.thread;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTextArea;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JXListDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JXList Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 创建 JXList
            JXList jxList = new JXList();
            JXTextArea textArea = new JXTextArea();
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

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(textArea, BorderLayout.CENTER);


            // 添加数据到列表
            DefaultListModel<Component> listModel = new DefaultListModel<>();
            listModel.addElement(panel);
            listModel.addElement(new JXLabel("Apple"));
            listModel.addElement( new JXLabel("Banana"));
            listModel.addElement(new JXLabel("Orange"));
            listModel.addElement(new JXLabel("Grapes"));
            jxList.setModel(listModel);
            jxList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> (Component)value);


            // 创建一个按钮来演示交互
            JButton button = new JButton("Remove Selected Item");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = jxList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        listModel.remove(selectedIndex);
                    }
                }
            });

            // 将 JXList 放置在 JScrollPane 中以便滚动
            JScrollPane scrollPane = new JScrollPane(jxList);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            // 设置布局
            frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
            frame.getContentPane().add(scrollPane);
            frame.getContentPane().add(button);

            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null); // 居中显示窗口
            frame.setVisible(true);
        });
    }
}
