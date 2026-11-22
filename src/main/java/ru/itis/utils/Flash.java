package ru.itis.utils;

import ru.itis.exceptions.ValidationException;
import javax.servlet.http.HttpServletRequest;

public final class Flash {

    public static void success(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashSuccess", message);
    }

    public static void error(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    public static void error(HttpServletRequest req, ValidationException e) {
        error(req, String.join("; ", e.getErrors().values()));
    }
}