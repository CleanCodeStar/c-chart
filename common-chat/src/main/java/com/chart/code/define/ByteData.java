package com.chart.code.define;

import com.chart.code.enums.MsgType;
import com.google.common.io.BaseEncoding;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * 消息协议类
 *
 * @author CleanCode
 */
@Data
public class ByteData {

    /**
     * 标记固定值0x10(1byte位)
     */
    private byte[] header = new byte[1];

    /**
     * 消息类型(1byte位)
     */
    private byte[] type = new byte[1];

    /**
     * 发送者Id(4byte位)
     */
    private byte[] senderId = new byte[4];

    /**
     * 接收者Id(4byte位)
     */
    private byte[] receiverId = new byte[4];

    /**
     * 文件Id(8byte位)
     */
    private byte[] fileId = new byte[0];

    /**
     * 文件名长度(4byte位)
     */
    private byte[] fileNameLength = new byte[0];

    /**
     * 文件名(由文件名长度确定长度)
     */
    private byte[] fileName = new byte[0];

    /**
     * 文件长度(8byte位)
     */
    private byte[] fileSize = new byte[0];

    /**
     * 消息长度(4byte位)
     */
    private byte[] length = new byte[0];

    /**
     * 消息体(由消息长度确定长度)
     */
    private byte[] body = new byte[0];

    private ByteData() {
    }

    /**
     * 构建消息 用于未登录或系统发出时使用
     *
     * @param msgType 消息类型
     * @param body    消息体
     * @return ByteData
     */
    public static ByteData build(MsgType msgType, byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = msgType.getType();
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }

    /**
     * 构建消息 一般用于登录后发送消息
     *
     * @param msgType    消息类型
     * @param senderId   发送者Id
     * @param receiverId 接收者Id
     * @param body       消息体
     * @return ByteData
     */
    public static ByteData build(MsgType msgType, Integer senderId, Integer receiverId, byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = msgType.getType();
        byteData.senderId = BaseEncoding.base16().decode(String.format("%08X", senderId));
        byteData.receiverId = BaseEncoding.base16().decode(String.format("%08X", receiverId));
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }

    /**
     * 构建登录消息
     *
     * @param body 消息体
     * @return ByteData
     */
    public static ByteData buildLogin(byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = MsgType.LOGIN.getType();
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }

    /**
     * 构建消息
     *
     * @param senderId   发送者Id
     * @param receiverId 接收者Id
     * @param body       消息体
     * @return ByteData
     */
    public static ByteData buildMessage(Integer senderId, Integer receiverId, byte[] body) {
        return build(MsgType.MESSAGE, senderId, receiverId, body);
    }

    /**
     * 构建文件消息
     *
     * @param senderId   发送者Id
     * @param receiverId 接收者Id
     * @param fileName   文件名
     * @param fileSize   文件大小
     * @param body       消息体
     * @return ByteData
     */
    public static ByteData buildFile(Integer senderId, Integer receiverId,Long fileId, String fileName, long fileSize, byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = MsgType.FILE.getType();
        byteData.senderId = BaseEncoding.base16().decode(String.format("%08X", senderId));
        byteData.receiverId = BaseEncoding.base16().decode(String.format("%08X", receiverId));
        byteData.fileId = BaseEncoding.base16().decode(String.format("%016X", fileId));
        byteData.fileName = fileName.getBytes(StandardCharsets.UTF_8);
        byteData.fileNameLength = BaseEncoding.base16().decode(String.format("%08X", byteData.fileName.length));
        byteData.fileSize = BaseEncoding.base16().decode(String.format("%016X", fileSize));
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }

    /**
     * 转换为字节数组
     *
     * @return 字节数组
     */
    public byte[] toByteArray() {
        byte[] bytes = new byte[header.length + type.length + senderId.length + receiverId.length + fileId.length + fileNameLength.length + fileName.length + fileSize.length + length.length + body.length];
        int desPos = 0;
        System.arraycopy(header, 0, bytes, 0, header.length);
        desPos += header.length;
        System.arraycopy(type, 0, bytes, desPos, type.length);
        desPos += type.length;
        System.arraycopy(senderId, 0, bytes, desPos, senderId.length);
        desPos += senderId.length;
        System.arraycopy(receiverId, 0, bytes, desPos, receiverId.length);
        desPos += receiverId.length;
        System.arraycopy(fileId, 0, bytes, desPos, fileId.length);
        desPos += fileId.length;
        System.arraycopy(fileNameLength, 0, bytes, desPos, fileNameLength.length);
        desPos += fileNameLength.length;
        System.arraycopy(fileName, 0, bytes, desPos, fileName.length);
        desPos += fileName.length;
        System.arraycopy(fileSize, 0, bytes, desPos, fileSize.length);
        desPos += fileSize.length;
        System.arraycopy(length, 0, bytes, desPos, length.length);
        desPos += length.length;
        System.arraycopy(body, 0, bytes, desPos, body.length);
        return bytes;
    }
}
