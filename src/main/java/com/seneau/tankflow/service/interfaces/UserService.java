package com.seneau.tankflow.service.interfaces;

import com.seneau.tankflow.data.enumeration.UserRole;
import com.seneau.tankflow.data.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(String username, String email, String passwordHash, UserRole role);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    User updateUser(Long id, String email, UserRole role);

    void deactivateUser(Long id);

    List<User> getUsersByRole(UserRole role);

    List<User> getActiveUsers();

    boolean canPerformAction(Long userId, String action);

    long getTotalUserCount();
}
