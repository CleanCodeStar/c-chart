package com.chart.code.component;

import com.alibaba.fastjson2.JSON;
import com.chart.code.MessageHandle;
import com.chart.code.define.ByteData;
import com.chart.code.dto.UserDTO;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;

/**
 * 登录页面
 *
 * @author CleanCode
 */
public class LoginBox extends VBox {
    public LoginBox(Stage primaryStage) {
        setAlignment(Pos.CENTER);
        setSpacing(10);

        HBox usernameRow = new HBox();
        usernameRow.setSpacing(10);
        usernameRow.setAlignment(Pos.CENTER);
        Label username = new Label("用户名");
        username.setPrefWidth(45);
        TextField usernameText = new TextField();
        usernameText.setText("admin");
        usernameText.setPromptText("请输入用户名");
        usernameRow.getChildren().addAll(username, usernameText);

        HBox passwordRow = new HBox();
        passwordRow.setSpacing(10);
        passwordRow.setAlignment(Pos.CENTER);
        Label password = new Label("密码");
        password.setPrefWidth(45);
        PasswordField passwordText = new PasswordField();
        passwordText.setPromptText("请输入密码");
        passwordText.setText("123456");
        passwordRow.getChildren().addAll(password, passwordText);

        CheckBox checkBox = new CheckBox("记住密码");
        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                System.out.println("记住密码");
            } else {
                System.out.println("不记住密码");
            }
        });
        Button login = new Button("登录");
        login.setPrefWidth(100);
        login.setOnAction(event -> {
            MessageHandle messageHandle = MessageHandle.getInstance();
            UserDTO user = new UserDTO();
            user.setUsername(usernameText.getText()).setPassword(passwordText.getText()).setRemember(checkBox.isSelected());
            ByteData byteData = ByteData.buildLogin(JSON.toJSONString(user).getBytes(StandardCharsets.UTF_8));
            messageHandle.send(byteData);

        });
        getChildren().addAll(usernameRow, passwordRow, checkBox, login);
        setPrefSize(400, 500);
    }
}
