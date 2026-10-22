package com.elite.elitebackend.controller;

import com.elite.elitebackend.dto.UsuarioRequest;
import com.elite.elitebackend.model.Cliente;
import com.elite.elitebackend.model.Usuario;
import com.elite.elitebackend.model.UsuarioResponse;
import com.elite.elitebackend.repository.ClienteRepository;
import com.elite.elitebackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody UsuarioRequest request) {
        // 1. Verificar si el correo ya existe
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo ya está registrado"));
        }

        // 2. Crear y guardar el Cliente primero
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setTelefono(request.getTelefono());
        cliente.setCorreo(request.getUsername()); // El correo es el username
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // 3. Crear y guardar el Usuario vinculado al Cliente
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoll(request.getRoll() != null ? request.getRoll() : "cliente");
        usuario.setCliente(clienteGuardado); // <-- Aquí está la magia de la relación

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 4. Retornar respuesta segura
        return ResponseEntity.ok(new UsuarioResponse(usuarioGuardado));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario credenciales) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(credenciales.getUsername());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            boolean esValida = passwordEncoder.matches(credenciales.getPassword(), usuario.getPassword());

            if (esValida) {
                return ResponseEntity.ok(new UsuarioResponse(usuario));
            }
        }
        return ResponseEntity.status(401).body(Map.of("mensaje", "Error en la autenticación"));
    }

    @GetMapping
    public java.util.List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
}