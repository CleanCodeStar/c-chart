package com.chart.code;

import com.chart.code.component.FriendPanel;
import com.chart.code.component.LoginFrame;
import com.chart.code.component.MainFrame;
import com.chart.code.define.User;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    /**
     * 文件发送缓存
     */
    public static final Map<Long, File> FILE_SEND_MAP = new ConcurrentHashMap<>(128);
    /**
     * 输入文件流缓存 （和文件分开，是因为防止文件在确认时被使用，导致其他操作受限）
     */
    public static final Map<Long, InputStream> FILE_INPUTSTREAM_MAP = new ConcurrentHashMap<>(128);

    /**
     * 文件接收缓存
     */
    public static final Map<Long, File> FILE_RECEIVE_MAP = new ConcurrentHashMap<>(128);
    /**
     * 输出文件流缓存
     */
    public static final Map<Long, BufferedOutputStream> FILE_OUTPUTSTREAM_MAP = new ConcurrentHashMap<>(128);
}
