package com.ryuqq.marketplace.domain.legacy.productimage.aggregate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 레거시 상품 이미지 컬렉션 래퍼.
 *
 * <p>기존 이미지 목록을 감싸며, 새로운 이미지 목록과의 diff 계산을 제공합니다. displayOrder + originUrl 기준으로 변경/추가/삭제를 판단합니다.
 */
public class LegacyProductImages {

    private final List<LegacyProductImage> images;

    public LegacyProductImages(List<LegacyProductImage> images) {
        this.images = List.copyOf(images);
    }

    /**
     * 새로운 이미지 목록과 비교하여 persist할 이미지 목록을 반환합니다.
     *
     * @param newImages 새로운 이미지 목록
     * @param changedAt 변경 시각 (soft-delete 시각)
     * @return persist 대상 이미지 목록 (soft-delete 대상 + 신규 insert 대상)
     */
    public ImageDiffResult diff(List<LegacyProductImage> newImages, Instant changedAt) {
        Map<Integer, LegacyProductImage> existingByOrder = new LinkedHashMap<>();
        for (LegacyProductImage image : images) {
            existingByOrder.put(image.displayOrder(), image);
        }

        List<LegacyProductImage> toPersist = new ArrayList<>();

        for (LegacyProductImage newImage : newImages) {
            int order = newImage.displayOrder();
            LegacyProductImage existing = existingByOrder.remove(order);

            if (existing == null) {
                toPersist.add(newImage);
            } else {
                String existingUrl = existing.originUrl();
                String newUrl = newImage.originUrl();
                if (!existingUrl.equals(newUrl)) {
                    existing.delete(changedAt);
                    toPersist.add(existing);
                    toPersist.add(newImage);
                }
            }
        }

        for (LegacyProductImage remaining : existingByOrder.values()) {
            remaining.delete(changedAt);
            toPersist.add(remaining);
        }

        return new ImageDiffResult(toPersist);
    }

    public List<LegacyProductImage> images() {
        return images;
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    /** 이미지 diff 결과. */
    public record ImageDiffResult(List<LegacyProductImage> toPersist) {

        public boolean hasChanges() {
            return !toPersist.isEmpty();
        }
    }
}
