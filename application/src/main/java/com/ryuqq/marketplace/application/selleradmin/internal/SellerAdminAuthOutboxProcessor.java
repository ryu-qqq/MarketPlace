package com.ryuqq.marketplace.application.selleradmin.internal;

import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminIdentityProvisioningResult;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxReadManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminEmailType;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 셀러 관리자 인증 Outbox 처리기.
 *
 * <p>비동기 이벤트 리스너 또는 스케줄러에서 호출됩니다.
 *
 * <p><strong>트랜잭션 전략</strong>:
 *
 * <ul>
 *   <li>PROCESSING 상태 변경: 별도 트랜잭션 (외부 API 호출 전 커밋 필요)
 *   <li>실패 시 상태 변경: 별도 트랜잭션 (실패 상태 즉시 커밋 필요)
 *   <li>성공 시 완료 처리: SellerAdminAuthCompletionFacade를 통해 원자적 처리
 * </ul>
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>PROCESSING 상태로 변경 (다른 프로세스와 충돌 방지)
 *   <li>Identity 서비스 API 호출 (사용자 등록)
 *   <li>결과에 따라 Outbox가 자체적으로 상태 전이 (COMPLETED/PENDING/FAILED)
 *   <li>성공 시 SellerAdmin에 authUserId 저장 (Facade를 통해 원자적 처리)
 * </ol>
 */
@Component
@ConditionalOnBean(SellerAdminIdentityClient.class)
public class SellerAdminAuthOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(SellerAdminAuthOutboxProcessor.class);

    private final SellerAdminAuthOutboxCommandManager outboxCommandManager;
    private final SellerAdminAuthOutboxReadManager outboxReadManager;
    private final SellerAdminReadManager sellerAdminReadManager;
    private final SellerAdminAuthCompletionFacade authCompletionFacade;
    private final SellerAdminIdentityClient identityClient;

    public SellerAdminAuthOutboxProcessor(
            SellerAdminAuthOutboxCommandManager outboxCommandManager,
            SellerAdminAuthOutboxReadManager outboxReadManager,
            SellerAdminReadManager sellerAdminReadManager,
            SellerAdminAuthCompletionFacade authCompletionFacade,
            SellerAdminIdentityClient identityClient) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.sellerAdminReadManager = sellerAdminReadManager;
        this.authCompletionFacade = authCompletionFacade;
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
    public boolean processOutbox(SellerAdminAuthOutbox outbox) {
        Instant now = Instant.now();
        String sellerAdminId = outbox.sellerAdminIdValue();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            SellerAdminIdentityProvisioningResult result =
                    identityClient.provisionSellerAdminIdentity(outbox);

            if (result.success()) {
                return handleSuccess(outbox, result, now);
            } else {
                return handleFailure(outbox, result, now);
            }

        } catch (Exception e) {
            log.error(
                    "셀러 관리자 인증 Outbox 처리 중 예외 발생: outboxId={}, sellerAdminId={}, error={}",
                    outbox.idValue(),
                    sellerAdminId,
                    e.getMessage(),
                    e);

            persistFailureWithReRead(outbox.idValue(), true, e.getMessage(), now);
            return false;
        }
    }

    private boolean handleSuccess(
            SellerAdminAuthOutbox outbox,
            SellerAdminIdentityProvisioningResult result,
            Instant now) {
        log.info(
                "Identity 프로비저닝 성공: sellerAdminId={}, authUserId={}",
                outbox.sellerAdminIdValue(),
                result.authUserId());

        SellerAdmin sellerAdmin = sellerAdminReadManager.getById(outbox.sellerAdminId());
        String emailPayload = buildEmailPayload(sellerAdmin, result);
        authCompletionFacade.completeAuthOutbox(
                outbox, sellerAdmin, result.authUserId(), emailPayload, now);

        return true;
    }

    private String buildEmailPayload(
            SellerAdmin sellerAdmin, SellerAdminIdentityProvisioningResult result) {
        return "{\"emailType\":\""
                + SellerAdminEmailType.SELLER_ADMIN_WELCOME.name()
                + "\""
                + ",\"sellerAdminId\":\""
                + sellerAdmin.idValue()
                + "\""
                + ",\"sellerId\":"
                + sellerAdmin.sellerIdValue()
                + ",\"authUserId\":\""
                + result.authUserId()
                + "\""
                + ",\"loginId\":\""
                + sellerAdmin.loginIdValue()
                + "\""
                + ",\"name\":\""
                + sellerAdmin.nameValue()
                + "\""
                + "}";
    }

    private boolean handleFailure(
            SellerAdminAuthOutbox outbox,
            SellerAdminIdentityProvisioningResult result,
            Instant now) {
        String errorMessage = formatErrorMessage(result);

        if (result.retryable()) {
            log.warn(
                    "Identity 프로비저닝 실패 (재시도 예정): sellerAdminId={}, error={}",
                    outbox.sellerAdminIdValue(),
                    errorMessage);
        } else {
            log.error(
                    "Identity 프로비저닝 영구 실패: sellerAdminId={}, error={}",
                    outbox.sellerAdminIdValue(),
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
            SellerAdminAuthOutbox freshOutbox = outboxReadManager.getById(outboxId);
            freshOutbox.recordFailure(retryable, errorMessage, now);
            outboxCommandManager.persist(freshOutbox);
        } catch (Exception reReadEx) {
            log.warn(
                    "Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    reReadEx.getMessage());
        }
    }

    private String formatErrorMessage(SellerAdminIdentityProvisioningResult result) {
        return "[" + result.errorCode() + "] " + result.errorMessage();
    }
}
