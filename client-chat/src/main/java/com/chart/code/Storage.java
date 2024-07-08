package com.chart.code;

import com.chart.code.component.DialoguePanel;
import com.chart.code.component.FriendPanel;
import com.chart.code.component.LoginFrame;
import com.chart.code.component.MainFrame;
import com.chart.code.define.User;
import com.chart.code.vo.UserVO;

/**
 * 存储
 *
 * @author CleanCode
 */
public class Storage {
    /**
     * 当前登录用户信息
     */
    public static User currentUser = new User();
    /**
     * 登录界面
     */
    public static LoginFrame loginFrame;
    /**
     * 主界面
     */
    public static MainFrame mainFrame;
    /**
     * 当前选择的聊天FriendPanel
     */
    public static FriendPanel currentFriendPanel;
}
