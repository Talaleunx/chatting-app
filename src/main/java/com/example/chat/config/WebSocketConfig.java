package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue","/user");  // Enables in-memory message broker for /queue
        config.setApplicationDestinationPrefixes("/app");  // Prefix for WebSocket messages
        config.setUserDestinationPrefix("/user");  // ✅ Required for sending messages to specific users
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // WebSocket endpoint
                .setAllowedOrigins("http://localhost:8081")  // 🔴 Change this to specific domains in production
                .withSockJS();  // Enable SockJS fallback for browsers without WebSocket support
    }
}



