package com.finance.service;

import com.finance.dao.AdviceDao;
import com.finance.dao.UserDao;
import com.finance.model.Advice;
import com.finance.model.User;
import com.finance.util.IdGenerator;
import com.finance.util.Validator;

import java.sql.SQLException;
import java.util.List;

public class AdviceService {

    private final AdviceDao adviceDao = new AdviceDao();
    private final UserDao userDao = new UserDao();

    public List<Advice> forUser(String userId) throws SQLException {
        return adviceDao.findByUser(userId);
    }

    public List<Advice> byAdvisor(String advisorId) throws SQLException {
        return adviceDao.findByAdvisor(advisorId);
    }

    public void send(String advisorId, String targetUserId, String date, String message)
            throws ValidationException, SQLException {
        String userId = Validator.text(targetUserId, "Target User ID", 30);
        User target = userDao.findById(userId);
        if (target == null || !User.ROLE_USER.equals(target.getRole())) {
            throw new ValidationException("No user account found with ID " + userId + ".");
        }
        adviceDao.insert(new Advice(
                IdGenerator.next("ADV"),
                advisorId,
                Validator.text(message, "Advice message", 200),
                Validator.date(date, "Date"),
                userId));
    }

    public void delete(String id, String advisorId) throws ValidationException, SQLException {
        if (Validator.isBlank(id) || !adviceDao.delete(id, advisorId)) {
            throw new ValidationException("Advice not found.");
        }
    }
}
