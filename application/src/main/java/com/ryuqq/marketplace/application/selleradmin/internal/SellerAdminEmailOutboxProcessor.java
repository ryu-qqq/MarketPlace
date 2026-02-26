package com.ryuqq.marketplace.application.selleradmin.internal;

import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminEmailSendResult;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminEmailOutboxCommandManager;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminEmailClient;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 셀러 관리자 이메일 Outbox 처리기.
 *
 * <p>스케줄러에서 호출되어 PENDING 상태의 이메일 Outbox를 처리합니다.
 *
 * <p><strong>트랜잭션 전략</strong>:
 *
 * <ul>
 *   <li>PROCESSING 상태 변경: 별도 트랜잭션 (외부 API 호출 전 커밋 필요)
 *   <li>실패 시 상태 변경: 별도 트랜잭션 (실패 상태 즉시 커밋 필요)
 *   <li>성공 시 완료 처리: 별도 트랜잭션 (완료 상태 즉시 커밋 필요)
 * </ul>
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>PROCESSING 상태로 변경 (다른 프로세스와 충돌 방지)
 *   <li>이메일 서비스 API 호출 (SES 등)
 *   <li>결과에 따라 Outbox가 자체적으로 상태 전이 (COMPLETED/PENDING/FAILED)
 * </ol>
 */
@Component
@ConditionalOnProperty(prefix = "ses", name = "sender-email")
public class SellerAdminEmailOutboxProcessor {

    private static final Logger log =
            LoggerFactory.getLogger(SellerAdminEmailOutboxProcessor.class);

    private final SellerAdminEmailOutboxCommandManager outboxCommandManager;
    private final SellerAdminEmailClient emailClient;

    public SellerAdminEmailOutboxProcessor(
            SellerAdminEmailOutboxCommandManager outboxCommandManager,
            SellerAdminEmailClient emailClient) {
        this.outboxCommandManager = outboxCommandManager;
        this.emailClient = emailClient;
    }

    /**
     * 단일 이메일 Outbox를 처리합니다.
     *
     * @param outbox 처리할 이메일 Outbox
     * @return 처리 성공 여부
     */
    public boolean processOutbox(SellerAdminEmailOutbox outbox) {
        Instant now = Instant.now();
        Long sellerId = outbox.sellerIdValue();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            SellerAdminEmailSendResult result = emailClient.sendEmail(outbox);

            if (result.success()) {
                return handleSuccess(outbox, result, now);
            } else {
                return handleFailure(outbox, result, now);
            }

        } catch (Exception e) {
            log.error(
                    "셀러 관리자 이메일 Outbox 처리 중 예외 발생: outboxId={}, sellerId={}, error={}",
                    outbox.idValue(),
                    sellerId,
                    e.getMessage(),
                    e);

            outbox.recordFailure(true, e.getMessage(), now);
            outboxCommandManager.persist(outbox);
            return false;
        }
    }

    private boolean handleSuccess(
            SellerAdminEmailOutbox outbox, SellerAdminEmailSendResult result, Instant now) {
        log.info(
                "셀러 관리자 이메일 발송 성공: outboxId={}, sellerId={}, messageId={}",
                outbox.idValue(),
                outbox.sellerIdValue(),
                result.messageId());

        outbox.complete(now);
        outboxCommandManager.persist(outbox);
        return true;
    }

    private boolean handleFailure(
            SellerAdminEmailOutbox outbox, SellerAdminEmailSendResult result, Instant now) {
        String errorMessage = formatErrorMessage(result);

        if (result.retryable()) {
            log.warn(
                    "셀러 관리자 이메일 발송 실패 (재시도 예정): outboxId={}, sellerId={}, error={}",
                    outbox.idValue(),
                    outbox.sellerIdValue(),
                    errorMessage);
        } else {
            log.error(
                    "셀러 관리자 이메일 발송 영구 실패: outboxId={}, sellerId={}, error={}",
                    outbox.idValue(),
                    outbox.sellerIdValue(),
                    errorMessage);
        }

        outbox.recordFailure(result.retryable(), errorMessage, now);
        outboxCommandManager.persist(outbox);
        return false;
    }

    private String formatErrorMessage(SellerAdminEmailSendResult result) {
        return "[" + result.errorCode() + "] " + result.errorMessage();
    }
}
