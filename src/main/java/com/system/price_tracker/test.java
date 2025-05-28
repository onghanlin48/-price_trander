package com.system.price_tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class test extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        loginDetail login = loginDetail.getInstance();
        login.setUsername("ong");
        search_by_code search = search_by_code.getInstance();
        search.setCode(2);
        login.setLogin(true);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("barchar.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),1210,676);
        stage.setTitle("Login");

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}