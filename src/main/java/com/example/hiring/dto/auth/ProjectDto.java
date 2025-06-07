package com.example.hiring.dto.auth;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDto {
    private String projectsName;
    private List<String> toolsUsed;
    private String outcome;
    private String projectLink;
    private String institution;
    private String duration;
    private String keyProjects;
    private String achievements;
    private String uploadCertifications;
    private String linkedinProfile;
    private String portfolioLinks;
}
