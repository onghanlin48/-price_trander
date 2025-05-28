package com.system.price_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class verify implements Initializable{

    @FXML
    private Button btn_verify;

    @FXML
    private Button button_log_in;

    @FXML
    private Label email;

    @FXML
    private TextField verify_code;

    passData data = passData.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Use receivedData in the initialize method

        email.setText(data.getEmail());
    }
    @FXML
    void resend(ActionEvent event) throws MessagingException {

        String code = function.generateRandomNumber();

            data.setCode(code);
            data.setResend(3);
        JavaMail.sendmail(data.getEmail(), "The Verify code is : " + code,"Verify Email");
            function.inform("Resend code",null,"Resend Verify Code Successfully.");

    }

    @FXML
    void verify(ActionEvent event) {
        String code = verify_code.getText();
        int resend = data.getResend();

        if(data.getCode().equals(code)){
            if(data.getResend() == 0){
                function.warning("Attempt finished",null,"Please resend the verify code.");
            }else{
            Object[] objects = {data.getUsername(),data.getPassword(),data.getEmail()};
            String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

            Node sourceNode = (Node) event.getSource();
            if(function.insert(sql,objects)){
                String username = data.getUsername();
                database.createDatabase(username);
                passData.getInstance().clearAllValues();
                function.success("Successfully",null,"Sign Up Successfully.");


                function.nextPage("login.fxml",sourceNode,"Login");
            }else{
                passData.getInstance().clearAllValues();
                function.warning("Unsuccessfully",null,"Please sign again or contact admin.");
                function.nextPage("SignUp.fxml",sourceNode,"Sign Up");
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
