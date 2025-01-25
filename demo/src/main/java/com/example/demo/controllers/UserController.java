package com.example.demo.controllers;

import com.example.demo.DTOs.LoginDTO;
import com.example.demo.DTOs.UserDTO;
import com.example.demo.models.Post;
import com.example.demo.models.User;
import com.example.demo.services.PostService;
import com.example.demo.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    private PostService postService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDTO userDTO){
        User user = userService.registerUser(userDTO);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(409).body("Пользователь уже зарегистрирован");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpSession session){
        User user = userService.authenticate(loginDTO.getEmail(), loginDTO.getPassword());
        if (user != null){
            session.setAttribute("userId",user.getId());
            userService.incrementVisitCount(user.getId());
            return ResponseEntity.ok("Добро пожаловать");
        }
        else{
            return ResponseEntity.status(401).body("Неверное имя пользователя или пароль");
        }
    }

    @GetMapping("/getInfo")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Пользователь не авторизован");
        }
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }
    }

    @PutMapping("/dashboard/edit")
    public ResponseEntity<?> uploadProfile(HttpSession session,
                                               @RequestParam(required=false) String name,
                                               @RequestParam(required=false) String password,
                                               @RequestParam(required=false) MultipartFile avatar){
        try{
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Пользователь не авторизован");
            }
            byte[] avatarBytes = null;
            if (avatar != null) {
                avatarBytes = avatar.getBytes();
            }
            userService.updateProfile(userId, password, name, avatarBytes);
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        }
        catch(Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/dashboard/setDefaultAvatar")
    public ResponseEntity<?> setDefaultAvatar(HttpSession session){
        try{
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Пользователь не авторизован");
            }
            userService.deleteAvatar(userId);
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        }
        catch(Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/dashboard/logout")
    public ResponseEntity<?> logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        session.invalidate();
        Cookie[] cookies = request.getCookies();

        // Ищем куку с именем "JSESSIONID"
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie);
                if ("JSESSIONID".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return ResponseEntity.ok("Вы успешно вышли из системы" + session);
    }

    // Добавить пост в избранное
    @PostMapping("/favorites/add/{postId}")
    public ResponseEntity<?> addFavoritePost(@PathVariable Long postId, HttpSession session) {
        try {
            userService.addFavoritePost((Long)session.getAttribute("userId"), postId);
            return ResponseEntity.ok("Пост добавлен в избранное");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка добавления поста в избранное");
        }
    }

    // Удалить пост из избранного
    @DeleteMapping("/favorites/remove/{postId}")
    public ResponseEntity<?> removeFavoritePost(@PathVariable Long postId, HttpSession session) {
        try {
            userService.removeFavoritePost((Long)session.getAttribute("userId"), postId);
            return ResponseEntity.ok("Пост удален из избранного");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка удаления поста из избранного");
        }
    }

    // Получить все избранные посты пользователя
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavoritePosts(HttpSession session) {
        try {
            List<Post> favoritePosts = userService.getFavoritePosts((Long)session.getAttribute("userId"));
            return ResponseEntity.ok(favoritePosts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка получения избранных постов");
        }
    }
}
