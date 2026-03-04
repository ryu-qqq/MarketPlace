package com.ryuqq.marketplace.adapter.out.client.setof.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Setof Commerce Client Configuration.
 *
 * <p>RestClient 빈을 생성하고 baseUrl, timeout, 서비스 토큰 헤더를 설정합니다.
 *
 * <p>setof-commerce.service-token 설정이 있을 때만 활성화됩니다.
 */
@Configuration
@EnableConfigurationProperties(SetofCommerceProperties.class)
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceClientConfig {

    /**
     * Setof Commerce 전용 RestClient 빈 생성.
     *
     * @param properties Setof Commerce 설정
     * @return RestClient 인스턴스
     */
    @Bean
    public RestClient setofCommerceRestClient(SetofCommerceProperties properties) {
        Duration connectTimeout = properties.getTimeout().getConnect();
        Duration readTimeout = properties.getTimeout().getRead();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("X-Service-Token", properties.getServiceToken())
                .defaultHeader("X-Service-Name", properties.getServiceName())
                .requestFactory(requestFactory)
                .build();
    }
}
