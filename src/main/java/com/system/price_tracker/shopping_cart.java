package com.system.price_tracker;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import org.kordamp.bootstrapfx.scene.layout.Panel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

public class shopping_cart implements Initializable {
    static ArrayList<Integer> item_code_store = new ArrayList<>();
    static ArrayList<Integer> id_cart_store = new ArrayList<>();
    @FXML
    private AnchorPane cart;

    @FXML
    private Button button_Home;

    @FXML
    private MenuItem button_SoldItem1;

    @FXML
    private MenuItem button_SoldItem11;

    @FXML
    private Button button_cancel;

    @FXML
    private MenuButton button_item_sold;

    @FXML
    private MenuButton button_item_sold1;

    @FXML
    private MenuButton button_username;

    @FXML
    private MenuItem menu_ChangeEmail;

    @FXML
    private MenuItem menu_ChangePassword;

    @FXML
    private MenuItem menu_ChangeUsername;

    @FXML
    private MenuItem menu_Logout;
    @FXML
    private CheckBox address_click;
    @FXML
    private ChoiceBox<String> district_a;
    @FXML
    private ChoiceBox<String> state_a;
    @FXML
    private AnchorPane recom;
    static String state_check;
    static String dis_check;
    static ArrayList<Integer> true_number = new ArrayList<>();
    static ArrayList<Integer> no_item_code = new ArrayList<>();
    static ArrayList<Integer> false_number = new ArrayList<>();

    static ArrayList<Integer> premise_one_by_one = new ArrayList<>();
    static ArrayList<Double> price_one_by_one = new ArrayList<>();
    static ArrayList<Integer> item_one_one = new ArrayList<>();
    static ArrayList<Integer> first_code = new ArrayList<>();
    static ArrayList<Integer> second_code = new ArrayList<>();
    static boolean c_o;
    static boolean e_o;
    static double price = 0;
    static int premise = 0;
    static  double s_price = 0;
    static int s_premise = 0;
    static double c_price = 0;
    static int c_premise = 0;
    static  double c_s_price = 0;
    static int c_s_premise = 0;
    static double c_total =0;
    static boolean only_one = false;
    static boolean no_item = false;
    static double latitude;
    static double longitude;
    static HashMap<String, GeoPoint> stateCoordinates = new HashMap<>();
    static int totalRequests = 0;
    static int completedRequests = 0;
    static String nearestState;
    static boolean chek_location = false;
    ArrayList<String> item_code = new ArrayList<>();
    ArrayList<Integer> premise_code = new ArrayList<>();
    ArrayList<String> location_check = new ArrayList<>();
    ArrayList<Double> total_price = new ArrayList<>();
    static class GeoPoint {
        double latitude;
        double longitude;

        public GeoPoint(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
    public static void map() {
        String accessToken = "pk.eyJ1IjoibGluY3J5IiwiYSI6ImNrcHo4MnlsNjAxN3IydW82emljcTk5d3AifQ.k3zr214v7w2bk6D67hkcPw"; // Replace with your Mapbox access token

        loginDetail login = loginDetail.getInstance();

        try (Connection connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername())) {
            String selectQuery = "SELECT DISTINCT district FROM lookup_premise";

            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String locationQuery = resultSet.getString("district");

                MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                        .accessToken(accessToken)
                        .query(locationQuery)
                        .country("MY")
                        .build();

                mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
                    @Override
                    public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            GeocodingResponse geocodingResponse = response.body();
                            if (!geocodingResponse.features().isEmpty()) {
                                CarmenFeature feature = geocodingResponse.features().getFirst();
                                Point coordinates = feature.center();

                                assert coordinates != null;
                                double latitude = coordinates.latitude();
                                double longitude = coordinates.longitude();
                                stateCoordinates.put(locationQuery, new GeoPoint(latitude, longitude));
                                System.out.println("Location: " + locationQuery + ", Coordinates: " + latitude + ", " + longitude);
                            } else {
                                System.out.println("No results found for the location: " + locationQuery);
                            }
                        } else {
                            System.out.println("Request failed for location: " + locationQuery);
                        }
                        completedRequests++;
                        if (completedRequests == totalRequests) {
                            chek_location = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                        t.printStackTrace();
                        completedRequests++;
                        if (completedRequests == totalRequests) {
                            chek_location = true;
                        }
                    }
                });

                totalRequests++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String findNearestState(GeoPoint userInput, HashMap<String, GeoPoint> stateCoordinates) {
        double minDistance = Double.MAX_VALUE;
        String nearestState = "";

        for (String state : stateCoordinates.keySet()) {
            GeoPoint stateCoords = stateCoordinates.get(state);
            double distance = calculateDistance(userInput, stateCoords);
            if (distance < minDistance) {
                minDistance = distance;
                nearestState = state;
            }
        }
        System.out.println("Minimum distance: " + minDistance);
        return nearestState;
    }
    public static double calculateDistance(GeoPoint point1, GeoPoint point2) {
        final int R = 6371; // Radius of the earth in kilometers

        double latDistance = Math.toRadians(point2.latitude - point1.latitude);
        double lonDistance = Math.toRadians(point2.longitude - point1.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
    public static void retrieveUserLocation() {
        // Replace 'YOUR_ACCESS_TOKEN' with your Mapbox access token
        String accessToken = "pk.eyJ1IjoibGluY3J5IiwiYSI6ImNrcHo4MnlsNjAxN3IydW82emljcTk5d3AifQ.k3zr214v7w2bk6D67hkcPw"; // Replace with your Mapbox access token

        // Get user's location using their IP address
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(accessToken)
                .query(dis_check) // You can set initial coordinates if needed
                .country("MY")
                .build();

        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                if (response.body() != null && !response.body().features().isEmpty()) {
                    CarmenFeature userLocation = response.body().features().getFirst();
                    Point point = userLocation.center(); // Retrieve the coordinates (geopoint)

                    // Extract latitude and longitude
                    assert point != null;
                    latitude = point.latitude();
                    longitude = point.longitude();

                } else {
                    System.out.println("No location found for the given query.");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                System.out.println("Failed to retrieve location: " + throwable.getMessage());
            }
        });
    }
    public void main(int[] number){
        if(state_check != null){
            map();
            retrieveUserLocation();
        }else{
            dis_check = "";
        }


        c_o = false;
        e_o = false;
        boolean check_in = false;
        do {
            while (chek_location) {
                check_in = true;
                if (state_check != null) {
                    GeoPoint userInput = new GeoPoint(latitude, longitude);
                    nearestState = findNearestState(userInput, stateCoordinates);
                    dis_check = nearestState;
                    System.out.println(dis_check);
                    stateCoordinates.remove(nearestState);
                }

                int length;
                int n = number.length;
                if (n == 1) {
                    length = 1;
                } else {
                    double result = (number.length) / 2;
                    length = (int) result;
                    if (n != 2) {
                        length += 2;
                    } else {
                        length += 1;
                    }

                }


                for (int i = 0; i < n; i++) {
                    int x = n - i;
                    System.out.println("c(" + n + "," + x + ")");

                    if (i == 0) {
                        if (database(number, 1)) {
                            c_o = true;
                            e_o = true;
                            true_number.clear();
                            first_code.clear();
                            for (int niu = 0; niu < number.length; niu++) {
                                true_number.add(number[niu]);
                                first_code.add(number[niu]);
                            }
                            System.out.println("hidf");
                            double total = price + s_price;
                            c_premise = premise;
                            c_price = price;
                            c_s_premise = s_premise;
                            c_s_price = s_price;
                            c_total = total;

                        } else {
                            c_o = false;
                            e_o = false;
                            only_one = false;
                            no_item = false;
                            c_total = 0;
                            c_premise = 0;
                            c_price = 0;
                            c_s_price = 0;
                            c_s_premise = 0;
                        }

                    } else {

                        combination(number, new boolean[number.length], 0, n, x);
                    }
                    if (only_one && no_item) {
                        break;
                    }
                    if (c_o && e_o) {
                        break;
                    } else {
                        c_o = false;
                        e_o = false;
                        only_one = false;
                        no_item = false;
                        c_total = 0;
                        c_premise = 0;
                        c_price = 0;
                        c_s_price = 0;
                        c_s_premise = 0;
                    }


                }
                if (c_o && e_o) {
                    int[] convert = new int[first_code.size()];
                    for (int x = 0; x < first_code.size(); x++) {
                        convert[x] = first_code.get(x);
                    }
                    String inClause = Arrays.toString(convert)
                            .replace("[", "(")
                            .replace("]", ")");

                    location_check.add(dis_check);

                    item_code.add(inClause);
                    premise_code.add(c_premise);
                    total_price.add(c_price);
                    if (c_s_premise != 0) {
                        convert = new int[second_code.size()];
                        for (int x = 0; x < second_code.size(); x++) {
                            convert[x] = second_code.get(x);
                        }
                        inClause = Arrays.toString(convert)
                                .replace("[", "(")
                                .replace("]", ")");
                        location_check.add(dis_check);
                        item_code.add(inClause);
                        premise_code.add(c_s_premise);
                        total_price.add(c_s_price);
                    }
                    break;
                }

                if (no_item) {
                    int[] true_value = new int[number.length - no_item_code.size()];
                    int z = 0;
                    boolean check;
                    for (int i = 0; i < number.length; i++) {
                        check = false;
                        for (int x = 0; x < no_item_code.size(); x++) {
                            if (number[i] == no_item_code.get(x)) {
                                check = true;
                            }
                        }
                        if (!(check)) {
                            true_value[z] = number[i];
                            z++;
                        }
                    }
                    String inClause = Arrays.toString(true_value)
                            .replace("[", "(")
                            .replace("]", ")");
                    number = new int[no_item_code.size()];
                    if (only_one) {

                        location_check.add(dis_check);

                        item_code.add(inClause);
                        premise_code.add(c_premise);
                        total_price.add(c_price);

                        for (int y = 0; y < no_item_code.size(); y++) {
                            number[y] = no_item_code.get(y);
                        }

                    } else {

                        for (int y = 0; y < no_item_code.size(); y++) {
                            number[y] = no_item_code.get(y);
                        }
                    }

                } else {
                    if (!(c_o && e_o && only_one)) {
                        for (int i = 0; i < number.length; i++) {
                            int[] one_by_one = {number[i]};
                            item_one_one.add(number[i]);
                            database(one_by_one, 3);
                        }
                        for (int i = 0; i < premise_one_by_one.size(); i++) {
                            location_check.add(dis_check);
                            item_code.add("" + item_one_one.get(i));
                            premise_code.add(premise_one_by_one.get(i));
                            total_price.add(price_one_by_one.get(i));

                        }

                    }
                }
            }
        } while (!check_in);



    }
    public static boolean database(int[] itemCodes,int second){

        boolean check = false;
        loginDetail login = loginDetail.getInstance();
        String inClause = Arrays.toString(itemCodes)
                .replace("[", "(")
                .replace("]", ")");
        try (Connection conn = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername())) {
            if (conn != null) {


                String sql = "WITH RankedItems AS (" +
                        "SELECT " +
                        "pc.item_code, " +
                        "pc.premise_code, " +
                        "pc.date AS max_date, " +
                        "pc.price, " +
                        "ROW_NUMBER() OVER (PARTITION BY pc.item_code, pc.premise_code ORDER BY pc.date DESC) AS rn " +
                        "FROM pricecatcher pc " +
                        "WHERE pc.item_code IN " + inClause +
                        "), " +
                        "ValidPremises AS (" +
                        "SELECT DISTINCT premise_code " +
                        "FROM RankedItems " +
                        "GROUP BY premise_code " +
                        "HAVING COUNT(DISTINCT item_code) = " + itemCodes.length +
                        ") " +
                        "SELECT ri.premise_code, SUM(ri.price) AS total_price, lp.address, lp.type, lp.state, lp.district " +
                        "FROM RankedItems ri " +
                        "JOIN ValidPremises vp ON ri.premise_code = vp.premise_code " +
                        "JOIN lookup_premise lp ON ri.premise_code = lp.premise_code " +
                        "WHERE ri.rn = 1 " +
                        "AND lp.district LIKE '%"+dis_check+"%' " +
                        "GROUP BY ri.premise_code, lp.address, lp.type, lp.state, lp.district " +
                        "ORDER BY SUM(ri.price) ASC " +
                        "LIMIT 1;";

                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                if (resultSet.next()) {
                    int premiseCode = resultSet.getInt("premise_code");
                    double totalPrice = resultSet.getDouble("total_price");
                    if(second == 1){
                        premise = premiseCode;
                        price = totalPrice;
                        System.out.println("databse 1 Premise Code: " + premiseCode + ", Total Price: " + totalPrice);

                    }else if(second == 2){
                        s_premise = premiseCode;
                        s_price = totalPrice;
                        System.out.println("database 2 Premise Code: " + premiseCode + ", Total Price: " + totalPrice);

                    }else{
                        premise_one_by_one.add(premiseCode);
                        price_one_by_one.add(totalPrice);
                    }

                    check = true;
                }
                conn.close();
                statement.close();
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }

    public static void combination(int[] arr, boolean[] used, int startIndex, int n, int r) {
        if (r == 0) {
            printCombination(arr, used);
            return;
        }

        for (int i = startIndex; i < n; i++) {
            used[i] = true;
            combination(arr, used, i + 1, n, r - 1);
            used[i] = false;
        }
    }

    public static void printCombination(int[] arr, boolean[] used) {
        true_number.clear();
        false_number.clear();
        int[] s_itemCodes;
        int[] itemCodes ;
        int z = 0;
        for (int i = 0; i < arr.length; i++) {
            if (used[i]) {
                z++;
                true_number.add(arr[i]);
            }
        }
        System.out.println(true_number);
        itemCodes = new int[z];
        for (int y = 0;y< true_number.size();y++){

            itemCodes[y] = true_number.get(y);
        }
        z =0;
        for (int i = 0; i < arr.length; i++) {
            if (!(used[i])) {
                z++;
                false_number.add(arr[i]);
            }
        }
        System.out.println(false_number);
        s_itemCodes = new int[z];
        for (int y = 0;y< false_number.size();y++){
            s_itemCodes[y] = false_number.get(y);
        }
        boolean check_1 = database(itemCodes,1);

        if(check_1){
            boolean check_2 = database(s_itemCodes,2);
            if(check_2){

                System.out.println("hi");
                e_o = true;
                c_o = true;
                double total = price + s_price;
                if(c_total == 0){
                    first_code.clear();
                    second_code.clear();
                    for (int i = 0;i<itemCodes.length;i++){
                        first_code.add(itemCodes[i]);
                    }
                    for (int i = 0;i<s_itemCodes.length;i++){
                        second_code.add(s_itemCodes[i]);
                    }
                    c_premise = premise;
                    c_price = price;
                    c_s_premise = s_premise;
                    c_s_price = s_price;
                    c_total =total;
                }else if(total < c_total){
                    first_code.clear();
                    second_code.clear();
                    for (int i = 0;i<itemCodes.length;i++){
                        first_code.add(itemCodes[i]);
                    }
                    System.out.println("hiyty");
                    for (int i = 0;i<s_itemCodes.length;i++){
                        second_code.add(s_itemCodes[i]);
                    }
                    c_premise = premise;
                    c_price = price;
                    c_s_premise = s_premise;
                    c_s_price = s_price;
                    c_total =total;
                }
            }else{
                System.out.println("hello");
                if(c_price == 0){
                    no_item_code.clear();
                    for (int i = 0;i<s_itemCodes.length;i++){
                        no_item_code.add(s_itemCodes[i]);
                        no_item = true;
                    }
                    c_premise = premise;
                    c_price = price;
                    only_one = true;
                }else if(price < c_price){
                    no_item_code.clear();
                    for (int i = 0;i<s_itemCodes.length;i++){
                        no_item_code.add(s_itemCodes[i]);
                        no_item = true;
                    }
                    c_premise = premise;
                    c_price = price;
                    only_one = true;
                }
            }
        }
        System.out.println(second_code+""+first_code);


    }
    @FXML
    void one_by_one(ActionEvent event) throws SQLException {
        true_number.clear();
        false_number.clear();
        no_item_code.clear();
        first_code.clear();
        second_code.clear();
        premise_one_by_one.clear();
        price_one_by_one.clear();
        item_one_one.clear();
        price = 0;
        premise = 0;
        s_price = 0;
        s_premise = 0;
        c_price = 0;
        c_premise = 0;
        c_s_price = 0;
        c_s_premise = 0;
        c_total =0;
        no_item = false;
        totalRequests = 0;
        completedRequests = 0;
        chek_location = false;
        only_one = false;
        recom.getChildren().clear();
        state_check = "";
        dis_check = "";
        if(address_click.isSelected()){
            state_check = state_a.getValue();
            dis_check = district_a.getValue();
            System.out.println(dis_check);
            if(dis_check == null || state_check == null){
                function.warning("Error",null,"Please choice the state and district");
            }else{
                location_check.clear();
                item_code.clear();
                premise_code.clear();
                total_price.clear();
                for(int i = 0;i<item_code_store.size();i++){
                    chek_location = false;
                    stateCoordinates.clear();
                    state_check = state_a.getValue();
                    dis_check = district_a.getValue();
                    int[] item_code = {item_code_store.get(i)};
                    main(item_code);
                }
                int y =0;
                for(int i =0;i<item_code.size();i++){
                    System.out.println(" ");
                    System.out.println("Location : "+location_check.get(i));
                    System.out.println("Item Code :" +item_code.get(i));
                    System.out.println("Premise Code: " + premise_code.get(i) + ", Total Price: " + total_price.get(i));
                    String sql_price = "SELECT pc.id,li.item_code, li.item, li.unit, li.item_group, li.item_category,\n" +
                            "       lp.premise, lp.address, lp.type, lp.state, lp.district,\n" +
                            "       pc.price\n" +
                            "FROM pricecatcher pc\n" +
                            "INNER JOIN lookup_item li ON pc.item_code = li.item_code\n" +
                            "INNER JOIN lookup_premise lp ON pc.premise_code = lp.premise_code\n" +
                            "WHERE pc.item_code = "+item_code.get(i)+" AND  pc.premise_code = "+premise_code.get(i)+
                            "\nORDER BY pc.date DESC Limit 1;";
                    ResultSet resultSet = function.getData(sql_price);


                    while (resultSet.next()){
                        int[] id ={id_cart_store.get(i)};
                        int[] code = {resultSet.getInt("item_code")};
                        int[] id_price ={resultSet.getInt("id")};
                        String[] item_name = {resultSet.getString("item")};
                        recommended(y,resultSet.getString("premise"),resultSet.getString("address"),total_price.get(i),item_name,id,code,id_price,resultSet.getString("state"),resultSet.getString("district"));
                    }
                    y += 230;
                }

            }
        }else{
            int y =0;
            for(int i =0;i<item_code_store.size();i++){
                String sql = "SELECT i.item_code, i.item, i.unit, i.item_group, i.item_category, p.premise_code, p.premise, p.address, p.type, p.state, p.district, pc.id, pc.date, pc.price " +
                        "FROM lookup_item AS i " +
                        "INNER JOIN pricecatcher AS pc ON i.item_code = pc.item_code " +
                        "INNER JOIN lookup_premise AS p ON pc.premise_code = p.premise_code " +
                        "INNER JOIN (SELECT item_code, premise_code, MAX(date) AS max_date " +
                        "            FROM pricecatcher " +
                        "            WHERE item_code =  " + item_code_store.get(i) +
                        "            GROUP BY item_code, premise_code) AS max_dates " +
                        "ON pc.item_code = max_dates.item_code AND pc.premise_code = max_dates.premise_code AND pc.date = max_dates.max_date " +
                        "WHERE i.item_code = "+item_code_store.get(i)+ " ORDER BY pc.price LIMIT 1";

                ResultSet resultSet = function.getData(sql);
                while (resultSet.next()){
                    String[] name = {resultSet.getString("item")};
                    int[] code = {resultSet.getInt("item_code")};
                    int[] id = {id_cart_store.get(i)};
                    int[] id_price = {resultSet.getInt("id")};
                    recommended(y,resultSet.getString("premise"),resultSet.getString("address"),resultSet.getDouble("price"),name,id,code,id_price,resultSet.getString("state"),resultSet.getString("district"));
                    y+= 230;
                }
            }
        }

    }
    @FXML
    void many_(ActionEvent event) throws SQLException {
        stateCoordinates.clear();
        true_number.clear();
        false_number.clear();
        no_item_code.clear();
        first_code.clear();
        second_code.clear();
        premise_one_by_one.clear();
        price_one_by_one.clear();
        item_one_one.clear();
        price = 0;
        premise = 0;
        s_price = 0;
        s_premise = 0;
        c_price = 0;
        c_premise = 0;
        c_s_price = 0;
        c_s_premise = 0;
        c_total =0;
        no_item = false;
        totalRequests = 0;
        completedRequests = 0;
        chek_location = false;
        only_one = false;
        location_check.clear();
        item_code.clear();
        premise_code.clear();
        total_price.clear();
        recom.getChildren().clear();
        state_check = "";
        dis_check = "";
        if (address_click.isSelected()) {
            int[] number = new int[item_code_store.size()];
            for (int i = 0; i < item_code_store.size(); i++) {
                number[i] = item_code_store.get(i);
            }
            state_check = state_a.getValue();
            dis_check = district_a.getValue();
            System.out.println(dis_check);
            if (dis_check == null || state_check == null) {
                function.warning("Error", null, "Please choice the state and district");
            } else {
                location_check.clear();
                item_code.clear();
                premise_code.clear();
                total_price.clear();

                chek_location = false;
                stateCoordinates.clear();
                state_check = state_a.getValue();
                dis_check = district_a.getValue();
                main(number);
                int y =0;
                for (int i = 0; i < item_code.size(); i++) {
                    System.out.println(" ");


                    System.out.println("Item Code :" + item_code.get(i));
                    System.out.println("Premise Code: " + premise_code.get(i) + ", Total Price: " + total_price.get(i));
                    String[] coordinatesArray = item_code.get(i).replaceAll("[()]", "").split(",");
                    if (coordinatesArray.length >= 2) {
                        try {
                            int[] code = new int[coordinatesArray.length];
                            int[] id = new int[coordinatesArray.length];
                            int[] id_price = new int[coordinatesArray.length];
                            String[] item_name = new String[coordinatesArray.length];
                            String premise_name = null;
                            String add = null;
                            String state = null;
                            String dis = null;
                            for (int x = 0; x < coordinatesArray.length; x++) {
                                code[x] = Integer.parseInt(coordinatesArray[x].trim());
                                for(int z = 0;z<item_code_store.size();z++){
                                    if(item_code_store.get(z) == code[x]){
                                        id[x]=id_cart_store.get(z);
                                        String sql_price = "SELECT pc.id,li.item_code, li.item, li.unit, li.item_group, li.item_category,\n" +
                                                "       lp.premise, lp.address, lp.type, lp.state, lp.district,\n" +
                                                "       pc.price\n" +
                                                "FROM pricecatcher pc\n" +
                                                "INNER JOIN lookup_item li ON pc.item_code = li.item_code\n" +
                                                "INNER JOIN lookup_premise lp ON pc.premise_code = lp.premise_code\n" +
                                                "WHERE pc.item_code = "+code[x]+" AND  pc.premise_code = "+premise_code.get(i)+
                                                "\nORDER BY pc.date DESC Limit 1;";
                                        ResultSet resultSet = function.getData(sql_price);
                                        while (resultSet.next()){
                                            premise_name = resultSet.getString("premise");
                                            add = resultSet.getString("address");
                                            state = resultSet.getString("state");
                                            dis = resultSet.getString("district");
                                            item_name[x] = resultSet.getString("item");
                                            id_price[x] = resultSet.getInt("id");
                                        }
                                        break;
                                    }
                                }
                            }
                           recommended(y, premise_name, add, total_price.get(i), item_name, id, code, id_price, state, dis);


                        } catch (NumberFormatException e) {
                            System.out.println("Invalid format for coordinates.");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String numericPart = item_code.get(i).replaceAll("[^0-9]", "");

                        // Parse the numeric part to an integer
                        int result = Integer.parseInt(numericPart);
                        String sql = "SELECT i.item_code, i.item, i.unit, i.item_group, i.item_category, p.premise_code, p.premise, p.address, p.type, p.state, p.district, pc.id, pc.date, pc.price " +
                                "FROM lookup_item AS i " +
                                "INNER JOIN pricecatcher AS pc ON i.item_code = pc.item_code " +
                                "INNER JOIN lookup_premise AS p ON pc.premise_code = p.premise_code " +
                                "INNER JOIN (SELECT item_code, premise_code, MAX(date) AS max_date " +
                                "            FROM pricecatcher " +
                                "            WHERE item_code =  " + result +
                                "            GROUP BY item_code, premise_code) AS max_dates " +
                                "ON pc.item_code = max_dates.item_code AND pc.premise_code = max_dates.premise_code AND pc.date = max_dates.max_date " +
                                "WHERE i.item_code = "+result+ " ORDER BY pc.price LIMIT 1";

                        ResultSet resultSet = function.getData(sql);
                        while (resultSet.next()){
                            String[] name = {resultSet.getString("item")};
                            int[] code = {resultSet.getInt("item_code")};
                            int id_cart =0;
                            for(int z=0;z<item_code_store.size();z++){
                                if(item_code_store.get(z) == result){
                                    id_cart = id_cart_store.get(z);
                                    break;
                                }
                            }
                            int[] id = {id_cart};
                            int[] id_price = {resultSet.getInt("id")};
                            recommended(y,resultSet.getString("premise"),resultSet.getString("address"),resultSet.getDouble("price"),name,id,code,id_price,resultSet.getString("state"),resultSet.getString("district"));
                        }
                    }
                    y+=230;
                }
            }




        }else{
            int[] number = new int[item_code_store.size()];
            for(int i=0;i<item_code_store.size();i++){
                number[i] = item_code_store.get(i);
            }
            location_check.clear();
            item_code.clear();
            premise_code.clear();
            total_price.clear();

            chek_location = true;
            state_check = state_a.getValue();
            dis_check = district_a.getValue();
            main(number);
            int y =0;
            for (int i = 0; i < item_code.size(); i++) {
                System.out.println(" ");


                System.out.println("Item Code :" + item_code.get(i));
                System.out.println("Premise Code: " + premise_code.get(i) + ", Total Price: " + total_price.get(i));
                String[] coordinatesArray = item_code.get(i).replaceAll("[()]", "").split(",");
                if (coordinatesArray.length >= 2) {
                    try {
                        int[] code = new int[coordinatesArray.length];
                        int[] id = new int[coordinatesArray.length];
                        int[] id_price = new int[coordinatesArray.length];
                        String[] item_name = new String[coordinatesArray.length];
                        String premise_name = null;
                        String add = null;
                        String state = null;
                        String dis = null;
                        for (int x = 0; x < coordinatesArray.length; x++) {
                            code[x] = Integer.parseInt(coordinatesArray[x].trim());
                            for(int z = 0;z<item_code_store.size();z++){
                                if(item_code_store.get(z) == code[x]){
                                    id[x]=id_cart_store.get(z);
                                    String sql_price = "SELECT pc.id,li.item_code, li.item, li.unit, li.item_group, li.item_category,\n" +
                                            "       lp.premise, lp.address, lp.type, lp.state, lp.district,\n" +
                                            "       pc.price\n" +
                                            "FROM pricecatcher pc\n" +
                                            "INNER JOIN lookup_item li ON pc.item_code = li.item_code\n" +
                                            "INNER JOIN lookup_premise lp ON pc.premise_code = lp.premise_code\n" +
                                            "WHERE pc.item_code = "+code[x]+" AND  pc.premise_code = "+premise_code.get(i)+
                                            "\nORDER BY pc.date DESC Limit 1;";
                                    ResultSet resultSet = function.getData(sql_price);
                                    while (resultSet.next()){
                                        premise_name = resultSet.getString("premise");
                                        add = resultSet.getString("address");
                                        state = resultSet.getString("state");
                                        dis = resultSet.getString("district");
                                        item_name[x] = resultSet.getString("item");
                                        id_price[x] = resultSet.getInt("id");
                                    }
                                    break;
                                }
                            }
                        }
                        recommended(y, premise_name, add, total_price.get(i), item_name, id, code, id_price, state, dis);


                    } catch (NumberFormatException e) {
                        System.out.println("Invalid format for coordinates.");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String numericPart = item_code.get(i).replaceAll("[^0-9]", "");

                    // Parse the numeric part to an integer
                    int result = Integer.parseInt(numericPart);
                    String sql = "SELECT i.item_code, i.item, i.unit, i.item_group, i.item_category, p.premise_code, p.premise, p.address, p.type, p.state, p.district, pc.id, pc.date, pc.price " +
                            "FROM lookup_item AS i " +
                            "INNER JOIN pricecatcher AS pc ON i.item_code = pc.item_code " +
                            "INNER JOIN lookup_premise AS p ON pc.premise_code = p.premise_code " +
                            "INNER JOIN (SELECT item_code, premise_code, MAX(date) AS max_date " +
                            "            FROM pricecatcher " +
                            "            WHERE item_code =  " + result +
                            "            GROUP BY item_code, premise_code) AS max_dates " +
                            "ON pc.item_code = max_dates.item_code AND pc.premise_code = max_dates.premise_code AND pc.date = max_dates.max_date " +
                            "WHERE i.item_code = "+result+ " ORDER BY pc.price LIMIT 1";

                    ResultSet resultSet = function.getData(sql);
                    while (resultSet.next()){
                        String[] name = {resultSet.getString("item")};
                        int[] code = {resultSet.getInt("item_code")};
                        int id_cart =0;
                        for(int z=0;z<item_code_store.size();z++){
                            if(item_code_store.get(z) == result){
                                id_cart = id_cart_store.get(z);
                                break;
                            }
                        }
                        int[] id = {id_cart};
                        int[] id_price = {resultSet.getInt("id")};
                        recommended(y,resultSet.getString("premise"),resultSet.getString("address"),resultSet.getDouble("price"),name,id,code,id_price,resultSet.getString("state"),resultSet.getString("district"));
                    }
                }
                y+=230;
            }

        }
    }
    @FXML
    void onaddress(ActionEvent event) throws SQLException {
        if(address_click.isSelected()){
            state_a.getItems().clear();
            district_a.getItems().clear();
            district_a.setDisable(false);
            state_a.setDisable(false);
            ObservableList<String> state = FXCollections.observableArrayList();
            String sql = "SELECT DISTINCT state FROM lookup_premise";
            ResultSet resultSet = function.getData(sql);
            while (resultSet.next()){
                state.add(resultSet.getString("state"));
            }
            state_a.setItems(state);
        }else{
            state_a.getItems().clear();
            district_a.setDisable(true);
            state_a.setDisable(true);
        }
    }
    @FXML
    void change_state(ActionEvent event) throws SQLException {
        district_a.getItems().clear();
        String state = state_a.getValue();
        String sql = "SELECT DISTINCT district FROM lookup_premise WHERE state='"+state+"'";
        ObservableList<String> dis = FXCollections.observableArrayList();
        ResultSet resultSet = function.getData(sql);
        while(resultSet.next()){
            dis.add(resultSet.getString("district"));
        }
        district_a.setItems(dis);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        map();
        recom.getChildren().clear();
        loginDetail login = loginDetail.getInstance();
        button_username.setText(login.getUsername());
        try {
            load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void load() throws SQLException {

        item_code_store.clear();
        id_cart_store.clear();
        cart.getChildren().clear();
        Label items = new Label("ITEMS");
        items.setFont(new Font("Ebrima Bold", 14));
        items.setPrefHeight(22);
        items.setPrefWidth(57);
        items.setLayoutX(78);
        items.setLayoutY(3);
        items.setStyle("-fx-alignment: Center;");

        cart.getChildren().add(items);

        Label seller = new Label("SELLER");
        seller.setFont(new Font("Ebrima Bold", 14));
        seller.setPrefWidth(134);
        seller.setPrefHeight(22);
        seller.setLayoutY(3);
        seller.setLayoutX(249);
        seller.setStyle("-fx-alignment: Center;");

        cart.getChildren().add(seller);

        Label price = new Label("PRICE");
        price.setFont(new Font("Ebrima Bold", 14));
        price.setPrefHeight(22);
        price.setPrefWidth(77);
        price.setLayoutX(502);
        price.setLayoutY(3);
        price.setStyle("-fx-alignment: Center_LEFT;");

        cart.getChildren().add(price);
        int number = 1;
        int y = 30;
        double total_price = 0;
        String sql = "SELECT * FROM cart";
        ResultSet resultSet = function.getData(sql);
        while (resultSet.next()){
            String sql_price = "SELECT li.item_code, li.item, li.unit, li.item_group, li.item_category,\n" +
                    "       lp.premise, lp.address, lp.type, lp.state, lp.district,\n" +
                    "       pc.price\n" +
                    "FROM pricecatcher pc\n" +
                    "INNER JOIN lookup_item li ON pc.item_code = li.item_code\n" +
                    "INNER JOIN lookup_premise lp ON pc.premise_code = lp.premise_code\n" +
                    "WHERE pc.id = "+resultSet.getInt("id")+";";
            ResultSet result_price = function.getData(sql_price);
            if(result_price.next()){
                panel_cart(y,number,result_price.getString("item"),result_price.getString("premise"),result_price.getDouble("price"),resultSet.getInt("id_cart"));
                total_price += result_price.getDouble("price");
                item_code_store.add(result_price.getInt("item_code"));
                id_cart_store.add(resultSet.getInt("id_cart"));
                y += 40;
                number++;
            }
        }
        Pane total = new Pane();
        total.setLayoutY(y);
        total.setLayoutX(0);
        total.setPrefWidth(700);
        total.setPrefHeight(60);

        Label price_l = new Label("Total : RM "+String.format("%.2f", total_price));
        price_l.setFont(new Font("Century Gothic", 14));
        price_l.setStyle("-fx-alignment: Center_Right;");
        price_l.setLayoutY(15);
        price_l.setLayoutX(85);
        price_l.setPrefHeight(35);
        price_l.setPrefWidth(486);

        total.getChildren().add(price_l);
        cart.getChildren().add(total);
    }
    public void panel_cart(int y,int number,String item_name,String premise_name,Double price_,int id){
        Pane pane = new Pane();
        pane.setPrefWidth(700);
        pane.setPrefWidth(60);
        pane.setLayoutX(0);
        pane.setLayoutY(y);

        Label num = new Label(""+number);
        num.setFont(new Font("Century Gothic Bold", 14));
        num.setStyle("-fx-alignment: Center;-fx-background-color: #2C4740;-fx-background-radius:100;-fx-text-fill: white;");
        num.setPrefWidth(26);
        num.setPrefHeight(25);
        num.setLayoutX(25);
        num.setLayoutY(15);

        pane.getChildren().add(num);

        Label item = new Label(item_name);
        item.setPrefWidth(170);
        item.setPrefHeight(30);
        item.setLayoutX(87);
        item.setLayoutY(15);
        item.setFont(new Font("Century Gothic", 12));
        item.setStyle("-fx-alignment: Center_Left;");

        pane.getChildren().add(item);

        Label premise = new Label(premise_name);
        premise.setPrefWidth(150);
        premise.setPrefHeight(35);
        premise.setLayoutX(283);
        premise.setLayoutY(15);
        premise.setFont(new Font("Century Gothic", 14));
        premise.setStyle("-fx-alignment: Center_Left;");

        pane.getChildren().add(premise);

        Label price = new Label("RM "+price_);
        price.setPrefWidth(130);
        price.setPrefHeight(35);
        price.setLayoutX(506);
        price.setLayoutY(15);
        price.setFont(new Font("Century Gothic", 14));
        price.setStyle("-fx-alignment: Center_Left;");

        pane.getChildren().add(price);

        Button cancel = new Button("Cancel");
        cancel.setLayoutX(625);
        cancel.setLayoutY(20);
        cancel.setStyle("-fx-background-color: #E8E8E8;");
        cancel.setOnAction(event -> {
            try {
                delete(id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        pane.getChildren().add(cancel);

        cart.getChildren().add(pane);
    }
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM cart WHERE id_cart="+id;
        if(function.delete(sql)){
            function.success("Delete Success",null,"Delete successfully");
            load();
        }else{
            function.warning("Delete Unsuccessfully",null,"Please contact admin");
            load();
        }
    }

    public void recommended(int y,String premise_name,String address,double price,String[] item_name,int[] id,int[] code,int[] id_price,String state,String dis){
        Pane pane = new Pane();
        pane.setPrefHeight(356);
        pane.setPrefWidth(230);
        pane.setLayoutY(y);
        pane.setLayoutX(0);

        Label premise = new Label(premise_name);
        premise.setFont(new Font("Century Gothic Bold",14));
        premise.setStyle("-fx-text-fill: white;-fx-alignment: Center;-fx-background-color: #2C4740;-fx-background-radius:20;");
        premise.setPrefHeight(30);
        premise.setLayoutY(8);
        premise.setPrefWidth(133);
        premise.setLayoutX(24);

        pane.getChildren().add(premise);

        Button button = new Button("REPLACE SHOPPING CART");
        button.setFont(new Font("Century Gothic",12));
        button.setStyle("-fx-alignment: Center;-fx-background-color:white;-fx-border-color:black;-fx-border-radius:100;");
        button.setLayoutY(11);
        button.setLayoutX(179);
        button.setOnAction(event -> {
            try {
                addToCart(id,code,id_price);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        pane.getChildren().add(button);

        Label total_label = new Label("TOTAL PRICE");
        total_label.setFont(new Font("Ebrima Bold",14));
        total_label.setStyle("-fx-alignment: Center_left;");
        total_label.setLayoutX(33);
        total_label.setLayoutY(51);
        total_label.setPrefWidth(114);
        total_label.setPrefHeight(22);

        pane.getChildren().add(total_label);

        Label total = new Label("RM "+String.format("%.2f", price));
        total.setFont(new Font("Century Gothic",12));
        total.setStyle("-fx-alignment: Center_left;");
        total.setPrefHeight(22);
        total.setPrefWidth(136);
        total.setLayoutX(49);
        total.setLayoutY(78);

        pane.getChildren().add(total);

        Label add_label = new Label("ADDRESS");
        add_label.setFont(new Font("Ebrima Bold",14));
        add_label.setStyle("-fx-alignment: Center_left;");
        add_label.setLayoutX(33);
        add_label.setLayoutY(102);
        add_label.setPrefWidth(66);
        add_label.setPrefHeight(22);

        pane.getChildren().add(add_label);


        MenuButton add = new MenuButton(address);
        add.setFont(new Font("Century Gothic",12));
        add.setStyle("-fx-alignment: center_left;");
        add.setPrefHeight(22);
        add.setPrefWidth(303);
        add.setLayoutX(49);
        add.setLayoutY(124);
        ObservableList<MenuItem> address_a = FXCollections.observableArrayList();
        MenuItem add_menu = new MenuItem(address);
        add.getItems().add(add_menu);

        pane.getChildren().add(add);

        Label state_label = new Label("State : "+state);
        state_label.setFont(new Font("Century Gothic",12));
        state_label.setStyle("-fx-alignment: center_left;");
        state_label.setPrefHeight(22);
        state_label.setPrefWidth(303);
        state_label.setLayoutX(49);
        state_label.setLayoutY(146);

        pane.getChildren().add(state_label);

        Label dis_label = new Label("District : "+dis);
        dis_label.setFont(new Font("Century Gothic",12));
        dis_label.setStyle("-fx-alignment: center_left;");
        dis_label.setPrefHeight(22);
        dis_label.setPrefWidth(303);
        dis_label.setLayoutX(49);
        dis_label.setLayoutY(166);

        pane.getChildren().add(dis_label);

        if(item_name.length == 1){
            Label item_label = new Label("ITEM NAME");
            item_label.setFont(new Font("Ebrima Bold",12));
            item_label.setStyle("-fx-alignment: Center_left;");
            item_label.setLayoutX(33);
            item_label.setLayoutY(182);
            item_label.setPrefWidth(100);
            item_label.setPrefHeight(22);

            pane.getChildren().add(item_label);

            Label item = new Label(item_name[0]);
            item.setFont(new Font("Century Gothic",14));
            item.setStyle("-fx-alignment: Top_left;");
            item.setPrefHeight(22);
            item.setPrefWidth(315);
            item.setLayoutX(49);
            item.setLayoutY(206);

            pane.getChildren().add(item);


        }else{
            MenuButton menuButton = new MenuButton("Click to see items sold");
            ObservableList<MenuItem> items = FXCollections.observableArrayList();

            for(int i =0;i<item_name.length;i++){
                MenuItem menuItem = new MenuItem(item_name[i]);
                items.add(menuItem);
            }
            menuButton.getItems().addAll(items);
            menuButton.setFont(new Font("Century Gothic",14));
            menuButton.setStyle("-fx-alignment: Center_left;");
            menuButton.setPrefHeight(28);
            menuButton.setPrefWidth(203);
            menuButton.setLayoutX(33);
            menuButton.setLayoutY(190);
            pane.getChildren().add(menuButton);
        }
        recom.getChildren().add(pane);

    }
    public void addToCart(int[] id,int[] code,int[] id_price) throws SQLException {
        boolean check = false;
        for (int i=0;i<id.length;i++){
            Object[] objects = {id_price[i],id[i],code[i]};
            String sql ="Update cart SET id=? WHERE id_cart=? AND item_code=?";
            check = function.update(sql,objects);
        }
        if(check){
            function.success("Replace Item Successfully",null,"Replace Item Successfully");
        }else{
            function.warning("Replace Unsuccessfully",null,"Please contact admin!");
        }
        load();
    }
}
