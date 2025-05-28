package com.system.price_tracker;

import java.sql.*;

public class database {
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    public static final String USER = "root";
    public static final String PASSWORD = "";
    public static Connection getConnection() throws SQLException {



        String jdbcUrl = "jdbc:mysql://localhost:3306/price_tracker";



        String username = "root";
        String password = "";

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    public static Connection getConnection(String jdbcUrl) throws SQLException {


        String username = "root";
        String password = "";

        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public static void createDatabase(String username){
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
        createDatabase(connection, username);
            System.out.println();
        // Connect to the created database
        String dbUrl = JDBC_URL + username;
        try (Connection dbConnection = DriverManager.getConnection(dbUrl, USER, PASSWORD)) {
            // Create tables
            createTable(dbConnection, "lookup_item", "CREATE TABLE lookup_item (item_code INT AUTO_INCREMENT PRIMARY KEY, item VARCHAR(255), unit VARCHAR(255), item_group VARCHAR(255),item_category VARCHAR(255))");
            createTable(dbConnection, "lookup_premise", "CREATE TABLE lookup_premise (premise_code INT AUTO_INCREMENT PRIMARY KEY, premise VARCHAR(255), address VARCHAR(1000),type VARCHAR(255),state VARCHAR(255), district VARCHAR(255))");
                createTable(dbConnection, "pricecatcher", "CREATE TABLE pricecatcher (id INT AUTO_INCREMENT PRIMARY KEY, date DATE, premise_code INT,item_code INT,price DOUBLE)");

            createTable(dbConnection, "Cart", "CREATE TABLE Cart (id_cart INT AUTO_INCREMENT PRIMARY KEY, id INT,item_code INT)");
            // Add more tables if needed
        } catch (SQLException e) {
            e.printStackTrace();
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    private static void createDatabase(Connection connection, String dbName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            statement.executeUpdate(sql);
            System.out.println("Database created successfully: " + dbName);
        }
    }
    private static void createTable(Connection connection, String tableName, String createTableQuery) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
            System.out.println("Table created successfully: " + tableName);
        }
    }
    static void change(String oldUsername, String NewUsername){
        try (Connection conn = DriverManager.getConnection(JDBC_URL + oldUsername, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE "+NewUsername);
            stmt.close();
            Statement stmt_u = conn.createStatement();

            stmt_u.executeUpdate("RENAME TABLE "+oldUsername+".cart to "+NewUsername+".cart;");
            stmt_u.close();
            Statement stmt_item = conn.createStatement();
            stmt_item.executeUpdate("RENAME TABLE "+oldUsername+".lookup_item to "+NewUsername+".lookup_item;");
            stmt_item.close();
            Statement stmt_premise = conn.createStatement();
            stmt_premise.executeUpdate("RENAME TABLE "+oldUsername+".lookup_premise to "+NewUsername+".lookup_premise;");
            stmt_premise.close();
            Statement stmt_price = conn.createStatement();
            stmt_price.executeUpdate("RENAME TABLE "+oldUsername+".pricecatcher to "+NewUsername+".pricecatcher;");
            stmt_price.close();

            Statement stmt_d = conn.createStatement();

            stmt_d.executeUpdate("DROP DATABASE "+oldUsername+";");
            stmt_d.close();
            System.out.println("Database name changed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
