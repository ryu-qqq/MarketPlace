package com.ryuqq.marketplace.application.legacy.productgroupimage.service.command;

import com.ryuqq.marketplace.application.legacy.productgroupimage.manager.LegacyProductImageCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command.LegacyProductUpdateImagesUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 이미지 수정 서비스.
 *
 * <p>표준 커맨드 → 표준 도메인 객체 생성 → 레거시 Port에 위임하여 luxurydb에 저장합니다.
 */
@Service
public class LegacyProductUpdateImagesService implements LegacyProductUpdateImagesUseCase {

    private final LegacyProductImageCommandManager imageCommandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateImagesService(
            LegacyProductImageCommandManager imageCommandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.imageCommandManager = imageCommandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(UpdateProductGroupImagesCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());

        List<ProductGroupImage> images =
                command.images().stream()
                        .map(
                                img ->
                                        ProductGroupImage.forNew(
                                                productGroupId,
                                                ImageUrl.of(img.originUrl()),
                                                ImageType.valueOf(img.imageType()),
                                                img.sortOrder()))
                        .toList();

        imageCommandManager.replaceAll(command.productGroupId(), images);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
