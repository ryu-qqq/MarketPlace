package com.ryuqq.marketplace.application.productgroupdescription.dto.response;

import java.util.List;

public record DescriptionPublishStatusResult(
        Long productGroupId,
        Long descriptionId,
        String publishStatus,
        String cdnPath,
        int totalImageCount,
        int completedImageCount,
        int pendingImageCount,
        int failedImageCount,
        List<DescriptionImageUploadDetail> images) {
    public record DescriptionImageUploadDetail(
            Long imageId,
            String originUrl,
            String uploadedUrl,
            String outboxStatus,
            int retryCount,
            String errorMessage) {}

    public static DescriptionPublishStatusResult empty(Long productGroupId) {
        return new DescriptionPublishStatusResult(
                productGroupId, null, null, null, 0, 0, 0, 0, List.of());
    }
}
