package com.example.hiring.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

// EmploymentDetails.java
@Entity
@Data
public class EmploymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currentOrganisation;
    private String currentDesignation;
    private String industry;
    private String department;
    private String totalYearsOfWorkExperience;
    private String jobType;
    private String currentCompensation;
    private String expectedCompensation;

    @ElementCollection
    private List<String> preferredRoles;

    @ElementCollection
    private List<String> skills;

    @Lob
    private byte[] cvUpload;

    @Lob
    private byte[] coverLetter;

    @Lob
    private byte[] videoIntroduction;

    @Lob
    private byte[] audioIntroduction;
}

