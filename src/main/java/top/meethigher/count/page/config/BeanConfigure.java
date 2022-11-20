package top.meethigher.count.page.config;

import jodd.http.HttpRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import top.meethigher.cache.CacheStore;
import top.meethigher.cache.impl.LeastRecentlyUsedCacheStore;
import top.meethigher.count.page.entity.IP;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 常用bean注册
 *
 * @author chenchuancheng
 * @since 2022/11/19 16:17
 */
@Configuration
public class BeanConfigure {

    @Bean
    public HttpRequest httpRequest() {
        return new HttpRequest();
    }

    @Bean
    public CacheStore<Integer, IP> cacheStore() {
        return new LeastRecentlyUsedCacheStore<>(80);
    }

    @Bean
    public ExecutorService countForPage() {
        return new ThreadPoolExecutor(
                2,
                4,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(Integer.MAX_VALUE),
                new CustomizableThreadFactory("count-page-"),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
