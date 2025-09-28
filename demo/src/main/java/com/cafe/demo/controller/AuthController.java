package com.cafe.demo.controller;

import com.cafe.demo.dto.JwtResponse;
import com.cafe.demo.dto.LoginRequest;
import com.cafe.demo.dto.RegisterRequest;
import com.cafe.demo.model.User;
import com.cafe.demo.repository.UserRepository;
import com.cafe.demo.security.jwt.JwtService;
import com.cafe.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body("Username ya existe");
        }
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Email ya existe");
        }
        User user = authService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok("Usuario registrado: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
