package com.example.chat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String encryptedContent;
    private String encryptedImage; // New field for encrypted image data
    private String encryptedPdf; // New field for encrypted PDF data
    private String timestamp;


    @Transient // Prevents these fields from being persisted in the database
    private String senderUsername;

    @Transient
    private String receiverUsername;
    // Constructors, getters, and setters
    public Message() {}




    public Message(Long senderId, Long receiverId, String senderUsername, String receiverUsername, String encryptedContent, String encryptedImage, String encryptedPdf, String timestamp) {
        this.receiverUsername = receiverUsername;
        this.senderUsername = senderUsername;
        this.timestamp = timestamp;
        this.encryptedPdf = encryptedPdf;
        this.encryptedImage = encryptedImage;
        this.encryptedContent = encryptedContent;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.id = id;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
// Existing getters and setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public String getEncryptedImage() {
        return encryptedImage;
    }

    public void setEncryptedImage(String encryptedImage) {
        this.encryptedImage = encryptedImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Add getter and setter for encryptedPdf
    public String getEncryptedPdf() {
        return encryptedPdf;
    }

    public void setEncryptedPdf(String encryptedPdf) {
        this.encryptedPdf = encryptedPdf;
    }
}
