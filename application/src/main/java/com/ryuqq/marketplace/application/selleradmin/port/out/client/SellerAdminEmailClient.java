package com.ryuqq.marketplace.application.selleradmin.port.out.client;

import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminEmailSendResult;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;

/**
 * 외부 이메일 서비스 클라이언트 인터페이스.
 *
 * <p>셀러 관리자 초대/환영 이메일 발송을 위한 외부 서비스 연동을 추상화합니다.
 *
 * <p>구현체는 adapter-out 레이어에서 AWS SES 등 실제 이메일 서비스 호출을 담당합니다.
 */
public interface SellerAdminEmailClient {

    /**
     * 셀러 관리자 이메일을 발송합니다.
     *
     * <p>Outbox의 payload(수신자, 셀러 정보 등)와 멱등키를 사용하여 이메일 서비스에 요청합니다.
     *
     * @param outbox 처리할 이메일 Outbox
     * @return 발송 결과
     */
    SellerAdminEmailSendResult sendEmail(SellerAdminEmailOutbox outbox);
}
