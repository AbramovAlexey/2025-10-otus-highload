package ru.otus.highload.sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.PostCreatePostRequest;
import ru.otus.highload.sn.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/post/feed")
    public List<Post> feed(@RequestParam(required = false, defaultValue = "0") Integer offset,
                           @RequestParam(required = false, defaultValue = "0") Integer limit) {
        return postService.feed(offset, limit);
    }

    @PostMapping("/post/create")
    public String feed(@RequestBody PostCreatePostRequest postCreatePostRequest) {
        return String.valueOf(postService.add(postCreatePostRequest));
    }

}
