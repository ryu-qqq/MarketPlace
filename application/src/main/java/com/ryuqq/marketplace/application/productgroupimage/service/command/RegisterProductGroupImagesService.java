package com.ryuqq.marketplace.application.productgroupimage.service.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.factory.ProductGroupImageFactory;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.port.in.command.RegisterProductGroupImagesUseCase;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * RegisterProductGroupImagesService - 상품 그룹 이미지 등록 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class RegisterProductGroupImagesService implements RegisterProductGroupImagesUseCase {

    private final ProductGroupImageFactory imageFactory;
    private final ImageCommandCoordinator imageCommandCoordinator;

    public RegisterProductGroupImagesService(
            ProductGroupImageFactory imageFactory,
            ImageCommandCoordinator imageCommandCoordinator) {
        this.imageFactory = imageFactory;
        this.imageCommandCoordinator = imageCommandCoordinator;
    }

    @Override
    public List<Long> execute(RegisterProductGroupImagesCommand command) {
        ProductGroupImages images =
                imageFactory.createFromImageRegistration(
                        ProductGroupId.of(command.productGroupId()), command.images());
        return imageCommandCoordinator.register(images);
    }
}
