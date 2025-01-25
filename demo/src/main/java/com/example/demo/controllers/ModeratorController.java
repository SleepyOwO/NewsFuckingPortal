package com.example.demo.controllers;

import com.example.demo.DTOs.PostDTO;
import com.example.demo.models.Post;
import com.example.demo.models.User;
import com.example.demo.services.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/news")
public class ModeratorController {
    private final PostService postService;

    public ModeratorController(PostService postService){
        this.postService = postService;
    }

    @PostMapping("/publishNews")
    public ResponseEntity<?> createPost(HttpSession session, @ModelAttribute @Valid PostDTO postDTO) {
        try {
            Long userId = (Long)session.getAttribute("userId");
            postService.createPost(userId, postDTO);
            return ResponseEntity.ok("Новость успешно создана");
        }
        catch (SecurityException e){
            return ResponseEntity.status(403).body("Ошибка " + e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка " + e.getMessage());
        }
    }

    @GetMapping("/viewNews")
    public ResponseEntity<?> viewAllPosts(){
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PutMapping("/viewNews/{id}/edit")
    public ResponseEntity<?> editNews(@PathVariable Long id, @RequestParam(required = false) String title,
                                      @RequestParam(required = false) String content,
                                      @RequestParam(required = false) MultipartFile image) {
        try{
            System.out.println(title);
            System.out.println(content);
            System.out.println(image);
            byte [] imageBytes = null;
            if(image != null){
                imageBytes = image.getBytes();
            }
            postService.editNews(id, title, content, imageBytes);
            Post post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/viewNews/{id}/delete")
    public ResponseEntity<?> deleteNews(@PathVariable Long id){
        postService.deletePostById(id);
        return ResponseEntity.ok("Новость успешно удалена");
    }
}
