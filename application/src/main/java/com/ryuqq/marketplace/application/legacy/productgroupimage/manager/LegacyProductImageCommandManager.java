package com.ryuqq.marketplace.application.legacy.productgroupimage.manager;

import com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.command.LegacyProductImageCommandPort;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품이미지 Command Manager. */
@Component
public class LegacyProductImageCommandManager {

    private final LegacyProductImageCommandPort commandPort;

    public LegacyProductImageCommandManager(LegacyProductImageCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persistAll(List<LegacyProductImage> images) {
        commandPort.persistAll(images);
    }

    @Transactional
    public void update(UpdateProductGroupImagesCommand command) {
        commandPort.update(command);
    }
}
