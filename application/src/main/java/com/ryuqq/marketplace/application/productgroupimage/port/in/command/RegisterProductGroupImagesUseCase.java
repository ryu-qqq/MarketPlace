package com.ryuqq.marketplace.application.productgroupimage.port.in.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import java.util.List;

/**
 * RegisterProductGroupImagesUseCase - 상품 그룹 이미지 등록 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface RegisterProductGroupImagesUseCase {

    /**
     * 상품 그룹 이미지를 등록합니다.
     *
     * @param command 등록할 이미지 Command
     * @return 생성된 이미지 ID 목록
     */
    List<Long> execute(RegisterProductGroupImagesCommand command);
}
