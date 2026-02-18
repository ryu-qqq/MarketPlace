package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.response;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import java.util.List;

public record DescriptionPublishStatusApiResponse(
        Long productGroupId,
        Long descriptionId,
        String publishStatus,
        String cdnPath,
        int totalImageCount,
        int completedImageCount,
        int pendingImageCount,
        int failedImageCount,
        List<DescriptionImageUploadDetailResponse> images) {
    public record DescriptionImageUploadDetailResponse(
            Long imageId,
            String originUrl,
            String uploadedUrl,
            String outboxStatus,
            int retryCount,
            String errorMessage) {}

    public static DescriptionPublishStatusApiResponse from(DescriptionPublishStatusResult result) {
        return new DescriptionPublishStatusApiResponse(
                result.productGroupId(),
                result.descriptionId(),
                result.publishStatus(),
                result.cdnPath(),
                result.totalImageCount(),
                result.completedImageCount(),
                result.pendingImageCount(),
                result.failedImageCount(),
                result.images().stream()
                        .map(
                                detail ->
                                        new DescriptionImageUploadDetailResponse(
                                                detail.imageId(),
                                                detail.originUrl(),
                                                detail.uploadedUrl(),
                                                detail.outboxStatus(),
                                                detail.retryCount(),
                                                detail.errorMessage()))
                        .toList());
    }
}
