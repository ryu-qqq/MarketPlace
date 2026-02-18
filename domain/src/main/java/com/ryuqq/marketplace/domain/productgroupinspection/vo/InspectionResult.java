package com.ryuqq.marketplace.domain.productgroupinspection.vo;

import java.util.List;
import java.util.Map;

/**
 * 검수 결과 Value Object.
 *
 * @param scoreResults Score 항목별 점수 (0~100)
 * @param totalScore 가중 합산 점수
 * @param passed 최종 통과 여부 (totalScore >= 70)
 * @param failureReasons 실패 사유 목록
 */
public record InspectionResult(
        Map<InspectionScoreType, Integer> scoreResults,
        int totalScore,
        boolean passed,
        List<String> failureReasons) {

    /** 통과 기준 점수. */
    public static final int PASSING_SCORE = 70;

    public static InspectionResult of(
            Map<InspectionScoreType, Integer> scoreResults,
            int totalScore,
            boolean passed,
            List<String> failureReasons) {
        return new InspectionResult(
                Map.copyOf(scoreResults), totalScore, passed, List.copyOf(failureReasons));
    }
}
