package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.List;

/**
 * SellerOptionValue 변경 비교 결과.
 *
 * <p>같은 SellerOptionGroup 내에서 optionValueName 기준으로 비교하여 추가/삭제/유지 목록을 제공합니다.
 */
public record SellerOptionValueDiff(
        List<SellerOptionValue> added,
        List<SellerOptionValue> removed,
        List<SellerOptionValue> retained) {

    public SellerOptionValueDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static SellerOptionValueDiff of(
            List<SellerOptionValue> added,
            List<SellerOptionValue> removed,
            List<SellerOptionValue> retained) {
        return new SellerOptionValueDiff(added, removed, retained);
    }

    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty();
    }
}
