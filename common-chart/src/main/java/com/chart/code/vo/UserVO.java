package com.chart.code.vo;

import com.chart.code.define.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户
 *
 * @author CleanCode
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVO extends User {
    /**
     * 是否在线
     */
    private Boolean onLine;
    /**
     * 所有好友
     */
    private List<UserVO> friends;
}
