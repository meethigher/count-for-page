package top.meethigher.countforpage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步线程池配置
 *
 * @author chenchuancheng
 * @since 2022/7/2 13:38
 */
@Configuration
public class AsyncExecutorConfig {


    @Bean
    public ExecutorService asyncExecutor() {
        return new ThreadPoolExecutor(
                4,
                10,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(4),
                new CustomizableThreadFactory("count-for-page-"),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
