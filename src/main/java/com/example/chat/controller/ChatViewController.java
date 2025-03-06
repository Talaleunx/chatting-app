package com.example.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatViewController {

    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // Returns the Thymeleaf template "chat.html"
    }
}
