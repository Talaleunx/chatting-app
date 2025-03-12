package com.example.chat.service;

import com.example.chat.model.Message;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;

    public ChatService(MessageRepository messageRepository, EncryptionService encryptionService, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
    }

    public Message saveMessage(Long senderId, Long receiverId, String content, MultipartFile image, MultipartFile pdf, String timestamp) throws Exception {
        String encryptedContent = content != null ? encryptionService.encrypt(content) : null;
        String encryptedImage = image != null ? encryptImage(image) : null;
        String encryptedPdf = pdf != null ? encryptPdf(pdf) : null;

        String senderUsername = userRepository.findUsernameById(senderId);
        String receiverUsername = userRepository.findUsernameById(receiverId);

        Message message = new Message(senderId, receiverId, senderUsername, receiverUsername, encryptedContent, encryptedImage, encryptedPdf, timestamp);

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
        messagesSent.sort(Comparator.comparing(Message::getTimestamp));

        // Fetch usernames from DB
        String senderUsername = userRepository.findUsernameById(userId1);
        String receiverUsername = userRepository.findUsernameById(userId2);

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

                // Set usernames dynamically
                message.setSenderUsername(senderUsername);
                message.setReceiverUsername(receiverUsername);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }).collect(Collectors.toList());
    }
}
