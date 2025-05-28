package com.system.price_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javax.mail.MessagingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class updated_username implements Initializable {
    loginDetail login = loginDetail.getInstance();
    passData data = passData.getInstance();
    @FXML
    private Button button_Home;

    @FXML
    private Label label_PASSWORD;

    @FXML
    private Label label_Username;

    @FXML
    private Label label_address;

    @FXML
    private Label label_profile;

    @FXML
    private Label label_username;

    @FXML
    private PasswordField tf_password;

    @FXML
    private TextField tf_username_new;

    @FXML
    private Label Label_email;

    @FXML
    private TextField tf_code;
    @FXML
    void VerifyUsername(ActionEvent event) {

        String code = tf_code.getText();
        int resend = data.getResend();

        if(data.getCode().equals(code)){
            if(data.getResend() == 0){
                function.warning("Attempt finished",null,"Please resend the verify code.");
            }else{
                String username = login.getUsername();
                String updateQuery = "UPDATE user SET username = ? WHERE username='"+username+"'";
                String newUsername = data.getUsername();
                Object[] objects = {newUsername};
                Node sourceNode = (Node) event.getSource();
                login.clearAllValues();
                data.clearAllValues();
                if(function.update(updateQuery,objects)){
                    System.out.println(username+newUsername);
                    database.change(username,newUsername);

                    function.success("Successfully",null,"Successfully Updated Username.");


                    function.nextPage("login.fxml",sourceNode,"Login");
                }else{
                    passData.getInstance().clearAllValues();
                    function.warning("Unsuccessfully",null,"Unsuccessfully Updated Username.");
                    function.nextPage("login.fxml",sourceNode,"Login");
                }

            }
        }else{
            if(resend == 0){
                function.warning("Attempt finished",null,"Please resend the verify code.");
            }else{
                resend--;
                data.setResend(resend);
                function.inform("Attempt Left",null,"Attempt left : "+data.getResend());
            }
        }
    }
    @FXML
    void resend(ActionEvent event) throws MessagingException {
        String code = function.generateRandomNumber();


        data.setCode(code);
        data.setResend(3);
        JavaMail.sendmail(login.getEmail(),"The change username verify code :"+code,"Change Username verify");
        function.inform("Resend code",null,"Resend Verify Code Successfully.");
    }

    @FXML
    void SaveUsername(ActionEvent event) throws MessagingException, SQLException {
        String username = tf_username_new.getText();
        String password = tf_password.getText();
        if(username.isEmpty() || password.isEmpty()){
            function.warning("Error",null,"Please fill all");
        }else{
            password = function.hashPassword(password);
            if(password.equals(login.getPassword())){
                if(username.equals(login.getUsername())){
                    function.warning("Error",null,"New Username cannot same with the old username!");
                }else{
                    login.setLogin(false);
                    String sql = "SELECT * FROM user WHERE username = '"+username+"'";
                    ResultSet resultSet = function.getData(sql);
                    if(resultSet.next()){
                        login.setLogin(true);
                        function.warning("Error",null,"Username already exist!");

                    }else{
                        login.setLogin(true);
                        String email = login.getEmail();
                        String code = function.generateRandomNumber();

                        data.setCode(code);
                        data.setUsername(username);
                        JavaMail.sendmail(email,"The change username verify code :"+code,"Change Username verify");
                        Node sourceNode = (Node) event.getSource();
                        function.nextPage("username_v.fxml",sourceNode,"Updated Username Verify");
                    }

                }

            }else{
                function.warning("Error",null,"Password Wrong!");

            }
        }
    }

    @FXML
    void click_email(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("UpdateEmail.fxml",sourceNode,"Change Email");
    }

    @FXML
    void click_password(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("UpdatePassword.fxml",sourceNode,"Change Password");
    }

    @FXML
    void click_username(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("UpdateUsername.fxml",sourceNode,"Change Username");
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
        if(Label_email != null){
            Label_email.setText(login.getEmail());
        }
        label_Username.setText(login.getUsername());
        label_username.setText(login.getUsername());
    }
}
