package com.example.chat.controller;

import com.example.chat.model.Message;
import com.example.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final KafkaTemplate<String, Message> kafkaTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService, KafkaTemplate<String, Message> kafkaTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @MessageMapping("/sendMessage")
    public void handleWebSocketMessage(@Payload Message message) throws Exception {
        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();

        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("Sender ID and Receiver ID cannot be null");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        // Save message in DB
        Message savedMessage = chatService.saveMessage(
                senderId, receiverId, message.getEncryptedContent(), null, null, timestamp
        );

        // Publish message to Kafka topic
        kafkaTemplate.send("chat-messages", savedMessage);
    }

    @PostMapping("/api/chat/sendImage")
    public ResponseEntity<Message> sendImage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam MultipartFile image) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        // Save image in DB
        Message savedMessage = chatService.saveMessage(senderId, receiverId, null, image, null, timestamp);

        // Publish message to Kafka topic
        kafkaTemplate.send("chat-messages", savedMessage);

        return ResponseEntity.ok(savedMessage);
    }

    @PostMapping("/api/chat/sendPdf")
    public ResponseEntity<Message> sendPdf(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam MultipartFile pdf) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        // Save PDF in DB
        Message savedMessage = chatService.saveMessage(senderId, receiverId, null, null, pdf, timestamp);

        // Publish message to Kafka topic
        kafkaTemplate.send("chat-messages", savedMessage);

        return ResponseEntity.ok(savedMessage);
    }

    // Existing getConversation method
    @GetMapping("/api/chat/conversation/{receiverId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable Long receiverId, @RequestParam Long senderId) {
        List<Message> conversation = chatService.getConversation(senderId, receiverId);
        return ResponseEntity.ok(conversation);
    }
}







