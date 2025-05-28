package com.system.price_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Search implements Initializable {
    static int x =0;
    @FXML
    private Button button_Home;

    @FXML
    private ImageView button_OK;

    @FXML
    private MenuButton button_username;

    @FXML
    private Pane cate;

    @FXML
    private Pane panelParent;

    @FXML
    private Pane sub_cate;

    @FXML
    private TextField tf_searchCategory;

    @FXML
    void search_object(ActionEvent event) throws SQLException {
        try {
            initialize(tf_searchCategory.getText());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void back(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Home.fxml",sourceNode,"Home");
    }

    @FXML
    void backToUsername(ActionEvent event) {

    }

    @FXML
    void change_email(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_email(),change_page.title_username());

    }

    @FXML
    void change_password(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_password(),change_page.title_password());
    }

    @FXML
    void change_username(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_username(),change_page.title_username());

    }


    @FXML
    void logout(ActionEvent event) {
        change_page.logout(event);

    }


    @FXML
    void switchToHome(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Home.fxml",sourceNode,"Home");

    }
    @FXML
    void view(ActionEvent event) {
        try {
            initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void switchToShoppingCart(ActionEvent event) {
        change_page.cart(event);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginDetail login = loginDetail.getInstance();
        button_username.setText(login.getUsername());
        try {
            initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() throws SQLException {
        sub_cate.getChildren().clear();
        panelParent.getChildren().clear();
        cate.getChildren().clear();
        String sql = "SELECT DISTINCT item_group FROM lookup_item";
        ResultSet resultSet = function.getData(sql);
        double x = 6;
        double y = 10;
        double cate_x = 6;
        double cate_y = 10;
        while (resultSet.next()){

            Button button = new Button(resultSet.getString("item_group"));

            create_button(panelParent,sub_cate,button,x,y);

            y += 30;
            cate.getChildren().add(button);
            Button cate_button = new Button(resultSet.getString("item_group"));

            create_button(panelParent,sub_cate,cate_button,cate_x,cate_y);

            sub_cate.getChildren().add(cate_button);
            sql = "SELECT DISTINCT item_category FROM lookup_item WHERE item_group = '"+resultSet.getString("item_group")+"'";
            ResultSet category = function.getData(sql);
            cate_y += 30;
            while (category.next()){
                Button sub_button = new Button(category.getString("item_category"));
                sub_create(panelParent,sub_button,cate_x,cate_y);
                sub_cate.getChildren().add(sub_button);
                cate_y += 20;
            }
            cate_y += 35;
        }

        cate.setPrefHeight(y);
        sub_cate.setPrefHeight(cate_y);
        sql = "SELECT * FROM lookup_item";
        ResultSet item = function.getData(sql);

        x = 1;
        y = 0;
        int check = 0;
        while(item.next()){
            if(check == 2){
                x = 1;
                y+=156;
                check = 0;
            }
            Pane pane = new Pane();
            create_item(pane,x,y,item.getString("item"),item.getString("unit"),item.getInt("item_code"), item.getString("item_group"), item.getString("item_category"));
            x = 435;
            panelParent.getChildren().add(pane);
            check ++;
        }
        panelParent.setPrefHeight(y+125);

    }
    private void initialize(String name) throws SQLException {
        sub_cate.getChildren().clear();
        panelParent.getChildren().clear();
        cate.getChildren().clear();
        String sql = "SELECT DISTINCT item_group FROM lookup_item WHERE item_group LIKE '%"+name+"%'";
        ResultSet resultSet = function.getData(sql);
        double x = 6;
        double y = 10;
        double cate_x = 6;
        double cate_y = 10;

        while (resultSet.next()){

            Button button = new Button(resultSet.getString("item_group"));

            create_button(panelParent,sub_cate,button,x,y);

            y += 30;
            cate.getChildren().add(button);
        }
        sql = "SELECT DISTINCT item_group FROM lookup_item WHERE item_category LIKE '%"+name+"%'";
        ResultSet cate_result = function.getData(sql);
        while (cate_result.next()){

            Button cate_button = new Button(cate_result.getString("item_group"));
            create_button(panelParent,sub_cate,cate_button,cate_x,cate_y);

            sub_cate.getChildren().add(cate_button);
            sql = "SELECT DISTINCT item_category FROM lookup_item WHERE item_group = '"+cate_result.getString("item_group")+"'";
            ResultSet category = function.getData(sql);
            cate_y += 30;
            while (category.next()){
                Button sub_button = new Button(category.getString("item_category"));
                sub_create(panelParent,sub_button,cate_x,cate_y);
                sub_cate.getChildren().add(sub_button);
                cate_y += 20;
            }
            cate_y += 35;
        }

        cate.setPrefHeight(y);
        sub_cate.setPrefHeight(cate_y);
        sql = "SELECT * FROM lookup_item WHERE item LIKE '%"+name+"%'";
        ResultSet item = function.getData(sql);

        x = 1;
        y = 0;
        int check = 0;
        boolean check_name = true;
        while(item.next()){
            check_name = false;
            if(check == 2){
                x = 1;
                y+=156;
                check = 0;
            }
            Pane pane = new Pane();
            create_item(pane,x,y,item.getString("item"),item.getString("unit"),item.getInt("item_code"), item.getString("item_group"), item.getString("item_category"));
            x = 435;
            panelParent.getChildren().add(pane);
            check ++;
        }
        if(check_name){
            loginDetail login = loginDetail.getInstance();
            String jdbcURL = database.JDBC_URL + login.getUsername();
            String username = database.USER;
            String password = database.PASSWORD;
            double check_g = 0.6535;

            String searchQuery = name;
            while (true){
                List<Integer> matchedResults = fuzzySearchFromDB(jdbcURL, username, password, searchQuery, check);

                if (!matchedResults.isEmpty()) {
                    System.out.println(x);
                    System.out.println("Matched items for '" + searchQuery + "': " + matchedResults);
                    for(int i =0;i<matchedResults.size();i++){

                        sql = "SELECT * FROM lookup_item WHERE item_code = "+matchedResults.get(i)+"";
                        Connection connection =  DriverManager.getConnection(jdbcURL, username, password);
                        Statement statement = connection.createStatement();
                        ResultSet item_new = statement.executeQuery(sql);
                        System.out.println(matchedResults.get(i));
                        if (item_new.next()){
                            if(check == 2){
                                x = 1;
                                y+=156;
                                check = 0;
                            }
                            Pane pane = new Pane();
                            create_item(pane,x,y,item_new.getString("item"),item_new.getString("unit"),item_new.getInt("item_code"), item_new.getString("item_group"), item_new.getString("item_category"));
                            x = 435;
                            panelParent.getChildren().add(pane);
                            check ++;
                        }
                        connection.close();
                        statement.close();
                        item_new.close();
                    }
                    break;
                } else {
                    check_g -= 0.1;
                    System.out.println("No matching items found.");
                }
            }
        }
        panelParent.setPrefHeight(y+125);

    }
    public static void create_item(Pane pane,double layoutX,double layoutY,String name,String unit,int code,String category,String sub){
        pane.setLayoutX(layoutX);
        pane.setLayoutY(layoutY);
        pane.setPrefHeight(156);
        pane.setPrefWidth(429);
        pane.setStyle("-fx-border-color:  #000000;-fx-border-radius: 10;");

        Label item = new Label(name);
        item.setLayoutX(5);
        item.setLayoutY(7);
        item.setPrefHeight(38);
        item.setPrefWidth(417);
        item.setStyle("-fx-alignment: Center;");
        item.setFont(new Font("Century Gothic Bold", 28));

        pane.getChildren().add(item);

        Label unit_L = new Label("Unit           :");
        unit_L.setLayoutX(14);
        unit_L.setLayoutY(57);
        unit_L.setPrefHeight(22);
        unit_L.setPrefWidth(142);
        unit_L.setStyle("-fx-alignment: Center_Left   ;");
        unit_L.setFont(new Font("Century Gothic Bold", 15));

        pane.getChildren().add(unit_L);

        Label cate = new Label("Category         :");
        cate.setLayoutX(14);
        cate.setLayoutY(90);
        cate.setPrefHeight(22);
        cate.setPrefWidth(142);
        cate.setStyle("-fx-alignment: Center_Left   ;");
        cate.setFont(new Font("Century Gothic Bold", 15));

        pane.getChildren().add(cate);

        Label sub_category = new Label("Sub Category :");
        sub_category.setLayoutX(14);
        sub_category.setLayoutY(120);
        sub_category.setPrefHeight(22);
        sub_category.setPrefWidth(142);
        sub_category.setStyle("-fx-alignment: Center_Left   ;");
        sub_category.setFont(new Font("Century Gothic Bold", 15));

        pane.getChildren().add(sub_category);

        Label v_unit = new Label(unit);
        v_unit.setLayoutX(135);
        v_unit.setLayoutY(57);
        v_unit.setPrefHeight(22);
        v_unit.setPrefWidth(286);
        v_unit.setStyle("-fx-alignment: Center_Left   ;");
        v_unit.setFont(new Font("Century Gothic", 13));

        pane.getChildren().add(v_unit);

        Label value_cate = new Label(category);
        value_cate.setLayoutX(135);
        value_cate.setLayoutY(90);
        value_cate.setPrefHeight(22);
        value_cate.setPrefWidth(286);
        value_cate.setStyle("-fx-alignment: Center_Left   ;");
        value_cate.setFont(new Font("Century Gothic", 13));

        pane.getChildren().add(value_cate);

        Label value_sub = new Label(sub);
        value_sub.setLayoutX(135);
        value_sub.setLayoutY(120);
        value_sub.setPrefHeight(22);
        value_sub.setPrefWidth(286);
        value_sub.setStyle("-fx-alignment: Center_Left   ;");
        value_sub.setFont(new Font("Century Gothic", 13));

        pane.getChildren().add(value_sub);

        pane.setOnMouseClicked(event ->select_item(code,event));


    }

    private static void select_item(int code, MouseEvent event) {
        search_by_code searchByCode = search_by_code.getInstance();
        searchByCode.setCode(code);

        Node sourceNode = (Node) event.getSource();
        function.nextPage("ItemPage.fxml",sourceNode,"Item Page");
    }

    public static void create_button(Pane panelParent,Pane sub_cate,Button button,double layoutX,double layoutY){

        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY);
        button.setPrefWidth(285);
        button.setPrefHeight(30);
        button.setStyle("-fx-background-color: #FFFFFF;-fx-alignment: Center;");
        button.setTextFill(javafx.scene.paint.Color.valueOf("black"));
        button.setWrapText(true);
        button.setFont(new Font("Century Gothic Bold", 15));
        search_by_code cate = search_by_code.getInstance();

        button.setOnAction(event -> {
            try {
                select_category(panelParent,sub_cate,button.getText());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


    }
    public static void sub_create(Pane panelParent,Button button,double layoutX,double layoutY){
        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY);
        button.setPrefWidth(285);
        button.setPrefHeight(20);
        button.setStyle("-fx-background-color: #FFFFFF;-fx-alignment: Center;");
        button.setTextFill(javafx.scene.paint.Color.valueOf("black"));
        button.setWrapText(true);
        button.setFont(new Font("Century Gothic", 11));
        search_by_code cate = search_by_code.getInstance();

        button.setOnAction(event -> {
            try {
                select_sub_category(panelParent,button.getText());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


    }

    private static void select_sub_category(Pane panelParent, String name) throws SQLException {
        panelParent.getChildren().clear();
        String sql = "SELECT * FROM lookup_item WHERE item_category = '"+name+"'";
        ResultSet item = function.getData(sql);

        double x = 1;
        double y = 0;
        int check = 0;
        while(item.next()){
            if(check == 2){
                x = 1;
                y+=156;
                check = 0;
            }
            Pane pane = new Pane();
            create_item(pane,x,y,item.getString("item"),item.getString("unit") ,item.getInt("item_code"), item.getString("item_group"), item.getString("item_category"));
            x = 435;
            panelParent.getChildren().add(pane);
            check ++;
        }
        panelParent.setPrefHeight(y+125);
    }

    private static void select_category(Pane panelParent,Pane sub_cate,String name) throws SQLException {
        Button cate = new Button(name);

        sub_cate.getChildren().clear();
        panelParent.getChildren().clear();
        double cate_x = 6;
        double cate_y = 10;
        create_button(panelParent,sub_cate,cate,cate_x,cate_y);
        sub_cate.getChildren().add(cate);
        String sql = "SELECT DISTINCT item_category FROM lookup_item WHERE item_group = '"+name+"'";
        ResultSet category = function.getData(sql);
        cate_y += 30;
        while (category.next()){
            Button sub_button = new Button(category.getString("item_category"));
            sub_create(panelParent,sub_button,cate_x,cate_y);
            sub_cate.getChildren().add(sub_button);
            cate_y += 20;
        }
        sub_cate.setPrefHeight(cate_y);


        sql = "SELECT * FROM lookup_item WHERE item_group = '"+name+"'";
        ResultSet item = function.getData(sql);

        double x = 1;
        double y = 0;
        int check = 0;
        while(item.next()){
            if(check == 2){
                x = 1;
                y+=156;
                check = 0;
            }
            Pane pane = new Pane();
            create_item(pane,x,y,item.getString("item"),item.getString("unit"),item.getInt("item_code"), item.getString("item_group"), item.getString("item_category"));
            x = 435;
            panelParent.getChildren().add(pane);
            check ++;
        }
        panelParent.setPrefHeight(y+125);



    }

    public static List<Integer> fuzzySearchFromDB(String jdbcURL, String username, String password,
                                                 String query, double threshold) {
        List<Integer> matchedItems = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "SELECT item,item_code FROM lookup_item";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    String o_itemName = resultSet.getString("item");
                    String itemName = o_itemName.toLowerCase();
                    int item_code =resultSet.getInt("item_code");

                    JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();
                    double similarity = jaroWinklerSimilarity.apply(query, itemName);


                    if (similarity >= threshold) {
                        x++;
                        matchedItems.add(item_code);
                    }
                }
                resultSet.close();
                connection.close();
                statement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchedItems;
    }
}
