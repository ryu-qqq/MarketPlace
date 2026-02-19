package com.ryuqq.marketplace.application.productgroupdescription.port.in.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;

/**
 * RegisterProductGroupDescriptionUseCase - 상품 그룹 상세 설명 등록 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface RegisterProductGroupDescriptionUseCase {

    /**
     * 상품 그룹 상세 설명을 등록합니다.
     *
     * @param command 등록할 상세 설명 Command
     * @return 생성된 descriptionId
     */
    Long execute(RegisterProductGroupDescriptionCommand command);
}
