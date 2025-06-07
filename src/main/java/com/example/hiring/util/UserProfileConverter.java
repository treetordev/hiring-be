package com.example.hiring.util;

import com.example.hiring.dto.auth.*;
import com.example.hiring.entity.*;
import com.example.hiring.entity.UserProfile;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UserProfileConverter {

    public static UserProfile convertRequestToEntity(UserProfileRequest request) {
        UserProfile profile = new UserProfile();
        profile.setUserId(request.getUserId());
        profile.setPersonalInfo(convertPersonalInfo(request.getPersonalInfo()));
        profile.setEmploymentDetails(convertEmploymentDetails(request.getEmploymentDetails()));
        profile.setProjects(request.getProjects().stream().map(UserProfileConverter::convertProject).collect(Collectors.toList()));
        profile.setQualifications(convertQualifications(request.getQualifications()));
        profile.setProfileOverview(convertProfileOverview(request.getProfileOverview()));
        return profile;
    }

    private static PersonalInfo convertPersonalInfo(PersonalInfoDto dto) {
        PersonalInfo entity = new PersonalInfo();
        entity.setFullName(dto.getFullName());
        entity.setEmailId(dto.getEmailId());
        entity.setContactNumber(dto.getContactNumber());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setGender(dto.getGender());
        entity.setLanguages(dto.getLanguages());
        entity.setCurrentLocation(dto.getCurrentLocation());
        entity.setPreferredLocation(dto.getPreferredLocation());
        entity.setShortBio(dto.getShortBio());
        entity.setProfilePicture(null); // handle file upload separately
        return entity;
    }

    private static EmploymentDetails convertEmploymentDetails(EmploymentDetailsDto dto) {
        EmploymentDetails entity = new EmploymentDetails();
        entity.setCurrentOrganisation(dto.getCurrentOrganisation());
        entity.setCurrentDesignation(dto.getCurrentDesignation());
        entity.setIndustry(dto.getIndustry());
        entity.setDepartment(dto.getDepartment());
        entity.setPreferredRoles(dto.getPreferredRoles());
        entity.setSkills(dto.getSkills());
        entity.setTotalYearsOfWorkExperience(dto.getTotalYearsOfWorkExperience());
        entity.setJobType(dto.getJobType());
        entity.setCurrentCompensation(dto.getCurrentCompensation());
        entity.setExpectedCompensation(dto.getExpectedCompensation());
        // handle uploads as null for now
        return entity;
    }

    private static Project convertProject(ProjectDto dto) {
        Project project = new Project();
        project.setProjectsName(dto.getProjectsName());
        project.setToolsUsed(dto.getToolsUsed());
        project.setOutcome(dto.getOutcome());
        project.setProjectLink(dto.getProjectLink());
        project.setInstitution(dto.getInstitution());
        project.setDuration(dto.getDuration());
        project.setKeyProjects(dto.getKeyProjects());
        project.setAchievements(dto.getAchievements());
        project.setUploadCertifications(null);
        project.setLinkedinProfile(dto.getLinkedinProfile());
        project.setPortfolioLinks(dto.getPortfolioLinks());
        return project;
    }

    private static Qualifications convertQualifications(QualificationsDto dto) {
        Qualifications q = new Qualifications();
        q.setEducation(dto.getEducation());
        q.setNoticePeriod(dto.getNoticePeriod());
        q.setWorkPreference(dto.getWorkPreference());
        q.setPortfolioLinks(dto.getPortfolioLinks());
        q.setWorkAuthorization(dto.getWorkAuthorization());
        q.setCareerGoals(dto.getCareerGoals());
        q.setAvailabilityForInterviews(dto.getAvailabilityForInterviews());
        q.setPreferredCompanyType(dto.getPreferredCompanyType());
        q.setAchievementsWithMeasurableOutcomes(dto.getAchievementsWithMeasurableOutcomes());
        q.setWantToLandAJobInTheNextDays(dto.getWantToLandAJobInTheNextDays());
        return q;
    }

    private static ProfileOverview convertProfileOverview(ProfileOverviewDto dto) {
        ProfileOverview p = new ProfileOverview();
        p.setPhone(dto.getPhone());
        p.setLocation(dto.getLocation());
        p.setAvailability(dto.getAvailability());
        p.setExperience(dto.getExperience());
        return p;
    }

    public UserProfileResponse toDto(UserProfile entity) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(String.valueOf(entity.getId()));
        response.setPersonalInfoDto(toPersonalInfoDto(entity.getPersonalInfo()));
        response.setEmploymentDetailsDto(toEmploymentDetailsDto(entity.getEmploymentDetails()));
        response.setProjectDtos(entity.getProjects().stream().map(this::toProjectDto).collect(Collectors.toList()));
        response.setQualificationsDto(toQualificationsDto(entity.getQualifications()));
        response.setProfileOverview(toProfileOverviewDto(entity.getProfileOverview()));
        return response;
    }

    // Example sub-conversion methods
    private PersonalInfoDto toPersonalInfoDto(PersonalInfo personalInfo) {
        PersonalInfoDto dto = new PersonalInfoDto();
        dto.setFullName(personalInfo.getFullName());
        dto.setEmailId(personalInfo.getEmailId());
        dto.setContactNumber(personalInfo.getContactNumber());
        dto.setDateOfBirth(personalInfo.getDateOfBirth());
        dto.setGender(personalInfo.getGender());
        //dto.setLanguages(Arrays.asList(personalInfo.getLanguages().split(",")));
        dto.setCurrentLocation(personalInfo.getCurrentLocation());
       // dto.setPreferredLocation(Arrays.asList(personalInfo.getPreferredLocation().split(",")));
        //dto.setProfilePicture(personalInfo.getProfilePicture());
        dto.setShortBio(personalInfo.getShortBio());
        return dto;
    }

    private EmploymentDetailsDto toEmploymentDetailsDto(EmploymentDetails employmentDetails) {
        EmploymentDetailsDto dto = new EmploymentDetailsDto();
        dto.setCurrentOrganisation(employmentDetails.getCurrentOrganisation());
        dto.setCurrentDesignation(employmentDetails.getCurrentDesignation());
        dto.setIndustry(employmentDetails.getIndustry());
        dto.setDepartment(employmentDetails.getDepartment());
        //dto.setPreferredRoles(Arrays.asList(employmentDetails.getPreferredRoles().split(",")));
       // dto.setSkills(Arrays.asList(employmentDetails.getSkills().split(",")));
        dto.setTotalYearsOfWorkExperience(employmentDetails.getTotalYearsOfWorkExperience());
        dto.setJobType(employmentDetails.getJobType());
        dto.setCurrentCompensation(employmentDetails.getCurrentCompensation());
        dto.setExpectedCompensation(employmentDetails.getExpectedCompensation());
       /* dto.setCvUpload(employmentDetails.getCvUpload());
        dto.setCoverLetter(employmentDetails.getCoverLetter());
        dto.setVideoIntroduction(employmentDetails.getVideoIntroduction());
        dto.setAudioIntroduction(employmentDetails.getAudioIntroduction());

        */
        return dto;
    }

    private ProjectDto toProjectDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setProjectsName(project.getProjectsName());
        //dto.setToolsUsed(Arrays.asList(project.getToolsUsed().split(",")));
        dto.setOutcome(project.getOutcome());
        dto.setProjectLink(project.getProjectLink());
        dto.setInstitution(project.getInstitution());
        dto.setDuration(project.getDuration());
        dto.setKeyProjects(project.getKeyProjects());
        dto.setAchievements(project.getAchievements());
        //dto.setUploadCertifications(project.getUploadCertifications());
        dto.setLinkedinProfile(project.getLinkedinProfile());
        dto.setPortfolioLinks(project.getPortfolioLinks());
        return dto;
    }

    private QualificationsDto toQualificationsDto(Qualifications qualifications) {
        QualificationsDto dto = new QualificationsDto();
        dto.setEducation(qualifications.getEducation());
        dto.setNoticePeriod(qualifications.getNoticePeriod());
        dto.setWorkPreference(qualifications.getWorkPreference());
        dto.setPortfolioLinks(qualifications.getPortfolioLinks());
        dto.setWorkAuthorization(qualifications.getWorkAuthorization());
        dto.setCareerGoals(qualifications.getCareerGoals());
        dto.setAvailabilityForInterviews(qualifications.getAvailabilityForInterviews());
        dto.setPreferredCompanyType(qualifications.getPreferredCompanyType());
        dto.setAchievementsWithMeasurableOutcomes(qualifications.getAchievementsWithMeasurableOutcomes());
        dto.setWantToLandAJobInTheNextDays(qualifications.getWantToLandAJobInTheNextDays());
        return dto;
    }

    private ProfileOverviewDto toProfileOverviewDto(ProfileOverview overview) {
        ProfileOverviewDto dto = new ProfileOverviewDto();
        dto.setPhone(overview.getPhone());
        dto.setLocation(overview.getLocation());
        dto.setAvailability(overview.getAvailability());
        dto.setExperience(overview.getExperience());
        return dto;
    }
}

