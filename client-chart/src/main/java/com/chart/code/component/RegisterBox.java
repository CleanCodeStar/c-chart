package com.chart.code.component;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.chart.code.MessageHandle;
import com.chart.code.Storage;
import com.chart.code.common.Constant;
import com.chart.code.common.ImageIconUtil;
import com.chart.code.define.ByteData;
import com.chart.code.dto.UserDTO;
import com.chart.code.enums.MsgType;
import com.google.common.io.BaseEncoding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 登录页面
 *
 * @author CleanCode
 */
public class RegisterBox extends BorderPane {
    public RegisterBox() {
        Button backButton = new Button("登录");
        backButton.setOnAction(e -> {
            Scene scene = new Scene(new LoginBox());
            // 加载和应用全局CSS样式
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
            Storage.stage.setTitle("登录");
            Storage.stage.setScene(scene);
        });
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(backButton, 10.0);
        AnchorPane.setLeftAnchor(backButton, 10.0);
        anchorPane.getChildren().add(backButton);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        HBox headRow = new HBox();
        headRow.setSpacing(10);
        headRow.setAlignment(Pos.CENTER);
        Button register = new Button();
        AtomicReference<String> imageBase64 = new AtomicReference<>("");
        ImageView headImageView = new ImageView(ImageIconUtil.base64ToImage(Constant.DEFAULT_HEAD));
        headImageView.setFitWidth(80);
        headImageView.setFitHeight(80);
        headImageView.setPreserveRatio(true);
        register.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            // 设置文件过滤器，只允许选择图片文件
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                    "图片文件 (JPG, PNG, GIF)", "*.jpg", "*.jpeg", "*.png", "*.gif");
            fileChooser.getExtensionFilters().add(imageFilter);
            File file = fileChooser.showOpenDialog(Storage.stage);
            if (file != null) {
                if (file.length() > 30 * 1024) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("提示");
                    alert.setHeaderText("头像大小不能超过30KB");
                    alert.showAndWait();
                    return;
                }
                byte[] bytes = FileUtil.readBytes(file);
                imageBase64.set(BaseEncoding.base64().encode(bytes));
                Image image = ImageIconUtil.base64ToImage(imageBase64.get());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                register.setGraphic(imageView);
                headRow.getChildren().remove(0);
                headRow.getChildren().add(register);
            }
        });
        register.setGraphic(headImageView);
        headRow.getChildren().add(register);

        HBox nickNameRow = new HBox();
        nickNameRow.setSpacing(10);
        nickNameRow.setAlignment(Pos.CENTER);
        Label nickName = new Label("昵称");
        nickName.setPrefWidth(45);
        TextField nickNameText = new TextField();
        nickNameText.setText("");
        nickNameText.setPromptText("请输入昵称");
        nickNameRow.getChildren().addAll(nickName, nickNameText);


        HBox usernameRow = new HBox();
        usernameRow.setSpacing(10);
        usernameRow.setAlignment(Pos.CENTER);
        Label username = new Label("用户名");
        username.setPrefWidth(45);
        TextField usernameText = new TextField();
        usernameText.setText("");
        usernameText.setPromptText("请输入用户名");
        usernameRow.getChildren().addAll(username, usernameText);

        HBox passwordRow = new HBox();
        passwordRow.setSpacing(10);
        passwordRow.setAlignment(Pos.CENTER);
        Label password = new Label("密码");
        password.setPrefWidth(45);
        PasswordField passwordText = new PasswordField();
        passwordText.setPromptText("请输入密码");
        passwordText.setText("");
        passwordRow.getChildren().addAll(password, passwordText);

        Button login = new Button("注册");
        login.setPrefWidth(100);
        login.setOnAction(event -> {
            MessageHandle messageHandle = MessageHandle.getInstance();
            UserDTO user = new UserDTO();
            user.setNickname(nickNameText.getText()).setUsername(usernameText.getText()).setPassword(passwordText.getText()).setHead(imageBase64.get());
            ByteData byteData = ByteData.build(MsgType.REGISTER, JSON.toJSONString(user).getBytes(StandardCharsets.UTF_8));
            messageHandle.send(byteData);

        });
        vBox.getChildren().addAll(headRow, nickNameRow, usernameRow, passwordRow, login);
        setTop(anchorPane);
        setCenter(vBox);
        setPrefSize(400, 500);
    }
}
