package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CommonCodeTypeApiResponse - 공통 코드 타입 API 응답.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: DTO 불변성 보장.
 *
 * <p>API-DTO-004: createdAt/updatedAt 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수 (Instant 타입 사용 금지).
 *
 * <p>Response DTO는 Domain 객체 의존 금지 → Result만 의존해야 하며, 변환은 Mapper에서 수행합니다.
 *
 * @param id 공통 코드 타입 ID
 * @param code 코드
 * @param name 이름
 * @param description 설명
 * @param displayOrder 표시 순서
 * @param active 활성화 여부
 * @param createdAt 생성일시 (ISO 8601 형식)
 * @param updatedAt 수정일시 (ISO 8601 형식)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "공통 코드 타입 응답")
public record CommonCodeTypeApiResponse(
        @Schema(description = "공통 코드 타입 ID", example = "1") Long id,
        @Schema(description = "코드", example = "PAYMENT_METHOD") String code,
        @Schema(description = "이름", example = "결제수단") String name,
        @Schema(description = "설명", example = "결제 시 사용 가능한 결제수단 목록") String description,
        @Schema(description = "표시 순서", example = "1") int displayOrder,
        @Schema(description = "활성화 여부", example = "true") boolean active,
        @Schema(description = "생성일시 (KST)", example = "2025-01-23 10:30:00") String createdAt,
        @Schema(description = "수정일시 (KST)", example = "2025-01-23 10:30:00") String updatedAt) {}
