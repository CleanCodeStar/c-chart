package com.chart.code.define;

import com.chart.code.enums.MsgType;
import com.google.common.io.BaseEncoding;
import lombok.Data;

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
    private byte[] header;
    /**
     * 消息类型(1byte位)
     */
    private byte[] type;
    /**
     * 消息长度(3byte位)
     */
    private byte[] length;
    /**
     * 消息体
     */
    private byte[] body;

    public ByteData(MsgType type, byte[] body) {
        this.header = new byte[]{0x10};
        this.type = type.getType();
        this.body = body;
        this.length = BaseEncoding.base16().decode(String.format("%06X", body.length));
    }


    public byte[] toByteArray() {
        byte[] bytes = new byte[header.length + type.length + length.length + body.length];
        System.arraycopy(header, 0, bytes, 0, header.length);
        System.arraycopy(type, 0, bytes, header.length, type.length);
        System.arraycopy(length, 0, bytes, header.length + type.length, length.length);
        System.arraycopy(body, 0, bytes, header.length + type.length + length.length, body.length);
        return bytes;
    }
}
