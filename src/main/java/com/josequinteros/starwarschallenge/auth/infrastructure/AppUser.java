package com.josequinteros.starwarschallenge.auth.infrastructure;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String passwordHash;

    protected AppUser() {}
    public AppUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}
