package com.example.usermanagementservice.service;

import com.example.usermanagementservice.dto.UserDto;
import com.example.usermanagementservice.mapper.ModelMapperService;
import com.example.usermanagementservice.model.User;
import com.example.usermanagementservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapperService modelMapperService;

    public UserService(UserRepository userRepository, ModelMapperService modelMapperService) {
        this.userRepository = userRepository;
        this.modelMapperService = modelMapperService;
    }


    public UserDto save(UserDto userDto) {
        User user = this.modelMapperService.forRequest().map(userDto, User.class);
        this.userRepository.save(user);
        return userDto;
    }

}