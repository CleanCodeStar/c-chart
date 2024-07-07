package com.chart.code.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回对象
 *
 * @author CleanCode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 8131708939787036027L;

    /**
     * 响应编码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * build成功返回结果
     */
    public static <T> Result<T> buildOk(String msg, T data) {
        return new Result<T>(200, msg, data);
    }

    /**
     * build成功返回结果
     */
    public static <T> Result<T> buildFail(String msg) {
        return new Result<T>(300, msg, null);
    }
}
