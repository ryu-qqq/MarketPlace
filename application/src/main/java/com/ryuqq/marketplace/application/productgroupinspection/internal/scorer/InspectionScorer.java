package com.ryuqq.marketplace.application.productgroupinspection.internal.scorer;

import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionScoreType;

/**
 * 검수 Scorer.
 *
 * <p>상품 그룹 검수의 품질 점수 계산 인터페이스입니다. 각 Scorer는 0~100 사이의 점수를 반환합니다.
 */
public interface InspectionScorer {

    /** 이 Scorer가 평가하는 타입. */
    InspectionScoreType type();

    /**
     * 품질 점수 계산.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 0~100 사이의 점수
     */
    int score(Long productGroupId);
}
