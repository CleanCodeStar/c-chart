package com.chart.code.component;

import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 主窗口
 *
 * @author CleanCode
 */
@Getter
public class MainFrame extends JFrame {
    private final Map<Integer, FriendPanel> friendPanelMap = new TreeMap<>();

    public MainFrame(List<UserVO> friends) throws HeadlessException {
        setTitle("Java聊天室 - " + Storage.currentUser.getNickname());
        setSize(1020, 600);
        setLayout(new TableLayout(new double[][]{{10, 240, 0, TableLayout.FILL, 10}, {10, TableLayout.FILL, 10}}));
        getContentPane().setBackground(Constant.BACKGROUND_COLOR);

        // 好友列表区
        JPanel friendPanel = new JPanel();
        friendPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        friendPanel.setBackground(Constant.BACKGROUND_COLOR);
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(friendPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        add(scrollPane, "1,1,1,1");

        for (UserVO friend : friends) {
            FriendPanel panel = new FriendPanel(friend);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            friendPanelMap.put(friend.getId(), panel);
            addListener(panel);
            friendPanel.add(panel);
        }
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addListener(FriendPanel friendPanel) {
        friendPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (Storage.currentFriendPanel != null) {
                    Storage.currentFriendPanel.setBackground(Constant.BACKGROUND_COLOR);
                    Storage.currentFriendPanel.getDialoguePanel().setVisible(false);
                }
                friendPanel.setBackground(Constant.CURRENT_COLOR);
                Storage.currentFriendPanel = friendPanel;
                DialoguePanel dialoguePanel = Storage.currentFriendPanel.getDialoguePanel();
                dialoguePanel.setVisible(true);
                add(dialoguePanel, "3,1,3,1");
                revalidate();
                repaint();
                dialoguePanel.getInputTextArea().requestFocus();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (Storage.currentFriendPanel != null && Storage.currentFriendPanel.equals(friendPanel)) {
                    return;
                }
                friendPanel.setBackground(Constant.CURRENT_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (Storage.currentFriendPanel != null && Storage.currentFriendPanel.equals(friendPanel)) {
                    return;
                }
                friendPanel.setBackground(Constant.BACKGROUND_COLOR);

            }
        });
    }
}
