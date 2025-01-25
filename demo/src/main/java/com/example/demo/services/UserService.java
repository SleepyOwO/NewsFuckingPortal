package com.example.demo.services;

import com.example.demo.DTOs.UserDTO;
import com.example.demo.models.Post;
import com.example.demo.models.Role;
import com.example.demo.repos.PostRepository;
import com.example.demo.repos.UserRepository;
import com.example.demo.models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepos;
    private final PasswordEncoder passEncoder;
    private final PostRepository postRepos;

    @Autowired
    public UserService(UserRepository userRepos, PasswordEncoder passEncoder, PostRepository postRepos){
        this.userRepos = userRepos;
        //this.passEncoder = new BCryptPasswordEncoder();
        this.passEncoder = passEncoder;
        this.postRepos = postRepos;
    }

    public User registerUser(UserDTO userDTO){
        if (userRepos.existsByEmail(userDTO.getEmail())){
            return null;
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPassword(passEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.USER);
        userRepos.save(user);

        return user;
    }

    public User saveUser(User user){
        if (userRepos.existsByEmail(user.getEmail())){
            return null;
        }
        userRepos.save(user);

        return user;
    }

    public User authenticate(String email, String password){
        User user = userRepos.findByEmail(email);
        if(user != null && passEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public void updateProfile(Long id, String password, String name, byte[] avatar){
        User user = userRepos.findById(id).orElseThrow(()->new RuntimeException("Пользователь не найден"));
        if (avatar != null && avatar.length > 0) {
            user.setAvatar(avatar);
        }
        if (password != null && !password.trim().isEmpty()){
            user.setPassword(passEncoder.encode(password));
        }
        if(name != null){
            user.setName(name);
        }
        userRepos.save(user);
    }

    public void deleteAvatar(Long id) {
        User user = userRepos.findById(id).orElseThrow(()->new RuntimeException("Пользователь не найден"));
        user.setAvatar(null);
        userRepos.save(user);
    }

    public User getUserById(Long userId){
        User user = userRepos.findById(userId).orElse(null);
        return user;
    }

    public void deleteUserById(Long id){
        userRepos.deleteById(id);
    }

    public void incrementVisitCount(Long userId) {
        User user = userRepos.findById(userId).orElse(null);
        user.setVisitCount(user.getVisitCount() + 1);
        userRepos.save(user);
    }

    public List<User> getAllUsers(){
        return userRepos.findAll();
    }

    public void editUserProfile(Long id, String name, String role){
        User user = userRepos.findById(id).orElseThrow(()->new RuntimeException("Пользователь не найден"));
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (role != null && !role.trim().isEmpty()) {
            try {
                Role newRole = Role.valueOf(role.toUpperCase());
                user.setRole(newRole);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Указана некорректная роль: " + role);
            }
        }
        userRepos.save(user);

    }

    public void addFavoritePost(Long userId, Long postId) {
        User user = userRepos.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Post post = postRepos.findById(postId).orElseThrow(()-> new RuntimeException("Новость не найдена"));
        if (!user.getFavorites().contains(post)) {
            user.getFavorites().add(post);
            userRepos.save(user);
        }
    }

    public void removeFavoritePost(Long userId, Long postId) {
        User user = userRepos.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Post post = postRepos.findById(postId).orElseThrow(()-> new RuntimeException("Новость не найдена"));
        if (user.getFavorites().contains(post)) {
            user.getFavorites().remove(post);
            userRepos.save(user);
        }
    }

    public List<Post> getFavoritePosts(Long userId) {
        User user = userRepos.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.getFavorites();
    }

}
