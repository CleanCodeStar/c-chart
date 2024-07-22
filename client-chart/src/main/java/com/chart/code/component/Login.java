package com.chart.code.component;

import com.chart.code.define.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Login extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        User user = new User();
        VBox vBox = new VBox();
        vBox.setPrefSize(300, 300);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();
    }
}
