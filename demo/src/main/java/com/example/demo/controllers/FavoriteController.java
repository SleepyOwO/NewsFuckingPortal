package com.example.demo.controllers;

import com.example.demo.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public ResponseEntity<?> addPostToFavorites(@RequestParam Long userId, @RequestParam Long postId) {
        favoriteService.addPostToFavorites(userId, postId);
        return ResponseEntity.ok("Post added to favorites");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removePostFromFavorites(@RequestParam Long userId, @RequestParam Long postId) {
        favoriteService.removePostFromFavorites(userId, postId);
        return ResponseEntity.ok("Post removed from favorites");
    }
}