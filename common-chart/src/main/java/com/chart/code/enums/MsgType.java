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
     * 登录
     */
    LOGIN("登录", new byte[]{0x11}),
    /**
     * 登出
     */
    LOGOUT("登出", new byte[]{0x12}),
    /**
     * 上线
     */
    ONLINE("上线", new byte[]{0x13}),
    /**
     * /**
     * 上线
     */
    OFFLINE("下线", new byte[]{0x14}),
    /**
     * /**
     * 不在线
     */
    ONT_LINE("不在线", new byte[]{0x15}),
    /**
     * 发送消息
     */
    MESSAGE("消息", new byte[]{0x16}),
    /**
     * 传输文件请求
     */
    TRANSFERRING_FILE_REQUEST("传输文件请求", new byte[]{0x17}),
    /**
     * 文件传输中
     */
    FILE_TRANSFERRING("文件传输中", new byte[]{0x18}),
    /**
     * 同意接收文件
     */
    AGREE_RECEIVE_FILE("同意接收文件", new byte[]{0x19}),
    /**
     * 拒绝接收文件
     */
    REFUSE_RECEIVE_FILE("拒绝接收文件", new byte[]{0x20}),
    /**
     * 取消文件传输
     */
    CANCEL_FILE_TRANSFER("取消文件传输", new byte[]{0x21}),
    /**
     * 重新连接
     */
    RECONNECT("重新连接", new byte[]{0x22}),
    ;

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
