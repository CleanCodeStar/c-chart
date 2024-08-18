package com.chart.code.common;

import javafx.geometry.Pos;
import lombok.Getter;

/**
 * 消息来源枚举
 *
 * @author CleanCode
 */
@Getter
public enum MessageOriginEnum {
    /**
     * 自己
     */
    OWN("own", Pos.CENTER_RIGHT),

    /**
     * 好友
     */

    FRIEND("friend", Pos.CENTER_LEFT);

    private final String name;
    private final Pos alignment;

    MessageOriginEnum(String name, Pos alignment) {
        this.name = name;
        this.alignment = alignment;
    }
}
