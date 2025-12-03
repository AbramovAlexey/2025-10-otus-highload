package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.highload.sn.dao.PostDao;
import ru.otus.highload.sn.dto.Post;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDao postDao;

    @Override
    public List<Post> feed(Integer offset, Integer limit) {
        return postDao.getPosts(offset, limit);
    }

}
