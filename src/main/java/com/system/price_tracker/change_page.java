package com.system.price_tracker;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class change_page {
    static void cart(ActionEvent event){
        Node sourceNode = (Node) event.getSource();
        function.nextPage("ShoppingCart.fxml",sourceNode,"Shopping Cart");
    }
    static String change_username(){
        return "UpdateUsername.fxml";
    }
    static String change_email(){
        return "UpdateEmail.fxml";
    }
    static String change_password(){
        return "UpdatePassword.fxml";
    }
    static String title_username(){
        return "Change Username";
    }
    static String title_email(){
        return "Change Email";
    }
    static String title_password(){
        return "Change Password";
    }

    static void logout(ActionEvent event){
        if(function.showConfirmation("Logout","Are you Sure?","Do you want to Logout?")){
            Platform.exit();Platform.exit();
        }
    }
    public static void close_scene(ActionEvent event){
        MenuItem sourceMenuItem = (MenuItem) event.getSource();
        Scene currentScene = sourceMenuItem.getParentPopup().getOwnerWindow().getScene();
        if (currentScene != null) {
            Stage stage = (Stage) currentScene.getWindow();
            if (stage != null) {
                stage.close();
                // Perform other operations with the current scene or stage
            }
        }
    }
    public static void loadScene(String fxmlFileName,String title) {
        try {

            FXMLLoader loader = new FXMLLoader(function.class.getResource(fxmlFileName));
            Parent root = loader.load();


            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
