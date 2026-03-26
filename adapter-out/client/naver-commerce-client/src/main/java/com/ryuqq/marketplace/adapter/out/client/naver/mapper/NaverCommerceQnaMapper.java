package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverCustomerInquiry;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverProductQna;
import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 네이버 QnA 응답 → ExternalQnaPayload 변환 매퍼. */
@Component
public class NaverCommerceQnaMapper {

    private static final Map<String, String> CATEGORY_TO_QNA_TYPE =
            Map.of(
                    "상품", "PRODUCT",
                    "배송", "SHIPPING",
                    "반품", "REFUND",
                    "교환", "EXCHANGE",
                    "환불", "REFUND",
                    "기타", "ETC");

    public ExternalQnaPayload toExternalPayload(NaverCustomerInquiry inquiry) {
        String qnaType = CATEGORY_TO_QNA_TYPE.getOrDefault(inquiry.category(), "ETC");
        return new ExternalQnaPayload(
                "INQUIRY-" + inquiry.inquiryNo(),
                qnaType,
                inquiry.inquiryContent(),
                inquiry.customerName(),
                inquiry.productNo(),
                inquiry.orderId(),
                "{\"type\":\"CUSTOMER_INQUIRY\",\"inquiryNo\":"
                        + inquiry.inquiryNo()
                        + ",\"category\":\""
                        + inquiry.category()
                        + "\""
                        + ",\"orderId\":\""
                        + inquiry.orderId()
                        + "\",\"productNo\":\""
                        + inquiry.productNo()
                        + "\"}");
    }

    public ExternalQnaPayload toExternalPayload(NaverProductQna qna) {
        return new ExternalQnaPayload(
                "PRODUCT-QNA-" + qna.questionId(),
                "PRODUCT",
                qna.question(),
                qna.maskedWriterId(),
                qna.productId() != null ? String.valueOf(qna.productId()) : null,
                null,
                "{\"type\":\"PRODUCT_QNA\",\"questionId\":"
                        + qna.questionId()
                        + ",\"productId\":"
                        + qna.productId()
                        + "}");
    }
}
