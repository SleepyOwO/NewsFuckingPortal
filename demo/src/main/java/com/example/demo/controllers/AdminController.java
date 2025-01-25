package com.example.demo.controllers;

import com.example.demo.DTOs.UserDTO;
import com.example.demo.models.User;
import com.example.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/userList")
    public ResponseEntity<?> getAllUsers(){
        List<User> users = userService.getAllUsers();
        if (users != null){
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.status(404).body("Пользователи не найдены");
    }

    @PostMapping("/userList/add")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/userList/{id}/edit")
    public ResponseEntity<?> editUserInfo(@PathVariable Long id,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) String role){
        try {
            userService.editUserProfile(id, name, role);
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch(Exception e){
            return ResponseEntity.status(500).body("Произошла ошибка" + e.getMessage());
        }
    }

    @DeleteMapping("/userList/{id}/deleteUser")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Пользователь успешно удален");
    }
}
