package com.ryuqq.marketplace.application.productgroupimage.dto.response;

import java.util.List;

public record ProductGroupImageUploadStatusResult(
        Long productGroupId,
        int totalCount,
        int completedCount,
        int pendingCount,
        int processingCount,
        int failedCount,
        List<ImageUploadDetail> images) {

    public record ImageUploadDetail(
            Long imageId,
            String imageType,
            String originUrl,
            String uploadedUrl,
            String outboxStatus,
            int retryCount,
            String errorMessage) {}
}
