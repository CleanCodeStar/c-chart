package com.chart.code.component;

import com.chart.code.vo.FileMessage;
import info.clearthought.layout.TableLayout;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXButton;

import javax.swing.*;
import java.awt.*;

/**
 * 文件列表面板Panel
 *
 * @author CleanCode
 */
@Getter
public class FilePanel extends JPanel {
    private final JProgressBar progressBar;
    private final FileMessage fileMessage;
    private int currentSize = 0;

    public FilePanel(FileMessage fileMessage) {
        this.fileMessage = fileMessage;
        setLayout(new TableLayout(new double[][]{{5, 160, 60,0}, {2, 30, 5, 30, 2}}));
        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
        JLabel fileName = new JLabel(fileMessage.getFileName());
        fileName.setToolTipText(fileMessage.getFileName());
        // JLabel fileSize = new JLabel(totalSize);
        JXButton option = new JXButton("取消");
        option.getInsets(new Insets(0, 0, 0, 0));
        option.addActionListener(e -> {
            // 接收文件

        });
        option.setVisible(false);
        add(fileName, "1,1,1,1");
        add(option, "2,1,2,1");


        // 进度条
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setMaximum(Math.toIntExact(fileMessage.getFileSize()));
        progressBar.setToolTipText("0/" + FileUtils.byteCountToDisplaySize(fileMessage.getFileSize()));
        // add(progressBar, "1,3,3,3");

        //选择框
        JPanel choosePanel = new JPanel();
        choosePanel.setLayout(new TableLayout(new double[][]{{30,70,10,70,30}, {2,26,2}}));
        add(choosePanel, "1,3,3,3");
        //拒绝
        JXButton reject = new JXButton("拒绝");
        reject.addActionListener(e -> {
            // 拒绝接收文件
            Container parent = getParent();
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        });
        choosePanel.add(reject, "1,1,1,1");

        //接收
        JXButton receive = new JXButton("接收");
        receive.addActionListener(e -> {
            // 接收文件
            remove(choosePanel);
            add(progressBar, "1,3,3,3");
            option.setVisible(true);
            updateUI();
        });
        choosePanel.add(receive, "3,1,3,1");

    }

    public void updateProgress(int progress) {
        currentSize += progress;
        progressBar.setValue(currentSize);
        progressBar.setToolTipText(FileUtils.byteCountToDisplaySize(currentSize) + "/" + FileUtils.byteCountToDisplaySize(fileMessage.getFileSize()));
    }
}
