package io.darbata.chat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Value("${app.rabbitmq.stomp.host}")
    private String rabbitMqHost;

    @Value("${app.rabbitmq.stomp.port}")
    private int rabbitMqPort;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            // websocket clients must connect to this endpoint
            .addEndpoint("/ws")
            // at HTTP level (before UPGRADE) this origin is allowed
            .setAllowedOrigins("http://localhost:5173");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // @MessageMapping method prefix
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");

        // any messages with these destination headers will be sent to the broker
         registry.enableStompBrokerRelay("/queue")
                 .setRelayHost(rabbitMqHost)
                 .setRelayPort(rabbitMqPort)

                 // share ws connections across instances
                 .setUserRegistryBroadcast("/topic/registry.broadcast")

                 // if this instance doesn't have ws connection yet, pass it on
                 .setUserDestinationBroadcast("/topic/unresolved.user.broadcast");
    }

    @Override
    // handles web socket authentication
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
            // handles CONNECT frames
            // so all subsequent messages on this socket shares the same context
            new ChannelInterceptor() {

                @Override
                public Message<?> preSend(Message<?> message, MessageChannel channel) {

                    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                        String userId = accessor.getFirstNativeHeader("X-User-Id"); // client must set this

                        if (userId == null || userId.isBlank()) {
                            throw new RuntimeException("X-User-Id header required from client");
                        }

                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userId,
                                null, // no password
                                List.of() // no no authorities
                        );
                        accessor.setUser(auth);
                    }
                    return message;
                }

            },
            // populates SecurityContext with the Principal set by ChannelInterceptor
            new SecurityContextChannelInterceptor()
        );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // allows Message handlers in Controllers to extract @AuthenticationPrincipal
        resolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public JacksonJsonMessageConverter producerJackson2MessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}