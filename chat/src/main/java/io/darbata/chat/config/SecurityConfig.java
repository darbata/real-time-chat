package io.darbata.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.public-key.location}")
    private RSAPublicKey rsaPublicKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                    // websockets don't carry JWT
                    // let it be intercepted by ws connections be intercepted
                    .requestMatchers("/ws/**").permitAll()
                    .anyRequest().authenticated() // everything else must be authenticated
            )
            .oauth2ResourceServer((oauth2) -> oauth2
                    // accept a Bearer token in the Authorization header
                    .jwt((jwt) -> jwt
                            .decoder(jwtDecoder()) // then verify with RSA public key
                    )
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }

}
