package com.chart.code.component;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * FriendBox
 *
 * @author CleanCode
 */
public class FriendBox extends ScrollPane {
    private final VBox friendBox;

    public FriendBox() {
        friendBox = new VBox();
        friendBox.setSpacing(1);
        setPadding(new Insets(0, 1, 0, 0));
        setContent(friendBox);
        setStyle("""
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;
                -fx-border-color: grey;
                """);
        setFitToWidth(true);
        setVvalue(1.0);
    }

    /**
     * 好友列表添加
     */
    public void addFriend(FriendRowBox friendRowBox) {
        friendBox.getChildren().add(friendRowBox);
    }
}
