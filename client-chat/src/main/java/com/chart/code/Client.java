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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 *
 * @author CleanCode
 */
public class Client {
    /**
     * 单例模式
     * 获取单例实例
     */
    private static volatile Client instance;

    private SocketChannel socketChannel;

    /**
     * 私有构造方法，防止被实例化
     */
    private Client() {
        connect();
    }

    /**
     * 获取单例实例
     *
     * @return 单例实例
     */
    public static Client getInstance() {
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
    private void connect() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                synchronized (Socket.class) {
                    if (socketChannel != null) {
                        return;
                    }
                    // 要连接的服务端IP地址和端口
                    String host = "127.0.0.1";
                    int port = 8199;
                    socketChannel = SocketChannel.open();
                    socketChannel.connect(new InetSocketAddress(host, port));
                    // 与服务端建立连接
                    System.out.println("连接服务器成功");
                    if (Storage.currentUser.getId() != null) {
                        send(ByteData.build(MsgType.RECONNECT, JSON.toJSONString(Storage.currentUser).getBytes(StandardCharsets.UTF_8)));
                    }
                    receive();
                }
            } catch (Exception e) {
                System.err.println("连接服务器失败");
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void receive() {
        ThreadUtil.getExecutor().submit(() -> {
            try {
                while (true) {
                    long dataSize = 0;
                    // 读取header
                    byte[] bytes = getBytes(1);
                    if (!Arrays.equals(new byte[]{0x10}, bytes)) {
                        // 返回错误
                        socketChannel.close();
                        continue;
                    }
                    dataSize += bytes.length;
                    // System.out.print("读取header长度:" + bytes.length+" / ");
                    // 读取type
                    bytes = getBytes(1);
                    MsgType msgType = MsgType.getMsgType(bytes);
                    if (msgType == null) {
                        // 返回错误
                        socketChannel.close();
                        continue;
                    }
                    dataSize += bytes.length;
                    // System.out.print("读取type长度:" + bytes.length+" / ");
                    // 发送者Id
                    bytes = getBytes(4);
                    int senderId = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                    dataSize += bytes.length;
                    // System.out.print("读取senderId长度:" + bytes.length+" / ");

                    // 接收者Id
                    bytes = getBytes(4);
                    int receiverId = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                    dataSize += bytes.length;
                    // System.out.print("读取接收者Id长度:" + bytes.length+" / ");
                    // 文件名称
                    String fileName = null;
                    // 文件大小
                    long fileSize = 0;
                    Long fileId;
                    if (MsgType.FILE_TRANSFERRING.equals(msgType)) {
                        // 文件Id
                        bytes = getBytes(8);
                        fileId = Long.parseLong(BaseEncoding.base16().encode(bytes), 16);
                        dataSize += bytes.length;
                        // System.out.print("读取文件Id长度:" + bytes.length+" / ");
                        // 文件名称长度
                        bytes = getBytes(4);
                        int fileNameLength = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                        dataSize += bytes.length;
                        // System.out.print("读取文件名称长度长度:" + bytes.length+" / ");
                        // 文件名称
                        bytes = getBytes(fileNameLength);
                        fileName = new String(bytes, StandardCharsets.UTF_8);
                        dataSize += bytes.length;
                        // System.out.print("读取文件名称长度:" + bytes.length+" / ");
                        // 文件大小
                        bytes = getBytes(8);
                        fileSize = Long.parseLong(BaseEncoding.base16().encode(bytes), 16);
                        dataSize += bytes.length;
                        // System.out.print("读取文件大小长度:" + bytes.length+" / ");
                    } else {
                        fileId = null;
                    }

                    // 消息长度
                    bytes = getBytes(4);
                    int bodyLength = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                    dataSize += bytes.length;
                    // System.out.print("读取消息长度长度:" + bytes.length+" / ");
                    // 消息体
                    bytes = getBytes(bodyLength);
                    dataSize += bytes.length;
                    // System.out.print("读取消息体长度:" + bytes.length+" / ");
                    // System.out.println(senderId + "  接收到消息总长度" + dataSize);
                    String data;
                    FileMessage fileMessage;
                    File file;
                    switch (msgType) {
                        case LOGIN:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            Result<UserVO> userResult = JSON.parseObject(data, new TypeReference<>() {
                            });
                            if (userResult.getCode() == 200) {
                                Storage.loginFrame.dispose();
                                BeanUtil.copyProperties(userResult.getData(), Storage.currentUser);
                                if (Storage.mainFrame == null) {
                                    Storage.mainFrame = new MainFrame(userResult.getData().getFriends());
                                }
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
                        case TRANSFERRING_FILE_REQUEST:
                            // 发送文件请求
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                            friendPanel.getDialoguePanel().getShowPanel().putFileMessage(FilePanelType.FRIEND, fileMessage);
                            break;
                        case AGREE_RECEIVE_FILE:
                            // 同意接收文件
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            file = Storage.FILE_SEND_MAP.get(fileMessage.getId());
                            sendFile(senderId, fileMessage.getId(), file);
                            break;
                        case REFUSE_RECEIVE_FILE:
                            // 拒绝接收文件
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            Storage.mainFrame.getFriendPanelMap().get(senderId).getDialoguePanel().getShowPanel().getFileMessageMap().get(fileMessage.getId()).cancelFile();
                            JOptionPane.showMessageDialog(Storage.mainFrame, fileMessage.getFileName() + "文件传输被拒绝", "提示", JOptionPane.ERROR_MESSAGE);
                            break;
                        case FILE_TRANSFERRING:
                            // 文件传输中
                            try {
                                BufferedOutputStream fileOutputStream = Storage.FILE_OUTPUTSTREAM_MAP.get(fileId);
                                if (fileOutputStream == null) {
                                    fileOutputStream = new BufferedOutputStream(new FileOutputStream(Storage.FILE_RECEIVE_MAP.get(fileId)));
                                    Storage.FILE_OUTPUTSTREAM_MAP.put(fileId, fileOutputStream);
                                }
                                // System.out.println(senderId + "  接收到文件大小" + fileSize + " 文件名称 " + fileName + " 文件Id " + fileId + " 本次文件数据包 " + bodyLength);
                                friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                                friendPanel.getDialoguePanel().getShowPanel().getFileMessageMap().get(fileId).updateProgress(bodyLength);
                                fileOutputStream.write(bytes);
                                // 取出输出文件流并向输出流写入数据
                            } catch (Exception e) {
                                System.err.println(fileName + " 文件保存被迫中断！");
                            }
                            break;
                        case CANCEL_FILE_TRANSFER:
                            // 取消文件传输
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            Storage.mainFrame.getFriendPanelMap().get(senderId).getDialoguePanel().getShowPanel().getFileMessageMap().get(fileMessage.getId()).cancelFile();
                            JOptionPane.showMessageDialog(Storage.mainFrame, fileMessage.getFileName() + "文件传输被取消", "提示", JOptionPane.ERROR_MESSAGE);
                            break;
                        default:
                    }
                }
            } catch (Exception e) {
                disconnect();
                System.err.println("连接断开,线程结束");
            }
        });
    }

    /**
     * 获取完整的数据
     *
     * @param length 长度
     * @return 完整的数据
     * @throws IOException 异常
     */
    public byte[] getBytes(int length) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        int len = socketChannel.read(buffer);
        if (len == -1) {
            disconnect();
            throw new IOException("读取失败");
        }
        byte[] bytes = new byte[len];
        buffer.rewind();
        buffer.get(bytes);
        if (len == length) {
            return bytes;
        }
        // System.err.println( "一次性没读够长度");
        byte[] bytes2 = getBytes(length - len);
        System.arraycopy(bytes2, 0, bytes, len, bytes2.length);
        return bytes;
    }


    /**
     * 发送文件
     *
     * @param friendId 好友Id
     * @param fileId   文件Id
     * @param file     文件
     */
    public void sendFile(Integer friendId, Long fileId, File file) {
        ThreadUtil.getExecutor().submit(() -> {
            String fileName = file.getName();
            long fileSize = file.length();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                Storage.FILE_INPUTSTREAM_MAP.put(fileId, bis);
                System.out.println("开始发送" + file.getName());
                // 添加文件进度
                byte[] buffer = new byte[8192];
                int bytesRead;
                // 从源文件读取内容并写入目标文件
                try {
                    FilePanel filePanel = Storage.mainFrame.getFriendPanelMap().get(friendId).getDialoguePanel().getShowPanel().getFileMessageMap().get(fileId);
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        ByteData byteData = ByteData.buildFile(Storage.currentUser.getId(), friendId, fileId, fileName, fileSize, Arrays.copyOf(buffer, bytesRead));
                        instance.send(byteData);
                        filePanel.updateProgress(bytesRead);
                    }
                    System.out.println("结束发送" + file.getName());
                } catch (IOException e) {
                    System.err.println("发送失败或被取消  " + file.getName());
                }
            } catch (IOException e) {
                System.err.println("发送失败" + file.getName());
            }
        });
    }

    /**
     * 发送消息
     *
     * @param byteData 消息
     */
    public void send(ByteData byteData) {
        try {
            socketChannel.write(byteData.toByteBuffer());
            // System.out.println("消息发送总长度：" + byteData.toByteArray().length);
        } catch (Exception e) {
            System.err.println("消息发送失败");
            disconnect();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socketChannel = null;
    }
}
