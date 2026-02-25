package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyImageCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateImagesUseCase;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 이미지 수정 서비스.
 *
 * <p>Factory로 도메인 객체를 생성한 뒤 LegacyImageCoordinator에 위임하여 diff 기반 이미지 업데이트를 수행합니다.
 */
@Service
public class LegacyProductUpdateImagesService implements LegacyProductUpdateImagesUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyImageCoordinator imageCoordinator;

    public LegacyProductUpdateImagesService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyImageCoordinator imageCoordinator) {
        this.commandFactory = commandFactory;
        this.imageCoordinator = imageCoordinator;
    }

    @Override
    public void execute(LegacyUpdateImagesCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());
        List<LegacyProductImage> newImages =
                commandFactory.createImagesForUpdate(groupId, command.images());
        imageCoordinator.updateImages(groupId, newImages);
    }
}
