package com.ryuqq.marketplace.adapter.out.client.ai.adapter;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.NoticeCompletionEnhancementResult;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.NoticeCompletionEnhancementClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 고시정보 LLM 보완 어댑터.
 *
 * <p>Spring AI ChatModel을 사용하여 고시정보 빈 필드를 보완합니다.
 *
 * <p>TODO: ChatModel 주입 및 프롬프트 구현은 Spring AI 의존성 통합 후 진행
 */
@Component
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class NoticeCompletionEnhancementAdapter implements NoticeCompletionEnhancementClient {

    private static final Logger log =
            LoggerFactory.getLogger(NoticeCompletionEnhancementAdapter.class);

    @Override
    public NoticeCompletionEnhancementResult enhance(Long productGroupId) {
        log.info("고시정보 LLM 보완 시작: productGroupId={}", productGroupId);

        // TODO: ChatModel을 사용한 실제 LLM 호출 구현
        // 1. 현재 고시정보 항목 조회
        // 2. 상세설명 HTML에서 텍스트 추출
        // 3. 해당 카테고리의 필수 NoticeField 목록 조회
        // 4. LLM 프롬프트 구성 및 호출
        // 5. 빈/부족한 필드에 대한 값 제안 생성

        return new NoticeCompletionEnhancementResult(List.of(), 0);
    }
}
