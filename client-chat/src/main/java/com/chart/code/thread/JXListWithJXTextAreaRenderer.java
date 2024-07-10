package com.chart.code.thread;

import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTextArea;
import org.jdesktop.swingx.renderer.ComponentProvider;

import javax.swing.*;
import java.awt.*;

public class JXListWithJXTextAreaRenderer {
    public static void main(String[] args) {
        // Mock data
        JLabel[] data = {new JLabel("Item 1"),new JLabel("Item 2"),new JLabel("Item 3")};

        // Create JList
        JList<JLabel> list = new JList<>(data);

        // Create JFrame and set layout
        JFrame frame = new JFrame("Swing Smart List Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Add JList to JFrame
        frame.add(new JScrollPane(list), BorderLayout.CENTER);

        // Set JFrame properties
        frame.setSize(300, 250);
        frame.setVisible(true);
    }
}
