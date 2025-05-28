package com.system.price_tracker;

import au.com.bytecode.opencsv.CSVReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class import_item implements Initializable {

    @FXML
    private TextField e_category;

    @FXML
    private TextField e_code;

    @FXML
    private TextField e_gruop;

    @FXML
    private TextField e_name;

    @FXML
    private TextField e_unit;

    @FXML
    private Label error;

    @FXML
    private MenuButton username_check;

    @FXML
    private TableColumn<table_item, String> item_category;

    @FXML
    private TableColumn<table_item, Integer> item_code;

    @FXML
    private TableColumn<table_item, String> item_group;

    @FXML
    private TableColumn<table_item, String> item_name;

    @FXML
    private TableColumn<table_item, Integer> item_unit;

    @FXML
    private TableView<com.system.price_tracker.table_item> table_item;

    ObservableList<table_item> item_list =FXCollections.observableArrayList();
    @FXML
    void action_browser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Perform actions with the selected file
            try (CSVReader reader = new CSVReader(new FileReader(selectedFile))) {
                String[] data;
                // Skipping the header row assuming it contains column names
                reader.readNext();
                StringBuilder message = new StringBuilder();
                boolean seccuss = false;

                while ((data = reader.readNext()) != null) {

                    if(data[0].isEmpty() || data[0].trim().equals("-1")){
                        continue;
                    }
                    if(data.length != 5){
                        message.append(" column error! Item code in number , Item Name , Item Unit , Item Group , Item Category \n");
                        continue;
                    }
                    if(data[0].isEmpty() || data[1].isEmpty() || data[2].isEmpty() || data[3].isEmpty() || data[4].isEmpty()){
                        message.append("This line ");
                        message.append(data[0]);
                        message.append(" ,");
                        message.append(data[1]);
                        message.append(" ,");
                        message.append(data[2]);
                        message.append(" ,");
                        message.append(data[3]);
                        message.append(" ,");
                        message.append(data[4]);
                        message.append(" cannot be empty!");
                        message.append("\n");
                        continue;
                    }
                    if(function.isNumeric(data[0])){

                        String sql = "SELECT * FROM lookup_item WHERE item_code =" + data[0] + " ";

                        if (function.checkData(sql)) {
                            message.append("This line ");
                            message.append(data[0]);
                            message.append(" ,");
                            message.append(data[1]);
                            message.append(" ,");
                            message.append(data[2]);
                            message.append(" ,");
                            message.append(data[3]);
                            message.append(" ,");
                            message.append(data[4]);
                            message.append(" already exist!");
                            message.append("\n");
                            continue;
                        }
                        Object[] objects = {data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim()};
                        sql = "INSERT INTO lookup_item (item_code, item, unit,item_group,item_category) VALUES (?, ?,?,?,?)";
                        if (function.insert(sql, objects)) {
                            seccuss = true;
                        }

                    }else{
                        message.append("This line ");
                        message.append(data[0]);
                        message.append(" ,");
                        message.append(data[1]);
                        message.append(" ,");
                        message.append(data[2]);
                        message.append(" ,");
                        message.append(data[3]);
                        message.append(" ,");
                        message.append(data[4]);
                        message.append(" Premise code need number!");
                        message.append("\n");
                    }


                }
                if(seccuss){
                    function.inform("Import",null,"Import item successfully");
                    loadData();
                }else{
                    loadData();
                }
                error.setText(String.valueOf(message));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void action_delete(ActionEvent event) throws SQLException {
        if(function.showConfirmation("Delete all?","Delete all item.","Do you want delete all?")){
            if (function.delete("DELETE FROM lookup_item")){
                function.inform("Deleted",null,"Item deleted successfully");
                loadData();
            }else{
                function.warning("Unsuccessfully",null,"Please inform admin!");
                loadData();
            }
        }
    }

    @FXML
    void action_save(ActionEvent event) throws SQLException {
        String code = e_code.getText();
        String name = e_name.getText();
        String unit = e_unit.getText();
        String group = e_gruop.getText();
        String cate = e_category.getText();

        if(code.isEmpty() && name.isEmpty() && unit.isEmpty() && group.isEmpty() && cate.isEmpty()){
            function.warning("Save error",null,"Please fill all.");
        }else{
            if(function.isNumeric(e_code.getText())){
                String sql = "SELECT * FROM lookup_item WHERE item_code =" + code + " ";

                if (function.checkData(sql)) {
                      if(function.showConfirmation("You want Update?","Item code already exist! You want to update?","You want update this code ("+code+")")){
                          String updateQuery = "UPDATE lookup_item SET item = ?, unit = ?, item_group = ?, item_category = ? WHERE item_code = ?";
                          Object[] objects = {name,unit,group,cate,code};
                          if(function.update(updateQuery,objects)){
                              function.success("Updated Success",null,"Update successfully.");
                              loadData();
                          }

                      }
                }else{
                    Object[] objects = {code, name, unit, group, cate};
                    sql = "INSERT INTO lookup_item (item_code, item, unit,item_group,item_category) VALUES (?, ?, ?,?,?)";
                    if (function.insert(sql, objects)) {
                        function.success("Save Success",null,"Save successfully.");
                        loadData();
                    }
                }

            }else{
                function.warning("Save error",null,"Item code format in number!");
            }
        }

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
        item_list.clear();
        String sqlQuery = "SELECT * FROM lookup_item";
        ResultSet resultSet = function.getData(sqlQuery);
        while (resultSet.next()){
            item_list.add(new table_item(
                    resultSet.getInt("item_code"),
                            resultSet.getString("item"),
                            resultSet.getString("item_group"),
                            resultSet.getString("unit"),
                            resultSet.getString("item_category")));
            table_item.setItems(item_list);

        }
    }
    public void loadData() throws SQLException {
        initialize();
        item_code.setCellValueFactory(new PropertyValueFactory<>("code"));
        item_name.setCellValueFactory(new PropertyValueFactory<>("item"));
        item_unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        item_group.setCellValueFactory(new PropertyValueFactory<>("group"));
        item_category.setCellValueFactory(new PropertyValueFactory<>("cate"));
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
    void logout(ActionEvent event) {
        change_page.logout(event);
    }
    @FXML
    void home(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("Home.fxml",sourceNode,"Home");
    }

}
