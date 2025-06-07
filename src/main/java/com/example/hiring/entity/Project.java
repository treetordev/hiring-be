package com.example.hiring.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

// Project.java
@Entity
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectsName;
    private String outcome;
    private String projectLink;
    private String institution;
    private String duration;
    private String keyProjects;
    private String achievements;

    @ElementCollection
    private List<String> toolsUsed;

    @Lob
    private byte[] uploadCertifications;

    private String linkedinProfile;
    private String portfolioLinks;
}

