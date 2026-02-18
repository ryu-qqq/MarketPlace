package com.ryuqq.marketplace.application.productgroupinspection.dto.response;

import java.util.List;

/**
 * LLM 최종 검증 결과.
 *
 * @param passed 통과 여부
 * @param overallScore 전체 품질 점수 (0~100)
 * @param reasons 판정 사유 목록
 */
public record InspectionVerificationResult(
        boolean passed, int overallScore, List<String> reasons) {}
