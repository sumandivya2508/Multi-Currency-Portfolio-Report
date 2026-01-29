package Portfolio.example.Portfolio.Contoller;

import Portfolio.example.Portfolio.DTO_Request.*;
import Portfolio.example.Portfolio.DTO_Response.AuthResponse;
import Portfolio.example.Portfolio.Entity.User;
import Portfolio.example.Portfolio.Security.JwtTokenProvider;
import Portfolio.example.Portfolio.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.registerUser(request);
        String token = jwtTokenProvider.generateToken(user.getUsername());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .message("User registered successfully")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        User user = userService.findByUsername(request.getUsername());
        String token = jwtTokenProvider.generateToken(user.getUsername());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .message("Login successful")
                        .build()
        );
    }
}
