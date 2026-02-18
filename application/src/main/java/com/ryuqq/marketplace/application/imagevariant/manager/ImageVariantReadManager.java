package com.ryuqq.marketplace.application.imagevariant.manager;

import com.ryuqq.marketplace.application.imagevariant.port.out.query.ImageVariantQueryPort;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ImageVariant Read Manager. */
@Component
public class ImageVariantReadManager {

    private final ImageVariantQueryPort queryPort;

    public ImageVariantReadManager(ImageVariantQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<ImageVariant> findBySourceImageId(Long sourceImageId, ImageSourceType sourceType) {
        return queryPort.findBySourceImageId(sourceImageId, sourceType);
    }
}
