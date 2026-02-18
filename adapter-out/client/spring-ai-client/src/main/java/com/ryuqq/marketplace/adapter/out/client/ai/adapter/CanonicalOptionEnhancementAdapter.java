package com.ryuqq.marketplace.adapter.out.client.ai.adapter;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.CanonicalOptionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.CanonicalOptionEnhancementClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 캐노니컬 옵션 매핑 LLM 보강 어댑터.
 *
 * <p>Spring AI ChatModel을 사용하여 셀러 옵션을 캐노니컬 옵션에 매핑합니다.
 *
 * <p>TODO: ChatModel 주입 및 프롬프트 구현은 Spring AI 의존성 통합 후 진행
 */
@Component
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class CanonicalOptionEnhancementAdapter implements CanonicalOptionEnhancementClient {

    private static final Logger log =
            LoggerFactory.getLogger(CanonicalOptionEnhancementAdapter.class);

    @Override
    public CanonicalOptionEnhancementResult enhance(Long productGroupId) {
        log.info("캐노니컬 옵션 LLM 보강 시작: productGroupId={}", productGroupId);

        // TODO: ChatModel을 사용한 실제 LLM 호출 구현
        // 1. 셀러 옵션 그룹/값 조회
        // 2. 해당 카테고리의 캐노니컬 옵션 후보 조회
        // 3. LLM 프롬프트 구성 및 호출
        // 4. 응답 파싱하여 매핑 제안 생성

        return new CanonicalOptionEnhancementResult(List.of(), 0);
    }
}
