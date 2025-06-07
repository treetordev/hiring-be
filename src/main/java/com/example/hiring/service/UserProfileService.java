package com.example.hiring.service;

import com.example.hiring.dto.auth.UserProfileRequest;
import com.example.hiring.entity.UserProfile;
import com.example.hiring.repository.UserProfileRepository;
import com.example.hiring.util.UserProfileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile saveUserProfile(UserProfileRequest request) {
        UserProfile profile = UserProfileConverter.convertRequestToEntity(request);
        log.info("user req:{}",profile);
        return userProfileRepository.save(profile);
    }
}
