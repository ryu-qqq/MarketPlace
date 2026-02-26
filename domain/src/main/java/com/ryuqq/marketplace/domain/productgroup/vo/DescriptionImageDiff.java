package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.util.List;

/**
 * Description 이미지 변경 비교 결과.
 *
 * <p>기존 이미지와 새 이미지를 originUrl 기준으로 비교하여 추가/삭제/유지 목록을 제공합니다.
 */
public record DescriptionImageDiff(
        List<DescriptionImage> added,
        List<DescriptionImage> removed,
        List<DescriptionImage> retained) {

    public DescriptionImageDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static DescriptionImageDiff of(
            List<DescriptionImage> added,
            List<DescriptionImage> removed,
            List<DescriptionImage> retained) {
        return new DescriptionImageDiff(added, removed, retained);
    }

    /** 삭제 대상 이미지 ID 목록. */
    public List<Long> removedIds() {
        return removed.stream().map(DescriptionImage::idValue).toList();
    }

    /** 변경 사항이 없는지 확인. */
    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty();
    }
}
