package com.example.notification_service.listener;

import com.example.notification_service.message.EmailMessage;
import com.example.notification_service.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailListener {
    ObjectMapper objectMapper;

    MailService mailService;

    @SneakyThrows
    @KafkaListener(topics = "${kafka.topic.emailReceived}")
    public void emailReceived(String message) {
        EmailMessage emailMessage = objectMapper.readValue(message, EmailMessage.class);
        mailService.processEmailReceived(emailMessage);
    }
}
