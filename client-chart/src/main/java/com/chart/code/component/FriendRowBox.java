package com.chart.code.component;

import com.chart.code.Storage;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.vo.UserVO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

/**
 * 好友列表
 *
 * @author CleanCode
 */
@Getter
public class FriendRowBox extends HBox {
    private final DialogueBox dialogueBox;
    private final Label lastMsg;
    private final Label status;

    public FriendRowBox(UserVO friend) {
        dialogueBox = new DialogueBox(friend);
        setSpacing(5);
        setPadding(new Insets(0, 5, 0, 5));
        setAlignment(Pos.CENTER);

        Image head = ImageIconUtil.base64ToImage(friend.getHead());
        ImageView avatar = new ImageView(head);
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);
        // 创建 Rectangle 并设置圆角弧度
        Rectangle clip = new Rectangle(40, 40);
        clip.setArcWidth(5);
        clip.setArcHeight(5);

        // 将 Rectangle 设置为 ImageView 的剪辑区域
        avatar.setClip(clip);

        VBox friendInfo = new VBox();
        friendInfo.setSpacing(5);
        Label name = new Label(friend.getNickname());
        name.setPrefWidth(110);
        name.setPrefHeight(20);
        lastMsg = new Label("信");
        lastMsg.setPrefWidth(110);
        lastMsg.setPrefHeight(20);
        friendInfo.getChildren().addAll(name, lastMsg);
        status = new Label(friend.getOnLine() ? "在线" : "离线");
        status.setPrefWidth(45);
        Background background = getBackground();
        getChildren().addAll(avatar, friendInfo, status);

        setOnMouseEntered(e -> {
            MainBorderPane mainBorderPane = Storage.mainBorderPane;
            if (mainBorderPane.getSelectedFriendPanel() != null && mainBorderPane.getSelectedFriendPanel() != this) {
                setBackground(Background.fill(Color.LIGHTGRAY));
            }
        });
        setOnMouseExited(e -> {
            MainBorderPane mainBorderPane = Storage.mainBorderPane;
            if (mainBorderPane.getSelectedFriendPanel() != null && mainBorderPane.getSelectedFriendPanel() != this) {
                setBackground(background);
            }
        });
        setOnMouseReleased(e -> {
            MainBorderPane mainBorderPane = Storage.mainBorderPane;
            mainBorderPane.setCenter(dialogueBox);
            mainBorderPane.getFriendPanelMap().values().forEach(friendPanel -> {
                friendPanel.setBackground(background);
            });
            mainBorderPane.setSelectedFriendPanel(this);
            setBackground(Background.fill(Color.LIGHTGRAY));
            lastMsg.setText("");
        });
    }

    public void setOnLine(boolean onLine) {
        Platform.runLater(() -> {
            this.status.setText(onLine ? "在线" : "离线");
        });
    }

    public void addLastMsg(String message) {


    }
}