package com.example.hiring.service;


import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.dto.auth.UserProfile;
import com.example.hiring.entity.User;
import com.example.hiring.exception.ResourceNotFoundException;
import com.example.hiring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserProfile getUserAccountDetails(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            UserProfile userProfile = new UserProfile();
            userProfile.setEmail(user.getEmail());
            userProfile.setProfilePicture(user.getProfilePicture());
            userProfile.setFirstName(user.getFirstName());
            userProfile.setLastName(user.getLastName());
            userProfile.setId(user.getId());
            return userProfile;
        }
        else{
            throw new ResourceNotFoundException("No user found with id "+id);
        }
    }
}
