package com.ryuqq.marketplace.adapter.out.client.naver.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceQnaClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceServerException;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaAnswerSyncStrategy;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 QnA 답변 동기화 전략.
 *
 * <p>externalQnaId의 prefix로 고객 문의(INQUIRY-)와 상품 문의(PRODUCT-QNA-)를 구분합니다.
 */
@Primary
@Component
@ConditionalOnBean(NaverCommerceQnaClientAdapter.class)
public class NaverQnaAnswerSyncStrategy implements QnaAnswerSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverQnaAnswerSyncStrategy.class);
    private static final String CUSTOMER_INQUIRY_PREFIX = "INQUIRY-";
    private static final String PRODUCT_QNA_PREFIX = "PRODUCT-QNA-";

    private final NaverCommerceQnaClientAdapter qnaClient;
    private final ObjectMapper objectMapper;

    public NaverQnaAnswerSyncStrategy(
            NaverCommerceQnaClientAdapter qnaClient, ObjectMapper objectMapper) {
        this.qnaClient = qnaClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public OutboxSyncResult execute(QnaOutbox outbox) {
        String externalQnaId = outbox.externalQnaId();
        String answerContent = outbox.payload();

        try {
            if (externalQnaId.startsWith(CUSTOMER_INQUIRY_PREFIX)) {
                long inquiryNo =
                        Long.parseLong(externalQnaId.substring(CUSTOMER_INQUIRY_PREFIX.length()));
                qnaClient.insertInquiryAnswer(inquiryNo, answerContent);
                log.info("네이버 고객 문의 답변 등록 성공: inquiryNo={}", inquiryNo);

            } else if (externalQnaId.startsWith(PRODUCT_QNA_PREFIX)) {
                long questionId =
                        Long.parseLong(externalQnaId.substring(PRODUCT_QNA_PREFIX.length()));
                qnaClient.answerProductQna(questionId, answerContent);
                log.info("네이버 상품 문의 답변 등록 성공: questionId={}", questionId);

            } else {
                log.warn("알 수 없는 externalQnaId 형식: {}", externalQnaId);
                return OutboxSyncResult.failure(false, "알 수 없는 externalQnaId 형식: " + externalQnaId);
            }

            return OutboxSyncResult.success();

        } catch (NaverCommerceBadRequestException | NaverCommerceClientException e) {
            return OutboxSyncResult.failure(false, e.getMessage());
        } catch (NaverCommerceServerException | NaverCommerceRateLimitException e) {
            return OutboxSyncResult.failure(true, e.getMessage());
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            log.error("QnA 답변 동기화 중 예외: externalQnaId={}", externalQnaId, e);
            return OutboxSyncResult.failure(true, e.getMessage());
        }
    }
}
