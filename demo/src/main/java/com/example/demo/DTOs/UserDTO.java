package com.example.demo.DTOs;

import jakarta.validation.constraints.*;

public class UserDTO {
    @NotBlank(message = "Никнейм не может быть пустым")
    @Pattern(regexp = "^[a-zA-Z0-9._]{3,20}$", message = "Никнейм должен быть от 3 до 20 символов и содержать только буквы, цифры, '_', '.'")
    private String name;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 5, message = "Пароль не может содержать менее 5-ти символов")
    private String password;

    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Неверный формат почты")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
