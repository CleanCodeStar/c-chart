package com.chart.code.component;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.chart.code.MessageHandle;
import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.common.MessageOriginEnum;
import com.chart.code.define.ByteData;
import com.chart.code.enums.MsgType;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.UserVO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

/**
 * 文件消息
 *
 * @author CleanCode
 */
public class FileBox extends HBox {
    /**
     * 自己/好友对象
     */
    private final UserVO userVO;
    private final FileMessage fileMessage;
    private final MessageOriginEnum messageOriginEnum;
    @Getter
    private final HBox buttons;
    @Getter
    private final HBox box;
    private final ImageView suffixImageView;
    private long currentSize = 0;
    @Getter
    private ProgressIndicator progressIndicator;
    private int ratio = 1;

    public FileBox(UserVO userVO, MessageOriginEnum messageOriginEnum, FileMessage fileMessage) {
        this.userVO = userVO;
        this.messageOriginEnum = messageOriginEnum;
        Image head = ImageIconUtil.base64ToImage(userVO.getHead());
        this.fileMessage = fileMessage;
        String fileName = fileMessage.getFileName();
        String icon = switch (FileUtil.getSuffix(fileName)) {
            case "xls", "xlsx" -> Constant.FILE_EXCEL_ICO;
            case "doc", "docx" -> Constant.FILE_DOC_ICO;
            default -> Constant.FILE_UNKNOWN_ICO;
        };
        String fileSize = FileUtils.byteCountToDisplaySize(fileMessage.getFileSize());
        Image suffix = ImageIconUtil.base64ToImage(icon);

        setSpacing(10);
        ImageView headImageView = new ImageView(head);
        headImageView.setFitWidth(50);
        headImageView.setFitHeight(50);

        suffixImageView = new ImageView(suffix);
        suffixImageView.setFitWidth(40);
        suffixImageView.setFitHeight(40);

        Label fileNameLabel = new Label(fileName);
        fileNameLabel.setFont(new Font("Arial", 14));
        fileNameLabel.setTextFill(Color.BLACK);

        Label fileSizeLabel = new Label(fileSize);
        fileSizeLabel.setFont(new Font("Arial", 14));
        fileSizeLabel.setTextFill(Color.GRAY);


        VBox fileDetails = new VBox(5, fileNameLabel, fileSizeLabel);

        setPadding(new Insets(5));
        setAlignment(messageOriginEnum.getAlignment());
        if (messageOriginEnum.getAlignment() == Pos.CENTER_RIGHT) {
            buttons = new HBox(10);
            Button cancel = new Button("取消");
            cancel.setOnAction(e -> {
                // 取消
                ByteData build = ByteData.build(MsgType.CANCEL_SEND_FILE_TRANSFER, Storage.currentUser.getId(), userVO.getId(), JSON.toJSONString(fileMessage).getBytes(StandardCharsets.UTF_8));
                MessageHandle.getInstance().send(build);
                cancelFile();
            });
            buttons.getChildren().addAll(cancel);
            fileDetails.getChildren().add(buttons);

            box = new HBox(10, fileDetails, suffixImageView);
            box.setStyle("-fx-background-color: lightgray");
            box.setPadding(new Insets(10, 10, 10, 10));
            setAlignment(Pos.TOP_RIGHT);
            getChildren().addAll(box, headImageView);
            HBox.setMargin(box, new Insets(0, 0, 0, 180));
        } else {
            buttons = new HBox(10);
            Button receive = new Button("接受");
            receive.setStyle("-fx-background-color: #338333;-fx-text-fill: #ffffff");
            receive.setOnAction(e -> receiveFile());
            Button turnDown = new Button("拒绝");
            turnDown.setOnAction(e -> {
                // 拒绝接收文件
                // 通知对方
                ByteData build = ByteData.build(MsgType.REFUSE_RECEIVE_FILE, Storage.currentUser.getId(), userVO.getId(), JSON.toJSONString(fileMessage).getBytes(StandardCharsets.UTF_8));
                MessageHandle.getInstance().send(build);
                buttons.getChildren().removeAll(buttons.getChildren());
                Label label = new Label("已拒收");
                buttons.getChildren().add(label);
            });
            buttons.getChildren().addAll(receive, turnDown);
            fileDetails.getChildren().add(buttons);


            box = new HBox(10, fileDetails, suffixImageView);
            box.setStyle("-fx-background-color: lightgray");
            box.setPadding(new Insets(10, 10, 10, 10));
            setAlignment(Pos.TOP_LEFT);
            getChildren().addAll(headImageView, box);
            HBox.setMargin(box, new Insets(0, 180, 0, 0));
        }

    }

    /**
     * 接收文件
     */
    private void receiveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择保存路径");
        // 设置初始目录
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Downloads"));
        // 设置文件过滤器
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(FileUtil.getSuffix(fileMessage.getFileName()), "*." + FileUtil.getSuffix(fileMessage.getFileName()));
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(fileMessage.getFileName());
        // 显示打开文件对话框
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file != null) {
            // 接收文件
            Storage.FILE_RECEIVE_MAP.put(fileMessage.getId(), file);
            FileOutputStream outputStream = null;
            BufferedOutputStream fileOutputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                fileOutputStream = new BufferedOutputStream(outputStream);
                Storage.FILE_OUTPUTSTREAM_MAP.put(fileMessage.getId(), fileOutputStream);
                // 发送确认接受的消息给对方
                ByteData build = ByteData.build(MsgType.AGREE_RECEIVE_FILE, Storage.currentUser.getId(), userVO.getId(), JSON.toJSONString(fileMessage).getBytes(StandardCharsets.UTF_8));
                MessageHandle.getInstance().send(build);
            } catch (Exception e) {
                // 防止发送失败时，流已经打开的情况
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException ex) {
                        System.out.println("关闭发送流失败");
                        throw new RuntimeException(ex);
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException ex) {
                        System.out.println("关闭接收流失败");
                        throw new RuntimeException(ex);
                    }
                }
                System.err.println("文件保存流创建失败");
                throw new RuntimeException(e);
            }
            buttons.getChildren().removeAll(buttons.getChildren());
            Button cancel = new Button("取消");
            cancel.setOnAction(e -> {
                // 取消
                ByteData build = ByteData.build(MsgType.CANCEL_RECEIVE_FILE_TRANSFER, Storage.currentUser.getId(), userVO.getId(), JSON.toJSONString(fileMessage).getBytes(StandardCharsets.UTF_8));
                MessageHandle.getInstance().send(build);
                cancelFile();
            });
            buttons.getChildren().add(cancel);
            addProgressIndicator();
        }
    }

    /**
     * 添加进度条
     */
    public void addProgressIndicator() {
        progressIndicator = new ProgressIndicator(0);
        progressIndicator.setMinWidth(60);
        progressIndicator.setMinHeight(65);
        box.getChildren().removeAll(suffixImageView);
        box.getChildren().add(progressIndicator);
    }

    /**
     * 取消文件传输
     */
    public void cancelFile() {
        if (MessageOriginEnum.OWN.equals(messageOriginEnum)) {
            // 发送放
            try {
                InputStream inputStream = Storage.FILE_INPUTSTREAM_MAP.remove(fileMessage.getId());
                if (inputStream != null) {
                    // 关闭发送流
                    inputStream.close();
                }
                Storage.FILE_SEND_MAP.remove(fileMessage.getId());
            } catch (IOException ex) {
                System.out.println("关闭发送流失败");
                throw new RuntimeException(ex);
            }
        } else {
            // 接收方
            try {
                // 先移除这个，防止下面被创建
                File file = Storage.FILE_RECEIVE_MAP.remove(fileMessage.getId());
                BufferedOutputStream outputStream = Storage.FILE_OUTPUTSTREAM_MAP.remove(fileMessage.getId());
                if (outputStream != null) {
                    outputStream.close();
                }
                // 把未接收完成的文件删除了
                if (file != null) {
                    file.delete();
                }
            } catch (IOException ex) {
                System.out.println("关闭发送流失败");
                throw new RuntimeException(ex);
            }
        }
        buttons.getChildren().removeAll(buttons.getChildren());
        Label label = new Label("已取消");
        buttons.getChildren().add(label);

        buttons.getChildren().removeAll(buttons.getChildren());
        box.getChildren().removeAll(progressIndicator);
        box.getChildren().add(suffixImageView);
    }

    /**
     * 更新接收或发送进度
     *
     * @param progress 进度
     */

    public void updateProgress(int progress) {
        currentSize += progress;
        // int value = Math.toIntExact(currentSize / ratio);
        // String progressText = FileUtils.byteCountToDisplaySize(currentSize) + "/" + FileUtils.byteCountToDisplaySize(fileMessage.getFileSize());
        if (fileMessage.getFileSize().equals(currentSize)) {
            if (MessageOriginEnum.FRIEND.equals(messageOriginEnum)) {
                try {
                    Storage.FILE_OUTPUTSTREAM_MAP.remove(fileMessage.getId()).close();
                    Storage.FILE_RECEIVE_MAP.remove(fileMessage.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("接受完成");
            }
            Platform.runLater(() ->{
                buttons.getChildren().removeAll(buttons.getChildren());
                box.getChildren().removeAll(progressIndicator);
                box.getChildren().add(suffixImageView);
            });
        }
        Platform.runLater(() -> {
            if (progressIndicator != null) {
                BigDecimal bd = new BigDecimal(currentSize * 1.0 / fileMessage.getFileSize());
                BigDecimal rounded = bd.setScale(2, RoundingMode.DOWN);
                progressIndicator.setProgress(rounded.doubleValue());
            }
        });
    }
}
