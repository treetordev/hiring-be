package com.example.hiring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Qualifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String education;
    private String noticePeriod;
    private String workPreference;
    private String portfolioLinks;
    private String workAuthorization;
    private String careerGoals;
    private String availabilityForInterviews;
    private String preferredCompanyType;
    private String achievementsWithMeasurableOutcomes;
    private String wantToLandAJobInTheNextDays;
}