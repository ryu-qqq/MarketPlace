package com.ryuqq.marketplace.domain.outboundproductimage.vo;

import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OutboundProductImage 컬렉션 VO.
 *
 * <p>캐시된 외부 채널 이미지 목록을 관리하고,
 * 현재 ProductGroupImage 목록과 비교하여 diff를 생성합니다.
 */
public class OutboundProductImages {

    private final List<OutboundProductImage> images;

    private OutboundProductImages(List<OutboundProductImage> images) {
        this.images = images;
    }

    public static OutboundProductImages of(List<OutboundProductImage> images) {
        return new OutboundProductImages(new ArrayList<>(images));
    }

    public static OutboundProductImages empty() {
        return new OutboundProductImages(new ArrayList<>());
    }

    /**
     * 현재 ProductGroupImage 목록과 비교하여 diff를 생성합니다.
     *
     * <p>비교 키: resolvedInternalUrl(uploadedUrl 우선, 없으면 originUrl) + imageType
     * <p>내부 상태를 변경하지 않고 새 인스턴스를 반환합니다.
     *
     * @param currentImages 현재 상품 이미지 목록
     * @param outboundProductId 아웃바운드 상품 ID
     * @param now diff 발생 시각
     * @return 변경 비교 결과
     */
    public OutboundProductImageDiff diff(
            List<ProductGroupImage> currentImages, Long outboundProductId, Instant now) {

        Map<String, OutboundProductImage> existingByKey =
                images.stream()
                        .filter(img -> !img.isDeleted())
                        .collect(Collectors.toMap(
                                OutboundProductImage::imageKey, img -> img,
                                (a, b) -> {
                                    throw new IllegalStateException(
                                            "중복된 imageKey가 존재합니다: " + a.imageKey());
                                }));

        List<OutboundProductImage> added = new ArrayList<>();
        List<OutboundProductImage> retained = new ArrayList<>();
        Set<String> newKeys = new HashSet<>();

        for (ProductGroupImage current : currentImages) {
            String resolvedUrl = resolveInternalUrl(current);
            String key = resolvedUrl + "::" + current.imageTypeName();
            if (!newKeys.add(key)) {
                throw new IllegalStateException(
                        "currentImages에 중복된 imageKey가 존재합니다: " + key);
            }

            OutboundProductImage existing = existingByKey.get(key);
            if (existing != null) {
                retained.add(existing.withSortOrder(current.sortOrder()));
            } else {
                OutboundProductImage newImage = OutboundProductImage.forNew(
                        outboundProductId,
                        current.idValue(),
                        resolvedUrl,
                        current.imageType(),
                        current.sortOrder());
                added.add(newImage);
            }
        }

        List<OutboundProductImage> removed = images.stream()
                .filter(img -> !img.isDeleted() && !newKeys.contains(img.imageKey()))
                .map(img -> img.asDeleted(now))
                .toList();

        return OutboundProductImageDiff.of(added, removed, retained, now);
    }

    /** 활성 이미지만 반환. */
    public List<OutboundProductImage> activeImages() {
        return images.stream()
                .filter(img -> !img.isDeleted())
                .toList();
    }

    /** 썸네일 이미지의 external URL 반환. */
    public String thumbnailExternalUrl() {
        return images.stream()
                .filter(img -> !img.isDeleted() && img.isThumbnail())
                .map(OutboundProductImage::externalUrl)
                .findFirst()
                .orElse(null);
    }

    /** 상세 이미지의 external URL 목록 반환 (sortOrder 정렬). */
    public List<String> detailExternalUrls() {
        return images.stream()
                .filter(img -> !img.isDeleted() && !img.isThumbnail() && img.hasExternalUrl())
                .sorted((a, b) -> Integer.compare(a.sortOrder(), b.sortOrder()))
                .map(OutboundProductImage::externalUrl)
                .toList();
    }

    public List<OutboundProductImage> toList() {
        return Collections.unmodifiableList(images);
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    private String resolveInternalUrl(ProductGroupImage image) {
        String uploaded = image.uploadedUrlValue();
        return uploaded != null ? uploaded : image.originUrlValue();
    }
}
