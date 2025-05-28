package com.system.price_tracker;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class barchar implements Initializable {
    loginDetail login = loginDetail.getInstance();
    search_by_code search = search_by_code.getInstance();
    @FXML
    private Label pen_number;
    @FXML
    private ProgressBar loading;
    @FXML
    private BarChart<String, Number> barchar;

    @FXML
    private Label item;

    @FXML
    private ChoiceBox<Integer> month_choose;

    @FXML
    private MenuButton username;

    @FXML
    private ChoiceBox<Integer> years_choose;

    @FXML
    void action_submit(ActionEvent event) throws SQLException {
        int month = month_choose.getValue();
        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
            car(31);
        }else if(month == 4 || month == 6 || month == 9 || month == 11){
            car(30);
        }else if(month == 2){
            car(29);
        }
    }
    public void car(int leng) throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        barchar.setAnimated(false);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return decimalFormat.format(object);
            }

            @Override
            public Number fromString(String string) {
                // Not needed for formatting, so returning null
                return null;
            }
        });
        double[] result = new double[leng];
        int month = month_choose.getValue();
        int year = years_choose.getValue();
        barchar.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {


                for (int i = 1; i <= leng; i++) {
                    String sql = "SELECT * FROM pricecatcher WHERE item_code=" + search.getCode() + " AND MONTH(date)=" + month + " AND YEAR(date)=" + year + " AND DAY(date)=" + i + " ORDER BY date";
                    ResultSet resultSet = function.getData(sql);
                    double sum = 0;
                    int total = 0;

                    while (resultSet.next()) {

                        sum = sum + resultSet.getDouble("price");
                        total++;

                    }
                    Thread.sleep(500);
                    double pen = (double) i / leng;
                    updateProgress(pen, 1);
                    pen = pen *100;
                    String pen_s = String.format("%.2f", pen);
                    updateMessage(pen_s+"%");
                    sum = sum / total;
                    String niu = String.format("%.2f", sum);
                    sum = Double.parseDouble(niu);
                    result[i - 1] = sum;

                }

                return null;
            }
        };
        loading.progressProperty().bind(task.progressProperty());
        pen_number.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(event -> {

            series.setName("Price");
            for (int i = 0; i < result.length; i++) {
                int x = i +1;
                String days = x+" day ("+result[i]+")";
                if(!(Double.isNaN(result[i]))){
                    series.getData().add(new XYChart.Data<>(days, result[i]));
                }else{
                    series.getData().add(new XYChart.Data<>(days, 0.0));
                }

            }

            barchar.getData().add(series);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    void back(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("ItemPage.fxml",sourceNode,"Item Page");
    }

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
    void logout(ActionEvent event) {
        change_page.logout(event);
    }

    @FXML
    void switchToShoppingCart(ActionEvent event) {
        change_page.cart(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loading.setStyle("-fx-accent:black");

        barchar.getXAxis().setLabel("Day");
        barchar.getYAxis().setLabel("Price (RM)");
        username.setText(login.getUsername());
        try {
            getItem();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void getItem() throws SQLException {
        String sql = "SELECT item,unit FROM lookup_item WHERE item_code = "+search.getCode();
        ResultSet resultSet = function.getData(sql);

        if(resultSet.next()){
            String name_item = resultSet.getString("item");
            String unit_item = resultSet.getString("unit");
            barchar.setTitle(name_item+" ("+unit_item+")");
            item.setText(name_item+" ("+unit_item+")");
        }
    }
    public void load() throws SQLException {
        String sql = "SELECT DISTINCT YEAR(date) AS extracted_year FROM pricecatcher WHERE item_code = "+search.getCode();
        ResultSet resultSet = function.getData(sql);

        while(resultSet.next()){
            years_choose.setValue(resultSet.getInt("extracted_year"));
            years_choose.getItems().add(resultSet.getInt("extracted_year"));
        }
    }
    @FXML
    void changeYears(ActionEvent event) throws SQLException {
        month_choose.getItems().clear();
        int year = years_choose.getValue();
        String sql = "SELECT DISTINCT MONTH(date) AS extracted_month  FROM pricecatcher WHERE item_code = "+search.getCode()+" AND YEAR(date)="+year;
        ResultSet resultSet = function.getData(sql);
        while(resultSet.next()){
            month_choose.setValue(resultSet.getInt("extracted_month"));
            month_choose.getItems().add(resultSet.getInt("extracted_month"));
        }


    }
}
