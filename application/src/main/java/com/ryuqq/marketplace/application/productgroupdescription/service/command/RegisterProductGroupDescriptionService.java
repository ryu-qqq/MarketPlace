package com.ryuqq.marketplace.application.productgroupdescription.service.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.command.RegisterProductGroupDescriptionUseCase;
import org.springframework.stereotype.Service;

/**
 * RegisterProductGroupDescriptionService - 상품 그룹 상세 설명 등록 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class RegisterProductGroupDescriptionService
        implements RegisterProductGroupDescriptionUseCase {

    private final DescriptionCommandCoordinator descriptionCommandCoordinator;

    public RegisterProductGroupDescriptionService(
            DescriptionCommandCoordinator descriptionCommandCoordinator) {
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
    }

    @Override
    public Long execute(RegisterProductGroupDescriptionCommand command) {
        return descriptionCommandCoordinator.register(command);
    }
}
