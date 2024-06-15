package com.example.demo.service.scheduled;


import com.example.demo.mailsender.MailSender;
import com.example.demo.message.EmailMessage;
import com.example.demo.repository.EmailMessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FailedMailsChecker {
    @Autowired
    MailSender mailSender;
    final EmailMessageRepository emailMessageRepository;

    @Scheduled(cron = "0 */5 * * * *")
    public void performTask() {
        List<EmailMessage> unsentMessages = emailMessageRepository.findBySentFalse();

        for (EmailMessage message : unsentMessages) {
            for (String s : message.getReceivers()) {
                try {
                    mailSender.send(s, message.getSubject(), message.getContent());
                    message.setSent(true);
                } catch (MailException e) {
                    message.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
                    message.setAttempts(message.getAttempts() + 1);
                    message.setLastAttempt(LocalDateTime.now());
                }
                emailMessageRepository.save(message);
            }
        }

    }
}