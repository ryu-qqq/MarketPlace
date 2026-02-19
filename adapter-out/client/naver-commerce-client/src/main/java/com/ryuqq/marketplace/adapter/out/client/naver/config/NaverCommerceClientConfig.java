package com.ryuqq.marketplace.adapter.out.client.naver.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Naver Commerce Client Configuration.
 *
 * <p>RestClient 빈을 생성하고 baseUrl, timeout을 설정합니다.
 *
 * <p>naver-commerce.client-id 설정이 있을 때만 활성화됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(NaverCommerceProperties.class)
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceClientConfig {

    /**
     * Naver Commerce 전용 RestClient 빈 생성.
     *
     * @param properties Naver Commerce 설정
     * @return RestClient 인스턴스
     */
    @Bean
    public RestClient naverCommerceRestClient(NaverCommerceProperties properties) {
        Duration connectTimeout = properties.getTimeout().getConnect();
        Duration readTimeout = properties.getTimeout().getRead();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
