package com.finance.service;

import com.finance.dao.ExpenseDao;
import com.finance.dao.UserDao;
import com.finance.model.User;
import com.finance.util.Validator;

import java.sql.SQLException;
import java.util.List;

/** User administration and lookups for the admin and advisor areas. Passwords are never returned. */
public class UserService {

    private final UserDao userDao = new UserDao();
    private final ExpenseDao expenseDao = new ExpenseDao();

    public List<User> all() throws SQLException {
        return withoutPasswords(userDao.findAll());
    }

    public List<User> recent(int limit) throws SQLException {
        return withoutPasswords(userDao.findRecent(limit));
    }

    /** Accounts with the USER role (the advisor's clients). */
    public List<User> clients() throws SQLException {
        return withoutPasswords(userDao.findByRole(User.ROLE_USER));
    }

    public int count() throws SQLException {
        return userDao.countAll();
    }

    public int expenseCount() throws SQLException {
        return expenseDao.countAll();
    }

    public void delete(String targetId, String currentAdminId) throws ValidationException, SQLException {
        if (Validator.isBlank(targetId)) {
            throw new ValidationException("User not found.");
        }
        if (targetId.equals(currentAdminId)) {
            throw new ValidationException("You cannot delete your own account.");
        }
        if (!userDao.deleteWithRelatedData(targetId)) {
            throw new ValidationException("User not found.");
        }
    }

    private List<User> withoutPasswords(List<User> users) {
        users.forEach(u -> u.setPassword(null));
        return users;
    }
}
