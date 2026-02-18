package com.ryuqq.marketplace.application.productgroupinspection.port.out.client;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.CanonicalOptionEnhancementResult;

/** 캐노니컬 옵션 매핑 LLM 보강 클라이언트. */
public interface CanonicalOptionEnhancementClient {

    /**
     * 셀러 옵션을 캐노니컬 옵션에 매핑합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 매핑 보강 결과
     */
    CanonicalOptionEnhancementResult enhance(Long productGroupId);
}
