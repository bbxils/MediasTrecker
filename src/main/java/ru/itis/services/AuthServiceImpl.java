package ru.itis.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itis.dto.SignInForm;
import ru.itis.dto.SignUpForm;
import ru.itis.exceptions.ValidationException;
import ru.itis.models.User;
import ru.itis.repositories.UserRepository;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthServiceImpl implements AuthService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_BYTES = 72;
    private static final String UNIQUE_VIOLATION = "23505";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User signUp(SignUpForm form) throws SQLException {
        Map<String, String> errors = new LinkedHashMap<>();

        String username = blankToNull(form.getUsername());
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            errors.put("username", "3–30 символов: латинские буквы, цифры и _");
        } else if (userRepository.findByUsername(username).isPresent()) {
            errors.put("username", "Этот логин уже занят");
        }

        String email = blankToNull(form.getEmail());
        if (email != null) {
            email = email.toLowerCase(Locale.ROOT);
        }
        if (email == null || email.length() > 200 || !EMAIL_PATTERN.matcher(email).matches()) {
            errors.put("email", "Введите корректный email");
        } else if (userRepository.findByEmail(email).isPresent()) {
            errors.put("email", "Этот email уже зарегистрирован");
        }

        String password = form.getPassword();
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            errors.put("password", "Пароль не короче " + MIN_PASSWORD_LENGTH + " символов");
        } else if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            errors.put("password", "Пароль слишком длинный");
        } else if (!password.equals(form.getConfirmPassword())) {
            errors.put("confirmPassword", "Пароли не совпадают");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role("user")                    // роль задаёт только сервер
                .build();
        try {
            return userRepository.save(user);
        } catch (SQLException e) {
            // два человека одновременно заняли один логин: проверка выше этого не поймала
            if (UNIQUE_VIOLATION.equals(e.getSQLState())) {
                errors.put("username", "Логин или email уже заняты");
                throw new ValidationException(errors);
            }
            throw e;
        }
    }

    @Override
    public User signIn(SignInForm form) throws SQLException {
        String login = blankToNull(form.getLogin());
        String password = form.getPassword();

        Optional<User> found = Optional.empty();
        if (login != null && password != null) {
            found = login.contains("@")
                    ? userRepository.findByEmail(login.toLowerCase(Locale.ROOT))
                    : userRepository.findByUsername(login);
        }

        // одно и то же сообщение для «нет такого» и «неверный пароль»: не подсказываем, что именно не так
        if (found.isEmpty() || !passwordEncoder.matches(password, found.get().getPasswordHash())) {
            throw new ValidationException(Map.of("form", "Неверный логин или пароль"));
        }
        return found.get();
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}