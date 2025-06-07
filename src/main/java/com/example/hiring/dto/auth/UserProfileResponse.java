package com.example.hiring.dto.auth;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileResponse {
    private String userId;
    private PersonalInfoDto personalInfoDto;
    private EmploymentDetailsDto employmentDetailsDto;
    private List<ProjectDto> projectDtos;
    private QualificationsDto qualificationsDto;
    private ProfileOverviewDto profileOverview;
}
