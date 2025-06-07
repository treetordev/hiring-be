package com.example.hiring.controller;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.dto.auth.UserProfile;
import com.example.hiring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user-acc-details/{id}")
    public UserProfile getUserAccountDetails(@RequestParam Long id){
        return userService.getUserAccountDetails(id);
    }

}
