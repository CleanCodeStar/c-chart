package com.chart.code.component;

import com.alibaba.fastjson2.JSON;
import com.chart.code.MessageHandle;
import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.common.MessageOriginEnum;
import com.chart.code.define.ByteData;
import com.chart.code.define.User;
import com.chart.code.enums.MsgType;
import com.chart.code.thread.ThreadUtil;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.UserVO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import lombok.Getter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对话窗
 *
 * @author CleanCode
 */
public class DialogueBox extends BorderPane {
    private final VBox chartBox;
    private final UserVO friend;
    private final TextArea inputTextArea;
    private LocalDateTime lastTime;
    @Getter
    private Map<String, FileBox> fileMap = new ConcurrentHashMap<>(128);

    public DialogueBox(UserVO friend) {
        this.friend = friend;
        chartBox = new VBox(10);
        chartBox.setPadding(new Insets(10));
        ScrollPane chartScrollPane = new ScrollPane(chartBox);
        BorderStroke borderStroke = new BorderStroke(
                Color.GRAY,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(1, 1, 1, 0)
        );
        setBorder(new Border(borderStroke));
        chartScrollPane.setBorder(null);
        chartScrollPane.setFitToWidth(true);
        chartScrollPane.setBorder(new Border(borderStroke));
        chartScrollPane.setVvalue(1.0);
        setCenter(chartScrollPane);
        chartBox.heightProperty().addListener((observable, oldValue, newValue) -> chartScrollPane.setVvalue(1.0));

        BorderPane optionBox = new BorderPane();
        optionBox.setPrefHeight(220);
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5, 10, 5, 10));
        Button emotion = new Button("表情");
        Button file = new Button("文件");
        file.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择要发送的文件");
            // 设置初始目录
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Documents"));
            List<File> files = fileChooser.showOpenMultipleDialog(this.getScene().getWindow());
            sendFile(files);
        });
        buttonBox.getChildren().addAll(emotion, file);
        inputTextArea = new TextArea();
        inputTextArea.setWrapText(true);
        ScrollPane inputScrollPane = new ScrollPane(inputTextArea);
        inputScrollPane.setPadding(new Insets(0));
        inputTextArea.prefWidthProperty().bind(widthProperty().subtract(4));
        inputTextArea.setPrefRowCount(7);
        inputTextArea.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && event.isControlDown()) {
                sendMessage();
            }
        });

        HBox sendBox = new HBox();
        Button send = new Button("发送");
        send.setOnAction(event -> {
            sendMessage();
        });
        sendBox.setAlignment(Pos.TOP_RIGHT);
        sendBox.getChildren().addAll(send);
        optionBox.setTop(buttonBox);
        optionBox.setCenter(inputScrollPane);
        optionBox.setBottom(sendBox);
        setBottom(optionBox);
    }

    /**
     * 发送消息
     */
    public void sendMessage() {
        if (!Storage.currentUser.getId().equals(friend.getId())) {
            ByteData byteData = ByteData.buildMessage(Storage.currentUser.getId(), friend.getId(), inputTextArea.getText().getBytes(StandardCharsets.UTF_8));
            MessageHandle.getInstance().send(byteData);
        }
        addOwnMessage(inputTextArea.getText());
        inputTextArea.setText("");

    }

    public void sendFile(List<File> files) {
        ThreadUtil.getExecutor().submit(() -> {
            files.forEach(file -> {
                FileMessage fileMessage = new FileMessage();
                fileMessage.setId(Constant.SNOWFLAKE.nextId());
                fileMessage.setFileName(file.getName());
                fileMessage.setFileSize(file.length());
                MessageHandle instance = MessageHandle.getInstance();
                ByteData byteData = ByteData.build(MsgType.TRANSFERRING_FILE_REQUEST, Storage.currentUser.getId(), friend.getId(), JSON.toJSONString(fileMessage).getBytes(StandardCharsets.UTF_8));
                instance.send(byteData);
                Storage.FILE_SEND_MAP.put(fileMessage.getId(), file);
                // getShowPanel().putFileMessage(FilePanelType.OWN, fileMessage);
                addOwnFile(fileMessage);
                // addFriendFile(file.getName(), FileUtils.byteCountToDisplaySize(fileMessage.getFileSize()));
            });
        });
    }


    /**
     * 自己发送的消息
     *
     * @param message 消息
     */
    public void addOwnMessage(String message) {
        User currentUser = Storage.currentUser;
        Platform.runLater(() -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            if (lastTime == null || Duration.between(lastTime, localDateTime).abs().toMinutes() > 3) {
                // 与上一条消息之间，超过三分钟，则加入时间
                chartBox.getChildren().add(createTimestamp(localDateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"))));
            }
            lastTime = localDateTime;
            // 自己
            chartBox.getChildren().add(createMessage(message, ImageIconUtil.base64ToImage(currentUser.getHead()), Pos.CENTER_RIGHT, Color.GRAY));
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
            LocalDateTime localDateTime = LocalDateTime.now();
            if (lastTime == null || Duration.between(lastTime, localDateTime).abs().toMinutes() > 3) {
                // 与上一条消息之间，超过三分钟，则加入时间
                chartBox.getChildren().add(createTimestamp(localDateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"))));
            }
            lastTime = localDateTime;
            // 朋友
            chartBox.getChildren().add(createMessage(message, ImageIconUtil.base64ToImage(friend.getHead()), Pos.CENTER_LEFT, Color.GREEN));
        });
    }

    /**
     * 自己发送的文件
     *
     * @param fileMessage 文件消息
     */
    public void addOwnFile(FileMessage fileMessage) {
        UserVO currentUser = Storage.currentUser;
        Platform.runLater(() -> {
            // // 自己
            chartBox.getChildren().add(createFile(currentUser, MessageOriginEnum.OWN, fileMessage));
        });
    }

    /**
     * 好友发送的文件
     *
     * @param fileMessage 文件消息
     */
    public void addFriendFile(FileMessage fileMessage) {
        Platform.runLater(() -> {
            // 朋友
            chartBox.getChildren().add(createFile(friend, MessageOriginEnum.FRIEND, fileMessage));
        });
    }

    /**
     * 发送文件
     *
     * @param userVO            自己或好友
     * @param messageOriginEnum 对齐方式
     * @param fileMessage       文件消息
     * @return HBox
     */
    private HBox createFile(UserVO userVO, MessageOriginEnum messageOriginEnum, FileMessage fileMessage) {
        FileBox fileBox = new FileBox(userVO, messageOriginEnum, fileMessage);
        fileMap.put(messageOriginEnum.getName() + fileMessage.getId(), fileBox);
        return fileBox;
    }

    /**
     * 消息面板内容
     *
     * @param msg       消息内容
     * @param head      头像
     * @param alignment 对齐方式
     * @param color     颜色
     * @return HBox
     */
    private HBox createMessage(String msg, Image head, Pos alignment, Color color) {
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
        label.setTextFill(Color.GRAY);
        label.setStyle("-fx-padding: 5;");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(label);

        return hBox;
    }
}
