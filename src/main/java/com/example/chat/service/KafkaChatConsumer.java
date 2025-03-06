package com.example.chat.service;

import com.example.chat.model.Message;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaChatConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    public KafkaChatConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(Message message) {
        // Forward message to receiver's WebSocket queue
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(), "/queue/messages", message
        );

        // Also send to sender to update their own chat box
        messagingTemplate.convertAndSendToUser(
                message.getSenderId().toString(), "/queue/messages", message
        );
    }
}