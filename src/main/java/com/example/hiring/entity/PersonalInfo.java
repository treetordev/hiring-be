package com.example.hiring.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String emailId;
    private String contactNumber;
    private String dateOfBirth;
    private String gender;
    private String currentLocation;
    private String shortBio;

    @ElementCollection
    private List<String> languages;

    @ElementCollection
    private List<String> preferredLocation;

    @Lob
    private byte[] profilePicture;
}

