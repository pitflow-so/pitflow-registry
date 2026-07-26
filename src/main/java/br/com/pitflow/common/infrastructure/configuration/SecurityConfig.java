package br.com.pitflow.common.infrastructure.configuration;

import br.com.pitflow.common.infrastructure.security.SecurityFilter;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.core.userdetails.User.builder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/actuator/metrics").permitAll()
                        .requestMatchers(GET, "/internal/registry/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(POST, "/mechanics", "/customers").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(MechanicGateway repository) {
        return username -> {
            var mechanic = repository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Mechanic not found: " + username));

            return builder()
                    .username(mechanic.getUsername())
                    .password(mechanic.getPassword())
                    .authorities(mechanic.getRole())
                    .build();
        };
    }
}
