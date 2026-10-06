package com.finance.service;

import com.finance.dao.UserDao;
import com.finance.model.User;
import com.finance.util.IdGenerator;
import com.finance.util.Validator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Set;

public class AuthService {

    private static final Set<String> ROLES = Set.of(User.ROLE_USER, User.ROLE_ADVISOR, User.ROLE_ADMIN);

    private final UserDao userDao = new UserDao();

    /** Returns the user (without password) or throws if the credentials are wrong. */
    public User login(String email, String password) throws ValidationException, SQLException {
        if (Validator.isBlank(email) || Validator.isBlank(password)) {
            throw new ValidationException("Email and password are required.");
        }
        User user = userDao.findByEmail(email.trim().toLowerCase());
        if (user == null || !passwordMatches(password, user.getPassword())) {
            throw new ValidationException("Invalid email or password.");
        }
        user.setPassword(null);
        return user;
    }

    public User register(String name, String email, String password, String role)
            throws ValidationException, SQLException {
        String cleanName = Validator.text(name, "Full name", 30);
        String cleanEmail = Validator.email(email);
        String cleanPassword = Validator.password(password);
        if (role == null || !ROLES.contains(role)) {
            throw new ValidationException("Please select a valid role.");
        }
        if (userDao.emailExists(cleanEmail)) {
            throw new ValidationException("An account with this email already exists.");
        }

        User user = new User(IdGenerator.next("USR"), cleanName, role, cleanPassword, cleanEmail);
        userDao.insert(user);
        user.setPassword(null);
        return user;
    }

    public void changePassword(String userId, String oldPassword, String newPassword)
            throws ValidationException, SQLException {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new ValidationException("Account not found.");
        }
        if (oldPassword == null || !passwordMatches(oldPassword, user.getPassword())) {
            throw new ValidationException("Current password is incorrect.");
        }
        String cleanPassword = Validator.password(newPassword);
        userDao.updatePassword(userId, cleanPassword);
    }

    /** Constant-time comparison so response timing does not leak password characters. */
    private boolean passwordMatches(String given, String stored) {
        return stored != null && MessageDigest.isEqual(
                given.getBytes(StandardCharsets.UTF_8), stored.getBytes(StandardCharsets.UTF_8));
    }
}
