package com.system.price_tracker;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;

public class ItemController implements Initializable {

    loginDetail login = loginDetail.getInstance();
    search_by_code search = search_by_code.getInstance();

    int item_code = search.getCode();
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label item_name;

    @FXML
    private Button button_Home;

    @FXML
    private Button button_cart;

    @FXML
    private Button button_item_details;

    @FXML
    private Button button_top5;

    @FXML
    private MenuButton button_username;

    @FXML
    private AnchorPane itemPane;

    @FXML
    private MenuItem menu_ChangeEmail;

    @FXML
    private MenuItem menu_ChangePassword;

    @FXML
    private MenuItem menu_ChangeUsername;

    @FXML
    private MenuItem menu_Logout;

    @FXML
    private AnchorPane topPane;

    @FXML
    void back(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Search.fxml",sourceNode,"Search Item");
    }


    @FXML
    void change_email(ActionEvent event) {
        change_page.close_scene(event);

        change_page.loadScene(change_page.change_email(),change_page.title_email());
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
    void price_trend(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("barchar.fxml",sourceNode,"Price Trend");
    }

    @FXML
    void switchToHome(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Home.fxml",sourceNode,"Home");
    }

    @FXML
    void switchToShoppingCart(ActionEvent event) {
        change_page.cart(event);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        topPane.getChildren().clear();
        itemPane.getChildren().clear();

        button_username.setText(login.getUsername());
        try {
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public void init() throws SQLException {
        Connection connection = null;
        ResultSet resultSet = null;
        double y = 0;

//        String sql = "SELECT i.item_code, i.item, i.unit, i.item_group, i.item_category, p.premise_code, p.premise, p.address, p.type, p.state, p.district, pc.id, pc.date, pc.price FROM lookup_item AS i INNER JOIN pricecatcher AS pc ON i.item_code = pc.item_code INNER JOIN lookup_premise AS p ON pc.premise_code = p.premise_code WHERE i.item_code = "+item_code ;
        String sql = "SELECT i.item_code, i.item, i.unit, i.item_group, i.item_category, p.premise_code, p.premise, p.address, p.type, p.state, p.district, pc.id, pc.date, pc.price " +
                "FROM lookup_item AS i " +
                "INNER JOIN pricecatcher AS pc ON i.item_code = pc.item_code " +
                "INNER JOIN lookup_premise AS p ON pc.premise_code = p.premise_code " +
                "INNER JOIN (SELECT item_code, premise_code, MAX(date) AS max_date " +
                "            FROM pricecatcher " +
                "            WHERE item_code =  " + item_code+
                "            GROUP BY item_code, premise_code) AS max_dates " +
                "ON pc.item_code = max_dates.item_code AND pc.premise_code = max_dates.premise_code AND pc.date = max_dates.max_date " +
                "WHERE i.item_code = "+item_code;
        connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
        Statement statement = connection.createStatement();

        resultSet = statement.executeQuery(sql);
        String name = null;
        String unit = null;

        while (resultSet.next()){
            unit = resultSet.getString("unit");
            name = resultSet.getString("item");
            Pane pane = load(resultSet.getInt("id"),resultSet.getString("premise"),resultSet.getDouble("price"),resultSet.getString("address"),y);
            y += 165;
            itemPane.getChildren().add(pane);
        }
        item_name.setText(name+" ("+unit+")");
        connection.close();
        statement.close();
        resultSet.close();
        y=0;
//        sql = "SELECT i.item_code, i.item, i.unit, i.item_group, i.item_category, p.premise_code, p.premise, p.address, p.type, p.state, p.district, pc.id, pc.date, pc.price FROM lookup_item AS i INNER JOIN pricecatcher AS pc ON i.item_code = pc.item_code INNER JOIN lookup_premise AS p ON pc.premise_code = p.premise_code WHERE i.item_code = "+item_code+ " ORDER BY pc.price LIMIT 5";
        sql = "SELECT i.item_code, i.item, i.unit, i.item_group, i.item_category, p.premise_code, p.premise, p.address, p.type, p.state, p.district, pc.id, pc.date, pc.price " +
                "FROM lookup_item AS i " +
                "INNER JOIN pricecatcher AS pc ON i.item_code = pc.item_code " +
                "INNER JOIN lookup_premise AS p ON pc.premise_code = p.premise_code " +
                "INNER JOIN (SELECT item_code, premise_code, MAX(date) AS max_date " +
                "            FROM pricecatcher " +
                "            WHERE item_code =  " + item_code+
                "            GROUP BY item_code, premise_code) AS max_dates " +
                "ON pc.item_code = max_dates.item_code AND pc.premise_code = max_dates.premise_code AND pc.date = max_dates.max_date " +
                "WHERE i.item_code = "+item_code+ " ORDER BY pc.price LIMIT 5";
        connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
        statement = connection.createStatement();
        resultSet = statement.executeQuery(sql);

        while (resultSet.next()){
            Pane pane = load(resultSet.getInt("id"),resultSet.getString("premise"),resultSet.getDouble("price"),resultSet.getString("address"),y);
            y += 165;
            topPane.getChildren().add(pane);
        }
        connection.close();
        statement.close();
        resultSet.close();
    }

    public Pane load(int code,String Premise,double price,String address,double y){
        Pane pane = new Pane();
        pane.setPrefHeight(165);
        pane.setPrefWidth(360);
        pane.setLayoutY(y);
        pane.setLayoutX(0);
        pane.setStyle("-fx-background-color: white;-fx-border-color: black;");

        Label premise = new Label(Premise);
        premise.setFont(new Font("Century Gothic Bold",18));
        premise.setPrefWidth(359);
        premise.setPrefHeight(24);
        premise.setLayoutX(0);
        premise.setLayoutY(1);

        pane.getChildren().add(premise);

        Label price_label = new Label("Price :");
        price_label.setFont(new Font("Century Gothic Bold",19));
        price_label.setPrefWidth(67);
        price_label.setPrefHeight(24);
        price_label.setLayoutX(1);
        price_label.setLayoutY(32);
        price_label.setStyle("-fx-alignment: Center_left;");

        pane.getChildren().add(price_label);

        Label price_price = new Label("RM "+price);
        price_price.setFont(new Font("Century Gothic",19));
        price_price.setPrefWidth(281);
        price_price.setPrefHeight(24);
        price_price.setLayoutX(70);
        price_price.setLayoutY(32);
        price_price.setStyle("-fx-alignment: Center_left;");

        pane.getChildren().add(price_price);

        Label address_label = new Label("Address :");
        address_label.setFont(new Font("Century Gothic Bold",19));
        address_label.setPrefWidth(92);
        address_label.setPrefHeight(24);
        address_label.setLayoutX(1);
        address_label.setLayoutY(63);
        address_label.setStyle("-fx-alignment: Center_left;");

        pane.getChildren().add(address_label);

        Label address_address = new Label(address);
        address_address.setFont(new Font("Candara Light",15));
        address_address.setPrefWidth(267);
        address_address.setPrefHeight(65);
        address_address.setLayoutX(92);
        address_address.setLayoutY(63);
        address_address.setStyle("-fx-alignment: top_left;");
        address_address.setWrapText(true);

        pane.getChildren().add(address_address);

        Pane pane1 = new Pane();
        pane1.setPrefHeight(36);
        pane1.setPrefWidth(109);
        pane1.setLayoutY(120);
        pane1.setLayoutX(242);
        pane1.setStyle("-fx-background-color: white;-fx-border-color: green;-fx-border-radius:100;");


        ImageView image = new ImageView();
        Image image_view = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/system/price_tracker/image/addToCartIcon.png")));

        image.setImage(image_view);
        image.setFitHeight(32);
        image.setFitWidth(29);
        image.setLayoutX(75);
        image.setLayoutY(4);

        pane1.getChildren().add(image);

        Label add = new Label("Add To Cart");
        add.setTextFill(Color.GREEN);
        add.setFont(new Font("Century Gothic Bold",10));
        add.setStyle("-fx-alignment:Center;");
        add.setLayoutY(4);
        add.setLayoutX(-6);
        add.setPrefHeight(28);
        add.setPrefWidth(86);

        pane1.getChildren().add(add);
        pane1.setOnMouseClicked(event -> {
            try {
                addCart(code);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        pane.getChildren().add(pane1);


        return pane;
    }

    private void addCart(int code) throws SQLException {
        String sql = "SELECT * FROM cart WHERE item_code="+search.getCode();
        if(function.checkData(sql)){
            if(function.showConfirmation("This item already add to cart!",null,"Do you want replace it?")){
                Object[] objects = {code,search.getCode()};
                sql = "UPDATE cart SET id = ? WHERE item_code = ?";
                if(function.update(sql,objects)){
                    function.success("Replace Item Successfully",null,"Replace Item Successfully");
                }else{
                    function.warning("Replace Unsuccessfully",null,"Please contact admin!");
                }
            }
        }else {
            String query = "SELECT COUNT(*) AS row_count FROM cart";
            ResultSet count = function.getData(query);
            if(count.next()){
                int num = count.getInt("row_count");
                if(num < 10){

                    Object[] objects = {code,search.getCode()};
                    sql ="INSERT INTO CART(id,item_code) VALUES (?,?)";
                    if(function.insert(sql,objects)){
                        function.success("Add Cart Successfully",null,"Add Cart Successfully");
                    }else{
                        function.warning("Add Unsuccessfully",null,"Please contact admin!");
                    }

                }else{
                    function.warning("Shopping Cart is full",null,"Maximum 10 items!");
                }
            }
        }


    }
}
