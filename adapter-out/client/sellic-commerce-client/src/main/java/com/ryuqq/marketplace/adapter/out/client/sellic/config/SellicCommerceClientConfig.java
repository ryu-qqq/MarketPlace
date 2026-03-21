package com.ryuqq.marketplace.adapter.out.client.sellic.config;

import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceNetworkException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceServerException;
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
 * Sellic Commerce Client Configuration.
 *
 * <p>RestClient 빈을 생성하고 baseUrl, timeout, 에러 핸들링을 설정합니다. 셀릭은 Body 인증 방식이므로 defaultHeader에 인증
 * 정보를 포함하지 않습니다.
 *
 * <p>HTTP 상태별 예외 변환:
 *
 * <ul>
 *   <li>400 → {@link SellicCommerceBadRequestException} (CB 무시, retryable=false)
 *   <li>429 → {@link SellicCommerceRateLimitException} (CB 기록, retryable=true)
 *   <li>5xx → {@link SellicCommerceServerException} (CB 기록, retryable=true)
 *   <li>기타 4xx → {@link SellicCommerceClientException} (CB 무시, retryable=false)
 *   <li>타임아웃/연결 실패 → {@link SellicCommerceNetworkException} (CB 기록, retryable=true)
 * </ul>
 */
@Configuration
@EnableConfigurationProperties(SellicCommerceProperties.class)
@ConditionalOnProperty(prefix = "sellic-commerce", name = "customer-id")
public class SellicCommerceClientConfig {

    /**
     * Sellic Commerce 전용 RestClient 빈 생성.
     *
     * @param properties Sellic Commerce 설정
     * @return RestClient 인스턴스
     */
    @Bean
    public RestClient sellicCommerceRestClient(SellicCommerceProperties properties) {
        Duration connectTimeout = properties.getTimeout().getConnect();
        Duration readTimeout = properties.getTimeout().getRead();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
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
                                throw new SellicCommerceBadRequestException(body);
                            }
                            if (statusCode == 429) {
                                throw new SellicCommerceRateLimitException(body);
                            }
                            if (statusCode >= 500) {
                                throw new SellicCommerceServerException(statusCode, body);
                            }
                            throw new SellicCommerceClientException(statusCode, body);
                        })
                .requestInterceptor(
                        (request, body, execution) -> {
                            try {
                                return execution.execute(request, body);
                            } catch (SocketTimeoutException e) {
                                throw new SellicCommerceNetworkException(
                                        "셀릭 커머스 API 타임아웃: " + request.getURI(), e);
                            } catch (IOException e) {
                                throw new SellicCommerceNetworkException(
                                        "셀릭 커머스 API 연결 실패: " + request.getURI(), e);
                            }
                        })
                .build();
    }
}
