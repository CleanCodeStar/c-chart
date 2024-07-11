package com.chart.code.component;

import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.enums.FilePanelType;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 展示面板
 *
 * @author CleanCode
 */
@Getter
public class ShowPanel extends JPanel {

    private final JLabel onLine;
    private final JPanel filesPanel;
    private final UserVO friend;
    private final Map<Long, FilePanel> fileMessageMap = new HashMap<>(32);

    public ShowPanel(UserVO friend) {
        this.friend = friend;
        setBackground(Constant.BACKGROUND_COLOR);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        setLayout(new TableLayout(new double[][]{{5, 50, 10, TableLayout.FILL, 0}, {5, 22, 6, 22, 10, TableLayout.FILL, 5}}));
        ImageIcon imageIcon = ImageIconUtil.base64ToImageIcon(friend.getHead());
        if (imageIcon != null) {
            JLabel label = new JLabel(imageIcon);
            add(label, "1,1,1,3");
        } else {
            JLabel label = new JLabel();
            add(label, "1,1,1,3");
        }
        JLabel nickname = new JLabel(friend.getNickname());
        nickname.setMaximumSize(new Dimension(0, 0));
        nickname.setPreferredSize(new Dimension(0, 0));
        nickname.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(nickname, "3,1,3,1");

        onLine = new JLabel(friend.getOnLine() ? "在线" : "离线");
        onLine.setMaximumSize(new Dimension(0, 0));
        onLine.setPreferredSize(new Dimension(0, 0));
        onLine.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        add(onLine, "3,3,3,3");
        setBackground(Color.WHITE);
        filesPanel = new JPanel();
        filesPanel.setBackground(Constant.BACKGROUND_COLOR);
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        filesPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
        JScrollPane scrollPane = new JScrollPane(filesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        add(scrollPane, "1,5,3,5");
    }

    public void putFileMessage(FilePanelType filePanelType, FileMessage fileMessage) {
        FilePanel filePanel = new FilePanel(filePanelType,friend,fileMessage);
        filePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        filesPanel.add(filePanel);
        fileMessageMap.put(fileMessage.getId(), filePanel);
        filePanel.updateUI();
    }
}
