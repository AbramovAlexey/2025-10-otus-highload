package ru.otus.highload.sn.dao;

import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.PostCreatePostRequest;

import java.util.List;

public interface PostDao {

    List<Post> getPosts(Long userId);

    Long add(PostCreatePostRequest postCreatePostRequest, Long authorId);

    Long addToFeed(String content, Long postId, Long subId, Long authorId);
}
