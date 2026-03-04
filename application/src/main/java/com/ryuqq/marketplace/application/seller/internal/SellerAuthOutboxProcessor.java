package com.ryuqq.marketplace.application.seller.internal;

import com.ryuqq.marketplace.application.seller.dto.response.SellerIdentityProvisioningResult;
import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxReadManager;
import com.ryuqq.marketplace.application.seller.port.out.client.IdentityClient;
import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminEmailType;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 셀러 인증 Outbox 처리기.
 *
 * <p>비동기 이벤트 리스너 또는 스케줄러에서 호출됩니다.
 *
 * <p><strong>트랜잭션 전략</strong>:
 *
 * <ul>
 *   <li>PROCESSING 상태 변경: 별도 트랜잭션 (외부 API 호출 전 커밋 필요)
 *   <li>실패 시 상태 변경: 별도 트랜잭션 (실패 상태 즉시 커밋 필요)
 *   <li>성공 시 완료 처리: SellerAuthCompletionFacade를 통해 원자적 처리
 * </ul>
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>PROCESSING 상태로 변경 (다른 프로세스와 충돌 방지)
 *   <li>Identity 서비스 API 호출 (Tenant/Organization 생성)
 *   <li>결과에 따라 Outbox가 자체적으로 상태 전이 (COMPLETED/PENDING/FAILED)
 *   <li>성공 시 Seller에 tenantId, organizationId 저장 (Facade를 통해 원자적 처리)
 * </ol>
 */
@Component
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class SellerAuthOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(SellerAuthOutboxProcessor.class);

    private final SellerAuthOutboxCommandManager outboxCommandManager;
    private final SellerAuthOutboxReadManager outboxReadManager;
    private final SellerAuthCompletionFacade authCompletionFacade;
    private final SellerApplicationReadManager sellerApplicationReadManager;
    private final IdentityClient identityClient;

    public SellerAuthOutboxProcessor(
            SellerAuthOutboxCommandManager outboxCommandManager,
            SellerAuthOutboxReadManager outboxReadManager,
            SellerAuthCompletionFacade authCompletionFacade,
            SellerApplicationReadManager sellerApplicationReadManager,
            IdentityClient identityClient) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.authCompletionFacade = authCompletionFacade;
        this.sellerApplicationReadManager = sellerApplicationReadManager;
        this.identityClient = identityClient;
    }

    /**
     * 단일 Outbox를 처리합니다.
     *
     * <p>이벤트 리스너 또는 스케줄러에서 호출됩니다.
     *
     * @param outbox 처리할 Outbox
     * @return 처리 성공 여부
     */
    public boolean processOutbox(SellerAuthOutbox outbox) {
        Instant now = Instant.now();
        Long sellerId = outbox.sellerIdValue();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            SellerIdentityProvisioningResult result =
                    identityClient.provisionSellerIdentity(outbox);

            if (result.success()) {
                return handleSuccess(outbox, result, now);
            } else {
                return handleFailure(outbox, result, now);
            }

        } catch (Exception e) {
            log.error(
                    "셀러 인증 Outbox 처리 중 예외 발생: outboxId={}, sellerId={}, error={}",
                    outbox.idValue(),
                    sellerId,
                    e.getMessage(),
                    e);

            persistFailureWithReRead(outbox.idValue(), true, e.getMessage(), now);
            return false;
        }
    }

    private boolean handleSuccess(
            SellerAuthOutbox outbox, SellerIdentityProvisioningResult result, Instant now) {
        log.info(
                "Identity 프로비저닝 성공: sellerId={}, tenantId={}, orgId={}",
                outbox.sellerIdValue(),
                result.tenantId(),
                result.organizationId());

        String emailPayload = buildEmailPayload(outbox);
        authCompletionFacade.completeAuthOutbox(
                outbox, result.tenantId(), result.organizationId(), emailPayload, now);

        return true;
    }

    private String buildEmailPayload(SellerAuthOutbox outbox) {
        SellerApplication application =
                sellerApplicationReadManager.getByApprovedSellerId(outbox.sellerId());

        return "{\"emailType\":\""
                + SellerAdminEmailType.SELLER_APPROVAL_INVITE.name()
                + "\",\"sellerId\":"
                + outbox.sellerIdValue()
                + ",\"sellerName\":\""
                + application.sellerNameValue()
                + "\",\"contactEmail\":\""
                + application.contactInfoEmail()
                + "\"}";
    }

    private boolean handleFailure(
            SellerAuthOutbox outbox, SellerIdentityProvisioningResult result, Instant now) {
        String errorMessage = formatErrorMessage(result);

        if (result.retryable()) {
            log.warn(
                    "Identity 프로비저닝 실패 (재시도 예정): sellerId={}, error={}",
                    outbox.sellerIdValue(),
                    errorMessage);
        } else {
            log.error(
                    "Identity 프로비저닝 영구 실패: sellerId={}, error={}",
                    outbox.sellerIdValue(),
                    errorMessage);
        }

        persistFailureWithReRead(outbox.idValue(), result.retryable(), errorMessage, now);

        return false;
    }

    /**
     * DB에서 최신 Outbox를 다시 읽은 뒤 실패 상태를 기록합니다.
     *
     * <p>낙관적 락 충돌 방지: 첫 번째 persist(PROCESSING) 이후 recoverTimeout 스케줄러가 버전을 올릴 수 있으므로, 두 번째 persist
     * 전에 최신 버전을 조회합니다.
     */
    private void persistFailureWithReRead(
            Long outboxId, boolean retryable, String errorMessage, Instant now) {
        try {
            SellerAuthOutbox freshOutbox = outboxReadManager.getById(outboxId);
            freshOutbox.recordFailure(retryable, errorMessage, now);
            outboxCommandManager.persist(freshOutbox);
        } catch (Exception reReadEx) {
            log.warn(
                    "Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    reReadEx.getMessage());
        }
    }

    private String formatErrorMessage(SellerIdentityProvisioningResult result) {
        return "[" + result.errorCode() + "] " + result.errorMessage();
    }
}
