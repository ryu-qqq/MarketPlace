package com.ryuqq.marketplace.domain.productnotice.aggregate;

import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 상품 고시정보 Aggregate Root. ProductGroup에 대한 고시정보를 NoticCategory 단위로 묶어 관리한다. ProductGroup과는 별도
 * Aggregate로 독립 관리.
 */
public class ProductNotice {

    private final ProductNoticeId id;
    private final ProductGroupId productGroupId;
    private final NoticeCategoryId noticeCategoryId;
    private final List<ProductNoticeEntry> entries;
    private final Instant createdAt;
    private Instant updatedAt;

    private ProductNotice(
            ProductNoticeId id,
            ProductGroupId productGroupId,
            NoticeCategoryId noticeCategoryId,
            List<ProductNoticeEntry> entries,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.noticeCategoryId = noticeCategoryId;
        this.entries = new ArrayList<>(entries);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 상품 고시정보 생성. */
    public static ProductNotice forNew(
            ProductGroupId productGroupId,
            NoticeCategoryId noticeCategoryId,
            List<ProductNoticeEntry> entries,
            Instant now) {
        return new ProductNotice(
                ProductNoticeId.forNew(), productGroupId, noticeCategoryId, entries, now, now);
    }

    /** 영속성에서 복원 시 사용. */
    public static ProductNotice reconstitute(
            ProductNoticeId id,
            ProductGroupId productGroupId,
            NoticeCategoryId noticeCategoryId,
            List<ProductNoticeEntry> entries,
            Instant createdAt,
            Instant updatedAt) {
        return new ProductNotice(
                id, productGroupId, noticeCategoryId, entries, createdAt, updatedAt);
    }

    /** 고시정보 항목 전체 교체. */
    public void replaceEntries(List<ProductNoticeEntry> entries, Instant now) {
        this.entries.clear();
        this.entries.addAll(entries);
        this.updatedAt = now;
    }

    /** 고시정보 항목 추가. */
    public void addEntry(ProductNoticeEntry entry, Instant now) {
        this.entries.add(entry);
        this.updatedAt = now;
    }

    /** 입력된 항목 수. */
    public int entryCount() {
        return entries.size();
    }

    // ── Accessor 메서드 ──

    public ProductNoticeId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public NoticeCategoryId noticeCategoryId() {
        return noticeCategoryId;
    }

    public Long noticeCategoryIdValue() {
        return noticeCategoryId.value();
    }

    public List<ProductNoticeEntry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
