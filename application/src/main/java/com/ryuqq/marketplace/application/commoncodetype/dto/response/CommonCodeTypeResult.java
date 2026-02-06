package com.ryuqq.marketplace.application.commoncodetype.dto.response;

import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import java.time.Instant;

/**
 * CommonCodeTypeResult - 공통 코드 타입 조회 결과 DTO.
 *
 * <p>APP-DTO-001: Application Result는 record 타입 필수.
 *
 * <p>APP-DTO-002: Result는 Domain 객체에서 직접 변환.
 *
 * @param id 공통 코드 타입 ID
 * @param code 코드
 * @param name 이름
 * @param description 설명
 * @param displayOrder 표시 순서
 * @param active 활성화 여부
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CommonCodeTypeResult(
        Long id,
        String code,
        String name,
        String description,
        int displayOrder,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain → Result 변환.
     *
     * @param domain CommonCodeType 도메인 객체
     * @return CommonCodeTypeResult
     */
    public static CommonCodeTypeResult from(CommonCodeType domain) {
        return new CommonCodeTypeResult(
                domain.idValue(),
                domain.code(),
                domain.name(),
                domain.description(),
                domain.displayOrder(),
                domain.isActive(),
                domain.createdAt(),
                domain.updatedAt());
    }
}
