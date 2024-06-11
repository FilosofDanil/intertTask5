package com.example.notification_service.repository;

import com.example.notification_service.message.EmailMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EmailMessageRepository extends ElasticsearchRepository<EmailMessage, String> {
    List<EmailMessage> findBySentFalse();
}

