package com.ryuqq.marketplace.domain.productgroupimage.vo;

import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNoThumbnailException;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 상품 그룹 이미지 컬렉션 VO.
 *
 * <p>불변식: THUMBNAIL 이미지가 정확히 1개 존재하고 sortOrder 0으로 배치. DETAIL 이미지는 sortOrder 1부터 순서대로 정렬.
 */
public class ProductGroupImages {

    private final List<ProductGroupImage> images;

    private ProductGroupImages(List<ProductGroupImage> images) {
        this.images = images;
    }

    /** 신규 생성 또는 수정 시 사용. 중복 제거 + 검증 + 정렬 적용. */
    public static ProductGroupImages of(List<ProductGroupImage> images) {
        List<ProductGroupImage> deduped = deduplicate(images);
        validate(deduped);
        List<ProductGroupImage> sorted = sort(deduped);
        return new ProductGroupImages(sorted);
    }

    /** 영속성에서 복원 시 사용. 검증 스킵. */
    public static ProductGroupImages reconstitute(List<ProductGroupImage> images) {
        return new ProductGroupImages(List.copyOf(images));
    }

    // === 중복 제거 ===

    private static List<ProductGroupImage> deduplicate(List<ProductGroupImage> images) {
        LinkedHashMap<String, ProductGroupImage> seen = new LinkedHashMap<>();
        for (ProductGroupImage img : images) {
            seen.putIfAbsent(imageKey(img), img);
        }
        return new ArrayList<>(seen.values());
    }

    // === 검증 ===

    private static void validate(List<ProductGroupImage> images) {
        long thumbnailCount = 0;
        for (ProductGroupImage image : images) {
            if (image.isThumbnail()) {
                thumbnailCount++;
            }
        }

        if (thumbnailCount != 1) {
            throw new ProductGroupNoThumbnailException(thumbnailCount);
        }
    }

    // === 정렬 ===

    private static List<ProductGroupImage> sort(List<ProductGroupImage> images) {
        ArrayList<ProductGroupImage> sorted = new ArrayList<>(images);
        Comparator<ProductGroupImage> comparator =
                Comparator.comparingInt((ProductGroupImage img) -> img.isThumbnail() ? 0 : 1);
        comparator = comparator.thenComparingInt(ProductGroupImage::sortOrder);
        sorted.sort(comparator);

        int sortOrder = 0;
        for (ProductGroupImage image : sorted) {
            image.updateSortOrder(sortOrder++);
        }

        return sorted;
    }

    // === 조회 ===

    public List<ProductGroupImage> toList() {
        return Collections.unmodifiableList(images);
    }

    public ProductGroupImage thumbnail() {
        return images.getFirst();
    }

    public List<ProductGroupImage> detailImages() {
        if (images.size() <= 1) {
            return List.of();
        }
        return Collections.unmodifiableList(images.subList(1, images.size()));
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    // === Update ===

    /**
     * 새 이미지 목록과 비교하여 추가/삭제/유지를 판단하고 상태를 갱신합니다.
     *
     * <p>originUrl + imageType 조합을 키로 비교합니다. 유지 대상은 sortOrder를 갱신하고, 삭제 대상은 soft delete 처리합니다.
     *
     * @param updateData 수정할 새 이미지 목록과 수정 시각
     * @return 변경 비교 결과
     */
    public ProductGroupImageDiff update(ProductGroupImageUpdateData updateData) {
        Map<String, ProductGroupImage> existingByKey =
                images.stream()
                        .collect(
                                Collectors.toMap(
                                        ProductGroupImages::imageKey, img -> img, (a, b) -> a));

        List<ProductGroupImage> added = new ArrayList<>();
        List<ProductGroupImage> retained = new ArrayList<>();
        Set<String> newKeys = new HashSet<>();

        for (ProductGroupImage newImage : updateData.newImages().toList()) {
            String key = imageKey(newImage);
            newKeys.add(key);

            ProductGroupImage existing = existingByKey.get(key);
            if (existing != null) {
                existing.updateSortOrder(newImage.sortOrder());
                retained.add(existing);
            } else {
                added.add(newImage);
            }
        }

        List<ProductGroupImage> removed =
                images.stream().filter(img -> !newKeys.contains(imageKey(img))).toList();

        for (ProductGroupImage image : removed) {
            image.delete(updateData.updatedAt());
        }

        return ProductGroupImageDiff.of(added, removed, retained, updateData.updatedAt());
    }

    private static String imageKey(ProductGroupImage image) {
        return image.originUrlValue() + "::" + image.imageTypeName();
    }
}
