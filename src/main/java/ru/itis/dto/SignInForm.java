package ru.itis.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInForm {
    private String login;      // логин или email
    private String password;
}