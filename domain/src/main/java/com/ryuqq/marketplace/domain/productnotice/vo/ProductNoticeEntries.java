package com.ryuqq.marketplace.domain.productnotice.vo;

import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.Collections;
import java.util.List;

/**
 * 고시정보 항목 컬렉션 VO.
 *
 * <p>NoticeCategoryId와 ProductNoticeEntry 컬렉션을 함께 불변으로 관리합니다.
 */
public class ProductNoticeEntries {

    private final NoticeCategoryId noticeCategoryId;
    private final List<ProductNoticeEntry> entries;

    private ProductNoticeEntries(
            NoticeCategoryId noticeCategoryId, List<ProductNoticeEntry> entries) {
        this.noticeCategoryId = noticeCategoryId;
        this.entries = entries;
    }

    /** 신규 생성 시 사용. */
    public static ProductNoticeEntries of(
            NoticeCategoryId noticeCategoryId, List<ProductNoticeEntry> entries) {
        return new ProductNoticeEntries(noticeCategoryId, List.copyOf(entries));
    }

    /** 영속성에서 복원 시 사용. 검증 스킵. */
    public static ProductNoticeEntries reconstitute(
            NoticeCategoryId noticeCategoryId, List<ProductNoticeEntry> entries) {
        return new ProductNoticeEntries(noticeCategoryId, List.copyOf(entries));
    }

    // === 조회 ===

    public NoticeCategoryId noticeCategoryId() {
        return noticeCategoryId;
    }

    public List<ProductNoticeEntry> toList() {
        return Collections.unmodifiableList(entries);
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
