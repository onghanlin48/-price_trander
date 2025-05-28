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
import java.util.ResourceBundle;

public class updated_password implements Initializable {
    loginDetail login = loginDetail.getInstance();
    passData data = passData.getInstance();
    @FXML
    private Button button_Home;

    @FXML
    private Button button_SavePassword;

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
    private PasswordField tf_ConfirmPassword;

    @FXML
    private PasswordField tf_NewPassword;

    @FXML
    private PasswordField tf_OldPassword;
    @FXML
    private TextField tf_code;
    @FXML
    private Label Label_email;
    @FXML
    void VerifyPassword(ActionEvent event) {
        String code = tf_code.getText();
        int resend = data.getResend();

        if(data.getCode().equals(code)){
            if(data.getResend() == 0){
                function.warning("Attempt finished",null,"Please resend the verify code.");
            }else{
                String username = login.getUsername();
                String updateQuery = "UPDATE user SET password = ? WHERE username='"+username+"'";

                Object[] objects = {data.getPassword()};
                Node sourceNode = (Node) event.getSource();
                login.clearAllValues();
                data.clearAllValues();
                if(function.update(updateQuery,objects)){

                    function.success("Successfully",null,"Successfully Updated Password.");


                    function.nextPage("login.fxml",sourceNode,"Login");
                }else{
                    passData.getInstance().clearAllValues();
                    function.warning("Unsuccessfully",null,"Unsuccessfully Updated Password.");
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
        JavaMail.sendmail(login.getEmail(),"The change password verify code :"+code,"Change password verify");
        function.inform("Resend code",null,"Resend Verify Code Successfully.");
    }

    @FXML
    void SavePassword(ActionEvent event) throws MessagingException {
        String o_password = tf_OldPassword.getText();
        String n_password = tf_NewPassword.getText();
        String c_password = tf_ConfirmPassword.getText();
        System.out.println(o_password);
        if(o_password.isEmpty() || n_password.isEmpty() || c_password.isEmpty()){
            function.warning("Error",null,"Please fill all");
        }else if(!(function.strongpasswrod(n_password))){
            function.warning("Password Weak",null,"It contains at least one lowercase English character.\nIt contains at least one uppercase English character.\nIt contains at least one special character.\nThe special characters are: !@#$%^&*()-+ Its length is at least 8.\nIt contains at least one digit.");

        }
        else{
            if(n_password.equals(c_password)){
                o_password= function.hashPassword(o_password);
                if(o_password.equals(login.getPassword())){
                    n_password = function.hashPassword(n_password);
                    if(o_password.equals(n_password)){
                        function.warning("Error",null,"New password cannot same with old password!");
                    }else{
                        String email = login.getEmail();
                        String code = function.generateRandomNumber();

                        data.setCode(code);
                        data.setPassword(n_password);
                        JavaMail.sendmail(email,"The change password verify code :"+code,"Change password verify");
                        Node sourceNode = (Node) event.getSource();
                        function.nextPage("password_v.fxml",sourceNode,"Updated Password Verify");
                    }
                }else{
                    function.warning("Error",null,"Old password Wrong!");
                }
            }else{
                function.warning("Error",null,"New Password and Confirm Password must be same!");
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
