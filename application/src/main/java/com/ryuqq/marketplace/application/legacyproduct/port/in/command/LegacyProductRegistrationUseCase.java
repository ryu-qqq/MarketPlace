package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.response.LegacyProductRegistrationResult;

/**
 * 레거시 상품 등록 Use Case.
 *
 * <p>세토프 어드민용 상품 등록 진입점. InboundProduct 파이프라인을 통해 등록 후, 상품 그룹과 활성화된 상품 목록을 반환합니다.
 */
public interface LegacyProductRegistrationUseCase {

    /**
     * 레거시 상품 등록을 실행합니다.
     *
     * @param command 인바운드 상품 수신 커맨드
     * @return 상품 그룹과 상품 목록 (변환 대기 시 productGroup null, products 비움)
     */
    LegacyProductRegistrationResult execute(ReceiveInboundProductCommand command);
}
