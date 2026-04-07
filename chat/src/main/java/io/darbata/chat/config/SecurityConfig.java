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
                    .requestMatchers("/ws/**").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer((oauth2) -> oauth2
                    .jwt((jwt) -> jwt
                            .decoder(jwtDecoder())
                    )
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // use public key in resources
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }

}
