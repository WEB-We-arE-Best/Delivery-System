package com.webest.user.application.service;

import com.webest.user.presentation.dto.request.UserJoinRequest;
import com.webest.user.presentation.dto.request.UserUpdateRequest;
import com.webest.user.presentation.dto.response.UserResponse;

import java.util.List;

public interface UserService{
    public UserResponse create(UserJoinRequest request);

//    UserDto getUserDetailsByUserId(String userName);

    List<UserResponse> getUserByAll();

    UserResponse getUser(String userName);

    UserResponse update(String userId, UserUpdateRequest request);

    void delete(String userId);
}