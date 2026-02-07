package com.ryuqq.marketplace.application.selleradmin.port.out.command;

import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;

/**
 * 셀러 관리자 인증 Outbox Command Port.
 *
 * <p>인증 서버 연동용 Outbox 저장/수정을 위한 포트입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface SellerAdminAuthOutboxCommandPort {

    /**
     * Outbox를 저장합니다.
     *
     * @param outbox 저장할 Outbox 도메인 객체
     * @return 저장된 Outbox ID
     */
    Long persist(SellerAdminAuthOutbox outbox);
}
