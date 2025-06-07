package com.example.hiring.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToOne(cascade = CascadeType.ALL)
    private PersonalInfo personalInfo;

    @OneToOne(cascade = CascadeType.ALL)
    private EmploymentDetails employmentDetails;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Project> projects;

    @OneToOne(cascade = CascadeType.ALL)
    private Qualifications qualifications;

    @OneToOne(cascade = CascadeType.ALL)
    private ProfileOverview profileOverview;
}

