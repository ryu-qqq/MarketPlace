package com.ryuqq.marketplace.domain.legacy.productimage.vo;

import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * LegacyProductImage 변경 비교 결과.
 *
 * <p>기존 이미지와 새 이미지를 originUrl + imageType 기준으로 비교하여 추가/삭제/유지 목록을 제공합니다.
 *
 * <p>added: 신규 INSERT 대상. removed: soft delete 완료 상태. retained: displayOrder 갱신 완료 상태.
 */
public record LegacyImageDiff(
        List<LegacyProductImage> added,
        List<LegacyProductImage> removed,
        List<LegacyProductImage> retained,
        Instant occurredAt) {

    public LegacyImageDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static LegacyImageDiff of(
            List<LegacyProductImage> added,
            List<LegacyProductImage> removed,
            List<LegacyProductImage> retained,
            Instant occurredAt) {
        return new LegacyImageDiff(added, removed, retained, occurredAt);
    }

    /** 변경 사항이 없는지 확인. */
    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty();
    }

    /** retained + removed: dirty check 대상 일괄 persist용. */
    public List<LegacyProductImage> allDirtyImages() {
        List<LegacyProductImage> result = new ArrayList<>(retained.size() + removed.size());
        result.addAll(retained);
        result.addAll(removed);
        return result;
    }
}
