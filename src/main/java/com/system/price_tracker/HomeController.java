package com.system.price_tracker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private MenuItem Logout;

    @FXML
    private Button btn_browse;

    @FXML
    private Button btn_import;

    @FXML
    private Button btn_search;

    @FXML
    private ImageView cart;

    @FXML
    private MenuItem change_email;

    @FXML
    private MenuItem change_password;

    @FXML
    private MenuItem change_username;

    @FXML
    private MenuButton username_pro;

    @FXML
    void btn_search(ActionEvent event){
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Search.fxml",sourceNode,"Search Item");
    }

    @FXML
    void btn_import(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("ImportData1.fxml",sourceNode,"Import");
    }

    @FXML
    void c_email(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_email(),change_page.title_email());

    }

    @FXML
    void c_password(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_password(),change_page.title_password());

    }

    @FXML
    void c_username(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_username(),change_page.title_username());
    }

    @FXML
    void logout(ActionEvent event) {
        change_page.logout(event);

    }

    @FXML
    void switchToShoppingCart(ActionEvent event) {
        change_page.cart(event);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginDetail login = loginDetail.getInstance();
        username_pro.setText(login.getUsername());
    }
}
