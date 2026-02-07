package com.ryuqq.marketplace.application.selleradmin.port.out.client;

import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminIdentityProvisioningResult;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;

/**
 * 외부 인증/Identity 서비스 클라이언트 인터페이스.
 *
 * <p>셀러 관리자 사용자 등록 등 외부 인증 서비스 연동을 추상화합니다.
 *
 * <p>구현체는 adapter-out 레이어에서 payload 파싱 및 실제 SDK 호출을 담당합니다.
 */
public interface SellerAdminIdentityClient {

    /**
     * 셀러 관리자용 사용자를 인증 서버에 등록합니다.
     *
     * <p>Outbox의 payload와 멱등키를 사용하여 Identity 서비스에 요청합니다.
     *
     * @param outbox 처리할 Outbox
     * @return 등록 결과
     */
    SellerAdminIdentityProvisioningResult provisionSellerAdminIdentity(
            SellerAdminAuthOutbox outbox);

    /**
     * 셀러 관리자의 비밀번호를 초기화합니다.
     *
     * <p>인증 서버에서 임시 비밀번호를 생성하고 사용자에게 전달합니다.
     *
     * @param authUserId 인증 서버 사용자 ID
     */
    void resetSellerAdminPassword(String authUserId);

    /**
     * 셀러 관리자의 비밀번호를 새 비밀번호로 변경합니다.
     *
     * <p>본인인증(이메일/문자 등)은 프론트 또는 외부에서 완료된 후 호출됩니다. 현재 비밀번호는 사용하지 않습니다.
     *
     * @param authUserId 인증 서버 사용자 ID
     * @param newPassword 새 비밀번호
     */
    void changeSellerAdminPassword(String authUserId, String newPassword);
}
