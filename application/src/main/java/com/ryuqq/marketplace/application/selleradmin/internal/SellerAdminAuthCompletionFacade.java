package com.ryuqq.marketplace.application.selleradmin.internal;

import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxReadManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminEmailOutboxCommandManager;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 셀러 관리자 인증 완료 Facade.
 *
 * <p>Outbox 완료, SellerAdmin 인증 정보 업데이트, 가입 완료 이메일 Outbox 생성을 하나의 트랜잭션으로 묶어 원자성을 보장합니다.
 *
 * <p>두 작업이 별도 트랜잭션으로 수행되면 데이터 불일치가 발생할 수 있습니다:
 *
 * <ul>
 *   <li>Outbox는 COMPLETED인데 SellerAdmin에 authUserId가 없는 상태
 *   <li>이 경우 재처리도 불가능하고 수동 복구가 필요
 * </ul>
 */
@Component
public class SellerAdminAuthCompletionFacade {

    private final SellerAdminAuthOutboxCommandManager outboxCommandManager;
    private final SellerAdminAuthOutboxReadManager outboxReadManager;
    private final SellerAdminCommandManager sellerAdminCommandManager;
    private final SellerAdminEmailOutboxCommandManager emailOutboxCommandManager;

    public SellerAdminAuthCompletionFacade(
            SellerAdminAuthOutboxCommandManager outboxCommandManager,
            SellerAdminAuthOutboxReadManager outboxReadManager,
            SellerAdminCommandManager sellerAdminCommandManager,
            SellerAdminEmailOutboxCommandManager emailOutboxCommandManager) {
        this.outboxCommandManager = outboxCommandManager;
        this.outboxReadManager = outboxReadManager;
        this.sellerAdminCommandManager = sellerAdminCommandManager;
        this.emailOutboxCommandManager = emailOutboxCommandManager;
    }

    /**
     * 인증 완료 처리를 원자적으로 수행합니다.
     *
     * <p>같은 트랜잭션에서 다음을 수행합니다:
     *
     * <ol>
     *   <li>AuthOutbox 상태를 COMPLETED로 변경
     *   <li>SellerAdmin에 authUserId 저장
     *   <li>가입 완료 안내 이메일 발송을 위한 EmailOutbox 생성
     * </ol>
     *
     * @param outbox 처리할 Outbox
     * @param sellerAdmin 업데이트할 셀러 관리자
     * @param authUserId 인증 서버 사용자 ID
     * @param emailPayload 이메일 발송용 JSON 페이로드
     * @param now 완료 시각
     */
    @Transactional
    public void completeAuthOutbox(
            SellerAdminAuthOutbox outbox,
            SellerAdmin sellerAdmin,
            String authUserId,
            String emailPayload,
            Instant now) {
        SellerAdminAuthOutbox freshOutbox = outboxReadManager.getById(outbox.idValue());
        freshOutbox.complete(now);
        outboxCommandManager.persist(freshOutbox);

        sellerAdmin.updateAuthUserId(authUserId, now);
        sellerAdminCommandManager.persist(sellerAdmin);

        SellerAdminEmailOutbox emailOutbox =
                SellerAdminEmailOutbox.forNew(sellerAdmin.sellerId(), emailPayload, now);
        emailOutboxCommandManager.persist(emailOutbox);
    }
}
