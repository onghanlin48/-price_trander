package com.system.price_tracker;

import au.com.bytecode.opencsv.CSVReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class import_premise implements Initializable {
    ObservableList<table_premise> premise_list = FXCollections.observableArrayList();
    @FXML
    private TableColumn<table_premise, String> address;

    @FXML
    private TableColumn<table_premise, String> district;

    @FXML
    private TextField e_address;

    @FXML
    private TextField e_code;

    @FXML
    private TextField e_district;

    @FXML
    private TextField e_name;

    @FXML
    private TextField e_state;

    @FXML
    private TextField e_type;

    @FXML
    private Label error;

    @FXML
    private TableColumn<table_premise, Integer> premise_code;

    @FXML
    private TableColumn<table_premise, String> premise_name;

    @FXML
    private TableColumn<table_premise, String> state;

    @FXML
    private TableView<com.system.price_tracker.table_premise> table_item;

    @FXML
    private TableColumn<table_premise, String> type;

    @FXML
    private MenuButton username_check;

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

                    if(data[0].isEmpty() || data[0].trim().equals("-1.0")){
                        continue;
                    }
                    if(data.length != 6){
                        message.append(" column error! Premise code in number , Premise Name ,  address , type, state,district \n");
                        continue;
                    }
                    if(data[0].isEmpty() || data[1].isEmpty() || data[2].isEmpty() || data[3].isEmpty() || data[4].isEmpty() || data[5].isEmpty()){
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
                        message.append(" ,");
                        message.append(data[5]);
                        message.append(" cannot be empty!");
                        message.append("\n");
                        continue;
                    }
                    if(function.isNumeric(data[0])){

                        String sql = "SELECT * FROM lookup_premise WHERE premise_code =" + data[0] + " ";

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
                            message.append(" ,");
                            message.append(data[5]);
                            message.append(" already exist!");
                            message.append("\n");
                            continue;
                        }
                        Object[] objects = {data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim(),data[5].trim()};
                        sql = "INSERT INTO lookup_premise (premise_code, premise, address,type,state,district) VALUES (?, ?, ?,?,?,?)";
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
                        message.append(" ,");
                        message.append(data[5]);
                        message.append(" Premise code need number!");
                        message.append("\n");
                    }


                }
                if(seccuss){
                    function.inform("Import",null,"Import premise successfully");
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
        if(function.showConfirmation("Delete all?","Delete all premise.","Do you want delete all?")){
            if (function.delete("DELETE FROM lookup_premise")){
                function.inform("Deleted",null,"Premise deleted successfully");
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
        String _address = e_address.getText();
        String _type = e_type.getText();
        String _state = e_state.getText();
        String _district = e_district.getText();

        if(code.isEmpty() && name.isEmpty() && _address.isEmpty() && _type.isEmpty() && _state.isEmpty() && _district.isEmpty()){
            function.warning("Save error",null,"Please fill all.");
        }else{
            if(function.isNumeric(e_code.getText())){
                String sql = "SELECT * FROM lookup_premise WHERE premise_code =" + code + " ";

                if (function.checkData(sql)) {
                    if(function.showConfirmation("You want Update?","Premise code already exist! You want to update?","You want update this code ("+code+")")){
                        String updateQuery = "UPDATE lookup_premise SET premise = ?, address = ?, type = ?, state = ?,district =? WHERE premise_code = ?";
                        Object[] objects = {name,_address,_type,_state,_district,code};
                        if(function.update(updateQuery,objects)){
                            function.success("Updated Success",null,"Update successfully.");
                            loadData();
                        }

                    }
                }else{
                    Object[] objects = {code, name, _address, _type, _state,_district};
                    sql = "INSERT INTO lookup_premise (premise_code, premise, address,type,state,district) VALUES (?, ?, ?,?,?,?)";
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
        premise_list.clear();
        String sqlQuery = "SELECT * FROM lookup_premise";
        ResultSet resultSet = function.getData(sqlQuery);
        while (resultSet.next()){
            premise_list.add(new table_premise(
                    resultSet.getInt("premise_code"),
                    resultSet.getString("premise"),
                    resultSet.getString("address"),
                    resultSet.getString("type"),
                    resultSet.getString("state"),
                    resultSet.getString("district")));

            table_item.setItems(premise_list);

        }
    }
    public void loadData() throws SQLException {
        initialize();
        premise_code.setCellValueFactory(new PropertyValueFactory<>("code"));
        premise_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        state.setCellValueFactory(new PropertyValueFactory<>("state"));
        district.setCellValueFactory(new PropertyValueFactory<>("district"));
    }
}
