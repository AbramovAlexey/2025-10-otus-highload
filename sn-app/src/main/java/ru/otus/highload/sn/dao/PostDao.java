package ru.otus.highload.sn.dao;

import ru.otus.highload.sn.dto.Post;

import java.util.List;

public interface PostDao {

    List<Post> getPosts(Integer offset, Integer limit);

}
