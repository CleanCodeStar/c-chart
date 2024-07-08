package com.chart.code.component;

import com.alibaba.fastjson.JSON;
import com.chart.code.Client;
import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.define.ByteData;
import com.chart.code.define.Dialogue;
import com.chart.code.define.User;
import com.chart.code.enums.MsgType;
import com.chart.code.vo.UserVO;
import info.clearthought.layout.TableLayout;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 对话面板
 *
 * @author CleanCode
 */
@Getter
public class DialoguePanel extends JPanel {
    public final UserVO userVO;
    public final JPanel panel;
    public final JTextArea inputTextArea;

    public DialoguePanel(UserVO userVO) {
        this.userVO = userVO;
        setLayout(new TableLayout(new double[][]{{10, TableLayout.FILL, 10, 120, 10}, {10, TableLayout.FILL, 34, 120, 34}}));
        setBackground(Constant.BACKGROUND_COLOR);
        // 消息区
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Constant.BACKGROUND_COLOR);
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        add(scrollPane, "1,1,1,1");
        // 操作区
        JPanel operationPanel = new JPanel();
        operationPanel.setBackground(Constant.CURRENT_COLOR);
        add(operationPanel, "0,2,2,2");
        operationPanel.setLayout(new TableLayout(new double[][]{{10, 60, 10, 60, 10}, {5, TableLayout.FILL, 5}}));
        JButton expressionButton = new JButton("表情");
        JButton fileButton = new JButton("文件");
        operationPanel.add(expressionButton, "1,1,1,1");
        operationPanel.add(fileButton, "3,1,3,1");


        // 输入区
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setBackground(Constant.BACKGROUND_COLOR);
        inputTextArea.requestFocus();
        add(inputTextArea, "1,3,1,3");
        // 发送区
        JPanel sendPanel = new JPanel();
        add(sendPanel, "1,4,1,4");
        sendPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL, 60, 10}, {5, TableLayout.FILL, 5}}));
        JButton sendButton = new JButton("发送");
        sendPanel.add(sendButton, "1,1,1,1");
        sendButton.addActionListener(e -> {
            try {
                Dialogue dialogue = new Dialogue();
                dialogue.setSenderId(Storage.currentUser.getId());
                dialogue.setReceiverId(userVO.getId());
                dialogue.setContent(inputTextArea.getText());
                Client.getInstance().send(new ByteData(MsgType.MESSAGE, JSON.toJSONString(dialogue).getBytes(StandardCharsets.UTF_8)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 好友信息展示区
        ShowPanel showPanel = new ShowPanel(userVO);
        add(showPanel, "3,1,3,4");
    }

    /**
     * 自己发送的消息
     *
     * @param message
     */
    public void addOwnMessage(String message) {
        User currentUser = Storage.currentUser;
        JPanel jPanel = new JPanel();
        panel.add(jPanel);
        jPanel.setLayout(new TableLayout(new double[][]{{50, 5, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 5, 50}, {10, 50, TableLayout.FILL, 10}}));
        ImageIcon imageIcon = ImageIconUtil.base64ToImageIcon(currentUser.getHead());
        JLabel jLabel = new JLabel(imageIcon);
        jLabel.setPreferredSize(new Dimension(50, 50));
        jPanel.add(jLabel, "0,1,0,1");

        JTextArea textArea = new JTextArea(message);
        textArea.setLineWrap(true);
        textArea.setMinimumSize(new Dimension(0, 0));
        textArea.setPreferredSize(new Dimension(0, 0));
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        jPanel.add(textArea, "2,1,3,2");
        updateUI();

    }

    /**
     * 好友发送的消息
     *
     * @param message
     */
    public void addFriendMessage(String message) {
        JPanel jPanel = new JPanel();
        panel.add(jPanel);

        jPanel.setLayout(new TableLayout(new double[][]{{30, 5, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 5, 30}, {10, TableLayout.FULL, 10}}));
        // ImageIcon imageIcon = ImageIconUtil.base64ToImageIcon(userVO.getHead());
        // JLabel head = new JLabel(imageIcon);
        // head.setMinimumSize(new Dimension(30, 30));
        // head.setPreferredSize(new Dimension(30, 30));
        // jPanel.add(head, "6,1,6,1");

        JTextArea textArea = new JTextArea();
        textArea.setMinimumSize(new Dimension(0, 0));
        textArea.setMaximumSize(new Dimension(0, 50));
        textArea.setPreferredSize(new Dimension(0, 0));
        textArea.setLineWrap(true);
        textArea.setText(message);
        jPanel.add(textArea, "3,1,4,2");
        panel.updateUI();
    }

}
