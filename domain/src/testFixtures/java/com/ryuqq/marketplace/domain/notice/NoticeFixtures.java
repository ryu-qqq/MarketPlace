package com.ryuqq.marketplace.domain.notice;

import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryName;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldName;

/**
 * Notice 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Notice 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class NoticeFixtures {

    private NoticeFixtures() {}

    // ===== NoticeCategoryId Fixtures =====
    public static NoticeCategoryId defaultNoticeCategoryId() {
        return NoticeCategoryId.of(1L);
    }

    public static NoticeCategoryId noticeCategoryId(Long value) {
        return NoticeCategoryId.of(value);
    }

    public static NoticeCategoryId newNoticeCategoryId() {
        return NoticeCategoryId.forNew();
    }

    // ===== NoticeFieldId Fixtures =====
    public static NoticeFieldId defaultNoticeFieldId() {
        return NoticeFieldId.of(1L);
    }

    public static NoticeFieldId noticeFieldId(Long value) {
        return NoticeFieldId.of(value);
    }

    public static NoticeFieldId newNoticeFieldId() {
        return NoticeFieldId.forNew();
    }

    // ===== NoticeCategoryCode Fixtures =====
    public static NoticeCategoryCode defaultNoticeCategoryCode() {
        return NoticeCategoryCode.of("CLOTHING");
    }

    public static NoticeCategoryCode noticeCategoryCode(String value) {
        return NoticeCategoryCode.of(value);
    }

    // ===== NoticeCategoryName Fixtures =====
    public static NoticeCategoryName defaultNoticeCategoryName() {
        return NoticeCategoryName.of("의류", "Clothing");
    }

    public static NoticeCategoryName noticeCategoryNameKoreanOnly() {
        return NoticeCategoryName.ofKorean("전자제품");
    }

    public static NoticeCategoryName noticeCategoryName(String nameKo, String nameEn) {
        return NoticeCategoryName.of(nameKo, nameEn);
    }

    // ===== NoticeFieldCode Fixtures =====
    public static NoticeFieldCode defaultNoticeFieldCode() {
        return NoticeFieldCode.of("MATERIAL");
    }

    public static NoticeFieldCode noticeFieldCode(String value) {
        return NoticeFieldCode.of(value);
    }

    // ===== NoticeFieldName Fixtures =====
    public static NoticeFieldName defaultNoticeFieldName() {
        return NoticeFieldName.of("소재");
    }

    public static NoticeFieldName noticeFieldName(String value) {
        return NoticeFieldName.of(value);
    }

    // ===== CategoryGroup Fixtures =====
    public static CategoryGroup defaultCategoryGroup() {
        return CategoryGroup.CLOTHING;
    }

    // ===== NoticeCategory Aggregate Fixtures =====
    public static NoticeCategory newNoticeCategory() {
        return NoticeCategory.forNew(
                defaultNoticeCategoryCode(),
                defaultNoticeCategoryName(),
                defaultCategoryGroup(),
                CommonVoFixtures.now());
    }

    public static NoticeCategory newNoticeCategory(
            NoticeCategoryCode code, NoticeCategoryName name, CategoryGroup group) {
        return NoticeCategory.forNew(code, name, group, CommonVoFixtures.now());
    }

    public static NoticeCategory activeNoticeCategory() {
        return NoticeCategory.reconstitute(
                NoticeCategoryId.of(1L),
                defaultNoticeCategoryCode(),
                defaultNoticeCategoryName(),
                defaultCategoryGroup(),
                true,
                java.util.List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static NoticeCategory activeNoticeCategory(Long id) {
        return NoticeCategory.reconstitute(
                NoticeCategoryId.of(id),
                defaultNoticeCategoryCode(),
                defaultNoticeCategoryName(),
                defaultCategoryGroup(),
                true,
                java.util.List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static NoticeCategory inactiveNoticeCategory() {
        return NoticeCategory.reconstitute(
                NoticeCategoryId.of(2L),
                defaultNoticeCategoryCode(),
                defaultNoticeCategoryName(),
                defaultCategoryGroup(),
                false,
                java.util.List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static NoticeCategory noticeCategoryWithFields() {
        NoticeField field1 = activeNoticeField(1L);
        NoticeField field2 = activeNoticeField(2L);

        return NoticeCategory.reconstitute(
                NoticeCategoryId.of(1L),
                defaultNoticeCategoryCode(),
                defaultNoticeCategoryName(),
                defaultCategoryGroup(),
                true,
                java.util.List.of(field1, field2),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== NoticeField Entity Fixtures =====
    public static NoticeField newNoticeField() {
        return NoticeField.forNew(defaultNoticeFieldCode(), defaultNoticeFieldName(), true, 1);
    }

    public static NoticeField newNoticeField(
            NoticeFieldCode code, NoticeFieldName name, boolean required, int sortOrder) {
        return NoticeField.forNew(code, name, required, sortOrder);
    }

    public static NoticeField activeNoticeField() {
        return NoticeField.reconstitute(
                NoticeFieldId.of(1L), defaultNoticeFieldCode(), defaultNoticeFieldName(), true, 1);
    }

    public static NoticeField activeNoticeField(Long id) {
        return NoticeField.reconstitute(
                NoticeFieldId.of(id),
                noticeFieldCode("FIELD_" + id),
                noticeFieldName("필드 " + id),
                true,
                id.intValue());
    }

    public static NoticeField optionalNoticeField() {
        return NoticeField.reconstitute(
                NoticeFieldId.of(2L),
                NoticeFieldCode.of("OPTIONAL_FIELD"),
                NoticeFieldName.of("선택 필드"),
                false,
                2);
    }
}
