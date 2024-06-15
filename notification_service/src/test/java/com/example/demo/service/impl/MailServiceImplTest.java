package com.example.demo.service.impl;
import com.example.demo.message.EmailMessage;
import com.example.demo.repository.EmailMessageRepository;
import com.example.demo.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MailServiceImplTest {

    @Autowired
    private MailService mailService;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private EmailMessageRepository emailMessageRepository;

    @Test
    public void testProcessEmailReceived_Success() {
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Test Subject")
                .content("Test Content")
                .receivers(Collections.singletonList("test@example.com"))
                .build();

        when(emailMessageRepository.save(any(EmailMessage.class))).thenReturn(emailMessage);

        mailService.processEmailReceived(emailMessage);

        ArgumentCaptor<EmailMessage> emailMessageArgumentCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailMessageRepository, times(2)).save(emailMessageArgumentCaptor.capture());

        EmailMessage savedEmailMessage = emailMessageArgumentCaptor.getValue();
        assertTrue(savedEmailMessage.isSent());
        assertNull(savedEmailMessage.getErrorMessage());
    }

    @Test
    public void testProcessEmailReceived_Failure() {
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Test Subject")
                .content("Test Content")
                .receivers(Collections.singletonList("test@example.com"))
                .build();

        when(emailMessageRepository.save(any(EmailMessage.class))).thenReturn(emailMessage);
        doThrow(new MailException("Mail sending failed") {
        }).when(javaMailSender).send((MimeMessage) any());

        mailService.processEmailReceived(emailMessage);

        ArgumentCaptor<EmailMessage> emailMessageArgumentCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailMessageRepository, times(2)).save(emailMessageArgumentCaptor.capture());
    }
}

