package com.ryuqq.marketplace.adapter.out.client.ai.adapter;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.InspectionVerificationResult;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionVerificationClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * LLM 최종 검증 어댑터.
 *
 * <p>Spring AI ChatModel을 사용하여 상품 그룹 최종 품질을 검증합니다.
 *
 * <p>TODO: ChatModel 주입 및 프롬프트 구현은 Spring AI 의존성 통합 후 진행
 */
@Component
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class InspectionVerificationAdapter implements InspectionVerificationClient {

    private static final Logger log = LoggerFactory.getLogger(InspectionVerificationAdapter.class);

    @Override
    public InspectionVerificationResult verify(Long productGroupId) {
        log.info("LLM 최종 검증 시작: productGroupId={}", productGroupId);

        // TODO: ChatModel을 사용한 실제 LLM 호출 구현
        // 1. 최종 상품 정보 전체 조회 (이름, 옵션, 고시정보, 이미지 수, 가격 등)
        // 2. LLM 프롬프트 구성 및 호출
        // 3. 통과/불통과 + 사유 파싱

        return new InspectionVerificationResult(true, 80, List.of("LLM 검증 미구현 - 기본 통과 처리"));
    }
}
