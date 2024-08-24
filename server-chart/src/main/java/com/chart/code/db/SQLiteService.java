package com.chart.code.db;

import cn.hutool.core.bean.BeanUtil;
import com.chart.code.annotation.FieldIgnore;
import com.chart.code.define.User;
import com.chart.code.vo.UserVO;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库服务
 *
 * @author CleanCode
 */
public class SQLiteService {
    private Connection connection = null;

    public SQLiteService() {
        try {
            // 加载 SQLite JDBC 驱动
            Class.forName("org.sqlite.JDBC");
            // SQLite 数据库文件的路径
            String dbUrl = "jdbc:sqlite:E:\\IdeaProjects\\c-chart\\c-chart.db";
            // String dbUrl = "jdbc:sqlite:D:\\IdeaProject\\c-chart\\c-chart.db";
            // 建立连接
            connection = DriverManager.getConnection(dbUrl);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found");
        } catch (SQLException e) {
            System.err.println("Error connecting to SQLite database");
        }
    }


    public <T> List<T> queryList(String sql, Class<T> clazz, Object... param) {
        List<T> resultList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    statement.setObject(i + 1, param[i]);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            Constructor<T> constructor = clazz.getConstructor();
            while (resultSet.next()) {
                T instance = constructor.newInstance();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    try {
                        var field = clazz.getDeclaredField(columnName);
                        field.setAccessible(true);
                        field.set(instance, columnValue);
                    } catch (NoSuchFieldException e) {
                    }
                }
                resultList.add(instance);
            }

        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }

    public User queryUser(String username, String password) {
        String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";
        List<User> users = queryList(sql, User.class, username, password);
        return users.isEmpty() ? null : users.get(0);
    }

    public User checkUser(String username) {
        String sql = "SELECT * FROM tb_user WHERE username = ? ";
        List<User> users = queryList(sql, User.class, username);
        return users.isEmpty() ? null : users.get(0);
    }

    public User queryUserById(Integer id) {
        String sql = "SELECT * FROM tb_user WHERE id = ?";
        List<User> users = queryList(sql, User.class, id);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    public List<UserVO> queryAll() {
        String sql = "SELECT * FROM tb_user";
        List<User> users = queryList(sql, User.class);
        return BeanUtil.copyToList(users, UserVO.class);
    }

    /**
     * 插入数据
     */
    public boolean insert(String tableName, Object entity) {
        List<String> colNameList = new ArrayList<>();
        // 占位符List
        List<String> placeholderList = new ArrayList<>();
        List<Object> colValueList = new ArrayList<>();

        Field[] declaredFields = entity.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }
            try {
                Object value = declaredField.get(entity);
                FieldIgnore ignore = declaredField.getDeclaredAnnotation(FieldIgnore.class);
                if (value != null && ignore == null) {
                    String name = declaredField.getName();
                    if (value instanceof String) {
                        if (StringUtils.isNotBlank(value.toString())) {
                            colNameList.add(name);
                            placeholderList.add("?");
                            colValueList.add(value);
                        }
                    } else {
                        colNameList.add(name);
                        placeholderList.add("?");
                        colValueList.add(value);
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        String sql = "INSERT INTO " + tableName + " (" + StringUtils.join(colNameList, ",") + ") VALUES (" + StringUtils.join(placeholderList, ",") + ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < colValueList.size(); i++) {
                statement.setObject(i + 1, colValueList.get(i));
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
