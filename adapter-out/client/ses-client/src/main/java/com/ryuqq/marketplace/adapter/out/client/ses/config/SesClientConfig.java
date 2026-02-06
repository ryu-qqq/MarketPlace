package com.ryuqq.marketplace.adapter.out.client.ses.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * AWS SES v2 Client Configuration.
 *
 * <p>SesV2Client 빈을 생성합니다.
 *
 * <p>ses.sender-email 설정이 있을 때만 활성화됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(SesProperties.class)
@ConditionalOnProperty(prefix = "ses", name = "sender-email")
public class SesClientConfig {

    /**
     * SesV2Client 빈 생성.
     *
     * <p>AWS 기본 인증 체인(환경변수, IAM Role 등)을 사용합니다.
     *
     * @param properties SES 설정
     * @return SesV2Client 인스턴스
     */
    @Bean
    public SesV2Client sesV2Client(SesProperties properties) {
        return SesV2Client.builder().region(Region.of(properties.getRegion())).build();
    }
}
