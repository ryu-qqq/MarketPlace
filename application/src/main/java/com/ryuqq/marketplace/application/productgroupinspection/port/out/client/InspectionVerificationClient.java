package com.ryuqq.marketplace.application.productgroupinspection.port.out.client;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.InspectionVerificationResult;

/** LLM 최종 품질 검증 클라이언트. */
public interface InspectionVerificationClient {

    /**
     * 상품 그룹의 최종 품질을 검증합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 검증 결과
     */
    InspectionVerificationResult verify(Long productGroupId);
}
