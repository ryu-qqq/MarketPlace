package com.ryuqq.marketplace.application.legacy.qna.dto.result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레거시 QnA 상세 결과.
 *
 * @param qnaId QnA ID
 * @param title 제목
 * @param content 내용
 * @param privateYn 비공개 여부
 * @param qnaStatus QnA 상태
 * @param qnaType QnA 유형
 * @param qnaDetailType QnA 상세 유형
 * @param userId 사용자 ID
 * @param sellerId 셀러 ID
 * @param userType 사용자 유형
 * @param insertOperator 등록자
 * @param insertDate 등록일시
 * @param updateDate 수정일시
 * @param productGroupId 상품그룹 ID (nullable)
 * @param orderId 주문 ID (nullable)
 * @param answers 답변 목록
 * @param images 질문 이미지 목록
 */
public record LegacyQnaDetailResult(
        long qnaId,
        String title,
        String content,
        String privateYn,
        String qnaStatus,
        String qnaType,
        String qnaDetailType,
        Long userId,
        Long sellerId,
        String userType,
        String insertOperator,
        LocalDateTime insertDate,
        LocalDateTime updateDate,
        Long productGroupId,
        Long orderId,
        List<LegacyQnaAnswerResult> answers,
        List<LegacyQnaImageResult> images) {}
