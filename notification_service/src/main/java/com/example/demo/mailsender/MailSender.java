package com.example.demo.mailsender;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailSender {
    @Autowired
    private JavaMailSender javaMailSender;

    public void send(String emailTo, String subject, String message) {
        Dotenv dotenv = Dotenv.load();
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(dotenv.get("SPRING_MAIL_USERNAME"));
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }
}
