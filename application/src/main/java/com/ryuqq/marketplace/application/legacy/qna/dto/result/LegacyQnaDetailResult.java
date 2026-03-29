package com.ryuqq.marketplace.application.legacy.qna.dto.result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레거시 QnA 상세 결과.
 *
 * <p>세토프 QnA 응답과 동일한 구조를 제공하기 위해 상품/브랜드 정보를 포함합니다.
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
        String sellerName,
        String userType,
        String insertOperator,
        LocalDateTime insertDate,
        LocalDateTime updateDate,
        Long productGroupId,
        Long orderId,
        List<LegacyQnaAnswerResult> answers,
        List<LegacyQnaImageResult> images,
        // 상품/브랜드 정보
        String productGroupName,
        String productGroupMainImageUrl,
        Long brandId,
        String brandName,
        // 사용자 상세
        String userName,
        String userPhone,
        String userEmail,
        String userGender) {}
