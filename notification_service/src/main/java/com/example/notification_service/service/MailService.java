package com.example.notification_service.service;


import com.example.notification_service.message.EmailMessage;

public interface MailService {
    void processEmailReceived(EmailMessage message);
}

