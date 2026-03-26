package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.dto;

import java.time.LocalDateTime;

/**
 * 레거시 QnA 복합 조회 flat projection DTO.
 *
 * <p>qna + qna_product + qna_order 조인 결과. 이미지/답변은 별도 쿼리로 조합됩니다.
 *
 * <p>QueryDSL Projections.constructor 사용으로 NumberPath&lt;Long&gt; 매핑을 위해 숫자 필드는 래퍼 타입(Long)을 사용합니다.
 * Mapper에서 null-safe 변환 처리합니다.
 *
 * @param qnaId QnA ID
 * @param title 제목
 * @param content 내용
 * @param privateYn 비공개 여부 (Y/N)
 * @param qnaStatus QnA 상태
 * @param qnaType QnA 유형 (PRODUCT/ORDER)
 * @param qnaDetailType QnA 상세 유형
 * @param userId 사용자 ID
 * @param sellerId 셀러 ID
 * @param userType 사용자 유형
 * @param insertDate 등록 일시
 * @param updateDate 수정 일시
 * @param productGroupId 상품그룹 ID (nullable, qna_product LEFT JOIN 결과)
 * @param orderId 주문 ID (nullable, qna_order LEFT JOIN 결과)
 */
public record LegacyQnaCompositeQueryDto(
        Long qnaId,
        String title,
        String content,
        String privateYn,
        String qnaStatus,
        String qnaType,
        String qnaDetailType,
        Long userId,
        Long sellerId,
        String userType,
        LocalDateTime insertDate,
        LocalDateTime updateDate,
        Long productGroupId,
        Long orderId) {}
