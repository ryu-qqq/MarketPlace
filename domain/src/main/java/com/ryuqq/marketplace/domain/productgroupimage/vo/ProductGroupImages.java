package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNoThumbnailException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    /** 신규 생성 또는 수정 시 사용. 검증 + 정렬 적용. */
    public static ProductGroupImages of(List<ProductGroupImage> images) {
        validate(images);
        List<ProductGroupImage> sorted = sort(images);
        return new ProductGroupImages(sorted);
    }

    /** 영속성에서 복원 시 사용. 검증 스킵. */
    public static ProductGroupImages reconstitute(List<ProductGroupImage> images) {
        return new ProductGroupImages(List.copyOf(images));
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
        return images.get(0);
    }

    public List<ProductGroupImage> detailImages() {
        if (images.size() <= 1) {
            return List.of();
        }
        return Collections.unmodifiableList(images.subList(1, images.size()));
    }

    public int size() {
        return images.size();
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }
}
