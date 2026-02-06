package com.ryuqq.marketplace.application.seller.port.out.client;

import com.ryuqq.marketplace.application.seller.dto.response.SellerIdentityProvisioningResult;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;

/**
 * 외부 인증/Identity 서비스 클라이언트 인터페이스.
 *
 * <p>Tenant/Organization 생성 등 외부 인증 서비스 연동을 추상화합니다.
 *
 * <p>구현체는 adapter-out 레이어에서 payload 파싱 및 실제 SDK 호출을 담당합니다.
 */
public interface IdentityClient {

    /**
     * 셀러용 Tenant와 Organization을 생성합니다.
     *
     * <p>Outbox의 payload와 멱등키를 사용하여 Identity 서비스에 요청합니다.
     *
     * @param outbox 처리할 Outbox
     * @return 생성 결과
     */
    SellerIdentityProvisioningResult provisionSellerIdentity(SellerAuthOutbox outbox);
}
