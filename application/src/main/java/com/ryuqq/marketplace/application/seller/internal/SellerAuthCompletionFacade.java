package com.ryuqq.marketplace.application.seller.internal;

import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxReadManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminEmailOutboxCommandManager;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 셀러 인증 완료 Facade.
 *
 * <p>Outbox 완료와 Seller 인증 정보 업데이트, 이메일 Outbox 생성을 하나의 트랜잭션으로 묶어 원자성을 보장합니다.
 *
 * <p>두 작업이 별도 트랜잭션으로 수행되면 데이터 불일치가 발생할 수 있습니다:
 *
 * <ul>
 *   <li>Outbox는 COMPLETED인데 Seller에 tenantId/organizationId가 없는 상태
 *   <li>이 경우 재처리도 불가능하고 수동 복구가 필요
 * </ul>
 */
@Component
public class SellerAuthCompletionFacade {

    private final SellerAuthOutboxCommandManager outboxCommandManager;
    private final SellerAuthOutboxReadManager outboxReadManager;
    private final SellerCommandManager sellerCommandManager;
    private final SellerAdminEmailOutboxCommandManager emailOutboxCommandManager;

    public SellerAuthCompletionFacade(
            SellerAuthOutboxCommandManager outboxCommandManager,
            SellerAuthOutboxReadManager outboxReadManager,
            SellerCommandManager sellerCommandManager,
            SellerAdminEmailOutboxCommandManager emailOutboxCommandManager) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.sellerCommandManager = sellerCommandManager;
        this.emailOutboxCommandManager = emailOutboxCommandManager;
    }

    /**
     * 인증 완료 처리를 원자적으로 수행합니다.
     *
     * <p>같은 트랜잭션에서 다음을 수행합니다:
     *
     * <ol>
     *   <li>AuthOutbox 상태를 COMPLETED로 변경
     *   <li>Seller에 인증 정보(tenantId, organizationId) 저장
     *   <li>초대 이메일 발송을 위한 EmailOutbox 생성
     * </ol>
     *
     * @param outbox 처리할 Outbox
     * @param tenantId 인증 서버 테넌트 ID
     * @param organizationId 인증 서버 조직 ID
     * @param emailPayload 이메일 발송용 JSON 페이로드
     * @param now 완료 시각
     */
    @Transactional
    public void completeAuthOutbox(
            SellerAuthOutbox outbox,
            String tenantId,
            String organizationId,
            String emailPayload,
            Instant now) {
        SellerAuthOutbox freshOutbox = outboxReadManager.getById(outbox.idValue());
        freshOutbox.complete(now);
        outboxCommandManager.persist(freshOutbox);
        sellerCommandManager.updateAuthInfo(freshOutbox.sellerId(), tenantId, organizationId);

        SellerAdminEmailOutbox emailOutbox =
                SellerAdminEmailOutbox.forNew(freshOutbox.sellerId(), emailPayload, now);
        emailOutboxCommandManager.persist(emailOutbox);
    }
}
