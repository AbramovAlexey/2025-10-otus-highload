package ru.otus.highload.sn.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.otus.highload.sn.dao.PostDao;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
@Slf4j
public class CacheConfig {

    public static final int POST_CACHE_SIZE = 1000;
    public static final String POST_CACHE_KEY = "last";
    public static final String POST_CACHE_NAME = "postFeed";


    @Bean
    public CacheManager cacheManager(CacheProperties cacheProperties, CacheLoader cacheLoader) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(POST_CACHE_NAME);
        String specification = cacheProperties.getCaffeine().getSpec();
        cacheManager.setCacheLoader(cacheLoader);
        if (StringUtils.hasText(specification)) {
            cacheManager.setCacheSpecification(specification);
        }
        return cacheManager;
    }

    @Bean
    public CacheLoader<Object, Object> getCacheLoader(PostDao postDao) {
        //для работы refreshAfterWrite
        //кэш обновляется асинхронно
        return key -> {
            log.debug("Cache load start");
            return POST_CACHE_KEY.equals(key) ? postDao.getLastPostsWithoutCache() : null;
        };
    }

}
