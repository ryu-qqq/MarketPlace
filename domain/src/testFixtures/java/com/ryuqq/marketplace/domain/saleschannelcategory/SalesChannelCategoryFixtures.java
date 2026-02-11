package com.ryuqq.marketplace.domain.saleschannelcategory;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategoryUpdateData;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;

/**
 * SalesChannelCategory 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 SalesChannelCategory 관련 객체들을 생성합니다.
 */
public final class SalesChannelCategoryFixtures {

    private SalesChannelCategoryFixtures() {}

    // ===== ID Fixtures =====
    public static SalesChannelCategoryId salesChannelCategoryId(Long value) {
        return SalesChannelCategoryId.of(value);
    }

    public static SalesChannelCategoryId defaultSalesChannelCategoryId() {
        return SalesChannelCategoryId.of(1L);
    }

    public static SalesChannelCategoryId newSalesChannelCategoryId() {
        return SalesChannelCategoryId.forNew();
    }

    // ===== Status Fixtures =====
    public static SalesChannelCategoryStatus activeStatus() {
        return SalesChannelCategoryStatus.ACTIVE;
    }

    public static SalesChannelCategoryStatus inactiveStatus() {
        return SalesChannelCategoryStatus.INACTIVE;
    }

    // ===== Aggregate Fixtures =====
    public static SalesChannelCategory newSalesChannelCategory() {
        return SalesChannelCategory.forNew(
                1L,
                "CAT001",
                "테스트 카테고리",
                null,
                1,
                "/CAT001",
                1,
                false,
                "테스트 카테고리",
                CommonVoFixtures.now());
    }

    public static SalesChannelCategory newSalesChannelCategory(
            Long salesChannelId, String externalCategoryCode, String externalCategoryName) {
        return SalesChannelCategory.forNew(
                salesChannelId,
                externalCategoryCode,
                externalCategoryName,
                null,
                1,
                "/" + externalCategoryCode,
                1,
                false,
                externalCategoryName,
                CommonVoFixtures.now());
    }

    public static SalesChannelCategory newChildCategory(Long parentId) {
        return SalesChannelCategory.forNew(
                1L,
                "CAT002",
                "하위 카테고리",
                parentId,
                2,
                "/CAT001/CAT002",
                1,
                false,
                "테스트 카테고리 > 하위 카테고리",
                CommonVoFixtures.now());
    }

    public static SalesChannelCategory activeSalesChannelCategory() {
        return SalesChannelCategory.reconstitute(
                defaultSalesChannelCategoryId(),
                1L,
                "CAT001",
                "활성 카테고리",
                null,
                1,
                "/CAT001",
                1,
                false,
                SalesChannelCategoryStatus.ACTIVE,
                "활성 카테고리",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelCategory activeSalesChannelCategory(Long id) {
        return SalesChannelCategory.reconstitute(
                SalesChannelCategoryId.of(id),
                1L,
                "CAT" + id,
                "활성 카테고리",
                null,
                1,
                "/CAT" + id,
                1,
                false,
                SalesChannelCategoryStatus.ACTIVE,
                "활성 카테고리",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelCategory inactiveSalesChannelCategory() {
        return SalesChannelCategory.reconstitute(
                SalesChannelCategoryId.of(2L),
                1L,
                "CAT002",
                "비활성 카테고리",
                null,
                1,
                "/CAT002",
                1,
                false,
                SalesChannelCategoryStatus.INACTIVE,
                "비활성 카테고리",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelCategory leafCategory() {
        return SalesChannelCategory.reconstitute(
                SalesChannelCategoryId.of(3L),
                1L,
                "CAT003",
                "말단 카테고리",
                100L,
                3,
                "/CAT001/CAT002/CAT003",
                1,
                true,
                SalesChannelCategoryStatus.ACTIVE,
                "상위 > 중간 > 말단 카테고리",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelCategory multiLevelCategory(int depth) {
        return SalesChannelCategory.reconstitute(
                defaultSalesChannelCategoryId(),
                1L,
                "CAT" + depth,
                "Depth " + depth + " 카테고리",
                depth > 1 ? 100L : null,
                depth,
                "/CAT001" + (depth > 1 ? "/CAT" + depth : ""),
                depth,
                depth >= 3,
                SalesChannelCategoryStatus.ACTIVE,
                "카테고리 경로",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== UpdateData Fixtures =====
    public static SalesChannelCategoryUpdateData salesChannelCategoryUpdateData() {
        return SalesChannelCategoryUpdateData.of(
                "수정된 카테고리명", 10, true, SalesChannelCategoryStatus.ACTIVE);
    }

    public static SalesChannelCategoryUpdateData salesChannelCategoryUpdateData(
            String externalCategoryName,
            int sortOrder,
            boolean leaf,
            SalesChannelCategoryStatus status) {
        return SalesChannelCategoryUpdateData.of(externalCategoryName, sortOrder, leaf, status);
    }
}
