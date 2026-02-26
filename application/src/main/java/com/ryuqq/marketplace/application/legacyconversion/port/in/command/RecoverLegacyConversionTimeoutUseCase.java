package com.ryuqq.marketplace.application.legacyconversion.port.in.command;

/**
 * PROCESSING 타임아웃 복구 UseCase.
 *
 * <p>PROCESSING 상태에서 장시간 머문 Outbox를 PENDING으로 복구합니다.
 *
 * @return 복구 건수
 */
public interface RecoverLegacyConversionTimeoutUseCase {
    int execute();
}
