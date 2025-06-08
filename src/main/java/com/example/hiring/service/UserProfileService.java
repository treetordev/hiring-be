package com.example.hiring.service;

import com.example.hiring.dto.auth.UserProfileDto;
import com.example.hiring.entity.User;
import com.example.hiring.entity.UserProfile;
import com.example.hiring.repository.UserProfileRepository;
import com.example.hiring.repository.UserRepository;
import com.example.hiring.util.UserProfileConverter;
import jakarta.transaction.Transactional;
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
    private UserRepository userRepository;

    @Transactional
    public UserProfile saveUserProfile(UserProfileDto request) {
        UserProfile profile = UserProfileConverter.convertRequestToEntity(request);
        log.info("user req:{}",profile);
        UserProfile save = userProfileRepository.save(profile);
        Optional<User> userById = userRepository.findById(request.getUserId());
            if (userById.isPresent()) {
                User user = userById.get();
                user.setSetupComplete(true);
                userRepository.save(user);
            }
            return save;
    }

    public UserProfileDto getProfileById(Long id) {
        UserProfile profile = userProfileRepository.findByUserId(id);
        log.info("user profile response :{}",profile);
        UserProfileDto dto = UserProfileConverter.toDto(profile);
        return dto;
    }
}
