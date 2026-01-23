package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.highload.sn.dao.PostDao;
import ru.otus.highload.sn.dao.UserDao;
import ru.otus.highload.api.dto.Post;
import ru.otus.highload.api.dto.PostCreatePostRequest;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDao postDao;
    private final RabbitService rabbitService;
    private final UserDao userDao;

    @Override
    public List<Post> feed(Long userId) {
        return postDao.getPosts(userId);
    }

    @Override
    public Long add(PostCreatePostRequest postCreatePostRequest, Long authorId) {
        var postId = postDao.add(postCreatePostRequest, authorId);
        if (userDao.isCelebrityById(authorId)) {
            rabbitService.sendEventCelebrity(postCreatePostRequest.getText(), postId, authorId);
        } else {
            rabbitService.sendEventUser(postCreatePostRequest.getText(), postId, authorId);
        }
        return postId;
    }

    @Override
    public void materialize(Long authorId, Long subId, Long postId, String content) {
        postDao.addToFeed(content, postId, subId, authorId);
    }

}
