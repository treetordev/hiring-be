package com.example.hiring.dto.auth;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileRequest {
    private String userId;
    private PersonalInfoDto personalInfo;
    private EmploymentDetailsDto employmentDetails;
    private List<ProjectDto> projects;
    private QualificationsDto qualifications;
    private ProfileOverviewDto profileOverview;
}
