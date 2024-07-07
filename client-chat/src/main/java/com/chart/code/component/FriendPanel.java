package com.chart.code.component;

import cn.hutool.core.img.ImgUtil;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.ByteArrayInputStream;

/**
 * 好友信息显示面板
 *
 * @author CleanCode
 */
public class FriendPanel extends JPanel {

    private final UserVO userVO;

    public FriendPanel(UserVO userVO) {
        this.userVO = userVO;
        setLayout(new TableLayout(new double[][]{{50, 5, TableLayout.FILL, 90}, {5, 22, 6, 22, 5}}));
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(userVO.getHead());
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        Image image = ImgUtil.read(bis);
        ImageIcon imageIcon = new ImageIcon(image);
        JLabel label = new JLabel(imageIcon);
        add(label, "0,1,0,3");

        JLabel nickname = new JLabel(userVO.getNickname());
        nickname.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(nickname, "2,1,2,1");
        JLabel username = new JLabel(userVO.getUsername());
        username.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(username, "2,3,2,3");

        JLabel onLine = new JLabel(userVO.getOnLine() ? "在线" : "离线");
        onLine.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(onLine, "3,1,3,1");
    }
}
