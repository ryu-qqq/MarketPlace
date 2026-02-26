package com.ryuqq.marketplace.application.selleradmin.listener;

import com.ryuqq.marketplace.application.selleradmin.internal.SellerAdminAuthOutboxProcessor;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxReadManager;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.event.SellerAdminApprovedEvent;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 셀러 관리자 가입 승인 이벤트 리스너.
 *
 * <p>셀러 관리자 가입 신청이 승인되면 인증 서버로 사용자 등록 요청을 처리합니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>이벤트 수신 (원본 트랜잭션 커밋 후 발행됨)
 *   <li>비동기 스레드에서 PENDING 상태 Outbox 조회
 *   <li>SellerAdminAuthOutboxProcessor로 처리 위임
 *   <li>실패 시 스케줄러에서 재시도
 * </ol>
 *
 * <p><strong>조건부 활성화</strong>: SellerAdminAuthOutboxProcessor가 존재할 때만 활성화됩니다. IdentityClient가 없는
 * 환경에서는 Processor와 함께 비활성화되어 스케줄러에서만 Outbox를 처리합니다.
 */
@Component
@ConditionalOnBean(SellerAdminAuthOutboxProcessor.class)
public class SellerAdminApprovedEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(SellerAdminApprovedEventListener.class);

    private final SellerAdminAuthOutboxReadManager outboxReadManager;
    private final SellerAdminAuthOutboxProcessor outboxProcessor;

    public SellerAdminApprovedEventListener(
            SellerAdminAuthOutboxReadManager outboxReadManager,
            SellerAdminAuthOutboxProcessor outboxProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.outboxProcessor = outboxProcessor;
    }

    /**
     * 셀러 관리자 가입 승인 이벤트를 비동기로 처리합니다.
     *
     * @param event 승인 이벤트
     */
    @Async
    @EventListener
    public void handleSellerAdminApproved(SellerAdminApprovedEvent event) {
        SellerAdminId sellerAdminId = event.sellerAdminId();

        log.info(
                "셀러 관리자 가입 승인 이벤트 수신: sellerAdminId={}, sellerId={}",
                sellerAdminId.value(),
                event.sellerId().value());

        Optional<SellerAdminAuthOutbox> outboxOpt =
                outboxReadManager.findPendingBySellerAdminId(sellerAdminId);

        if (outboxOpt.isEmpty()) {
            log.warn("처리할 Outbox가 없습니다: sellerAdminId={}", sellerAdminId.value());
            return;
        }

        outboxProcessor.processOutbox(outboxOpt.get());
    }
}
