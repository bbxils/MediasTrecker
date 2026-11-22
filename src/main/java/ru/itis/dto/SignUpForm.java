package ru.itis.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}