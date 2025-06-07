package com.example.hiring.service;

import com.example.hiring.dto.auth.UserProfileRequest;
import com.example.hiring.dto.auth.UserProfileResponse;
import com.example.hiring.entity.User;
import com.example.hiring.entity.UserProfile;
import com.example.hiring.exception.ResourceNotFoundException;
import com.example.hiring.repository.UserProfileRepository;
import com.example.hiring.repository.UserRepository;
import com.example.hiring.util.UserProfileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public UserProfile saveUserProfile(UserProfileRequest request) {
        UserProfile profile = UserProfileConverter.convertRequestToEntity(request);
        log.info("user req:{}",profile);
        UserProfile save = userProfileRepository.save(profile);
        User referenceById = userRepository.getReferenceById(request.getUserId());
        referenceById.setSetupComplete(true);
        userRepository.save(referenceById);
        return save;
    }

    public UserProfileResponse getUserAccountDetails(Long id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        if(!userProfile.isPresent()){
            throw  new ResourceNotFoundException("No resource found for id :"+id);
        }
        UserProfileResponse dto = UserProfileConverter.toDto(userProfile.get());
        return dto;
    }
}
