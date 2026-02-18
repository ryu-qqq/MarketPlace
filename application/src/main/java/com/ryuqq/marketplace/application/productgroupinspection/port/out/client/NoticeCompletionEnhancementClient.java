package com.ryuqq.marketplace.application.productgroupinspection.port.out.client;

import com.ryuqq.marketplace.application.productgroupinspection.dto.response.NoticeCompletionEnhancementResult;

/** 고시정보 LLM 보완 클라이언트. */
public interface NoticeCompletionEnhancementClient {

    /**
     * 상품 그룹의 고시정보를 보완합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 고시정보 보완 결과
     */
    NoticeCompletionEnhancementResult enhance(Long productGroupId);
}
