package com.example.hiring.controller;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.dto.auth.UserProfile;
import com.example.hiring.dto.auth.UserProfileRequest;
import com.example.hiring.service.UserProfileService;
import com.example.hiring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/user-acc-details/{id}")
    public UserProfile getUserAccountDetails(@PathVariable Long id){
        return userService.getUserAccountDetails(id);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<String> createOrUpdateUserProfile(@RequestBody UserProfileRequest request) {
        // You can process or save the request here
        System.out.println("Received user profile for user: " + request);
        userProfileService.saveUserProfile(request);
        return ResponseEntity.ok("User profile saved successfully");
    }



}
