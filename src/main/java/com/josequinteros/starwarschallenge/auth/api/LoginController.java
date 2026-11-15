package com.josequinteros.starwarschallenge.auth.api;

import com.josequinteros.starwarschallenge.auth.application.LoginService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginService service;
    private final long ttl;
    public LoginController(LoginService service, @Value("${jwt.ttl-seconds}") long ttl) {
        this.service = service;
        this.ttl = ttl;
    }
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(String accessToken, String tokenType, long expiresIn) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        //Prevenir que los navegadores e intermediarios almacenen en caché una respuesta que contenga un token de acceso.
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(new LoginResponse(service.login(request.username(), request.password()), "Bearer", ttl));
    }
}
