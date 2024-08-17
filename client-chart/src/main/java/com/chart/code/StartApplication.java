package com.chart.code;

import com.chart.code.component.LoginBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


/**
 * 主程序入口
 *
 * @author CleanCode
 */
public class StartApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Storage.stage = primaryStage;
        LoginBox loginBox = new LoginBox(primaryStage);
        Scene scene = new Scene(loginBox);
        // 加载和应用全局CSS样式
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        primaryStage.setTitle("登录");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            System.exit(1);
        });
    }
}
