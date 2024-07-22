package com.chart.code.component;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.chart.code.Client;
import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.define.ByteData;
import com.chart.code.define.User;
import com.chart.code.enums.FilePanelType;
import com.chart.code.enums.MsgType;
import com.chart.code.thread.ThreadUtil;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import lombok.Getter;
import org.jdesktop.swingx.JXTextArea;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * 对话面板
 *
 * @author CleanCode
 */
@Getter
public class DialoguePanel extends JPanel {
    public final UserVO friend;
    public final JFXPanel jfxPanel;
    public final JXTextArea inputTextArea;
    public final ShowPanel showPanel;
    public VBox chartBox;

    /**
     * 消息面板内容
     *
     * @param msg       消息内容
     * @param head      头像
     * @param alignment 对齐方式
     * @param color     颜色
     * @return HBox
     */
    private HBox createMessage(String msg, Image head, Pos alignment, javafx.scene.paint.Color color) {
        Label label = new Label(msg);
        label.setFont(new javafx.scene.text.Font("Arial", 14));
        label.setTextFill(color);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: lightgray; -fx-background-radius: 10;-fx-padding: 10");
        ImageView avatar = new ImageView(head);
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);


        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(alignment);

        if (alignment == Pos.CENTER_RIGHT) {
            hBox.setAlignment(Pos.TOP_RIGHT);
            hBox.getChildren().addAll(label, avatar);
            HBox.setMargin(label, new Insets(0, 0, 0, 200));
        } else {
            hBox.setAlignment(Pos.TOP_LEFT);
            hBox.getChildren().addAll(avatar, label);
            HBox.setMargin(label, new Insets(0, 200, 0, 0));
        }

        return hBox;
    }

    /**
     * 插入时间
     *
     * @param datetime 时间值
     * @return HBox
     */
    private HBox createTimestamp(String datetime) {
        javafx.scene.control.Label label = new Label(datetime);
        label.setFont(new Font("Arial", 12));
        label.setTextFill(javafx.scene.paint.Color.GRAY);
        label.setStyle("-fx-padding: 5;");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(label);

        return hBox;
    }

    public DialoguePanel(UserVO friend) {
        this.friend = friend;
        setLayout(new TableLayout(new double[][]{{0, TableLayout.FILL, 0, 245, 0}, {10, TableLayout.FILL, 34, 120, 34}}));
        setBackground(Constant.BACKGROUND_COLOR);
        // 消息区


        jfxPanel = new JFXPanel();
        jfxPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        add(jfxPanel, "1,1,1,1");
        Platform.runLater(() -> {
            // 将 WebView 放入 JFXPanel 中
            chartBox = new VBox(10);
            chartBox.setPadding(new Insets(10));
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(chartBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setVvalue(1.0);
            scrollPane.setBorder(null);

            chartBox.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(1.0));
            jfxPanel.setScene(new Scene(scrollPane));
            jfxPanel.updateUI();
        });
        // 操作区
        JPanel operationPanel = new JPanel();
        operationPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
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
        showPanel = new ShowPanel(friend);
        add(showPanel, "3,1,3,4");
        updateUI();
    }

    /**
     * 发送消息
     */
    public void sendMessage() {
        if (!Storage.currentUser.getId().equals(friend.getId())) {
            ByteData byteData = ByteData.buildMessage(Storage.currentUser.getId(), friend.getId(), inputTextArea.getText().getBytes(StandardCharsets.UTF_8));
            Client.getInstance().send(byteData);
        }
        addOwnMessage(inputTextArea.getText());
        inputTextArea.setText("");

    }

    public void sendFile(File[] files) {
        ThreadUtil.getExecutor().submit(() -> {
            Arrays.stream(files).forEach(file -> {
                FileMessage fileMessage = new FileMessage();
                fileMessage.setId(Constant.SNOWFLAKE.nextId());
                fileMessage.setFileName(file.getName());
                fileMessage.setFileSize(file.length());
                Client instance = Client.getInstance();
                ByteData byteData = ByteData.build(MsgType.TRANSFERRING_FILE_REQUEST, Storage.currentUser.getId(), friend.getId(), JSON.toJSONString(fileMessage).getBytes(StandardCharsets.UTF_8));
                instance.send(byteData);
                Storage.FILE_SEND_MAP.put(fileMessage.getId(), file);
                getShowPanel().putFileMessage(FilePanelType.OWN, fileMessage);
            });
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
            // 时间
            chartBox.getChildren().add(createTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"))));
            // 自己
            chartBox.getChildren().add(createMessage(message, ImageIconUtil.base64ToImage(currentUser.getHead()), Pos.CENTER_RIGHT, javafx.scene.paint.Color.GRAY));
        });
    }

    /**
     * 好友发送的消息
     *
     * @param message
     */
    public void addFriendMessage(String message) {
        // 显示HTML内容
        Platform.runLater(() -> {
            // 朋友
            chartBox.getChildren().add(createMessage(message, ImageIconUtil.base64ToImage(friend.getHead()), Pos.CENTER_LEFT, javafx.scene.paint.Color.GREEN));
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
            // // 自己
            chartBox.getChildren().add(createFile(ImageIconUtil.base64ToImage(currentUser.getHead()), fileName, fileSize, Pos.CENTER_RIGHT, ImageIconUtil.base64ToImage(icon)));
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
            // 朋友
            chartBox.getChildren().add(createFile(ImageIconUtil.base64ToImage(friend.getHead()), fileName, fileSize, Pos.CENTER_LEFT, ImageIconUtil.base64ToImage(icon)));
        });
    }

    /**
     * 发送文件
     *
     * @param head      头像
     * @param fileName  文件名称
     * @param fileSize  文件大小
     * @param alignment 对齐方式
     * @param suffix    后缀
     * @return HBox
     */
    private HBox createFile(Image head, String fileName, String fileSize, Pos alignment, Image suffix) {
        // File icon
        ImageView headImageView = new ImageView(head);
        headImageView.setFitWidth(50);
        headImageView.setFitHeight(50);

        ImageView suffixImageView = new ImageView(suffix);
        suffixImageView.setFitWidth(40);
        suffixImageView.setFitHeight(40);

        Label fileNameLabel = new Label(fileName);
        fileNameLabel.setFont(new Font("Arial", 14));
        fileNameLabel.setTextFill(javafx.scene.paint.Color.BLACK);

        Label fileSizeLabel = new Label(fileSize);
        fileSizeLabel.setFont(new Font("Arial", 14));
        fileSizeLabel.setTextFill(javafx.scene.paint.Color.GRAY);

        VBox fileDetails = new VBox(5, fileNameLabel, fileSizeLabel);

        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(alignment);
        if (alignment == Pos.CENTER_RIGHT) {
            hBox.setAlignment(Pos.TOP_RIGHT);
            hBox.getChildren().addAll(suffixImageView, fileDetails, headImageView);
            HBox.setMargin(suffixImageView, new Insets(0, 0, 0, 180));
        } else {
            hBox.setAlignment(Pos.TOP_LEFT);
            hBox.getChildren().addAll(headImageView, fileDetails, suffixImageView);
            HBox.setMargin(suffixImageView, new Insets(0, 180, 0, 0));
        }

        return hBox;
    }
}
