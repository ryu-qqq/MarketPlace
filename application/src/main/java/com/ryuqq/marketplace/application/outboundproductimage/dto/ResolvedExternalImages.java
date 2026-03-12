package com.ryuqq.marketplace.application.outboundproductimage.dto;

import java.util.Comparator;
import java.util.List;

/**
 * 외부 채널 이미지 결과 컬렉션.
 *
 * <p>thumbnailUrl과 detailUrls를 제공하여 매퍼에서 바로 사용할 수 있도록 합니다.
 */
public record ResolvedExternalImages(List<ResolvedExternalImage> images) {

    public ResolvedExternalImages {
        images = List.copyOf(images);
    }

    public static ResolvedExternalImages of(List<ResolvedExternalImage> images) {
        return new ResolvedExternalImages(images);
    }

    public static ResolvedExternalImages empty() {
        return new ResolvedExternalImages(List.of());
    }

    /** 썸네일 external URL. */
    public String thumbnailUrl() {
        return images.stream()
                .filter(ResolvedExternalImage::isThumbnail)
                .map(ResolvedExternalImage::externalUrl)
                .findFirst()
                .orElse(null);
    }

    /** 상세 이미지 external URL 목록 (sortOrder 정렬). */
    public List<String> detailUrls() {
        return images.stream()
                .filter(img -> !img.isThumbnail())
                .sorted(Comparator.comparingInt(ResolvedExternalImage::sortOrder))
                .map(ResolvedExternalImage::externalUrl)
                .toList();
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }
}
