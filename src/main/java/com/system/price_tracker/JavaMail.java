package com.system.price_tracker;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class JavaMail {
    public static void sendmail(String receipt,String code,String title) throws MessagingException {
        String username = "linwu212@gmail.com"; // Replace with your email
        final String password = "pdhktjzacpzzcqhd"; // Replace with your password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your SMTP server
        props.put("mail.smtp.port", "587"); // Replace with your SMTP port (e.g., 587 for Gmail)

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
            
        });
        Message message = prepareMessage(session,username,receipt,code,title);
        Transport.send(message);

        System.out.println("Email sent successfully!");

    }

    private static Message prepareMessage(Session session, String username, String receipt,String code,String title) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Replace with your email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receipt)); // Replace with recipient's email
            message.setSubject(title);
            message.setText(code);
            return message;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
