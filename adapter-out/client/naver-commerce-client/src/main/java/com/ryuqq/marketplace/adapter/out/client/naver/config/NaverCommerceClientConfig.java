package com.ryuqq.marketplace.adapter.out.client.naver.config;

import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceNetworkException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceServerException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * Naver Commerce Client Configuration.
 *
 * <p>RestClient 빈을 생성하고 baseUrl, timeout, 에러 핸들링을 설정합니다.
 *
 * <p>HTTP 상태별 예외 변환:
 *
 * <ul>
 *   <li>429 → {@link NaverCommerceRateLimitException} (CB 기록, 재시도 O)
 *   <li>400 → {@link NaverCommerceBadRequestException} (CB 무시, 재시도 X)
 *   <li>5xx → {@link NaverCommerceServerException} (CB 기록, 재시도 O)
 *   <li>타임아웃/연결실패 → {@link NaverCommerceNetworkException} (CB 기록, 재시도 O)
 *   <li>기타 4xx → {@link NaverCommerceClientException} (CB 무시, 재시도 X)
 * </ul>
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
                .defaultStatusHandler(
                        status -> status.value() >= 400,
                        (request, response) -> {
                            int statusCode = response.getStatusCode().value();
                            String body =
                                    new String(
                                            response.getBody().readAllBytes(),
                                            StandardCharsets.UTF_8);

                            if (statusCode == 429) {
                                throw new NaverCommerceRateLimitException(body);
                            }
                            if (statusCode == 400) {
                                throw new NaverCommerceBadRequestException(body);
                            }
                            if (statusCode >= 500) {
                                throw new NaverCommerceServerException(statusCode, body);
                            }
                            throw new NaverCommerceClientException(statusCode, body);
                        })
                .build();
    }

    /**
     * ResourceAccessException(타임아웃/연결실패)을 NaverCommerceNetworkException으로 변환합니다.
     *
     * <p>RestClient의 requestFactory 레벨에서 발생하는 IOException 기반 예외를 처리합니다.
     *
     * @param e RestClient 호출 중 발생한 ResourceAccessException
     * @return NaverCommerceNetworkException
     */
    public static NaverCommerceNetworkException toNetworkException(ResourceAccessException e) {
        if (e.getCause() instanceof SocketTimeoutException) {
            return new NaverCommerceNetworkException("네이버 커머스 API 타임아웃", e);
        }
        return new NaverCommerceNetworkException("네이버 커머스 API 연결 실패: " + e.getMessage(), e);
    }
}
