package com.chart.code.component;

import cn.hutool.core.io.FileUtil;
import com.chart.code.Client;
import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.define.ByteData;
import com.chart.code.define.User;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTextArea;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 对话面板
 *
 * @author CleanCode
 */
@Getter
public class DialoguePanel extends JPanel {
    public final UserVO userVO;
    public WebView webView;
    public final JFXPanel jfxPanel;
    public final JXTextArea inputTextArea;
    public final ShowPanel showPanel;

    public DialoguePanel(UserVO userVO) {
        this.userVO = userVO;
        setLayout(new TableLayout(new double[][]{{10, TableLayout.FILL, 10, 120, 10}, {10, TableLayout.FILL, 34, 120, 34}}));
        setBackground(Constant.BACKGROUND_COLOR);
        // 消息区
        jfxPanel = new JFXPanel();
        Platform.runLater(() -> {
            webView = new WebView();
            WebEngine engine = webView.getEngine();
            engine.loadContent(Constant.DIALOGUE_HTML);
            // 将 WebView 放入 JFXPanel 中
            jfxPanel.setScene(new Scene(webView));
        });
        add(jfxPanel, "1,1,1,1");
        // 操作区
        JPanel operationPanel = new JPanel();
        operationPanel.setBackground(Constant.CURRENT_COLOR);
        add(operationPanel, "0,2,2,2");
        operationPanel.setLayout(new TableLayout(new double[][]{{10, 60, 10, 60, 10}, {5, TableLayout.FILL, 5}}));
        JButton expressionButton = new JButton("表情");
        JButton fileButton = new JButton("文件");
        operationPanel.add(expressionButton, "1,1,1,1");
        operationPanel.add(fileButton, "3,1,3,1");
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            // 只选择文件
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // 可以选择多个文件
            fileChooser.setMultiSelectionEnabled(true);
            // 设置标题
            fileChooser.setDialogTitle("选择要发送的文件");
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                sendFile(fileChooser.getSelectedFiles());
            }
        });


        // 输入区
        inputTextArea = new JXTextArea("输入要发送的内容");
        inputTextArea.setLineWrap(true);
        inputTextArea.setBackground(Constant.BACKGROUND_COLOR);
        inputTextArea.requestFocus();
        add(inputTextArea, "1,3,1,3");
        // 发送区
        JPanel sendPanel = new JPanel();
        add(sendPanel, "1,4,1,4");
        sendPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL, 60, 10}, {5, TableLayout.FILL, 5}}));
        JButton sendButton = new JButton("发送");
        sendPanel.add(sendButton, "1,1,1,1");
        sendButton.addActionListener(e -> sendMessage());

        // 好友信息展示区
        showPanel = new ShowPanel(userVO);
        add(showPanel, "3,1,3,4");
    }

    /**
     * 发送消息
     */
    public void sendMessage() {
        try {
            if (!Storage.currentUser.getId().equals(userVO.getId())) {
                ByteData byteData = ByteData.buildMsg(Storage.currentUser.getId(), userVO.getId(), inputTextArea.getText().getBytes(StandardCharsets.UTF_8));
                Client.getInstance().send(byteData);
            }
            addOwnMessage(inputTextArea.getText());
            inputTextArea.setText("");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sendFile(File[] files) {
        Arrays.stream(files).forEach(file -> {
            System.out.println("开始发送" + file.getName());
            String fileName = file.getName();
            long fileSize = file.length();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                // 添加文件进度
                byte[] buffer = new byte[1024];
                int bytesRead;
                // 从源文件读取内容并写入目标文件
                while ((bytesRead = bis.read(buffer)) != -1) {
                    ByteData byteData = ByteData.buildFile(Storage.currentUser.getId(), userVO.getId(), fileName, fileSize, Arrays.copyOf(buffer, bytesRead));
                    try {
                        Client.getInstance().send(byteData);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                addOwnFile(fileName, FileUtils.byteCountToDisplaySize(fileSize));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("结束发送" + file.getName());
        });
    }


    /**
     * 自己发送的消息
     *
     * @param message
     */
    public void addOwnMessage(String message) {
        User currentUser = Storage.currentUser;
        Platform.runLater(() -> {
            // 自己
            webView.getEngine().executeScript(String.format(Constant.DIALOGUE_OWN_MESSAGE_JS, currentUser.getHead(), message));
            // // 时间
            // engine.executeScript(String.format(Constant.DIALOGUE_TIME_JS, "2024年"));
            // // 朋友
            // engine.executeScript(String.format(Constant.DIALOGUE_FRIEND_JS, "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT", "你好啊，我是小红"));
            // 自动滚动到底部
            webView.getEngine().executeScript(Constant.DIALOGUE_SCROLL_JS);
        });
    }

    /**
     * 自己发送的文件
     *
     * @param fileName 文件名称
     * @param fileSize 文件大小
     */
    public void addOwnFile(String fileName, String fileSize) {
        String icon = switch (FileUtil.getSuffix(fileName)) {
            case "xls", "xlsx" -> Constant.FILE_EXCEL_ICO;
            case "doc", "docx" -> Constant.FILE_DOC_ICO;
            default -> Constant.FILE_UNKNOWN_ICO;
        };
        User currentUser = Storage.currentUser;
        Platform.runLater(() -> {
            // 自己
            webView.getEngine().executeScript(String.format(Constant.DIALOGUE_OWN_FILE_JS, currentUser.getHead(), fileName, fileSize, icon));
            // // 时间
            // engine.executeScript(String.format(Constant.DIALOGUE_TIME_JS, "2024年"));
            // // 朋友
            // engine.executeScript(String.format(Constant.DIALOGUE_FRIEND_JS, "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT", "你好啊，我是小红"));
            // 自动滚动到底部
            webView.getEngine().executeScript(Constant.DIALOGUE_SCROLL_JS);
        });
    }

    /**
     * 好友发送的文件
     *
     * @param fileName 文件名称
     * @param fileSize 文件大小
     */
    public void addFriendFile(String fileName, String fileSize) {
        String icon = switch (FileUtil.getSuffix(fileName)) {
            case "xls", "xlsx" -> Constant.FILE_EXCEL_ICO;
            case "doc", "docx" -> Constant.FILE_DOC_ICO;
            default -> Constant.FILE_UNKNOWN_ICO;
        };
        Platform.runLater(() -> {
            // 自己
            webView.getEngine().executeScript(String.format(Constant.DIALOGUE_FRIEND_FILE_JS, userVO.getHead(), fileName, fileSize, icon));
            // // 时间
            // engine.executeScript(String.format(Constant.DIALOGUE_TIME_JS, "2024年"));
            // // 朋友
            // engine.executeScript(String.format(Constant.DIALOGUE_FRIEND_JS, "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT", "你好啊，我是小红"));
            // 自动滚动到底部
            webView.getEngine().executeScript(Constant.DIALOGUE_SCROLL_JS);
        });
    }

    /**
     * 好友发送的消息
     *
     * @param message
     */
    public void addFriendMessage(String message) {
        // 显示HTML内容
        boolean showing = jfxPanel.isShowing();
        System.out.println(showing);
        Platform.runLater(() -> {
            // // 自己
            // engine.executeScript(String.format(Constant.DIALOGUE_OWN_JS, userVO.getHead(), message));
            // // 时间
            // engine.executeScript(String.format(Constant.DIALOGUE_TIME_JS, "2024年"));
            // 朋友
            webView.getEngine().executeScript(String.format(Constant.DIALOGUE_FRIEND_MESSAGE_JS, userVO.getHead(), message));
            // 自动滚动到底部
            webView.getEngine().executeScript(Constant.DIALOGUE_SCROLL_JS);
        });
        // // jPanel.setPreferredSize(new Dimension(0, 0));
        // // jPanel.setMinimumSize(new Dimension(0, 0));
        // jPanel.setLayout((new TableLayout(new double[][]{{30, 10, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 10, 30}, {30, TableLayout.MINIMUM}})));
        // ImageIcon imageIcon = ImageIconUtil.base64ToImageIcon(userVO.getHead());
        // JLabel head = new JLabel(imageIcon);
        // head.setMinimumSize(new Dimension(30, 30));
        // head.setPreferredSize(new Dimension(30, 30));
        // jPanel.add(head, "6,0,6,0");
        //
        // JXTextArea textArea = new JXTextArea();
        // textArea.setLineWrap(true);
        // textArea.setText(message);
        // jPanel.add(textArea, "3,0,4,1");
        // // jPanel.add(textArea, "3,1,4,2");
        // if (panel.getComponentCount() % 2 == 0) {
        //     panel.setPreferredSize(new Dimension(0, 0));
        //     panel.setMinimumSize(new Dimension(0, 0));
        // }else{
        //     panel.setPreferredSize(null);
        //     panel.setMinimumSize(null);
        // }
        // panel.updateUI();
    }

}
