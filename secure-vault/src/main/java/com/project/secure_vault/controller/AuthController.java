package com.project.secure_vault.controller;

import com.project.secure_vault.model.User;
import com.project.secure_vault.repository.UserRepository;
import com.project.secure_vault.security.JwtTokenProvider;      // ← now exists!
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtTokenProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo,
                          JwtTokenProvider jwtProvider) {
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // @PostMapping("/signup")
    // public ResponseEntity<?> signup(@RequestBody Map<String,String> body) {
    //     String username = body.get("username");
    //     String rawPassword = body.get("password");
    //     if (username == null || rawPassword == null) {
    //         return ResponseEntity
    //             .status(HttpStatus.BAD_REQUEST)
    //             .body("username and password must be provided");
    //     }
    //     if (userRepo.findByUsername(username) != null) {
    //         return ResponseEntity.status(HttpStatus.CONFLICT)
    //                              .body("User already exists");
    //     }
    //     User u = new User();
    //     u.setUsername(username);
    //     u.setPasswordHash(passwordEncoder.encode(rawPassword));
    //     userRepo.save(u);
    //     String token = jwtProvider.createToken(username);
    //     return ResponseEntity.ok(Map.of("token", token));
    // }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String rawPassword = body.get("password");
        if (username == null || rawPassword == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("username and password must be provided");
        }
        User u = userRepo.findByUsername(username);
        if (u == null || !passwordEncoder.matches(rawPassword, u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtProvider.createToken(u.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
