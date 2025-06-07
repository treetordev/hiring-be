package com.example.hiring.dto.auth;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDto {
    private long userId;
    private PersonalInfoDto personalInfo;
    private EmploymentDetailsDto employmentDetails;
    private List<ProjectDto> projects;
    private QualificationsDto qualifications;
    private ProfileOverviewDto profileOverview;
}
