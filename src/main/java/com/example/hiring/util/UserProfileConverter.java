package com.example.hiring.util;

import com.example.hiring.dto.auth.*;
import com.example.hiring.entity.*;
import com.example.hiring.entity.UserProfile;

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
}

