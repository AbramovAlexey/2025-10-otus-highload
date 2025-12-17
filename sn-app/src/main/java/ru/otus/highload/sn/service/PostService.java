package ru.otus.highload.sn.service;

import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.PostCreatePostRequest;

import java.util.List;

public interface PostService {

    List<Post> feed(Integer offset, Integer limit);

    Long add(PostCreatePostRequest postCreatePostRequest);
}
