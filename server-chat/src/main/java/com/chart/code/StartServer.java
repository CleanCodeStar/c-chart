package com.chart.code;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chart.code.db.SQLiteService;
import com.chart.code.define.ByteData;
import com.chart.code.define.User;
import com.chart.code.dto.UserDTO;
import com.chart.code.enums.MsgType;
import com.chart.code.thread.ThreadUtil;
import com.chart.code.vo.Result;
import com.chart.code.vo.UserVO;
import com.google.common.io.BaseEncoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 服务启动类
 *
 * @author CleanCode
 */
public class StartServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8199)) {
            System.out.println("服务启动成功,等待客户端连接");
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                ThreadUtil.addRunnable(new Runnable() {
                    private User user;

                    @Override
                    public void run() {
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
                                if (MsgType.FILE.equals(msgType)) {
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
                                Socket receiverSocket;
                                switch (msgType) {
                                    case LOGIN:
                                        UserDTO user = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), UserDTO.class);
                                        // 判断登录信息，然后返回消息
                                        SQLiteService sqLiteService = new SQLiteService();
                                        User currentUser = sqLiteService.queryUser(user.getUsername(), user.getPassword());
                                        if (currentUser != null) {
                                            this.user = currentUser;
                                            System.out.println(this.user.getNickname() + "  登录成功");
                                            UserVO userVO = BeanUtil.copyProperties(currentUser, UserVO.class);
                                            List<UserVO> users = sqLiteService.queryAll();
                                            Set<Integer> onLineIds = new HashSet<>(Storage.userSocketsMap.keySet());
                                            onLineIds.add(this.user.getId());
                                            users = users.stream().peek(value -> value.setOnLine(onLineIds.contains(value.getId()))).collect(Collectors.toList());
                                            userVO.setFriends(users);
                                            Result<UserVO> result = Result.buildOk("登录成功", userVO);
                                            ByteData byteData = ByteData.buildLogin(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                            outputStream.write(byteData.toByteArray());
                                            // 发送给所有好友上线信息
                                            Storage.userSocketsMap.values().forEach(userSocket -> {
                                                try {
                                                    ByteData build = ByteData.build(MsgType.ONLINE, JSON.toJSONString(userVO).getBytes(StandardCharsets.UTF_8));
                                                    userSocket.getOutputStream().write(build.toByteArray());
                                                } catch (IOException ignored) {
                                                }
                                            });
                                            // 先向所有好友发送上线信息，然后加入缓存
                                            Storage.userSocketsMap.put(this.user.getId(), socket);
                                        } else {
                                            Result<UserVO> result = Result.buildFail("用户名或密码错误！");
                                            ByteData byteData = ByteData.buildLogin(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                            outputStream.write(byteData.toByteArray());
                                            // 断开连接
                                            socket.close();
                                        }
                                        break;
                                    case MESSAGE:
                                        receiverSocket = Storage.userSocketsMap.get(receiverId);
                                        if (receiverSocket != null) {
                                            try {
                                                OutputStream receiverOutputStream = receiverSocket.getOutputStream();
                                                ByteData byteData = ByteData.buildMsg(senderId, receiverId, bytes);
                                                receiverOutputStream.write(byteData.toByteArray());
                                            } catch (Exception e) {
                                                Result<UserVO> result = Result.buildFail("对方不在线！");
                                                ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                                outputStream.write(build.toByteArray());
                                            }
                                        }
                                        break;
                                    case FILE:
                                        receiverSocket = Storage.userSocketsMap.get(receiverId);
                                        if (receiverSocket != null) {
                                            try {
                                                OutputStream receiverOutputStream = receiverSocket.getOutputStream();
                                                ByteData byteData = ByteData.buildFile(senderId, receiverId, fileName, fileSize, bytes);
                                                receiverOutputStream.write(byteData.toByteArray());
                                                receiverOutputStream.flush();
                                            } catch (Exception e) {
                                                Result<UserVO> result = Result.buildFail("对方不在线！");
                                                ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                                outputStream.write(build.toByteArray());
                                            }
                                        }else {
                                            System.err.println("没得到socket");
                                        }
                                        break;
                                    default:
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Storage.userSocketsMap.remove(this.user.getId(), socket);
                            // 发送给所有好友离线信息
                            Storage.userSocketsMap.values().forEach(userSocket -> {
                                try {
                                    ByteData build = ByteData.build(MsgType.OFFLINE, JSON.toJSONString(this.user).getBytes(StandardCharsets.UTF_8));
                                    userSocket.getOutputStream().write(build.toByteArray());
                                } catch (IOException ignored) {
                                }
                            });
                            System.out.println(this.user.getNickname() + "  连接断开,线程结束");
                        }
                    }
                });
            }
        }
    }
}
