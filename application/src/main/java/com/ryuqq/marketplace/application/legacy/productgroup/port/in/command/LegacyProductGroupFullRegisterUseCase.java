package com.ryuqq.marketplace.application.legacy.productgroup.port.in.command;

import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;

/**
 * 레거시 상품 등록 Use Case.
 *
 * <p>표준 RegisterProductGroupCommand를 받아 luxurydb에 저장 후,
 * 상품그룹 PK와 상품 목록을 반환합니다.
 */
public interface LegacyProductGroupFullRegisterUseCase {

    LegacyProductRegistrationResult execute(RegisterProductGroupCommand command);
}
