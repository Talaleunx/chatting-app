package com.example.chat.service;

import com.example.chat.model.Message;
import com.example.chat.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final EncryptionService encryptionService;

    public ChatService(MessageRepository messageRepository, EncryptionService encryptionService) {
        this.messageRepository = messageRepository;
        this.encryptionService = encryptionService;
    }

    public Message saveMessage(Long senderId, Long receiverId, String content, MultipartFile image, MultipartFile pdf, String timestamp) throws Exception {
        String encryptedContent = content != null ? encryptionService.encrypt(content) : null;
        String encryptedImage = image != null ? encryptImage(image) : null;
        String encryptedPdf = pdf != null ? encryptPdf(pdf) : null;

        Message message = new Message(senderId, receiverId, encryptedContent, encryptedImage, encryptedPdf, timestamp);
        Message savedMessage = messageRepository.save(message);

        // Decrypt the message before returning
        savedMessage.setEncryptedContent(encryptedContent != null ? encryptionService.decrypt(savedMessage.getEncryptedContent()) : null);
        savedMessage.setEncryptedImage(encryptedImage != null ? decryptImage(savedMessage.getEncryptedImage()) : null);
        savedMessage.setEncryptedPdf(encryptedPdf != null ? decryptPdf(savedMessage.getEncryptedPdf()) : null);
        return savedMessage;
    }

    private String encryptImage(MultipartFile image) throws Exception {
        byte[] imageBytes = image.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return encryptionService.encrypt(base64Image);
    }

    private String decryptImage(String encryptedImage) throws Exception {
        String base64Image = encryptionService.decrypt(encryptedImage);
        return base64Image;
    }

    private String encryptPdf(MultipartFile pdf) throws Exception {
        byte[] pdfBytes = pdf.getBytes();
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
        return encryptionService.encrypt(base64Pdf);
    }

    private String decryptPdf(String encryptedPdf) throws Exception {
        String base64Pdf = encryptionService.decrypt(encryptedPdf);
        return base64Pdf;
    }

    // Existing getConversation method
    public List<Message> getConversation(Long userId1, Long userId2) {
        List<Message> messagesSent = messageRepository.findBySenderIdAndReceiverId(userId1, userId2);
        List<Message> messagesReceived = messageRepository.findByReceiverIdAndSenderId(userId1, userId2);

        messagesSent.addAll(messagesReceived);
        messagesSent.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        return messagesSent.stream().map(message -> {
            try {
                if (message.getEncryptedContent() != null) {
                    message.setEncryptedContent(encryptionService.decrypt(message.getEncryptedContent()));
                }
                if (message.getEncryptedImage() != null) {
                    message.setEncryptedImage(decryptImage(message.getEncryptedImage()));
                }
                if (message.getEncryptedPdf() != null) {
                    message.setEncryptedPdf(decryptPdf(message.getEncryptedPdf()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }).collect(Collectors.toList());
    }
}
