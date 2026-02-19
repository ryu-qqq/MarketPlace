package com.ryuqq.marketplace.application.productgroupinspection.fallback;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.CanonicalOptionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.dto.response.InspectionVerificationResult;
import com.ryuqq.marketplace.application.productgroupinspection.dto.response.NoticeCompletionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.CanonicalOptionEnhancementClient;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionEnhancementPublishClient;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionScoringPublishClient;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionVerificationClient;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionVerificationPublishClient;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.NoticeCompletionEnhancementClient;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 검수 클라이언트 폴백 설정.
 *
 * <p>SQS/AI 어댑터 모듈이 클래스패스에 없거나 속성이 비활성화된 경우 NoOp 구현체를 제공합니다. 실제 어댑터 빈이 존재하면 이 폴백은 무시됩니다.
 */
@Configuration
public class InspectionClientFallbackConfig {

    @Bean
    @ConditionalOnMissingBean
    InspectionScoringPublishClient noOpInspectionScoringPublishClient() {
        return messageBody -> "noop";
    }

    @Bean
    @ConditionalOnMissingBean
    InspectionEnhancementPublishClient noOpInspectionEnhancementPublishClient() {
        return messageBody -> "noop";
    }

    @Bean
    @ConditionalOnMissingBean
    InspectionVerificationPublishClient noOpInspectionVerificationPublishClient() {
        return messageBody -> "noop";
    }

    @Bean
    @ConditionalOnMissingBean
    CanonicalOptionEnhancementClient noOpCanonicalOptionEnhancementClient() {
        return productGroupId -> new CanonicalOptionEnhancementResult(List.of(), 0);
    }

    @Bean
    @ConditionalOnMissingBean
    InspectionVerificationClient noOpInspectionVerificationClient() {
        return productGroupId -> new InspectionVerificationResult(false, 0, List.of());
    }

    @Bean
    @ConditionalOnMissingBean
    NoticeCompletionEnhancementClient noOpNoticeCompletionEnhancementClient() {
        return productGroupId -> new NoticeCompletionEnhancementResult(List.of(), 0);
    }
}
