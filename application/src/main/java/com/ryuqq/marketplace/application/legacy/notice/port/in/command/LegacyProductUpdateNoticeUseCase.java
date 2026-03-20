package com.ryuqq.marketplace.application.legacy.notice.port.in.command;

import com.ryuqq.marketplace.application.legacy.notice.dto.command.LegacyUpdateNoticeCommand;

/**
 * 레거시 상품 고시정보 수정 UseCase.
 *
 * <p>luxurydb에 flat 컬럼으로 저장합니다.
 * 새 스키마 전환 시 표준 UpdateProductNoticeCommand를 받도록 변경 예정.
 */
public interface LegacyProductUpdateNoticeUseCase {

    void execute(LegacyUpdateNoticeCommand command);
}
