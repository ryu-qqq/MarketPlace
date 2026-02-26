package com.ryuqq.marketplace.application.selleradmin.manager;

import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminAuthOutboxCommandPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import org.springframework.stereotype.Component;

/**
 * SellerAdminAuthOutbox Command Manager.
 *
 * <p>셀러 관리자 인증 Outbox 저장을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminAuthOutboxCommandManager {

    private final SellerAdminAuthOutboxCommandPort commandPort;

    public SellerAdminAuthOutboxCommandManager(SellerAdminAuthOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * Outbox를 저장합니다.
     *
     * @param outbox 저장할 Outbox 도메인 객체
     * @return 저장된 Outbox ID
     */
    public Long persist(SellerAdminAuthOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
