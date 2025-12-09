package ru.otus.highload.sn.dao;

import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.PostCreatePostRequest;

import java.util.List;

public interface PostDao {

    List<Post> getPosts(Integer offset, Integer limit);

    List<Post> getLastPosts();

    List<Post> getLastPostsWithoutCache();

    Long add(PostCreatePostRequest postCreatePostRequest);
}
