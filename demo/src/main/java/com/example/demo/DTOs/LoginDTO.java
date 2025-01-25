package com.example.demo.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDTO {
    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Неверный формат почты")
    private String email;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 5, message = "Пароль не может содержать менее 5-ти символов")
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
