package com.chart.code;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chart.code.db.SQLiteService;
import com.chart.code.define.ByteData;
import com.chart.code.define.User;
import com.chart.code.dto.UserDTO;
import com.chart.code.enums.MsgType;
import com.chart.code.thread.ThreadUtil;
import com.chart.code.vo.DialogueVO;
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
        ServerSocket serverSocket = new ServerSocket(8199);
        while (true) {
            Socket socket = serverSocket.accept();
            ThreadUtil.addRunnable(new Runnable() {
                private Integer userId;

                @Override
                public void run() {
                    try {
                        System.out.println("接收到连接信息");
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
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
                                    UserDTO user = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), UserDTO.class);
                                    // 判断登录信息，然后返回消息
                                    SQLiteService sqLiteService = new SQLiteService();
                                    User queryUser = sqLiteService.queryUser(user.getUsername(), user.getPassword());
                                    if (queryUser != null) {
                                        userId = queryUser.getId();
                                        UserVO userVO = BeanUtil.copyProperties(queryUser, UserVO.class);
                                        List<UserVO> userVOS = sqLiteService.queryAll();
                                        Set<Integer> onLineIds = new HashSet<>(Storage.userSocketsMap.keySet());
                                        onLineIds.add(userId);
                                        userVOS = userVOS.stream().peek(value -> value.setOnLine(onLineIds.contains(value.getId()))).collect(Collectors.toList());
                                        userVO.setFriends(userVOS);
                                        Result<UserVO> result = Result.buildOk("登录成功", userVO);
                                        outputStream.write(new ByteData(MsgType.LOGIN, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8)).toByteArray());
                                        // 发送给所有好友上线信息
                                        Storage.userSocketsMap.values().forEach(userSocket -> {
                                            try {
                                                userSocket.getOutputStream().write(new ByteData(MsgType.ONLINE, JSON.toJSONString(userVO).getBytes(StandardCharsets.UTF_8)).toByteArray());
                                            } catch (IOException ignored) {
                                            }
                                        });
                                        // 先向所有好友发送上线信息，然后加入缓存
                                        Storage.userSocketsMap.put(userId, socket);
                                    } else {
                                        Result<UserVO> result = Result.buildFail("用户名或密码错误！");
                                        outputStream.write(new ByteData(MsgType.LOGIN, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8)).toByteArray());
                                        // 断开连接
                                        socket.close();
                                    }
                                    break;
                                case MESSAGE:
                                    DialogueVO dialogueVO = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), DialogueVO.class);
                                    Integer receiverId = dialogueVO.getReceiverId();
                                    Socket receiverSocket = Storage.userSocketsMap.get(receiverId);
                                    if (receiverSocket != null) {
                                        try {
                                            OutputStream receiverOutputStream = receiverSocket.getOutputStream();
                                            receiverOutputStream.write(new ByteData(MsgType.MESSAGE, bytes).toByteArray());
                                        } catch (Exception e) {
                                            Result<UserVO> result = Result.buildFail("对方不在线！");
                                            outputStream.write(new ByteData(MsgType.LOGIN, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8)).toByteArray());
                                        }
                                    }
                                    break;
                                default:
                            }
                        }
                    } catch (Exception e) {
                        Storage.userSocketsMap.remove(userId, socket);
                        System.err.println(userId + " 连接断开,线程结束");
                    }
                }
            });
        }
    }
}
