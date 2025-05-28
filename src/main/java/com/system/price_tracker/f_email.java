package com.system.price_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class f_email {

    @FXML
    private Button btn_submit;

    @FXML
    private Button button_back;

    @FXML
    private TextField tf_email;

    @FXML
    void back(ActionEvent event) {
        Node sourceNode = (Node) event.getSource();
        function.nextPage("login.fxml",sourceNode,"Login");
    }

    @FXML
    void submit(ActionEvent event) {
        String email = tf_email.getText();
        if(!function.isValidEmail(email)){
            function.warning("Invalid Email",null,"Email invalid.");
        }else{
            String sqlQuery = "SELECT * FROM user WHERE email='"+email+"'";
            ResultSet resultSet = function.getData(sqlQuery);

            // Process the ResultSet as needed
            try {
                if(resultSet.next()){

                    String code = function.generateRandomNumber();
                    passData data = passData.getInstance();
                    data.setUsername(resultSet.getString("username"));
                    data.setEmail(email);
                    data.setCode(code);
                    data.setResend(3);
                    JavaMail.sendmail(resultSet.getString("email"),"Verify Code : " + code,"Recovery Username and Password!");
                    Node sourceNode = (Node) event.getSource();
                    function.nextPage("forgot.fxml",sourceNode,"Verify");

                }else{
                    function.inform("Email Error",null,"Your email hasn't been sign up.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }


    }

}
