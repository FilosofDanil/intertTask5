package com.example.demo.service;


import com.example.demo.message.EmailMessage;

public interface MailService {
    void processEmailReceived(EmailMessage message);
}

