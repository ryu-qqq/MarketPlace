package com.ryuqq.marketplace.application.legacy.qna.dto.result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레거시 QnA 답변 결과.
 *
 * @param qnaAnswerId 답변 ID
 * @param qnaAnswerParentId 부모 답변 ID (nullable)
 * @param qnaWriterType 작성자 유형
 * @param title 제목
 * @param content 내용
 * @param insertOperator 등록자
 * @param updateOperator 수정자
 * @param insertDate 등록일시
 * @param updateDate 수정일시
 * @param images 답변 이미지 목록
 */
public record LegacyQnaAnswerResult(
        long qnaAnswerId,
        Long qnaAnswerParentId,
        String qnaWriterType,
        String title,
        String content,
        String insertOperator,
        String updateOperator,
        LocalDateTime insertDate,
        LocalDateTime updateDate,
        List<LegacyQnaImageResult> images) {}
