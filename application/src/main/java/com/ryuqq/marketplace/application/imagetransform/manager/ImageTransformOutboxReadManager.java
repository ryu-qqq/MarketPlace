package com.ryuqq.marketplace.application.imagetransform.manager;

import com.ryuqq.marketplace.application.imagetransform.port.out.query.ImageTransformOutboxQueryPort;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ImageTransformOutbox Read Manager. */
@Component
public class ImageTransformOutboxReadManager {

    private final ImageTransformOutboxQueryPort queryPort;

    public ImageTransformOutboxReadManager(ImageTransformOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<ImageTransformOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxes(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<ImageTransformOutbox> findProcessingOutboxes(int limit) {
        return queryPort.findProcessingOutboxes(limit);
    }

    @Transactional(readOnly = true)
    public List<ImageTransformOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit);
    }

    @Transactional(readOnly = true)
    public Map<Long, Set<ImageVariantType>> findActiveVariantTypesBySourceImageIds(
            List<Long> sourceImageIds, List<ImageVariantType> variantTypes) {
        return queryPort.findActiveVariantTypesBySourceImageIds(sourceImageIds, variantTypes);
    }
}
