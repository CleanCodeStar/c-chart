package com.chart.code.component;

import com.alibaba.fastjson.JSON;
import com.chart.code.Client;
import com.chart.code.Storage;
import com.chart.code.define.ByteData;
import com.chart.code.enums.FilePanelType;
import com.chart.code.enums.MsgType;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXButton;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 文件列表面板Panel
 *
 * @author CleanCode
 */
@Getter
public class FilePanel extends JPanel {
    private final UserVO friend;
    private final JProgressBar progressBar;
    private final FileMessage fileMessage;
    private long currentSize = 0;
    private int ratio = 1;

    public FilePanel(FilePanelType filePanelType, UserVO friend, FileMessage fileMessage) {
        this.friend = friend;
        this.fileMessage = fileMessage;
        setLayout(new TableLayout(new double[][]{{5, 160, 60, 0}, {2, 26, 5, 30, 2}}));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        JLabel fileName = new JLabel(fileMessage.getFileName());
        fileName.setToolTipText(fileMessage.getFileName());
        // JLabel fileSize = new JLabel(totalSize);
        JXButton option = new JXButton("取消");
        option.getInsets(new Insets(0, 0, 0, 0));
        option.addActionListener(e -> {
            // 取消

        });
        option.setVisible(false);
        add(fileName, "1,1,1,1");
        add(option, "2,1,2,1");


        // 进度条
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        Long fileSize = fileMessage.getFileSize();
        while (fileSize > Integer.MAX_VALUE) {
            ratio *= 2;
            fileSize = fileMessage.getFileSize() / ratio;
        }

        progressBar.setMaximum(Math.toIntExact(fileSize));
        progressBar.setToolTipText("0/" + FileUtils.byteCountToDisplaySize(fileSize));
        // add(progressBar, "1,3,3,3");


        // 选择框
        JPanel choosePanel = new JPanel();
        choosePanel.setLayout(new TableLayout(new double[][]{{30, 70, 10, 70, 30}, {2, 26, 2}}));
        add(choosePanel, "1,3,3,3");
        // 拒绝
        JXButton reject = new JXButton("拒绝");
        reject.addActionListener(e -> {
            // 拒绝接收文件
            Container parent = getParent();
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        });

        // 接收
        JXButton receive = new JXButton("接收");
        receive.addActionListener(e -> {
            // 接收文件
            choosePanel.remove(reject);
            choosePanel.remove(receive);
            choosePanel.setLayout(new TableLayout(new double[][]{{30, 70, 10, 70, 30}, {2, 20, 2}}));
            choosePanel.add(progressBar, "0,1,4,1");
            option.setVisible(true);
            updateUI();
            // 发送确认接受的消息给对方
            ByteData build = ByteData.build(MsgType.RECEIVE_FILE, Storage.currentUser.getId(), friend.getId(), JSON.toJSONString(fileMessage).getBytes(StandardCharsets.UTF_8));
            try {
                Client.getInstance().send(build);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        if (FilePanelType.OWN.equals(filePanelType)) {
            choosePanel.add(progressBar, "0,1,4,1");
            option.setVisible(true);
        } else {
            choosePanel.add(reject, "1,1,1,1");
            choosePanel.add(receive, "3,1,3,1");
        }

    }

    public void updateProgress(int progress) {
        currentSize += progress;
        progressBar.setValue(Math.toIntExact(currentSize/ratio));
        progressBar.setToolTipText(FileUtils.byteCountToDisplaySize(currentSize) + "/" + FileUtils.byteCountToDisplaySize(fileMessage.getFileSize()));
    }
}
