package com.chart.code;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.chart.code.common.MessageOriginEnum;
import com.chart.code.component.DialogueBox;
import com.chart.code.component.FileBox;
import com.chart.code.component.FriendRowBox;
import com.chart.code.component.MainBorderPane;
import com.chart.code.define.ByteData;
import com.chart.code.enums.MsgType;
import com.chart.code.thread.ThreadUtil;
import com.chart.code.vo.FileMessage;
import com.chart.code.vo.Result;
import com.chart.code.vo.UserVO;
import com.google.common.io.BaseEncoding;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 *
 * @author CleanCode
 */
public class MessageHandle {
    /**
     * 单例模式
     * 获取单例实例
     */
    private static volatile MessageHandle instance;

    private SocketChannel socketChannel;

    /**
     * 私有构造方法，防止被实例化
     */
    private MessageHandle() {
        connect();
    }

    /**
     * 获取单例实例
     *
     * @return 单例实例
     */
    public static MessageHandle getInstance() {
        if (instance == null) {
            synchronized (MessageHandle.class) {
                if (instance == null) {
                    instance = new MessageHandle();
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
                    // System.out.print("读取消息长度长度:" + bytes.length + " / ");
                    // 消息体
                    // System.out.println(bodyLength);
                    bytes = getBytes(bodyLength);
                    dataSize += bytes.length;
                    // System.out.print("读取消息体长度:" + bytes.length+" / ");
                    // System.out.println(senderId + "  接收到消息总长度" + dataSize);
                    Result<UserVO> userResult;
                    String data;
                    FileMessage fileMessage;
                    File file;
                    FriendRowBox friendRowBox;
                    UserVO userVO;
                    DialogueBox dialogueBox;
                    switch (msgType) {
                        case REGISTER:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            userResult = JSON.parseObject(data, new TypeReference<>() {
                            });
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("提示");
                                alert.setHeaderText(userResult.getMsg());
                                alert.showAndWait();
                            });
                            break;
                        case LOGIN:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            userResult = JSON.parseObject(data, new TypeReference<>() {
                            });
                            if (userResult.getCode() == 200) {
                                Storage.currentUser = userResult.getData();
                                Platform.runLater(() -> {
                                    // Storage.stage.close();
                                    // 创建新窗口
                                    Storage.stage.setTitle("Java聊天室 - " + Storage.currentUser.getNickname());
                                    // 设置新窗口的布局和内容
                                    MainBorderPane mainBorderPane = new MainBorderPane();
                                    Scene scene = new Scene(mainBorderPane, 970, 770);
                                    // 加载和应用全局CSS样式
                                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
                                    Storage.stage.setScene(scene);
                                    // Storage.loginFrame.dispose();
                                    // if (Storage.mainFrame == null) {
                                    //     Storage.mainFrame = new MainFrame(userResult.getData().getFriends());
                                    // }
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("提示");
                                    alert.setHeaderText(userResult.getMsg());
                                    alert.showAndWait();
                                });
                            }
                            break;
                        case MESSAGE:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            friendRowBox = Storage.mainBorderPane.getFriendPanelMap().get(senderId);
                            friendRowBox.getDialogueBox().addFriendMessage(data);
                            break;
                        case ONLINE:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            userVO = JSON.parseObject(data, UserVO.class);
                            friendRowBox = Storage.mainBorderPane.getFriendPanelMap().get(userVO.getId());
                            friendRowBox.setOnLine(true);
                            break;
                        case OFFLINE:
                            data = new String(bytes, StandardCharsets.UTF_8);
                            userVO = JSON.parseObject(data, UserVO.class);
                            friendRowBox = Storage.mainBorderPane.getFriendPanelMap().get(userVO.getId());
                            friendRowBox.setOnLine(false);
                            break;
                        case TRANSFERRING_FILE_REQUEST:
                            // 发送文件请求
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            dialogueBox = Storage.mainBorderPane.getFriendPanelMap().get(senderId).getDialogueBox();
                            dialogueBox.addFriendFile(fileMessage);
                            // FriendPanel friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                            // friendPanel.getDialoguePanel().getShowPanel().putFileMessage(FilePanelType.FRIEND, fileMessage);
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
                            Platform.runLater(() -> {
                                HBox buttons = Storage.mainBorderPane.getFriendPanelMap().get(senderId).getDialogueBox().getFileMap().get(MessageOriginEnum.OWN.getName() + fileMessage.getId()).getButtons();
                                buttons.getChildren().removeAll(buttons.getChildren());
                                Label label = new Label("被拒收");
                                buttons.getChildren().add(label);
                            });
                            // fileMessage = JSON.parseObject(data, FileMessage.class);
                            // Storage.mainFrame.getFriendPanelMap().get(senderId).getDialoguePanel().getShowPanel().getFileMessageMap().get(fileMessage.getId()).cancelFile();
                            // JOptionPane.showMessageDialog(Storage.mainFrame, fileMessage.getFileName() + "文件传输被拒绝", "提示", JOptionPane.ERROR_MESSAGE);
                            break;
                        case FILE_TRANSFERRING:
                            // 文件传输中
                            try {
                                BufferedOutputStream fileOutputStream = Storage.FILE_OUTPUTSTREAM_MAP.get(fileId);
                                if (fileOutputStream == null) {
                                    // 表示已经取消接受
                                    break;
                                }
                                fileOutputStream.write(bytes);
                                dialogueBox = Storage.mainBorderPane.getFriendPanelMap().get(senderId).getDialogueBox();
                                dialogueBox.getFileMap().get(MessageOriginEnum.FRIEND.getName() + fileId).updateProgress(bodyLength);
                                // System.out.println(senderId + "  接收到文件大小" + fileSize + " 文件名称 " + fileName + " 文件Id " + fileId + " 本次文件数据包 " + bodyLength);
                                // friendPanel = Storage.mainFrame.getFriendPanelMap().get(senderId);
                                // friendPanel.getDialoguePanel().getShowPanel().getFileMessageMap().get(fileId).updateProgress(bodyLength);
                                // 取出输出文件流并向输出流写入数据
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println(fileName + " 文件保存被迫中断！");
                            }
                            break;
                        case CANCEL_SEND_FILE_TRANSFER:
                            // 取消文件传输
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            Platform.runLater(() -> Storage.mainBorderPane.getFriendPanelMap().get(senderId).getDialogueBox().getFileMap().get(MessageOriginEnum.FRIEND.getName() + fileMessage.getId()).cancelFile());
                            // Storage.mainFrame.getFriendPanelMap().get(senderId).getDialoguePanel().getShowPanel().getFileMessageMap().get(fileMessage.getId()).cancelFile();
                            // JOptionPane.showMessageDialog(Storage.mainFrame, fileMessage.getFileName() + "文件传输被取消", "提示", JOptionPane.ERROR_MESSAGE);
                            break;
                        case CANCEL_RECEIVE_FILE_TRANSFER:
                            // 取消文件传输
                            data = new String(bytes, StandardCharsets.UTF_8);
                            fileMessage = JSON.parseObject(data, FileMessage.class);
                            Platform.runLater(() -> Storage.mainBorderPane.getFriendPanelMap().get(senderId).getDialogueBox().getFileMap().get(MessageOriginEnum.OWN.getName() + fileMessage.getId()).cancelFile());
                            // Storage.mainFrame.getFriendPanelMap().get(senderId).getDialoguePanel().getShowPanel().getFileMessageMap().get(fileMessage.getId()).cancelFile();
                            // JOptionPane.showMessageDialog(Storage.mainFrame, fileMessage.getFileName() + "文件传输被取消", "提示", JOptionPane.ERROR_MESSAGE);
                            break;
                        default:
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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

        if (len == length) {
            byte[] bytes = new byte[len];
            buffer.rewind();
            buffer.get(bytes);
            return bytes;
        }
        byte[] bytes = new byte[length];
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
                    DialogueBox dialogueBox = Storage.mainBorderPane.getFriendPanelMap().get(friendId).getDialogueBox();
                    FileBox fileBox = dialogueBox.getFileMap().get(MessageOriginEnum.OWN.getName() + fileId);
                    Platform.runLater(() -> {
                        dialogueBox.getFileMap().get(MessageOriginEnum.OWN.getName() + fileId).addProgressIndicator();
                    });
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        ByteData byteData = ByteData.buildFile(Storage.currentUser.getId(), friendId, fileId, fileName, fileSize, Arrays.copyOf(buffer, bytesRead));
                        instance.send(byteData);
                        fileBox.updateProgress(bytesRead);
                    }
                    Storage.FILE_INPUTSTREAM_MAP.remove(fileId, bis);
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
