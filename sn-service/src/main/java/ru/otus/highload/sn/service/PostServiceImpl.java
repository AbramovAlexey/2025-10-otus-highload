package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.highload.sn.dao.PostDao;
import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.PostCreatePostRequest;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDao postDao;
    private final RabbitService rabbitService;

    @Override
    public List<Post> feed(Integer offset, Integer limit) {
        var sumOffsetLimit = offset + limit;
            //отдаем данные напрямую из БД
            return postDao.getPosts(offset, limit);
    }

    @Override
    public Long add(PostCreatePostRequest postCreatePostRequest, Long authorId) {
        var postId = postDao.add(postCreatePostRequest, authorId);
        rabbitService.sendEventUser(postCreatePostRequest.getText(), postId, authorId);
        return postId;
    }

}
