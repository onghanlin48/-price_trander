package com.system.price_tracker;

import au.com.bytecode.opencsv.CSVReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class import_price implements Initializable {
    ObservableList<table_price> price_list = FXCollections.observableArrayList();
    @FXML
    private TableColumn<table_price, String> date;

    @FXML
    private DatePicker e_date;

    @FXML
    private TextField e_id;

    @FXML
    private TextField e_item_code;

    @FXML
    private TextField e_premise_code;

    @FXML
    private TextField e_price;

    @FXML
    private Label error;

    @FXML
    private TableColumn<table_price, Integer> id;

    @FXML
    private TableColumn<table_price, Integer> item_code;

    @FXML
    private TableColumn<table_price, Integer> premise_code;

    @FXML
    private TableColumn<table_price, Double> t_price;

    @FXML
    private TableView<com.system.price_tracker.table_price> table_price;

    @FXML
    private MenuButton username_check;

    @FXML
    void action_browser(ActionEvent event) throws SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String file = String.valueOf(selectedFile);
            String unixFilePath = file.replace("\\", "/");
            String sql = "LOAD DATA INFILE '"+unixFilePath+"' \n" +
                    "INTO TABLE `pricecatcher`\n" +
                    "CHARACTER SET UTF8\n" +
                    "FIELDS TERMINATED BY ',' \n" +
                    "ENCLOSED BY '\"' \n" +
                    "LINES TERMINATED BY '\\n'\n" +
                    "IGNORE 1 LINES \n" +
                    "(date, premise_code, item_code, price);";

            if (function.load(sql)){
                function.inform("Import",null,"Import price catcher successfully");
                loadData();
            }else{
                loadData();
            }
        }
    }

    @FXML
    void action_delete(ActionEvent event) throws SQLException {
        if(function.showConfirmation("Delete all?","Delete all Price Catcher.","Do you want delete all?")){
            if (function.delete("DELETE FROM pricecatcher")){
                function.inform("Deleted",null,"Price Catcher deleted successfully");
                loadData();
            }else{
                function.warning("Unsuccessfully",null,"Please inform admin!");
                loadData();
            }
        }
    }

    @FXML
    void action_save(ActionEvent event) throws SQLException {
        String code = e_id.getText();


        LocalDate date = null;

        String formattedDate_check = null;

        boolean check= false;
        try {
            date = e_date.getValue();
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formattedDate_check = date.format(outputFormatter);


            check = true;
        } catch (Exception e) {
            check = false;
        }

        System.out.println(formattedDate_check);
        String premise_code = e_premise_code.getText();
        String item_code = e_item_code.getText();
        String price_ = e_price.getText();

        if(code.isEmpty() && premise_code.isEmpty() && item_code.isEmpty() && price_.isEmpty()){
            function.warning("Save error",null,"Please fill all.");
        }
        else{
            if(function.isNumeric(code) && function.isNumeric(premise_code) && function.isNumeric(item_code) && function.isPrice(price_) && check){
                String sql = "SELECT * FROM pricecatcher WHERE id =" + code + " ";

                if (function.checkData(sql)) {
                    if(function.showConfirmation("You want Update?","Price Catcher already exist! You want to update?","You want update this id ("+code+")")){
                        String updateQuery = "UPDATE pricecatcher SET date = ?, premise_code = ?, item_code = ?, price = ? WHERE id = ?";

                        Object[] objects = {formattedDate_check,premise_code,item_code,price_,code};
                        if(function.update(updateQuery,objects)){
                            function.success("Updated Success",null,"Update successfully.");
                            loadData();
                        }

                    }
                }else{
                    Object[] objects = {code,formattedDate_check,premise_code,item_code,price_};
                    sql = "INSERT INTO pricecatcher (id,date, premise_code, item_code,price) VALUES (?, ?, ?,?,?)";
                    if (function.insert(sql, objects)) {
                        function.success("Save Success",null,"Save successfully.");
                        loadData();
                    }
                }

            }else{
                function.warning("Save error",null,"id,premise_code,item_code and price format in number!\n(or) Date format error!");
            }
        }



    }

    @FXML
    void back(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("ImportData1.fxml",sourceNode,"Import");
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
    void home(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Home.fxml",sourceNode,"Home");
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
        username_check.setText(login.getUsername());

        try {
            loadData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void initialize() throws SQLException {
        price_list.clear();
        String sqlQuery = "SELECT * FROM pricecatcher";
        ResultSet resultSet = function.getData(sqlQuery);
        while (resultSet.next()){
            price_list.add(new table_price(
                    resultSet.getInt("id"),
                    resultSet.getString("date"),
                    resultSet.getInt("premise_code"),
                    resultSet.getInt("item_code"),
                    resultSet.getDouble("price")));

            table_price.setItems(price_list);
        }
    }
    public void loadData() throws SQLException {
        initialize();
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        premise_code.setCellValueFactory(new PropertyValueFactory<>("premise_code"));
        item_code.setCellValueFactory(new PropertyValueFactory<>("item_code"));
        t_price.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
}
