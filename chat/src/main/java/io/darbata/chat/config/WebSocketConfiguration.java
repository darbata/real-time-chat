package io.darbata.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // websocket clients must connect to this endpoint
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // @MessageMapping method prefix
        registry.setApplicationDestinationPrefixes("/app");

        // enable external broker e.g. RabbitMQ
        // registry.enableStompBrokerRelay("/topic", "/queue");

        // TODO: remove this simple broker
        // any messages with these destination headers will be sent to the broker
        registry.enableSimpleBroker("/topic", "/queue");
    }

}