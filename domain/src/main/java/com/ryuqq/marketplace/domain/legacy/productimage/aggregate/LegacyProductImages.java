package com.ryuqq.marketplace.domain.legacy.productimage.aggregate;

import com.ryuqq.marketplace.domain.legacy.productimage.vo.LegacyImageDiff;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 레거시 상품 이미지 컬렉션 래퍼.
 *
 * <p>기존 이미지 목록을 감싸며, 새로운 이미지 목록과의 diff 계산을 제공합니다. originUrl + imageType 기준으로 추가/삭제/유지를 판단합니다.
 */
public class LegacyProductImages {

    private final List<LegacyProductImage> images;

    public LegacyProductImages(List<LegacyProductImage> images) {
        this.images = List.copyOf(images);
    }

    /**
     * 새로운 이미지 목록과 비교하여 추가/삭제/유지를 판단하고 상태를 갱신합니다.
     *
     * <p>originUrl + imageType 조합을 키로 비교합니다. 유지 대상은 displayOrder를 갱신하고, 삭제 대상은 soft delete 처리합니다.
     *
     * @param newImages 새로운 이미지 목록
     * @param changedAt 변경 시각 (soft-delete 시각)
     * @return 변경 비교 결과 (added/removed/retained)
     */
    public LegacyImageDiff update(List<LegacyProductImage> newImages, Instant changedAt) {
        Map<String, LegacyProductImage> existingByKey =
                images.stream()
                        .collect(Collectors.toMap(LegacyProductImages::imageKey, img -> img));

        List<LegacyProductImage> added = new ArrayList<>();
        List<LegacyProductImage> retained = new ArrayList<>();
        Set<String> newKeys = new HashSet<>();

        for (LegacyProductImage newImage : newImages) {
            String key = imageKey(newImage);
            newKeys.add(key);

            LegacyProductImage existing = existingByKey.get(key);
            if (existing != null) {
                existing.updateDisplayOrder(newImage.displayOrder());
                retained.add(existing);
            } else {
                added.add(newImage);
            }
        }

        List<LegacyProductImage> removed =
                images.stream().filter(img -> !newKeys.contains(imageKey(img))).toList();

        for (LegacyProductImage image : removed) {
            image.delete(changedAt);
        }

        return LegacyImageDiff.of(added, removed, retained, changedAt);
    }

    public List<LegacyProductImage> images() {
        return images;
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    private static String imageKey(LegacyProductImage image) {
        return image.originUrl() + "::" + image.imageTypeName();
    }
}
