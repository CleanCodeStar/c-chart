package com.chart.code;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chart.code.component.FilePanel;
import com.chart.code.component.FriendPanel;
import com.chart.code.component.MainFrame;
import com.chart.code.define.ByteData;
import com.chart.code.enums.FilePanelType;
import com.chart.code.enums.MsgType;
import com.chart.code.thread.ThreadUtil;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.Result;
import com.chart.code.vo.UserVO;
import com.google.common.io.BaseEncoding;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 客户端
 *
 * @author CleanCode
 */
public class Client {
    int x = 0;
    /**
     * 单例模式
     */
    private static volatile Client instance;

    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;

    /**
     * 私有构造方法，防止被实例化
     */
    private Client() throws IOException {
        connect();
        receive();
    }

    /**
     * 获取单例实例
     *
     * @return 单例实例
     */
    public static Client getInstance() throws IOException {
        if (instance == null) {
            synchronized (Client.class) {
                if (instance == null) {
                    instance = new Client();
                }
            }
        }
        return instance;
    }

    /**
     * 连接服务端
     */
    private void connect() throws IOException {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 8199;
        socket = new Socket(host, port);
        // 与服务端建立连接
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public void receive() {
        ThreadUtil.getExecutor().submit(() -> {
            try {
                System.out.println("接收到连接信息");
                while (true) {
                    // 读取header
                    byte[] bytes = new byte[1];
                    int len = inputStream.read(bytes);
                    if (len == -1) {
                        socket.close();
                        continue;
                    }
                    if (!Arrays.equals(new byte[]{0x10}, bytes)) {
                        // 返回错误
                        socket.close();
                        continue;
                    }
                    // 读取type
                    bytes = new byte[1];
                    len = inputStream.read(bytes);
                    if (len == -1) {
                        socket.close();
                        continue;
                    }
                    MsgType msgType = MsgType.getMsgType(bytes);
                    if (msgType == null) {
                        // 返回错误
                        socket.close();
                        continue;
                    }
                    // 发送者Id
                    bytes = new byte[4];
                    len = inputStream.read(bytes);
                    if (len == -1) {
                        socket.close();
                        continue;
                    }
                    int senderId = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                    // 接收者Id
                    bytes = new byte[4];
                    len = inputStream.read(bytes);
                    if (len == -1) {
                        socket.close();
                        continue;
                    }
                    int receiverId = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                    // 文件名称
                    String fileName = null;
                    // 文件大小
                    long fileSize = 0;
                    Long fileId = null;
                    if (MsgType.FILE.equals(msgType)) {
                        // 文件Id
                        bytes = new byte[8];
                        len = inputStream.read(bytes);
                        if (len == -1) {
                            socket.close();
                            continue;
                        }
                        fileId = Long.parseLong(BaseEncoding.base16().encode(bytes), 16);
                        // 文件名称长度
                        bytes = new byte[4];
                        len = inputStream.read(bytes);
                        if (len == -1) {
                            socket.close();
                            continue;
                        }
                        int fileNameLength = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                        // 文件名称
                        bytes = new byte[fileNameLength];
                        len = inputStream.read(bytes);
                        if (len == -1) {
                            socket.close();
                            continue;
                        }
                        fileName = new String(bytes, StandardCharsets.UTF_8);
                        // 文件大小
                        bytes = new byte[8];
                        len = inputStream.read(bytes);
                        if (len == -1) {
                            socket.close();
                            continue;
                        }
                        fileSize = Long.parseLong(BaseEncoding.base16().encode(bytes), 16);
                    }

                    // 消息长度
                    bytes = new byte[4];
                    len = inputStream.read(bytes);
                    if (len == -1) {
                        socket.close();
                        continue;
                    }
                    int bodyLength = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                    // 消息体
                    bytes = new byte[bodyLength];
                    len = inputStream.read(bytes);
                    if (len == -1) {
                        socket.close();
                        continue;
                    }
                    String data;
                    FileMessage fileMessage;
                    switch (msgType) {
                        case LOGIN:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            Result<UserVO> userResult = JSON.parseObject(data, new TypeReference<>() {
                            });
                            if (userResult.getCode() == 200) {
                                Storage.loginFrame.dispose();
                                BeanUtil.copyProperties(userResult.getData(), Storage.currentUser);
                                Storage.mainFrame = new MainFrame(userResult.getData().getFriends());
                            } else {
                                JOptionPane.showMessageDialog(Storage.loginFrame, userResult.getMsg(), "提示", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case MESSAGE:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            FriendPanel friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                            friendPanel.getDialoguePanel().addFriendMessage(data);
                            break;
                        case ONLINE:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            UserVO userVO = JSON.parseObject(data, UserVO.class);
                            friendPanel = Storage.mainFrame.getFriendPanelMap().get(userVO.getId());
                            friendPanel.setOnLine(true);
                            break;
                        case OFFLINE:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            userVO = JSON.parseObject(data, UserVO.class);
                            friendPanel = Storage.mainFrame.getFriendPanelMap().get(userVO.getId());
                            friendPanel.setOnLine(false);
                            break;
                        case SEND_FILE:
                            // 收到文件发送请求
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                            friendPanel.getDialoguePanel().getShowPanel().putFileMessage(FilePanelType.FRIEND, fileMessage);
                            break;
                        case RECEIVE_FILE:
                            // 收到文件发送请求
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                            friendPanel.getDialoguePanel().getShowPanel().putFileMessage(FilePanelType.OWN, fileMessage);
                            File file = Storage.FILE_MESSAGE_MAP.get(fileMessage.getId());
                            sendFile(senderId,fileMessage.getId(), file);
                            break;
                        case FILE:
                            try {
                                friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                                friendPanel.getDialoguePanel().getShowPanel().getFileMessageMap().get(fileId).updateProgress(bodyLength);
                            } catch (Exception e) {
                                Result<UserVO> result = Result.buildFail("对方不在线！");
                                ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                outputStream.write(build.toByteArray());
                            }
                            break;
                        default:
                    }
                }
            } catch (Exception e) {
                try {
                    Client.getInstance().disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                System.err.println("连接断开,线程结束");
            }
        });
    }

    public void sendFile(Integer friendId, Long fileId, File file) {
        ThreadUtil.getExecutor().submit(() -> {
            System.out.println("开始发送" + file.getName());
            String fileName = file.getName();
            long fileSize = file.length();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                // 添加文件进度
                byte[] buffer = new byte[4096];
                int bytesRead;
                // 从源文件读取内容并写入目标文件
                try {
                    FriendPanel friendPanel = Storage.mainFrame.getFriendPanelMap().get(friendId);
                    FilePanel filePanel = friendPanel.getDialoguePanel().getShowPanel().getFileMessageMap().get(fileId);
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        ByteData byteData = ByteData.buildFile(Storage.currentUser.getId(), friendId, fileId,fileName, fileSize, Arrays.copyOf(buffer, bytesRead));
                        instance.send(byteData);
                        filePanel.updateProgress(bytesRead);
                    }
                    // // 文件最后发送结束
                    // ByteData byteData = ByteData.buildFile(Storage.currentUser.getId(), senderId, fileName, fileSize, new byte[]{});
                    // byteData.setFileSize(BaseEncoding.base16().decode(String.format("%016X", 0)));
                    // instance.send(byteData);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("发送失败" + file.getName());
                    throw new RuntimeException(e);
                }
                // addOwnFile(fileName, FileUtils.byteCountToDisplaySize(fileSize));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("结束发送" + file.getName());
        });
    }

    /**
     * 发送消息
     *
     * @param byteData 消息
     */
    public void send(ByteData byteData) {
        try {
            outputStream.write(byteData.toByteArray());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
            System.err.println("消息发送失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket = null;
        instance = null;
    }

    /**
     * 释放资源
     *
     * @throws Throwable 异常
     */
    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
}
