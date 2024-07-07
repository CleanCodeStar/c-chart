package com.chart.code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chart.code.component.MainFrame;
import com.chart.code.define.ByteData;
import com.chart.code.enums.MsgType;
import com.chart.code.thread.ThreadUtil;
import com.chart.code.vo.Result;
import com.chart.code.vo.UserVO;
import com.google.common.io.BaseEncoding;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 客户端
 *
 * @author CleanCode
 */
public class Client {

    /**
     * 单例模式
     */
    private static Client instance;

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
                    // 消息长度
                    bytes = new byte[3];
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
                    switch (msgType) {
                        case LOGIN:
                            Result<UserVO> userResult = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), new TypeReference<Result<UserVO>>() {
                            });
                            if (userResult.getCode() == 200) {
                                Storage.loginFrame.dispose();
                                Storage.mainFrame = new MainFrame(userResult.getData().getFriends());
                            } else {
                                JOptionPane.showMessageDialog(Storage.loginFrame, userResult.getMsg(), "提示", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case LOGOUT:
                            System.out.println("logout");
                            break;
                        default:
                    }
                }
            } catch (Exception e) {
                System.err.println("连接断开,线程结束");
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
            outputStream.write(byteData.toByteArray());
        } catch (IOException e) {
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
