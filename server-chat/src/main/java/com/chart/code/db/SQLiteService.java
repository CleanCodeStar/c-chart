package com.chart.code.db;

import com.chart.code.define.User;
import com.chart.code.vo.UserVO;

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
            String dbUrl = "jdbc:sqlite:E:\\IdeaProjects\\c-chat\\c-chat.db";
            // String dbUrl = "jdbc:sqlite:D:\\IdeaProject\\c-chat\\c-chat.db";
            // 建立连接
            connection = DriverManager.getConnection(dbUrl);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found");
        } catch (SQLException e) {
            System.err.println("Error connecting to SQLite database");
        }
    }

    public User queryUser(String username, String password) {
        String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User().setId(resultSet.getInt("id"))
                            .setHead(resultSet.getString("head"))
                            .setUsername(resultSet.getString("username"))
                            .setPassword(resultSet.getString("password"))
                            .setNickname(resultSet.getString("nickname"))
                            .setCreateTime(resultSet.getDate("create_time"))
                            .setLastLoginTime(resultSet.getDate("last_login_time"));

                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying user", e);
        }
    }

    public User queryUserById(Integer id) {
        String sql = "SELECT * FROM tb_user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User().setId(resultSet.getInt("id"))
                            .setHead(resultSet.getString("head"))
                            .setUsername(resultSet.getString("username"))
                            .setPassword(resultSet.getString("password"))
                            .setNickname(resultSet.getString("nickname"))
                            .setCreateTime(resultSet.getDate("create_time"))
                            .setLastLoginTime(resultSet.getDate("last_login_time"));

                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying user", e);
        }
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    public List<UserVO> queryAll() {
        // 执行查询示例
        try {
            List<UserVO> users = new ArrayList<>();
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM tb_user";
            ResultSet resultSet = statement.executeQuery(query);
            // 处理查询结果
            while (resultSet.next()) {
                UserVO userVO = new UserVO();
                userVO.setId(resultSet.getInt("id"))
                        .setHead(resultSet.getString("head"))
                        .setUsername(resultSet.getString("username"))
                        .setPassword(resultSet.getString("password"))
                        .setNickname(resultSet.getString("nickname"))
                        .setCreateTime(resultSet.getDate("create_time"))
                        .setLastLoginTime(resultSet.getDate("last_login_time"));
                users.add(userVO);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
