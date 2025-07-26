package com.github.biplab.nic.service;
import com.github.biplab.nic.entity.Post;
import com.github.biplab.nic.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(UUID id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(UUID id, Post postDetails) {
        return postRepository.findById(id).map(post -> {
            post.setPostName(postDetails.getPostName());
            post.setRank(postDetails.getRank());
            post.setDepartment(postDetails.getDepartment());
            return postRepository.save(post);
        }).orElse(null);
    }

    public boolean deletePost(UUID id) {
        return postRepository.findById(id).map(post -> {
            postRepository.delete(post);
            return true;
        }).orElse(false);
    }
}

