package com.chart.code.define;

import lombok.Data;

import java.io.Serializable;

/**
 * 对话信息
 *
 * @author CleanCode
 */
@Data
public class Dialogue implements Serializable {
    /**
     * 发送者Id
     */
    Integer senderId;
    /**
     * 接收者 Id
     */
    Integer receiverId;
    /**
     * 对话内容
     */
    String content;
}
