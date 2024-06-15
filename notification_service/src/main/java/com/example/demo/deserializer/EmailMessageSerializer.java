package com.example.demo.deserializer;


import com.example.demo.message.EmailMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;


import java.io.IOException;

public class EmailMessageSerializer implements Deserializer<EmailMessage> {

    @Override
    public EmailMessage deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        EmailMessage emailMessage = null;
        try {
            emailMessage = mapper.readValue(data, EmailMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emailMessage;
    }
}