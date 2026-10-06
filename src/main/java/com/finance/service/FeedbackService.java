package com.finance.service;

import com.finance.dao.FeedbackDao;
import com.finance.model.Feedback;
import com.finance.util.IdGenerator;
import com.finance.util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FeedbackService {

    private final FeedbackDao feedbackDao = new FeedbackDao();

    public void submit(String userId, String message) throws ValidationException, SQLException {
        feedbackDao.insert(new Feedback(
                IdGenerator.next("FDB"),
                userId,
                Validator.text(message, "Message", 100),
                Feedback.STATUS_PENDING,
                LocalDate.now()));
    }

    public List<Feedback> all() throws SQLException {
        return feedbackDao.findAll();
    }

    public List<Feedback> recent(int limit) throws SQLException {
        return feedbackDao.findRecent(limit);
    }

    public int pendingCount() throws SQLException {
        return feedbackDao.countByStatus(Feedback.STATUS_PENDING);
    }

    public void updateStatus(String id, String status) throws ValidationException, SQLException {
        if (status == null || !Feedback.STATUSES.contains(status)) {
            throw new ValidationException("Invalid feedback status.");
        }
        if (Validator.isBlank(id) || !feedbackDao.updateStatus(id, status)) {
            throw new ValidationException("Feedback ticket not found.");
        }
    }
}
