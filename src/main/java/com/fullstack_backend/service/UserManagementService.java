package com.fullstack_backend.service;

import com.fullstack_backend.payload.request.UserManagementRequest;
import com.fullstack_backend.payload.response.UserManagementResponse;

import java.util.List;

public interface UserManagementService {

    List<UserManagementResponse> getAllUsers();

    UserManagementResponse getUserById(Long id);

    UserManagementResponse createUser(UserManagementRequest request);

    UserManagementResponse updateUser(Long id, UserManagementRequest request, String currentUsername);

    void deleteUser(Long id, String currentUsername);
}
