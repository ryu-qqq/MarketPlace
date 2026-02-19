package com.ryuqq.marketplace.domain.productgroupimage.vo;

import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.time.Instant;
import java.util.List;

/**
 * ProductGroupImage 변경 비교 결과.
 *
 * <p>기존 이미지와 새 이미지를 originUrl + imageType 기준으로 비교하여 추가/삭제/유지 목록을 제공합니다.
 */
public record ProductGroupImageDiff(
        List<ProductGroupImage> added,
        List<ProductGroupImage> removed,
        List<ProductGroupImage> retained,
        Instant occurredAt) {

    public ProductGroupImageDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static ProductGroupImageDiff of(
            List<ProductGroupImage> added,
            List<ProductGroupImage> removed,
            List<ProductGroupImage> retained,
            Instant occurredAt) {
        return new ProductGroupImageDiff(added, removed, retained, occurredAt);
    }

    /** 변경 사항이 없는지 확인. */
    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty();
    }
}
