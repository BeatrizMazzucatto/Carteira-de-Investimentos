package com.invest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração WebSocket para streaming de cotações em tempo real
 * Permite que clientes se conectem e recebam atualizações automáticas
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configura broker para enviar mensagens aos clientes
        config.enableSimpleBroker("/topic", "/queue");
        // Prefixo para mensagens enviadas pelos clientes
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket
        registry.addEndpoint("/ws-cotacoes")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
