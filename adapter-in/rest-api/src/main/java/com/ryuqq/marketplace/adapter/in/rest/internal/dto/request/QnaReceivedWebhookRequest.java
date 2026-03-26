package com.ryuqq.marketplace.adapter.in.rest.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.List;

/**
 * QnA 수신 웹훅 요청.
 *
 * <p>자사몰에서 QnA 등록 시 호출. ExternalQnaPayload로 변환되어 InboundQna 파이프라인 진입.
 *
 * @param salesChannelId 판매채널 ID
 * @param qnas QnA 목록
 */
public record QnaReceivedWebhookRequest(
        @Positive long salesChannelId, @NotEmpty @Valid List<QnaItemRequest> qnas) {

    /**
     * QnA 아이템 요청.
     *
     * @param externalQnaId 외부 QnA ID (세토프 QnA PK)
     * @param qnaType 문의 유형 (PRODUCT, SHIPPING, ORDER, EXCHANGE, REFUND, RESTOCK, PRICE, ETC)
     * @param questionTitle 문의 제목
     * @param questionContent 문의 내용
     * @param questionAuthor 작성자명
     * @param sellerId 셀러 ID (세토프 seller PK → shop.account_id 역조회)
     * @param externalProductId 외부 상품 그룹 ID (nullable, 상품 매핑용)
     * @param externalOrderId 외부 주문 ID (nullable, 주문 문의일 때만)
     * @param questionedAt 문의 작성 시각
     */
    public record QnaItemRequest(
            @NotNull Long externalQnaId,
            @NotBlank String qnaType,
            String questionTitle,
            @NotBlank String questionContent,
            String questionAuthor,
            @NotNull Long sellerId,
            Long externalProductId,
            Long externalOrderId,
            Instant questionedAt) {}
}
