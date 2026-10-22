package com.elite.elitebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserFilter customUserFilter;

    // Inyección del filtro
    public SecurityConfig(CustomUserFilter customUserFilter) {
        this.customUserFilter = customUserFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints publicos (cualquiera puede acceder)
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/usuarios/**").permitAll() // Registro y login son públicos
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll() // Ver productos es público
                        .requestMatchers(HttpMethod.GET, "/api/clientes/**").permitAll()

                        // 2. Endpoints protegidos: Solo ADMIN puede crear, modificar o eliminar productos
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        // 3. Endpoints de Pedidos y Carritos (requieren estar autenticados, cliente o admin)
                        .requestMatchers("/api/pedidos/**").authenticated()
                        .requestMatchers("/api/carritos/**").authenticated()
                        .requestMatchers("/api/carrito-detalles/**").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // AGREGAR EL FILTRO ANTES DEL FILTRO DE AUTENTICACIÓN POR DEFECTO
                .addFilterBefore(customUserFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}