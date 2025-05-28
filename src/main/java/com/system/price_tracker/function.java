package com.system.price_tracker;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.NoSuchAlgorithmException;

public class function {
    public static void nextPage(String page,Node sourceNode,String title){
        try {
            // Load the FXML file
            Parent loader = FXMLLoader.load(function.class.getResource(page));

            // Get the stage from the sourceNode (usually a button)
            Stage stage = (Stage) sourceNode.getScene().getWindow();

            // Create a new scene and set it in the stage
            Scene scene = new Scene(loader);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the IOException if the FXML file cannot be loaded
        }
    }
    public static ResultSet getData(String sqlQuery) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            // Establishing the database connection
            loginDetail login = loginDetail.getInstance();
            boolean data = login.getLogin();
            if(data){
                connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
            }else{
                connection = database.getConnection();
            }


            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query and obtain a result set
            resultSet = statement.executeQuery(sqlQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public static boolean checkData(String sqlQuery) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            // Establishing the database connection
            loginDetail login = loginDetail.getInstance();
            boolean data = login.getLogin();
            if(data){
                connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
            }else{
                connection = database.getConnection();
            }



            // Create a statement
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);

            if(resultSet.next()){
                connection.close();
                statement.close();
                resultSet.close();
                return true;
            }else{
                connection.close();
                statement.close();
                resultSet.close();
                return false;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean delete(String sql) throws SQLException {
        Connection connection = null;
        try {
            // Establishing the database connection
            loginDetail login = loginDetail.getInstance();
            boolean data = login.getLogin();
            if(data){
                connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
            }else{
                connection = database.getConnection();
            }
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);

            return true;


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(String Query,Object[] item){
        Connection connection = null;
        try {
            loginDetail login = loginDetail.getInstance();
            boolean data = login.getLogin();
            if(data){
                connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
            }else{
                connection = database.getConnection();
            }

            PreparedStatement preparedStatement = connection.prepareStatement(Query);

            int i = 1;
            for (Object element : item) {
                if (element instanceof Number) {
                    preparedStatement.setInt(i, (Integer) element);
                } else {
                    preparedStatement.setString(i, (String) element);
                }
                i++;
            }


            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                connection.close();
                preparedStatement.close();
                return true;
            } else {
                connection.close();
                preparedStatement.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean insert(String insertQuery,Object[] arr){
        Connection connection = null;
        try  {
            loginDetail login = loginDetail.getInstance();
            boolean data = login.getLogin();
            if(data){
                connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
            }else{
                connection = database.getConnection();
            }
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            int i = 1;
            for (Object element : arr) {
                if (element instanceof Number) {
                    preparedStatement.setInt(i, (Integer) element);
                } else {
                    preparedStatement.setString(i, (String) element);
                }
                i++;
            }

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public  static void inform(String title,String header,String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Add custom buttons (if needed)
        alert.getButtonTypes().setAll(ButtonType.OK);

        // Show the alert and wait for a button press
        alert.showAndWait();
    }
    public  static void success(String title,String header,String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Add custom buttons (if needed)
        alert.getButtonTypes().setAll(ButtonType.OK);

        // Show the alert and wait for a button press
        alert.showAndWait();
    }
    public static void warning(String title,String header,String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Add custom buttons (if needed)
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and wait for a button press
        alert.showAndWait();
    }
    public static boolean username(String username){
        boolean check = true;
        try (Connection connection = database.getConnection()) {
            if (connection != null) {
                System.out.println("Connected");
                String selectQuery = "SELECT * FROM user WHERE username='" + username + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery);
                if (resultSet.next()) {
                    check = true;
                } else {

                    check = false;
                }
                resultSet.close();
                statement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
            check = false;
        }
        return check;
    }
    public static boolean isUpdatedUsername(String username,String current_username){
        boolean check = true;

            if(username.equals(current_username)){
                warning("Username same with current username",null,"Please user other username");
                check = true;
            }else{
                check = username(username);
            }

        return check;
    }

    public static boolean email(String email){
        boolean check = true;
        try (Connection connection = database.getConnection()) {
            if (connection != null) {
                System.out.println("Connected");
                String selectQuery = "SELECT * FROM user WHERE email='" + email + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery);
                if (resultSet.next()) {
                    check = true;
                } else {

                    check = false;
                }
                resultSet.close();
                statement.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
            check = false;
        }
        return check;
    }
    public static boolean isUpdatedEmail(String email,String current_email){
        boolean check = true;

        if(email.equals(current_email)){
            warning("Email same with current Email",null,"Please user other Email");
            check = true;
        }else{
            check = username(email);
        }

        return check;
    }
    public static String generateRandomNumber() {
        Random random = new Random();
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10); // Generates random digit (0-9)
            number.append(digit);
        }

        return number.toString();
    }
    public static boolean isValidEmail(String email) {
        // Regular expression for basic email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isMatchPassword(String passwrod,String c_password){
        return passwrod.equals(c_password);
    }
    public static String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform the hashing
            byte[] hashedBytes = digest.digest(password.getBytes());

            // Convert bytes to hexadecimal representation
            StringBuilder hexString = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                String hex = Integer.toHexString(0xff & hashedByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Return hashed password in hexadecimal format
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle exception if algorithm is not available
            e.printStackTrace();
            return null;
        }
    }
    public static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }
    public static boolean showConfirmation(String title,String header,String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Add "Yes" and "No" buttons
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Show the confirmation dialog and wait for user response
        Optional<ButtonType> result = alert.showAndWait();

        // Check which button was clicked and return boolean value accordingly
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    public static boolean isPrice(String price) {
        String pricePattern = "^[\\d]+(\\.\\d{1,2})?$";
        Pattern pattern = Pattern.compile(pricePattern);
        Matcher matcher = pattern.matcher(price);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean load(String sql){
        Connection connection = null;
        try  {
            loginDetail login = loginDetail.getInstance();
            boolean data = login.getLogin();
            if(data){
                connection = database.getConnection("jdbc:mysql://localhost:3306/"+login.getUsername());
            }else{
                connection = database.getConnection();
            }


            // Create a statement
            Statement statement = connection.createStatement();

            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            warning("Failed Import",null,"Failed Import please make sure format is right!");
            return false;
        }
    }
    public static boolean strongpasswrod(String password_1){
        String string = password_1;
        int length = string.length();
        boolean Strong = false;
        boolean Moderate = false;

        boolean lowercase = false;
        boolean uppercase = false;
        boolean specialCharacter = false;
        boolean digit = false;
        for ( int i = 0 ; i < length ; i ++){
            char password = string.charAt(i);
            if ( password >= 'a' && password <= 'z'){
                lowercase = true;
            }
            else if (password >= 'A' && password <= 'Z'){
                uppercase = true;
            }
            else if ( hasSpecialCharacter (password)){
                specialCharacter = true;
            }
            else if (password >= '0' && password <= '9'){
                digit =true;
            }
        }
        if (length>= 8 && lowercase && uppercase && specialCharacter && digit ){
            Strong = true;
        }
        if (length >=6 && lowercase && uppercase && specialCharacter){
            Moderate = true ;
        }

        if (Strong){
            return true;
        }
        else if (Moderate){
            return false;
        }
        else {
            return false;
        }

    }
    private static boolean hasSpecialCharacter( char password ){
        switch (password){
            case'!':
            case'@':
            case'#':
            case'$':
            case'%':
            case'^':
            case'&':
            case'*':
            case'(':
            case')':
            case'-':
            case'+':
                return true;
            default:
                return false;
        }
    }

}
