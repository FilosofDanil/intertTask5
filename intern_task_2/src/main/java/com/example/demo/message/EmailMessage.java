package com.example.demo.message;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Jacksonized
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailMessage {
    @Id
    String id;
    String subject;
    String content;
    List<String> receivers;
    String errorMessage;
    boolean sent;
    int attempts;
    LocalDateTime lastAttempt;
}
