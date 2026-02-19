package com.ryuqq.marketplace.domain.productnotice.vo;

import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.time.Instant;
import java.util.List;

/**
 * 고시정보 수정 데이터.
 *
 * <p>카테고리 ID, 새 항목 목록, 수정 시각을 불변으로 보관합니다.
 */
public class ProductNoticeUpdateData {

    private final NoticeCategoryId noticeCategoryId;
    private final List<ProductNoticeEntry> entries;
    private final Instant updatedAt;

    private ProductNoticeUpdateData(
            NoticeCategoryId noticeCategoryId,
            List<ProductNoticeEntry> entries,
            Instant updatedAt) {
        this.noticeCategoryId = noticeCategoryId;
        this.entries = entries;
        this.updatedAt = updatedAt;
    }

    public static ProductNoticeUpdateData of(
            NoticeCategoryId noticeCategoryId,
            List<ProductNoticeEntry> entries,
            Instant updatedAt) {
        return new ProductNoticeUpdateData(noticeCategoryId, List.copyOf(entries), updatedAt);
    }

    public NoticeCategoryId noticeCategoryId() {
        return noticeCategoryId;
    }

    public List<ProductNoticeEntry> entries() {
        return entries;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
