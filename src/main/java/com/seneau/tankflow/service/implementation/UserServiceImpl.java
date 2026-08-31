package com.seneau.tankflow.service.implementation;

import com.seneau.tankflow.data.enumeration.UserRole;
import com.seneau.tankflow.data.model.User;
import com.seneau.tankflow.data.repository.UserRepository;
import com.seneau.tankflow.service.interfaces.UserService;
import com.seneau.tankflow.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(String username, String email, String passwordHash, UserRole role) {
        log.info("Creating user: {} with role: {}", username, role);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setIsActive(true);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUser(Long id, String email, UserRole role) {
        log.info("Updating user: {} with email: {}, role: {}", id, email, role);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        if (email != null) {
            user.setEmail(email);
        }
        if (role != null) {
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {
        log.info("Deactivating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(UserRole role) {
        log.debug("Fetching users by role: {}", role);
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        log.debug("Fetching active users");
        return userRepository.findByIsActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canPerformAction(Long userId, String action) {
        log.debug("Checking if user {} can perform action: {}", userId, action);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!user.getIsActive()) {
            log.warn("Inactive user {} attempted action: {}", userId, action);
            return false;
        }

        return user.getRole().equals(UserRole.ADMIN) ||
               canRolePerformAction(user.getRole(), action);
    }

    private boolean canRolePerformAction(UserRole role, String action) {
        return switch (role) {
            case ADMIN -> true;
            case COORDINATEUR_LOGISTIQUE -> action.matches("^(consulter|rapport|gerer_planning).*");
            case MAGASINIER_CENTRAL, MAGASINIER_USINE -> action.matches("^(consulter|enregistrer_reception|enregistrer_expedition).*");
            case AGENT_PRODUCTION -> action.matches("^(consulter|enregistrer_utilisation).*");
            case RESPONSABLE_ACHATS -> action.matches("^(consulter|gerer_fournisseurs).*");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        long count = userRepository.count();
        log.debug("Total user count: {}", count);
        return count;
    }
}
