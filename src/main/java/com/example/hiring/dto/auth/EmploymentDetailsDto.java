package com.example.hiring.dto.auth;


import java.util.List;
import lombok.Data;


@Data
public class EmploymentDetailsDto {
    private String currentOrganisation;
    private String currentDesignation;
    private String industry;
    private String department;
    private List<String> preferredRoles;
    private List<String> skills;
    private String totalYearsOfWorkExperience;
    private String jobType;
    private String currentCompensation;
    private String expectedCompensation;
    private String cvUpload;
    private String coverLetter;
    private String videoIntroduction;
    private String audioIntroduction;
}
