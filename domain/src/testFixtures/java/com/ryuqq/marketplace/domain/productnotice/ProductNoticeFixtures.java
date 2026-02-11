package com.ryuqq.marketplace.domain.productnotice;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeEntryId;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import java.util.List;

/**
 * ProductNotice 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ProductNotice 관련 객체들을 생성합니다.
 */
public final class ProductNoticeFixtures {

    private ProductNoticeFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final Long DEFAULT_NOTICE_CATEGORY_ID = 10L;
    public static final Long DEFAULT_NOTICE_FIELD_ID = 100L;
    public static final String DEFAULT_FIELD_VALUE = "기본 고시정보 값";

    // ===== ID Fixtures =====
    public static ProductNoticeId defaultProductNoticeId() {
        return ProductNoticeId.of(1L);
    }

    public static ProductNoticeId newProductNoticeId() {
        return ProductNoticeId.forNew();
    }

    public static ProductNoticeEntryId defaultProductNoticeEntryId() {
        return ProductNoticeEntryId.of(1L);
    }

    public static ProductNoticeEntryId newProductNoticeEntryId() {
        return ProductNoticeEntryId.forNew();
    }

    // ===== VO Fixtures =====
    public static NoticeFieldValue defaultNoticeFieldValue() {
        return NoticeFieldValue.of(DEFAULT_FIELD_VALUE);
    }

    public static NoticeFieldValue noticeFieldValue(String value) {
        return NoticeFieldValue.of(value);
    }

    // ===== ProductNoticeEntry Fixtures =====
    public static ProductNoticeEntry defaultEntry() {
        return ProductNoticeEntry.forNew(
                NoticeFieldId.of(DEFAULT_NOTICE_FIELD_ID),
                defaultNoticeFieldValue());
    }

    public static ProductNoticeEntry entry(Long fieldId, String value) {
        return ProductNoticeEntry.forNew(
                NoticeFieldId.of(fieldId),
                NoticeFieldValue.of(value));
    }

    public static ProductNoticeEntry existingEntry(Long id, Long fieldId, String value) {
        return ProductNoticeEntry.reconstitute(
                ProductNoticeEntryId.of(id),
                NoticeFieldId.of(fieldId),
                NoticeFieldValue.of(value));
    }

    public static List<ProductNoticeEntry> defaultEntries() {
        return List.of(
                entry(100L, "제조국"),
                entry(101L, "제조사"),
                entry(102L, "품질보증기준"));
    }

    public static List<ProductNoticeEntry> singleEntryList() {
        return List.of(defaultEntry());
    }

    // ===== ProductNotice Aggregate Fixtures =====
    public static ProductNotice newProductNotice() {
        return ProductNotice.forNew(
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                NoticeCategoryId.of(DEFAULT_NOTICE_CATEGORY_ID),
                defaultEntries(),
                CommonVoFixtures.now());
    }

    public static ProductNotice newProductNotice(
            Long productGroupId, Long noticeCategoryId, List<ProductNoticeEntry> entries) {
        return ProductNotice.forNew(
                ProductGroupId.of(productGroupId),
                NoticeCategoryId.of(noticeCategoryId),
                entries,
                CommonVoFixtures.now());
    }

    public static ProductNotice existingProductNotice() {
        return ProductNotice.reconstitute(
                ProductNoticeId.of(1L),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                NoticeCategoryId.of(DEFAULT_NOTICE_CATEGORY_ID),
                defaultEntries(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ProductNotice existingProductNotice(Long id) {
        return ProductNotice.reconstitute(
                ProductNoticeId.of(id),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                NoticeCategoryId.of(DEFAULT_NOTICE_CATEGORY_ID),
                defaultEntries(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ProductNotice existingProductNotice(
            Long id, Long productGroupId, Long noticeCategoryId) {
        return ProductNotice.reconstitute(
                ProductNoticeId.of(id),
                ProductGroupId.of(productGroupId),
                NoticeCategoryId.of(noticeCategoryId),
                defaultEntries(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
