package com.chart.code.component;

import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 主窗口
 *
 * @author CleanCode
 */
public class MainFrame extends JFrame {
    Map<Integer, FriendPanel> friendPanelMap = new TreeMap<>();

    public MainFrame(List<UserVO> friends) throws HeadlessException {
        setTitle("聊天室");
        setSize(800, 600);
        setLayout(new TableLayout(new double[][]{{10, 200, 10, TableLayout.FILL, 200, 10, 10}, {10, TableLayout.FILL, 10}}));

        JPanel friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
        friendPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        add(friendPanel, "1,1");

        for (UserVO friend : friends) {
            FriendPanel panel = new FriendPanel(friend);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            friendPanelMap.put(friend.getId(), panel);
            friendPanel.add(panel);
        }
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
