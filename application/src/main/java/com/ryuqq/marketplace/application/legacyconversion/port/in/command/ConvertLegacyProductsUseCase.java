package com.ryuqq.marketplace.application.legacyconversion.port.in.command;

/**
 * 레거시 상품 변환 UseCase.
 *
 * <p>PENDING 상태의 Outbox를 조회하여 내부 상품으로 변환합니다.
 *
 * @return 변환 성공 건수
 */
public interface ConvertLegacyProductsUseCase {
    int execute();
}
