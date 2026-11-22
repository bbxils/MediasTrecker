package ru.itis.services;

import ru.itis.dto.SignInForm;
import ru.itis.dto.SignUpForm;
import ru.itis.models.User;

import java.sql.SQLException;

public interface AuthService {
    User signUp(SignUpForm form) throws SQLException;
    User signIn(SignInForm form) throws SQLException;
}