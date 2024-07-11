package com.chart.code.component;

import com.chart.code.common.ImageIconUtil;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * 好友信息显示面板
 *
 * @author CleanCode
 */
@Getter
public class FriendPanel extends JPanel {
    private final UserVO userVO;
    private final JLabel onLine ;
    private final DialoguePanel dialoguePanel;

    public FriendPanel(UserVO userVO) {
        this.userVO = userVO;
        dialoguePanel = new DialoguePanel(userVO);
        setLayout(new TableLayout(new double[][]{{50, 5, TableLayout.FILL, 90}, {5, 22, 6, 22, 5}}));
        ImageIcon imageIcon = ImageIconUtil.base64ToImageIcon(userVO.getHead());
        if (imageIcon != null) {
            JLabel label = new JLabel(imageIcon);
            add(label, "0,1,0,3");
        } else {
            JLabel label = new JLabel();
            add(label, "0,1,0,3");
        }
        JLabel nickname = new JLabel(userVO.getNickname());
        nickname.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(nickname, "2,1,2,1");
        JLabel username = new JLabel(userVO.getUsername());
        username.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(username, "2,3,2,3");

         onLine = new JLabel(userVO.getOnLine() ? "在线" : "离线");
        onLine.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(onLine, "3,1,3,1");
        setBackground(Color.WHITE);
    }
    public void setOnLine(boolean onLine) {
        this.onLine.setText(onLine ? "在线" : "离线");
        dialoguePanel.showPanel.getOnLine().setText(onLine ? "在线" : "离线");
    }
}
