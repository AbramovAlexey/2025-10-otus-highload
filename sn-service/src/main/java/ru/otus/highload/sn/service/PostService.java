package ru.otus.highload.sn.service;

import ru.otus.highload.api.dto.Post;
import ru.otus.highload.api.dto.PostCreatePostRequest;

import java.util.List;

public interface PostService {

    List<Post> feed(Long userId);

    Long add(PostCreatePostRequest postCreatePostRequest, Long authorId);

    void materialize(Long authorId, Long subId, Long postId, String content);
}
