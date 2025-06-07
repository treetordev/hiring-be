package com.example.hiring.dto.auth;

import lombok.Data;

@Data
public class ProfileOverviewDto {
    private String phone;
    private String location;
    private String availability;
    private String experience;
}
