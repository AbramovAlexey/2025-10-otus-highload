package ru.otus.highload.sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.highload.api.dto.Post;
import ru.otus.highload.api.dto.PostCreatePostRequest;
import ru.otus.highload.sn.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/post/feed")
    public List<Post> feed(Long userId) {
        return postService.feed(userId);
    }

    @PostMapping("/post/create")
    public String feed(@RequestBody PostCreatePostRequest postCreatePostRequest,
                       @RequestParam(value = "author") Long authorId) {
        return String.valueOf(postService.add(postCreatePostRequest, authorId));
    }

}
