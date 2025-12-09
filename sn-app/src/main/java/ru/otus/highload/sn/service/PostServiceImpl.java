package ru.otus.highload.sn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.highload.sn.dao.PostDao;
import ru.otus.highload.sn.dto.Post;
import ru.otus.highload.sn.dto.PostCreatePostRequest;

import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.highload.sn.config.CacheConfig.POST_CACHE_SIZE;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDao postDao;

    @Override
    public List<Post> feed(Integer offset, Integer limit) {
        var sumOffsetLimit = offset + limit;
        if (sumOffsetLimit > POST_CACHE_SIZE) {
            //отдаем данные напрямую из БД
            return postDao.getPosts(offset, limit);
        } else {
            //используем кэш
            var posts = postDao.getLastPosts();
            if (sumOffsetLimit == 0) {
                //отдаем все кэшированное
                return posts;
            } else {
                //фильтруем
                return posts.stream()
                            .skip(offset)
                            .limit(limit)
                            .collect(Collectors.toList());

            }
        }
    }

    @Override
    public Long add(PostCreatePostRequest postCreatePostRequest) {
        return postDao.add(postCreatePostRequest);
    }

}
