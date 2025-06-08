package com.example.hiring.dto.auth;

import com.example.hiring.entity.AuthProvider;
import com.example.hiring.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserProfile {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private AuthProvider provider;
    private Set<Role> roles;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private Boolean isSetupComplete;

    public UserProfile() {}

    public UserProfile(Long id, String firstName, String lastName, String email,
                       String profilePicture, AuthProvider provider, Set<Role> roles,
                       Boolean emailVerified, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.provider = provider;
        this.roles = roles;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public AuthProvider getProvider() { return provider; }
    public void setProvider(AuthProvider provider) { this.provider = provider; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFullName() { return firstName + " " + lastName; }
}
