package com.ryuqq.marketplace.domain.legacy.product.vo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * LegacyProductOption 변경 비교 결과.
 *
 * <p>기존 옵션과 새 옵션을 optionGroupId:optionDetailId 기준으로 비교하여 추가/삭제/유지 목록을 제공합니다.
 *
 * <p>added: 신규 INSERT 대상. removed: soft delete 완료 상태. retained: additionalPrice 갱신 완료 상태.
 */
public record LegacyProductOptionDiff(
        List<LegacyProductOption> added,
        List<LegacyProductOption> removed,
        List<LegacyProductOption> retained,
        Instant occurredAt) {

    public LegacyProductOptionDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static LegacyProductOptionDiff of(
            List<LegacyProductOption> added,
            List<LegacyProductOption> removed,
            List<LegacyProductOption> retained,
            Instant occurredAt) {
        return new LegacyProductOptionDiff(added, removed, retained, occurredAt);
    }

    public static LegacyProductOptionDiff empty(Instant occurredAt) {
        return new LegacyProductOptionDiff(List.of(), List.of(), List.of(), occurredAt);
    }

    /** 변경 사항이 없는지 확인. */
    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty();
    }

    /** retained + removed: dirty check 대상 일괄 persist용. */
    public List<LegacyProductOption> allDirtyOptions() {
        List<LegacyProductOption> result = new ArrayList<>(retained.size() + removed.size());
        result.addAll(retained);
        result.addAll(removed);
        return result;
    }
}
