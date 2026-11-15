package com.josequinteros.starwarschallenge.auth.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class UserConfiguration {

    
    // Adaptar el user persistido al formato esperado por Spring Security.
    @Bean
    UserDetailsService userDetailsService(AppUserRepository users) {
        return username -> users.findByUsername(username)
            .map(user -> User.withUsername(user.getUsername()).password(user.getPasswordHash()).authorities("ROLE_USER").build())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }


    // Recrear el usuario inicial en la base de datos en memoria al inicio y almacenar solo su hash de contraseña BCrypt.
    @Bean
    ApplicationRunner seedUser(AppUserRepository users, PasswordEncoder encoder,
            @Value("${auth.seed.username}") String username, @Value("${auth.seed.password}") String password) {
        if (username.isBlank() || password.isBlank()) throw new IllegalStateException("AUTH_SEED_USERNAME and AUTH_SEED_PASSWORD are required");
        if (username.length() > 100 || password.length() > 200) {
            throw new IllegalStateException("Seed username or password exceeds the supported length");
        }
        return args -> {
            if (users.findByUsername(username).isEmpty()) {
                users.save(new AppUser(username, encoder.encode(password)));
            }
        };
    }
}
