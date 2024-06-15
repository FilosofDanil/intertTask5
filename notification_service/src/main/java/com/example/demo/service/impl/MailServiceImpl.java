package com.example.demo.service.impl;


import com.example.demo.mailsender.MailSender;
import com.example.demo.message.EmailMessage;
import com.example.demo.repository.EmailMessageRepository;
import com.example.demo.service.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailServiceImpl implements MailService {
    @Autowired
    MailSender mailSender;

    final EmailMessageRepository emailMessageRepository;

    @Override
    public void processEmailReceived(EmailMessage message) {
        EmailMessage savedMessage = emailMessageRepository.save(message);
        for (String s: message.getReceivers()){
            try{
                mailSender.send(s, message.getSubject(), message.getContent());
                savedMessage.setSent(true);
            } catch (MailException e){
                savedMessage.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
                message.setLastAttempt(LocalDateTime.now());
            }
            emailMessageRepository.save(savedMessage);
        }
    }
}
