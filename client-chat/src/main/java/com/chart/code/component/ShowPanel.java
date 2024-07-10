package com.chart.code.component;

import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * 展示面板
 *
 * @author CleanCode
 */
@Getter
public class ShowPanel extends JPanel {

    private final JLabel onLine;

    public ShowPanel(UserVO userVO) {
        setBackground(Constant.BACKGROUND_COLOR);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        setLayout(new TableLayout(new double[][]{{10, 50, 10, TableLayout.FILL, 0}, {5, 22, 6, 22, TableLayout.FILL, 5}}));
        ImageIcon imageIcon = ImageIconUtil.base64ToImageIcon(userVO.getHead());
        if (imageIcon != null) {
            JLabel label = new JLabel(imageIcon);
            add(label, "1,1,1,3");
        } else {
            JLabel label = new JLabel();
            add(label, "1,1,1,3");
        }
        JLabel nickname = new JLabel(userVO.getNickname());
        nickname.setMaximumSize(new Dimension(0, 0));
        nickname.setPreferredSize(new Dimension(0, 0));
        nickname.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(nickname, "3,1,3,1");

        onLine = new JLabel(userVO.getOnLine() ? "在线" : "离线");
        onLine.setMaximumSize(new Dimension(0, 0));
        onLine.setPreferredSize(new Dimension(0, 0));
        onLine.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(onLine, "3,3,3,3");
        setBackground(Color.WHITE);
    }
}
