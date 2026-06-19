package com.why.buildingmanagement.chat.infrastructure.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");

        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {

        registry.addEndpoint("/ws/chat").setAllowedOriginPatterns("http://localhost",
                                                                  "http://localhost:4200",
                                                                  "http://localhost:8080",
                                                                  "http://167.233.48.218",

                                                                  "http://joritna.com",
                                                                  "https://joritna.com",

                                                                  "http://www.joritna.com",
                                                                  "https://www.joritna.com",

                                                                  "http://app.joritna.com",
                                                                  "https://app.joritna.com")
                .withSockJS()
                .setSessionCookieNeeded(false);
    }

}