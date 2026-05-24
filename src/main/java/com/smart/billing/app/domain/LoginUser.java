package com.smart.billing.app.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUser implements UserDetails  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Column(name = "user_name", nullable = false)
    private String username;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Builder.Default
    @Column(name = "auth_provider")
    private String authProvider = "local";

    @Column(name = "google_id")
    private String googleId;

    @Builder.Default
    @Column(name = "role_user")
    private String roleUser = "USER";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @PrePersist
    protected void onCreate() { 
        this.createdAt = LocalDateTime.now(); 
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
         this.updatedAt = LocalDateTime.now();
    }


    // UserDetailsService
    @Override
    public String getUsername() {
        return email; 
    }

    @Override
    public String getPassword() {
        return passwordHash; 
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(roleUser));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

   

}
