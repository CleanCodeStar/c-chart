package com.chart.code.enums;


import lombok.Getter;

/**
 * 消息类型
 *
 * @author CleanCode
 */
@Getter
public enum MsgType {
    /**
     * 上线
     */
    ONLINE("上线", new byte[]{0x10}),
    /**
     * 登录
     */
    LOGIN("登录", new byte[]{0x11}),
    /**
     * 登出
     */
    LOGOUT("登出", new byte[]{0x12}),
    /**
     * 发送消息
     */
    MESSAGE("发送消息", new byte[]{0x13}),
    /**
     * 发送文件
     */
    SEND_FILE("发送文件", new byte[]{0x14}),
    /**
     * 接收文件
     */
    RECEIVE_FILE("接收文件", new byte[]{0x15}),
    /**
     * 接收文件完成
     */
    RECEIVE_FILE_FINISH("接收文件完成", new byte[]{0x16}),
    /**
     * 接收文件错误
     */
    RECEIVE_FILE_ERROR("接收文件错误", new byte[]{0x17}),
    /**
     * 接收文件进度
     */
    RECEIVE_FILE_PROGRESS("接收文件进度", new byte[]{0x18}),
    /**
     * 发送文件完成
     */
    SEND_FILE_FINISH("发送文件完成", new byte[]{0x19}),
    /**
     * 发送文件错误
     */
    SEND_FILE_ERROR("发送文件错误", new byte[]{0x20}),
    /**
     * 发送文件进度
     */
    SEND_FILE_PROGRESS("发送文件进度", new byte[]{0x21});

    /**
     * 消息类型
     */
    private final String name;
    /**
     * 消息类型
     */
    private final byte[] type;

    MsgType(String name, byte[] type) {
        this.name = name;
        this.type = type;
    }

    public static MsgType getMsgType(byte[] type) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getType()[0] == type[0]) {
                return msgType;
            }
        }
        return null;
    }
}
