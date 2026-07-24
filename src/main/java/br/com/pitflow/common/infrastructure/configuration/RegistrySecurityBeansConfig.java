package br.com.pitflow.common.infrastructure.configuration;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.common.infrastructure.security.JwtServiceImp;
import br.com.pitflow.common.infrastructure.security.SecurityFilter;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrySecurityBeansConfig {

    @Bean
    public TokenGateway tokenGateway(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.expiration-hours:3}") Integer expirationHours) {
        return new JwtServiceImp(secret, expirationHours);
    }

    @Bean
    public SecurityFilter securityFilter(TokenGateway tokenGateway, MechanicGateway mechanicGateway) {
        return new SecurityFilter(tokenGateway, mechanicGateway);
    }
}
