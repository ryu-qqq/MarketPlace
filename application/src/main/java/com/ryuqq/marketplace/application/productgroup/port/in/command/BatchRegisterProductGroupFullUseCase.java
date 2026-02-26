package com.ryuqq.marketplace.application.productgroup.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import java.util.List;

/**
 * 상품 그룹 배치 등록 UseCase.
 *
 * <p>여러 상품 그룹을 병렬로 등록하고 개별 성공/실패 결과를 반환합니다.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface BatchRegisterProductGroupFullUseCase {

    /**
     * 상품 그룹을 배치로 등록합니다.
     *
     * <p>각 항목은 독립 트랜잭션으로 처리되며, 일부 실패 시 나머지는 정상 등록됩니다.
     *
     * @param commands 등록 Command 목록
     * @return 배치 처리 결과 (성공/실패 건수 + 개별 항목 결과)
     */
    BatchProcessingResult<Long> execute(List<RegisterProductGroupCommand> commands);
}
