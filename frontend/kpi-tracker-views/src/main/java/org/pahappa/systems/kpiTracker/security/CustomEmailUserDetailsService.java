package org.pahappa.systems.kpiTracker.security;

import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.security.service.impl.CustomUserDetailsServiceImpl;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * Custom UserDetailsService that supports email-based authentication.
 * This service extends the existing CustomUserDetailsServiceImpl and adds
 * the ability to load users by email address instead of just username.
 */
public class CustomEmailUserDetailsService extends CustomUserDetailsServiceImpl {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First, try to load by username (existing behavior)
        try {
            return super.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            // If username not found, try to load by email
            return loadUserByEmail(username);
        }
    }

    /**
     * Load user by email address.
     * This method attempts to find a user by their email address and
     * converts them to UserDetails for Spring Security.
     */
    private UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email address is required");
        }

        try {
            // Get UserService from ApplicationContext
            UserService userService = ApplicationContextProvider.getBean(UserService.class);
            if (userService == null) {
                throw new UsernameNotFoundException("UserService not available");
            }

            // Try to find user by email address
            User user = findUserByEmail(userService, email.trim());

            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            // Convert to UserDetails and return by loading with the actual username
            return super.loadUserByUsername(user.getUsername());

        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user by email: " + email, e);
        }
    }

    /**
     * Find a user by email address using the provided UserService.
     */
    private User findUserByEmail(UserService userService, String email) {
        try {
            // Get all users and search by email
            List<User> users = userService.getUsers();

            for (User user : users) {
                if (email.equalsIgnoreCase(user.getEmailAddress())) {
                    return user;
                }
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            return null;
        }
    }
}
