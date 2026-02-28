package com.ryuqq.marketplace.application.legacyconversion.port.in.command;

import com.ryuqq.marketplace.application.legacyconversion.dto.command.SeedLegacyConversionCommand;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.SeedLegacyConversionResult;

/** 레거시 벌크 변환 시딩 유스케이스. */
public interface SeedLegacyConversionUseCase {

    /**
     * luxurydb의 활성 상품에 대해 PENDING outbox 엔트리를 시딩합니다.
     *
     * @param command 배치 크기, 최대 총 개수, 커서 정보
     * @return 시딩 결과 (스캔/생성/건너뛴 개수, 마지막 커서, 완료 여부)
     */
    SeedLegacyConversionResult execute(SeedLegacyConversionCommand command);
}
