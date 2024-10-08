package com.chart.code;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(8199));
            System.out.println("服务启动成功,等待客户端连接");
            SQLiteService sqLiteService = new SQLiteService();
            while (true) {
                ThreadUtil.addRunnable(new Runnable() {
                    private User user;
                    final SocketChannel socketChannel = serverSocketChannel.accept();

                    @Override
                    public void run() {
                        try {
                            while (true) {
                                // 读取header
                                byte[] bytes = getBytes(1);
                                if (!Arrays.equals(new byte[]{0x10}, bytes)) {
                                    // 返回错误
                                    closeClient();
                                    throw new RuntimeException("客户端关闭连接");
                                }
                                // 读取type
                                bytes = getBytes(1);
                                MsgType msgType = MsgType.getMsgType(bytes);
                                if (msgType == null) {
                                    // 返回错误
                                    closeClient();
                                    throw new RuntimeException("客户端关闭连接");
                                }
                                // 发送者Id
                                bytes = getBytes(4);
                                int senderId = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                                // 接收者Id
                                bytes = getBytes(4);
                                int receiverId = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                                // 文件名称
                                String fileName = null;
                                // 文件大小
                                long fileSize = 0;
                                Long fileId = null;
                                if (MsgType.FILE_TRANSFERRING.equals(msgType)) {
                                    // 文件Id
                                    bytes = getBytes(8);
                                    fileId = Long.parseLong(BaseEncoding.base16().encode(bytes), 16);
                                    // 文件名称长度
                                    bytes = getBytes(4);
                                    int fileNameLength = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                                    // 文件名称
                                    bytes = getBytes(fileNameLength);
                                    fileName = new String(bytes, StandardCharsets.UTF_8);
                                    // 文件大小
                                    bytes = getBytes(8);
                                    fileSize = Long.parseLong(BaseEncoding.base16().encode(bytes), 16);
                                }
                                // 消息长度
                                bytes = getBytes(4);
                                int bodyLength = Integer.parseInt(BaseEncoding.base16().encode(bytes), 16);
                                // 消息体
                                bytes = getBytes(bodyLength);
                                // System.out.println((user != null ? user.getNickname() : "") + " 接收到的消息长度：" + dataSize);
                                SocketChannel receiverSocket;
                                User currentUser;
                                UserDTO user;
                                switch (msgType) {
                                    case REGISTER:
                                        register(JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), User.class));
                                        break;
                                    case LOGIN:
                                        user = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), UserDTO.class);
                                        // 判断登录信息，然后返回消息
                                        currentUser = sqLiteService.queryUser(user.getUsername(), user.getPassword());
                                        login(currentUser);
                                        break;
                                    case RECONNECT:
                                        // 重连
                                        user = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), UserDTO.class);
                                        currentUser = sqLiteService.queryUserById(user.getId());
                                        login(currentUser);
                                        break;
                                    case MESSAGE:
                                        receiverSocket = Storage.userSocketsMap.get(receiverId);
                                        if (receiverSocket != null) {
                                            try {
                                                ByteData byteData = ByteData.buildMessage(senderId, receiverId, bytes);
                                                receiverSocket.write(byteData.toByteBuffer());
                                            } catch (Exception e) {
                                                Result<UserVO> result = Result.buildFail("对方不在线！");
                                                ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                                send(build);
                                            }
                                        } else {
                                            Result<UserVO> result = Result.buildFail("对方不在线！");
                                            ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                            send(build);

                                        }
                                        break;
                                    case TRANSFERRING_FILE_REQUEST:
                                        // 传输文件请求
                                    case AGREE_RECEIVE_FILE:
                                        // 同意接收文件
                                    case REFUSE_RECEIVE_FILE:
                                        // 拒绝接收文件
                                    case CANCEL_SEND_FILE_TRANSFER:
                                    case CANCEL_RECEIVE_FILE_TRANSFER:
                                        // 取消文件传输
                                        receiverSocket = Storage.userSocketsMap.get(receiverId);
                                        if (receiverSocket != null) {
                                            try {
                                                ByteData byteData = ByteData.build(msgType, senderId, receiverId, bytes);
                                                receiverSocket.write(byteData.toByteBuffer());
                                            } catch (Exception e) {
                                                Result<UserVO> result = Result.buildFail("对方不在线！");
                                                ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                                send(build);
                                            }
                                        } else {
                                            Result<UserVO> result = Result.buildFail("对方不在线！");
                                            ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                            send(build);

                                        }
                                        break;
                                    case FILE_TRANSFERRING:
                                        receiverSocket = Storage.userSocketsMap.get(receiverId);
                                        if (receiverSocket != null) {
                                            try {
                                                ByteData byteData = ByteData.buildFile(senderId, receiverId, fileId, fileName, fileSize, bytes);
                                                receiverSocket.write(byteData.toByteBuffer());
                                                // System.err.println("发送消息:" + byteData.toByteArray().length);
                                            } catch (Exception e) {
                                                Result<UserVO> result = Result.buildFail("对方不在线！");
                                                ByteData build = ByteData.build(MsgType.ONT_LINE, senderId, receiverId, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                                send(build);
                                            }
                                        } else {
                                            Result<UserVO> result = Result.buildFail("对方不在线！");
                                            ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                            send(build);

                                        }
                                        break;
                                    default:
                                        Result<UserVO> result = Result.buildFail("对方不在线！");
                                        ByteData build = ByteData.build(MsgType.ONT_LINE, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                                        send(build);
                                }
                            }
                        } catch (Exception e) {
                            closeClient();
                            // 发送给所有好友离线信息
                            Storage.userSocketsMap.values().forEach(userSocket -> {
                                try {
                                    ByteData build = ByteData.build(MsgType.OFFLINE, JSON.toJSONString(this.user).getBytes(StandardCharsets.UTF_8));
                                    userSocket.write(build.toByteBuffer());
                                } catch (IOException ignored) {
                                }
                            });
                            System.err.println(this.user.getNickname() + "  连接断开,线程结束");
                        }
                    }

                    /**
                     * 注册
                     */
                    private void register(User user) {
                        User checkUser = sqLiteService.checkUser(user.getUsername());
                        if (checkUser != null) {
                            Result<UserVO> result = Result.buildFail("用户已被注册！");
                            ByteData build = ByteData.build(MsgType.REGISTER, JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                            send(build);
                        } else {
                            boolean insert = sqLiteService.insert("tb_user", user);
                            if (insert) {
                                user = sqLiteService.queryUser(user.getUsername(), user.getPassword());
                                login(user);
                            } else {
                                Result<UserVO> result = Result.buildFail("注册失败！");
                                ByteData build = ByteData.build(MsgType.REGISTER, JSON.toJSONString(result).getBytes());
                                send(build);
                            }
                        }
                    }

                    private void login(User currentUser) {
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
                            send(byteData);
                            // 发送给所有好友上线信息
                            Storage.userSocketsMap.values().forEach(userSocket -> {
                                try {
                                    ByteData build = ByteData.build(MsgType.ONLINE, JSON.toJSONString(userVO).getBytes(StandardCharsets.UTF_8));
                                    userSocket.write(build.toByteBuffer());
                                } catch (IOException ignored) {
                                }
                            });
                            // 先向所有好友发送上线信息，然后加入缓存
                            Storage.userSocketsMap.put(this.user.getId(), socketChannel);
                        } else {
                            Result<UserVO> result = Result.buildFail("用户名或密码错误！");
                            ByteData build = ByteData.buildLogin(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
                            send(build);
                            // // 断开连接
                            // closeClient();
                            // throw new RuntimeException("客户端关闭连接");
                        }
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
                            closeClient();
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
                     * 发送消息
                     * @param build 消息
                     */
                    public void send(ByteData build) {
                        try {
                            socketChannel.write(build.toByteBuffer());
                        } catch (IOException e) {
                            System.err.println(this.user.getNickname() + "发送失败");
                            throw new RuntimeException(e);
                        }
                    }

                    public void closeClient() {
                        try {
                            Storage.userSocketsMap.remove(this.user.getId(), socketChannel);
                            socketChannel.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
    }
}
