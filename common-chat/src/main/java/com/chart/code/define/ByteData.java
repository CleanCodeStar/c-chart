package com.chart.code.define;

import com.chart.code.enums.MsgType;
import com.google.common.io.BaseEncoding;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 消息头
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

    public ByteData(MsgType type, Integer senderId, Integer receiverId, byte[] body) {
        this.header = new byte[]{0x10};
        this.type = type.getType();
        this.senderId = BaseEncoding.base16().decode(String.format("%08X", senderId));
        this.receiverId = BaseEncoding.base16().decode(String.format("%08X", receiverId));
        this.body = body;
        this.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
    }

    public static ByteData build(MsgType msgType, byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = msgType.getType();
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }

    public static ByteData buildLogin(byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = MsgType.LOGIN.getType();
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }

    public static ByteData buildMsg(Integer senderId, Integer receiverId, byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = MsgType.MESSAGE.getType();
        byteData.senderId = BaseEncoding.base16().decode(String.format("%08X", senderId));
        byteData.receiverId = BaseEncoding.base16().decode(String.format("%08X", receiverId));
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }

    public static ByteData buildFile(Integer senderId, Integer receiverId, String fileName, long fileSize, byte[] body) {
        ByteData byteData = new ByteData();
        byteData.header = new byte[]{0x10};
        byteData.type = MsgType.FILE.getType();
        byteData.senderId = BaseEncoding.base16().decode(String.format("%08X", senderId));
        byteData.receiverId = BaseEncoding.base16().decode(String.format("%08X", receiverId));
        byteData.fileName = fileName.getBytes(StandardCharsets.UTF_8);
        byteData.fileNameLength = BaseEncoding.base16().decode(String.format("%08X", fileName.length()));
        byteData.fileSize = BaseEncoding.base16().decode(String.format("%016X", fileSize));
        byteData.body = body;
        byteData.length = BaseEncoding.base16().decode(String.format("%08X", body.length));
        return byteData;
    }


    public byte[] toByteArray() {
        byte[] bytes = new byte[header.length + type.length + senderId.length + receiverId.length + fileNameLength.length + fileName.length + fileSize.length + length.length + body.length];
        System.arraycopy(header, 0, bytes, 0, header.length);
        System.arraycopy(type, 0, bytes, header.length, type.length);
        System.arraycopy(senderId, 0, bytes, header.length + type.length, senderId.length);
        System.arraycopy(receiverId, 0, bytes, header.length + type.length + senderId.length, receiverId.length);
        System.arraycopy(fileNameLength, 0, bytes, header.length + type.length + senderId.length + receiverId.length, fileNameLength.length);
        System.arraycopy(fileName, 0, bytes, header.length + type.length + senderId.length + receiverId.length + fileNameLength.length, fileName.length);
        System.arraycopy(fileSize, 0, bytes, header.length + type.length + senderId.length + receiverId.length + fileNameLength.length + fileName.length, fileSize.length);
        System.arraycopy(length, 0, bytes, header.length + type.length + senderId.length + receiverId.length + fileNameLength.length + fileName.length + fileSize.length, length.length);
        System.arraycopy(body, 0, bytes, header.length + type.length + senderId.length + receiverId.length + fileNameLength.length + fileName.length + fileSize.length + length.length, body.length);
        return bytes;
    }

    public static void main(String[] args) {
        byte[] decode = BaseEncoding.base16().decode(String.format("%016X", -1L));
        System.out.println(Arrays.toString(decode));
        System.out.println(Long.parseLong(BaseEncoding.base16().encode(decode), 16));
        System.out.println(String.format("%020X", Long.MAX_VALUE-1));
        System.out.println(String.format("%04X", Integer.MAX_VALUE));
        System.out.println(Long.MAX_VALUE);
    }
}
