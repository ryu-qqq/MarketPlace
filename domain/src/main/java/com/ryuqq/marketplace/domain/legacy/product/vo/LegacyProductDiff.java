package com.ryuqq.marketplace.domain.legacy.product.vo;

import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * LegacyProduct 변경 비교 결과.
 *
 * <p>기존 상품과 새 상품을 productId 기준으로 비교하여 추가/삭제/유지 목록을 제공합니다. 옵션 변경은 별도의 LegacyProductOptionDiff로
 * 제공됩니다.
 *
 * <p>added: 신규 INSERT 대상. removed: soft delete 완료 상태. retained: stock 갱신 완료 상태.
 */
public record LegacyProductDiff(
        List<LegacyProduct> added,
        List<LegacyProduct> removed,
        List<LegacyProduct> retained,
        LegacyProductOptionDiff optionDiff,
        Instant occurredAt) {

    public LegacyProductDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static LegacyProductDiff of(
            List<LegacyProduct> added,
            List<LegacyProduct> removed,
            List<LegacyProduct> retained,
            LegacyProductOptionDiff optionDiff,
            Instant occurredAt) {
        return new LegacyProductDiff(added, removed, retained, optionDiff, occurredAt);
    }

    /** 변경 사항이 없는지 확인. */
    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty() && optionDiff.hasNoChanges();
    }

    /** retained + removed: dirty check 대상 일괄 persist용. */
    public List<LegacyProduct> allDirtyProducts() {
        List<LegacyProduct> result = new ArrayList<>(retained.size() + removed.size());
        result.addAll(retained);
        result.addAll(removed);
        return result;
    }
}
