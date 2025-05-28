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
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class updated_email implements Initializable {
    loginDetail login = loginDetail.getInstance();
    passData data = passData.getInstance();
    @FXML
    private Label Label_email;
    @FXML
    private Button button_Home;

    @FXML
    private Button button_log_in;

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
    private TextField tf_verify_code;

    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_code_v;

    @FXML
    void VerifyEmail(ActionEvent event) {
        String code = tf_code_v.getText();
        int resend = data.getResend();

        if(data.getCode().equals(code)){
            if(data.getResend() == 0){
                function.warning("Attempt finished",null,"Please resend the verify code.");
            }else{
                String username = login.getUsername();
                String updateQuery = "UPDATE user SET email = ? WHERE username='"+username+"'";
                String newEmail = data.getEmail();
                Object[] objects = {newEmail};
                Node sourceNode = (Node) event.getSource();
                login.clearAllValues();
                data.clearAllValues();
                if(function.update(updateQuery,objects)){;

                    function.success("Successfully",null,"Successfully Updated Email.");


                    function.nextPage("login.fxml",sourceNode,"Login");
                }else{
                    passData.getInstance().clearAllValues();
                    function.warning("Unsuccessfully",null,"Unsuccessfully Updated Email.");
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
    void resend_old(ActionEvent event) throws MessagingException {
        String code = function.generateRandomNumber();

        data.setCode(code);
        data.setResend(3);
        JavaMail.sendmail(login.getEmail(),"The change email verify code :"+code,"Change email verify");
        function.inform("Resend code",null,"Resend Verify Code Successfully.");
    }

    @FXML
    void resend(ActionEvent event) throws MessagingException, SQLException {


        String code = function.generateRandomNumber();

        data.setCode(code);
        data.setResend(3);

        if(tf_email.getText().isEmpty()){
            function.warning("Error",null,"Please fill new email.");
        }else{
            if(function.isValidEmail(tf_email.getText())){
                if(tf_email.getText().equals(login.getEmail())){
                    function.warning("Error",null,"New email cannot same with the old email!");
                }else{
                    login.setLogin(false);
                    String sql = "SELECT * FROM user WHERE email = '"+tf_email.getText()+"'";
                    ResultSet resultSet = function.getData(sql);
                    if(resultSet.next()){
                        function.warning("Error",null,"Email already exist!");
                    }else{
                        tf_email.setDisable(true);
                        JavaMail.sendmail(tf_email.getText(),"The change email verify code :"+code,"Change email verify");
                        function.inform("Resend code",null,"Resend Verify Code Successfully.");
                        tf_verify_code.setDisable(false);
                    }
                    login.setLogin(true);

                }

            }else{
                function.warning("Error",null,"Email no valid.");

            }

        }

    }
    @FXML
    void SaveEmail(ActionEvent event) throws MessagingException, SQLException {
        String email = tf_email.getText();
        String password = tf_password.getText();
        String code = tf_verify_code.getText();
        int resend = data.getResend();

        if(email.isEmpty() || password.isEmpty()){
            function.warning("Error",null,"Please fill all");

        }else{
            if(function.isValidEmail(email)){
                if(code.isEmpty()){
                    function.warning("Error",null,"Please verify you new email.");
                }else{
                    password = function.hashPassword(password);
                    if(password.equals(login.getPassword())){
                        if(email.equals(login.getEmail())){
                            function.warning("Error",null,"New email cannot same with the old email!");
                        }else{
                            if(data.getCode().equals(code)){
                                if(data.getResend() == 0){
                                    function.warning("Attempt finished",null,"Please resend the verify code.");
                                }else{
                                    login.setLogin(false);
                                    String sql = "SELECT * FROM user WHERE email = '"+email+"'";
                                    ResultSet resultSet = function.getData(sql);
                                    if(resultSet.next()){
                                        login.setLogin(true);
                                        function.warning("Error",null,"Email already exist!");

                                    }else{
                                        login.setLogin(true);
                                        String email_v = login.getEmail();
                                        String code_v = function.generateRandomNumber();

                                        data.setCode(code);
                                        data.setEmail(email);
                                        JavaMail.sendmail(email_v,"The change username verify code :"+code_v,"Change Username verify");
                                        Node sourceNode = (Node) event.getSource();
                                        function.nextPage("email_v.fxml",sourceNode,"Updated Email Verify");
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

                    }else{
                        function.warning("Error",null,"Password Wrong!");
                    }
                }
            }else{
                function.warning("Error",null,"Email no valid.");
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
