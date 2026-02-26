package com.ryuqq.marketplace.application.selleradmin.manager;

import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminEmailOutboxCommandPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import org.springframework.stereotype.Component;

/**
 * SellerAdminEmailOutbox Command Manager.
 *
 * <p>셀러 관리자 이메일 Outbox 저장을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminEmailOutboxCommandManager {

    private final SellerAdminEmailOutboxCommandPort commandPort;

    public SellerAdminEmailOutboxCommandManager(SellerAdminEmailOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 이메일 Outbox를 저장합니다.
     *
     * @param outbox 저장할 이메일 Outbox 도메인 객체
     * @return 저장된 Outbox ID
     */
    public Long persist(SellerAdminEmailOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
