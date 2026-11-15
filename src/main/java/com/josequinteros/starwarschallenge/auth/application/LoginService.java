package com.josequinteros.starwarschallenge.auth.application;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final AuthenticationManager authentication;
    private final JwtEncoder encoder;
    private final String issuer;
    private final long ttl;

    public LoginService(AuthenticationManager authentication, JwtEncoder encoder,
            @Value("${jwt.issuer}") String issuer, @Value("${jwt.ttl-seconds}") long ttl) {
        if (issuer == null || issuer.isBlank()) throw new IllegalStateException("JWT_ISSUER must not be blank");
        if (ttl <= 0) throw new IllegalStateException("JWT_TTL_SECONDS must be greater than zero");
        this.authentication = authentication;
        this.encoder = encoder;
        this.issuer = issuer;
        this.ttl = ttl;
    }

    //Verificar credenciales antes de emitir un token de corta duración; la autenticación fallida nunca debe producir un JWT.
    public String login(String username, String password) {
        authentication.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer(issuer).subject(username).issuedAt(now).expiresAt(now.plusSeconds(ttl)).build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
