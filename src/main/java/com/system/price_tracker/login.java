package com.system.price_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login {
    @FXML
    private Button button_forgot;

    @FXML
    private Button button_login;

    @FXML
    private Button button_sign_up;

    @FXML
    private PasswordField pf_password;

    @FXML
    private TextField tf_username;
    private Stage stage;
    private Scene scene;

    @FXML
    void login(ActionEvent event) throws IOException{
        String username = tf_username.getText();
        String password = pf_password.getText();
        if(username.isEmpty() && password.isEmpty()){
            function.warning("Login Error",null,"Please fill username and password.");
        }else{

            String sqlQuery = "SELECT * FROM user WHERE username='"+username+"' AND password='"+function.hashPassword(password)+"'";
            ResultSet resultSet = function.getData(sqlQuery);

            // Process the ResultSet as needed
            try {
                if(resultSet.next()){
                    String code = function.generateRandomNumber();
                    passData data = passData.getInstance();
                    data.setCode(code);
                    data.setResend(3);
                    loginDetail login = loginDetail.getInstance();
                    login.setLogin(false);
                    login.setEmail(resultSet.getString("email"));
                    login.setPassword(function.hashPassword(password));
                    login.setUsername(username);
                    JavaMail.sendmail(resultSet.getString("email"),"The login code is : " + code,"Login Verify");
                    Node sourceNode = (Node) event.getSource();
                    function.nextPage("login_verify.fxml",sourceNode,"Login Verify");

                }else{

                    function.inform("Login Error",null,"Wrong username and password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void sign(ActionEvent event) throws IOException {

        Node sourceNode = (Node) event.getSource();
        function.nextPage("SignUp.fxml",sourceNode,"Sign Up");
    }
    @FXML
    void forgot(ActionEvent event) throws IOException {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("email_forgot.fxml",sourceNode,"Forgot Username and Password");
    }

}
