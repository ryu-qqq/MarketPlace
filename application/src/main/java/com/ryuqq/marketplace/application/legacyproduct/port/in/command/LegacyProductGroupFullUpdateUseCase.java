package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductGroupCommand;

/** 레거시 상품그룹 전체 수정 UseCase. */
public interface LegacyProductGroupFullUpdateUseCase {

    /**
     * updateStatus 플래그에 따라 변경된 섹션만 선택적으로 레거시 DB에 직접 반영합니다.
     *
     * @param command 상품그룹 전체 수정 커맨드
     */
    void execute(LegacyUpdateProductGroupCommand command);
}
