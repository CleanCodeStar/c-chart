package com.chart.code.component;

import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.vo.UserVO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
        setPadding(new Insets(5, 5, 5, 5));

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
        lastMsg = new Label();
        lastMsg.setPrefWidth(110);
        lastMsg.setPrefHeight(20);
        friendInfo.getChildren().addAll(name, lastMsg);
        status = new Label();
        setAlignment(Pos.TOP_CENTER);
        BackgroundFill backgroundFill = new BackgroundFill(
                Color.GREEN,
                new CornerRadii(5),
                null
        );
        status.setBackground(new Background(backgroundFill));
        status.setPrefWidth(10);
        status.setMaxWidth(10);
        status.setMinWidth(10);
        status.setPrefHeight(10);
        status.setMaxHeight(10);
        status.setMinHeight(10);
        status.setVisible(friend.getOnLine());
        Background background = getBackground();
        getChildren().addAll(avatar, friendInfo, status);

        setOnMouseEntered(e -> {
            if (Constant.REMINDER_COLOR.equals(this.getBackground())) {
                return;
            }
            MainBorderPane mainBorderPane = Storage.mainBorderPane;
            if (mainBorderPane.getSelectedFriendPanel() != null && mainBorderPane.getSelectedFriendPanel() != this) {
                setBackground(Background.fill(Color.LIGHTGRAY));
            }
        });
        setOnMouseExited(e -> {
            if (Constant.REMINDER_COLOR.equals(this.getBackground())) {
                return;
            }
            MainBorderPane mainBorderPane = Storage.mainBorderPane;
            if (mainBorderPane.getSelectedFriendPanel() != null && mainBorderPane.getSelectedFriendPanel() != this) {
                setBackground(background);
            }
        });
        setOnMouseReleased(e -> {

            MainBorderPane mainBorderPane = Storage.mainBorderPane;
            mainBorderPane.setCenter(dialogueBox);
            mainBorderPane.getFriendPanelMap().values().forEach(friendPanel -> {
                if (!Constant.REMINDER_COLOR.equals(friendPanel.getBackground()) && !Constant.REMINDER_COLOR.equals(this.getBackground())) {
                    friendPanel.setBackground(background);
                }
            });
            mainBorderPane.setSelectedFriendPanel(this);
            setBackground(Background.fill(Color.LIGHTGRAY));
            lastMsg.setText("");
        });
    }

    public void setOnLine(boolean onLine) {
        Platform.runLater(() -> {
            this.status.setVisible(onLine);
        });
    }

    public void setLastMsg(String msg) {
        Platform.runLater(() -> {
            this.lastMsg.setText(msg);
        });
    }
}