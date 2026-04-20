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
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtDecoder jwtDecoder;
    private final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Value("${app.rabbitmq.stomp.host}")
    private String rabbitMqHost;

    @Value("${app.rabbitmq.stomp.port}")
    private int rabbitMqPort;

    public WebSocketConfig(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

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

                public Message<?> preSend(Message<?> message, MessageChannel channel) {

                    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                    logger.info("invoking preSend()");

                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        String token = accessor.getFirstNativeHeader("Authorization");

                        if (token == null || !token.startsWith("Bearer ")) {
                            throw new RuntimeException("No authentication token");
                        }

                        String jwtToken = token.substring(7); // remove Bearer

                        Principal user = validateTokenAndCreatePrincipal(jwtToken);

                        logger.info("Connecting to user: {}", user.getName());

                        // the accessor is responsible for auto subscribing connections to their user-specific queue
                        accessor.setUser(user);

                        // by default accessor will create user sessions based on Principal.getName()
                        // we override this function in `validateTokenAndCreatePrincipal(String token)`
                        // to use the 'sub' instead


                    }
                    return message;
                }

            },
            // populates SecurityContext with the Principal set by ChannelInterceptor
            new SecurityContextChannelInterceptor()
        );
    }

    private Principal validateTokenAndCreatePrincipal(String token) {

        try {
            Jwt jwt = jwtDecoder.decode(token);
            return new JwtAuthenticationToken(jwt) {
                @Override
                public String getName() {
                    return jwt.getClaimAsString("sub");
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }

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