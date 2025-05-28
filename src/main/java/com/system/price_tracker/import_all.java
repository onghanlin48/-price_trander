package com.system.price_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class import_all implements Initializable {

    @FXML
    private MenuButton username;

    @FXML
    void c_email(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_email(),change_page.title_email());
    }

    @FXML
    void c_passowrd(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_password(),change_page.title_password());
    }

    @FXML
    void c_username(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_username(),change_page.title_username());
    }

    @FXML
    void home(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Home.fxml",sourceNode,"Home");
    }

    @FXML
    void item(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("import_item.fxml",sourceNode,"Import Item");
    }

    @FXML
    void logout(ActionEvent event) {
        change_page.logout(event);
    }

    @FXML
    void premise(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("import_premise.fxml",sourceNode,"Import Premise");
    }

    @FXML
    void price(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("import_price.fxml",sourceNode,"Import Price Catcher");
    }

    @FXML
    void switchToShoppingCart(ActionEvent event) {
        change_page.cart(event);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginDetail login = loginDetail.getInstance();
        username.setText(login.getUsername());
    }
}
