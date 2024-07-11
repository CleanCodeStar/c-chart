package com.chart.code.enums;

/**
 * 文件发送面板类型
 * @author CleanCode
 */
public enum FilePanelType {
    /**
     * 自己
     */
    OWN("自己"),
    /**
     * 好友
     */
    FRIEND("好友");

    private final String name;

    FilePanelType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
