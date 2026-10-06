package com.finance.servlet;

import com.finance.service.FeedbackService;
import com.finance.service.ValidationException;
import com.finance.util.Flash;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** POST /feedback/submit — the user id is taken from the session, not from the form. */
@WebServlet("/feedback/submit")
public class FeedbackServlet extends BaseServlet {

    private final FeedbackService feedbackService = new FeedbackService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            feedbackService.submit(currentUser(request).getId(), request.getParameter("message"));
            Flash.success(request, "Thanks! Your feedback has been submitted.");
        } catch (ValidationException e) {
            Flash.error(request, e.getMessage());
        } catch (SQLException e) {
            throw new ServletException("Could not submit feedback", e);
        }
        redirect(request, response, "/user/profile");
    }
}
