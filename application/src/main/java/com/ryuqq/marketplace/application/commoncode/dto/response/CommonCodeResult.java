package com.ryuqq.marketplace.application.commoncode.dto.response;

import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.time.Instant;

/**
 * CommonCodeResult - 공통 코드 조회 결과 DTO.
 *
 * <p>APP-DTO-001: Application Result는 record 타입 필수.
 *
 * <p>APP-DTO-002: Result는 Domain 객체에서 직접 변환.
 *
 * @param id 공통 코드 ID
 * @param commonCodeTypeId 공통 코드 타입 ID
 * @param code 코드값
 * @param displayName 표시명
 * @param displayOrder 표시 순서
 * @param active 활성화 여부
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CommonCodeResult(
        Long id,
        Long commonCodeTypeId,
        String code,
        String displayName,
        int displayOrder,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain → Result 변환.
     *
     * @param domain CommonCode 도메인 객체
     * @return CommonCodeResult
     */
    public static CommonCodeResult from(CommonCode domain) {
        return new CommonCodeResult(
                domain.idValue(),
                domain.commonCodeTypeIdValue(),
                domain.codeValue(),
                domain.displayNameValue(),
                domain.displayOrderValue(),
                domain.isActive(),
                domain.createdAt(),
                domain.updatedAt());
    }
}
