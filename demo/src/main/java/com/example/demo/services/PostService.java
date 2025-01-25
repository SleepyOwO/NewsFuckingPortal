package com.example.demo.services;

import com.example.demo.DTOs.PostDTO;
import com.example.demo.models.Post;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repos.PostRepository;
import com.example.demo.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final UserRepository userRepos;
    private final PostRepository postRepos;

    @Autowired
    public PostService(UserRepository userRepos, PostRepository postRepos) {
        this.userRepos = userRepos;
        this.postRepos = postRepos;
    }

    public void createPost(Long id, PostDTO postDTO) throws IOException {
        User user = userRepos.findById(id).orElseThrow(()->new RuntimeException("Пользователь не найден"));
        if(user.getRole() != Role.MODERATOR){
            throw new SecurityException("Только модератор может публиковать новости");
        }
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthor(user);
        post.setAuthorName(user.getName());
        if (postDTO.getImage() != null) {
            post.setImage(postDTO.getImage().getBytes());
        }
        user.addPost(post);
        postRepos.save(post);
    }

    public List<Post> getAllPosts(){
        return postRepos.findAll();
    }

    public void editNews(Long id, String title, String content, byte[] image){
        Post post = postRepos.findById(id).orElseThrow(()->new RuntimeException("Пост не найден"));
        if (title != null && !title.trim().isEmpty())
        {
            post.setTitle(title);
        }
        if (content != null && !content.trim().isEmpty())
        {
            post.setContent(content);
        }
        if (image != null && image.length > 0)
        {
            post.setImage(image);
        }
        postRepos.save(post);
    }

    public Post getPostById(Long id){
        Post post = postRepos.findById(id).orElse(null);
        return post;
    }

    public void deletePostById(Long id){
        postRepos.deleteById(id);
    }

}
