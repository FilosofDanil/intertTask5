package com.example.demo.listener;

import com.example.demo.message.EmailMessage;
import com.example.demo.service.MailService;
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

    MailService mailService;

    @SneakyThrows
    @KafkaListener(topics = "${kafka.topic.emailReceived}")
    public void emailReceived(EmailMessage message) {
        mailService.processEmailReceived(message);
    }
}
