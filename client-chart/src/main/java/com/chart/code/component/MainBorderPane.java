package com.chart.code.component;

import com.chart.code.Storage;
import com.chart.code.vo.UserVO;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 主窗口
 *
 * @author CleanCode
 */
@Getter
public class MainBorderPane extends BorderPane {
    /**
     * 好友面板
     */
    private final Map<Integer, FriendRowBox> friendPanelMap = new TreeMap<>();

    /**
     * 当前选择的好友
     */
    @Setter
    private FriendRowBox selectedFriendPanel;

    public MainBorderPane() {
        setPadding(new Insets(10));
        setBackground(Background.fill(Color.WHITE));
        FriendBox friendBox = new FriendBox();
        setLeft(friendBox);
        List<UserVO> friends = Storage.currentUser.getFriends();
        for (UserVO friend : friends) {
            FriendRowBox friendRowBox = new FriendRowBox(friend);
            friendBox.addFriend(friendRowBox);
            friendPanelMap.put(friend.getId(), friendRowBox);
        }
        Storage.mainBorderPane = this;
    }

}
