package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.response.LegacyProductRegistrationResult;

/**
 * 레거시 상품 등록 Use Case.
 *
 * <p>세토프 어드민용 상품 등록 진입점. luxurydb에 직접 저장 후, 상품그룹 PK와 상품 목록을 반환합니다.
 */
public interface LegacyProductGroupFullRegisterUseCase {

    /**
     * 레거시 상품 등록을 실행합니다.
     *
     * @param command 레거시 상품그룹 등록 커맨드
     * @return 등록된 상품그룹 PK와 상품 목록
     */
    LegacyProductRegistrationResult execute(LegacyRegisterProductGroupCommand command);
}
