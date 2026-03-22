package com.ryuqq.marketplace.adapter.out.client.setof.config;

import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceNetworkException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceServerException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
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
 * <p>RestClient 빈을 생성하고 baseUrl, timeout, 서비스 토큰 헤더, 에러 핸들링을 설정합니다.
 *
 * <p>HTTP 상태별 예외 변환:
 *
 * <ul>
 *   <li>400 → {@link SetofCommerceBadRequestException} (CB 무시, retryable=false)
 *   <li>429 → {@link SetofCommerceRateLimitException} (CB 기록, retryable=true)
 *   <li>5xx → {@link SetofCommerceServerException} (CB 기록, retryable=true)
 *   <li>기타 4xx → {@link SetofCommerceClientException} (CB 무시, retryable=false)
 *   <li>타임아웃/연결 실패 → {@link SetofCommerceNetworkException} (CB 기록, retryable=true)
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
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
                .defaultStatusHandler(
                        status -> status.value() >= 400,
                        (request, response) -> {
                            int statusCode = response.getStatusCode().value();
                            String body =
                                    new String(
                                            response.getBody().readAllBytes(),
                                            StandardCharsets.UTF_8);

                            if (statusCode == 400) {
                                throw new SetofCommerceBadRequestException(body);
                            }
                            if (statusCode == 429) {
                                throw new SetofCommerceRateLimitException(body);
                            }
                            if (statusCode >= 500) {
                                throw new SetofCommerceServerException(statusCode, body);
                            }
                            throw new SetofCommerceClientException(statusCode, body);
                        })
                .requestInterceptor(
                        (request, body, execution) -> {
                            try {
                                return execution.execute(request, body);
                            } catch (SocketTimeoutException e) {
                                throw new SetofCommerceNetworkException(
                                        "세토프 커머스 API 타임아웃: " + request.getURI(), e);
                            } catch (IOException e) {
                                throw new SetofCommerceNetworkException(
                                        "세토프 커머스 API 연결 실패: " + request.getURI(), e);
                            }
                        })
                .build();
    }
}
