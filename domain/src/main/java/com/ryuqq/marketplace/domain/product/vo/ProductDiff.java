package com.ryuqq.marketplace.domain.product.vo;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.time.Instant;
import java.util.List;

/**
 * Product 변경 비교 결과.
 *
 * <p>기존 Product 컬렉션과 새 요청을 ID 기반으로 비교하여 추가/삭제/유지 목록을 제공합니다.
 *
 * <p>added: 신규 생성할 Product. removed: soft delete 완료 상태. retained: 가격/재고/정렬 갱신 완료 상태.
 */
public record ProductDiff(
        List<Product> added, List<Product> removed, List<Product> retained, Instant occurredAt) {

    public ProductDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static ProductDiff of(
            List<Product> added,
            List<Product> removed,
            List<Product> retained,
            Instant occurredAt) {
        return new ProductDiff(added, removed, retained, occurredAt);
    }

    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty();
    }

    /** retained + removed: dirty check 대상 일괄 persist용. */
    public List<Product> allDirtyProducts() {
        List<Product> result = new java.util.ArrayList<>(retained.size() + removed.size());
        result.addAll(retained);
        result.addAll(removed);
        return result;
    }
}
