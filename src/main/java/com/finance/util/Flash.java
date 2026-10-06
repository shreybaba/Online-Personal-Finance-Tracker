package com.finance.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * One-time messages that survive a redirect (Post/Redirect/Get).
 * Stored in the session and moved to the request attributes
 * "successMessage" / "errorMessage" by RequestSetupFilter on the next request.
 */
public final class Flash {

    public static final String SUCCESS_KEY = "flashSuccess";
    public static final String ERROR_KEY = "flashError";

    private Flash() {
    }

    public static void success(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SUCCESS_KEY, message);
    }

    public static void error(HttpServletRequest request, String message) {
        request.getSession().setAttribute(ERROR_KEY, message);
    }

    public static void moveToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute(SUCCESS_KEY);
        Object error = session.getAttribute(ERROR_KEY);
        if (success != null) {
            request.setAttribute("successMessage", success);
            session.removeAttribute(SUCCESS_KEY);
        }
        if (error != null) {
            request.setAttribute("errorMessage", error);
            session.removeAttribute(ERROR_KEY);
        }
    }
}
