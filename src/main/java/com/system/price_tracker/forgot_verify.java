package com.system.price_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class forgot_verify implements Initializable {

    @FXML
    private Button btn_verify;

    @FXML
    private Button button_log_in;

    @FXML
    private Label email;
    passData data = passData.getInstance();
    @FXML
    private TextField verify_code;
    public void initialize(URL location, ResourceBundle resources) {
        // Use receivedData in the initialize method

        email.setText(data.getEmail());
    }
    @FXML
    void resend(ActionEvent event) throws MessagingException {
        String code = function.generateRandomNumber();

        data.setCode(code);
        data.setResend(3);
        JavaMail.sendmail(data.getEmail(),"Verify Code : " + code,"Recovery Username and Password!");
        function.inform("Resend code",null,"Resend Verify Code Successfully.");
    }

    @FXML
    void verify(ActionEvent event) throws MessagingException {
        String email= data.getEmail();
        String code = verify_code.getText();
        int resend = data.getResend();

        if(data.getCode().equals(code)){
            if(data.getResend() == 0){
                function.warning("Attempt finished",null,"Please resend the verify code.");
            }else{

                String updateQuery = "UPDATE user SET password = ? WHERE email='"+email+"'";
                String name = function.generateRandomString();
                String password = function.hashPassword(name);

                Object[] objects = {password};

                Node sourceNode = (Node) event.getSource();
                if(function.update(updateQuery,objects)){

                    function.success("Successfully",null,"Successfully send Username and New Password to you email.");

                    JavaMail.sendmail(email,"Username : " + data.getUsername() + "\nPassword : "+name,"Username and Password!");
                    String username = data.getUsername();
                    passData.getInstance().clearAllValues();
                    function.nextPage("login.fxml",sourceNode,"Login");
                }else{
                    passData.getInstance().clearAllValues();
                    function.warning("Unsuccessfully",null,"Unsuccessfully send the Username and New Password.");
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

}
