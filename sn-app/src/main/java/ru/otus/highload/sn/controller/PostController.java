package ru.otus.highload.sn.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/post/feed")
    public List<Post> feed(@RequestParam Integer offset, @RequestParam Integer limit) {
        return postService.feed(offset, limit);
    }

}
