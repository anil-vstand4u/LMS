package com.lms.service;

import com.lms.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerUser(User user);
    User findByEmail(String email);
    User updateUser(User user);
    void deleteUser(Long id);
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    void initiatePasswordReset(String email);
    boolean completePasswordReset(String token, String newPassword);
    String generateJwtToken(User user);
    boolean validateJwtToken(String token);
    User getCurrentUser();
    boolean hasRole(User user, String role);
    void addRole(User user, String role);
    void removeRole(User user, String role);
}
