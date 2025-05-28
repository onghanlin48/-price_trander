package com.system.price_tracker;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.sql.*;

import java.io.IOException;


public class sign {

    @FXML
    private Button button_log_in;

    @FXML
    private Button button_sign_up;

    @FXML
    private PasswordField pf_password;

    @FXML
    private PasswordField tf_cpass;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_username;
    private Stage stage;
    private Scene scene;
    @FXML
    public void login(ActionEvent event) throws IOException {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("login.fxml",sourceNode,"Login");
    }

    @FXML
    public void signup(ActionEvent event) throws IOException, MessagingException {
        String username = tf_username.getText();
        String password = pf_password.getText();
        String c_password = tf_cpass.getText();
        String email = tf_email.getText();

        if(username.isEmpty() && password.isEmpty() && c_password.isEmpty() && email.isEmpty()){
            function.warning("Error",null,"Please fill in all required fields.");
        }else{

            if (!(function.isValidEmail(email)) && !(function.isMatchPassword(password,c_password))){
                function.warning("Invalid and No Match",null,"Email invalid and Password no match.");
            } else if (!(function.isValidEmail(email))) {
                function.warning("Invalid email",null,"Please enter a valid email address.");
            } else if (!(function.isMatchPassword(password,c_password))) {
                function.warning("Password No Match",null,"Password and Confirm Password must be same.");
            }else if(!(function.strongpasswrod(password))){
                function.warning("Password Weak",null,"It contains at least one lowercase English character.\nIt contains at least one uppercase English character.\nIt contains at least one special character.\nThe special characters are: !@#$%^&*()-+ Its length is at least 8.\nIt contains at least one digit.");

            }
            else{
                if(function.username(username) && function.email(email)){
                    function.warning("Username and Email Exist",null,"Username and Email already exits!\nPlease use other username and email.");
                } else if (function.username(username)) {
                    function.warning("Username Exist",null,"Username already exits!\nPlease use other username.");
                } else if (function.email(email)) {
                    function.warning("Email Exist",null,"Email already exits!\nPlease use other email.");
                }else{
                    String code = function.generateRandomNumber();
                    String h_p = function.hashPassword(password);

                        JavaMail.sendmail(email,"The Verify code is : " + code,"Verify Email");
                        passData data = passData.getInstance();
                        data.setUsername(username);
                        data.setEmail(email);
                        data.setPassword(h_p);
                        data.setCode(code);
                        data.setResend(3);

                    Node sourceNode = (Node) event.getSource();
                    function.nextPage("verify_email.fxml",sourceNode,"Verify Email");

                }

            }

        }


    }


}
