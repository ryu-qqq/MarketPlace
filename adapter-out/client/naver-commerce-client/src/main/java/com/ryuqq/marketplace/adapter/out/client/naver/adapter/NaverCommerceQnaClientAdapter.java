package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverCustomerInquiryPageResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverInquiryAnswerResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverProductQnaPageResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 QnA 클라이언트 어댑터.
 *
 * <p>고객 문의(1:1 문의) + 상품 문의 API를 모두 처리합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceQnaClientAdapter {

    private final NaverCommerceApiClient apiClient;

    public NaverCommerceQnaClientAdapter(NaverCommerceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // ===== 고객 문의 (주문 기반 1:1) =====

    /** 고객 문의 목록 조회. */
    public NaverCustomerInquiryPageResponse getCustomerInquiries(
            String startSearchDate,
            String endSearchDate,
            boolean answeredOnly,
            int page,
            int size) {
        return apiClient.getCustomerInquiries(
                startSearchDate, endSearchDate, answeredOnly, page, size);
    }

    /** 고객 문의 답변 등록. */
    public NaverInquiryAnswerResponse insertInquiryAnswer(long inquiryNo, String answerComment) {
        return apiClient.insertInquiryAnswer(inquiryNo, answerComment);
    }

    /** 고객 문의 답변 수정. */
    public NaverInquiryAnswerResponse updateInquiryAnswer(
            long inquiryNo, long answerContentId, String answerComment) {
        return apiClient.updateInquiryAnswer(inquiryNo, answerContentId, answerComment);
    }

    // ===== 상품 문의 =====

    /** 상품 문의 목록 조회. */
    public NaverProductQnaPageResponse getProductQnas(
            String fromDate, String toDate, boolean answeredOnly, int page, int size) {
        return apiClient.getProductQnas(fromDate, toDate, answeredOnly, page, size);
    }

    /** 상품 문의 답변 등록/수정. */
    public void answerProductQna(long questionId, String commentContent) {
        apiClient.answerProductQna(questionId, commentContent);
    }
}
