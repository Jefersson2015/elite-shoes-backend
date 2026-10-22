package com.elite.elitebackend.config;

import com.elite.elitebackend.model.Usuario;
import com.elite.elitebackend.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class CustomUserFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias por constructor
    public CustomUserFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leer el header que envía el frontend
        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Integer userId = Integer.parseInt(userIdHeader);

                // 2. Buscar el usuario en la base de datos
                Usuario usuario = usuarioRepository.findById(userId).orElse(null);

                if (usuario != null) {
                    // 3. Crear el rol con el prefijo "ROLE_"
                    // Ejemplo: si role es "admin", se convierte en "ROLE_ADMIN"
                    String role = usuario.getRoll() != null ? usuario.getRoll().toUpperCase() : "CLIENTE";
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                    // 4. Crear el objeto de autenticación y guardarlo en el contexto de seguridad
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            usuario.getUsername(),
                            null, // No necesitamos la contraseña aquí
                            Collections.singletonList(authority)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (NumberFormatException e) {
                // Si el ID no es un número válido, simplemente continuamos sin autenticar
                System.out.println("Error: X-User-Id no es un número válido");
            }
        }

        // 5. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}