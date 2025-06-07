package com.example.hiring.dto.auth;


import lombok.Data;
import java.util.List;

@Data
public class PersonalInfoDto {
    private String fullName;
    private String emailId;
    private String contactNumber;
    private String dateOfBirth;
    private String gender;
    private List<String> languages;
    private String currentLocation;
    private List<String> preferredLocation;
    private String profilePicture;
    private String shortBio;
}
