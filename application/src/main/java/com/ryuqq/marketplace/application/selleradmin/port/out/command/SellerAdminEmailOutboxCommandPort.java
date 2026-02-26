package com.ryuqq.marketplace.application.selleradmin.port.out.command;

import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;

/**
 * 셀러 관리자 이메일 Outbox 명령 포트.
 *
 * <p>이메일 Outbox 영속화를 위한 아웃바운드 포트입니다.
 */
public interface SellerAdminEmailOutboxCommandPort {

    /**
     * 이메일 Outbox를 영속화합니다.
     *
     * @param outbox 영속화할 이메일 Outbox 도메인 객체
     * @return 영속화된 Outbox ID
     */
    Long persist(SellerAdminEmailOutbox outbox);
}
