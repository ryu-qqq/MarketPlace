package com.ryuqq.marketplace.application.imagevariantsync.manager;

import com.ryuqq.marketplace.application.imagevariantsync.port.out.query.ImageVariantSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ImageVariantSyncOutbox Read Manager. */
@Component
public class ImageVariantSyncOutboxReadManager {

    private final ImageVariantSyncOutboxQueryPort queryPort;

    public ImageVariantSyncOutboxReadManager(ImageVariantSyncOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<ImageVariantSyncOutbox> findPendingOutboxes(int limit) {
        return queryPort.findPendingOutboxes(limit);
    }

    @Transactional(readOnly = true)
    public boolean existsPendingBySourceImageId(long sourceImageId) {
        return queryPort.existsPendingBySourceImageId(sourceImageId);
    }
}
