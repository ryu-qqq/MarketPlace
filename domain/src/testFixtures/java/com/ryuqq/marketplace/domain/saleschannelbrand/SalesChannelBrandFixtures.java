package com.ryuqq.marketplace.domain.saleschannelbrand;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;

/**
 * SalesChannelBrand 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 SalesChannelBrand 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelBrandFixtures {

    private SalesChannelBrandFixtures() {}

    // ===== ID Fixtures =====
    public static SalesChannelBrandId salesChannelBrandId(Long value) {
        return SalesChannelBrandId.of(value);
    }

    public static SalesChannelBrandId defaultSalesChannelBrandId() {
        return SalesChannelBrandId.of(1L);
    }

    public static SalesChannelBrandId newSalesChannelBrandId() {
        return SalesChannelBrandId.forNew();
    }

    // ===== VO Fixtures =====
    public static SalesChannelBrandStatus activeStatus() {
        return SalesChannelBrandStatus.ACTIVE;
    }

    public static SalesChannelBrandStatus inactiveStatus() {
        return SalesChannelBrandStatus.INACTIVE;
    }

    // ===== Aggregate Fixtures =====
    public static SalesChannelBrand newSalesChannelBrand() {
        return SalesChannelBrand.forNew(
                1L,
                "BRAND-001",
                "테스트 브랜드",
                CommonVoFixtures.now());
    }

    public static SalesChannelBrand newSalesChannelBrand(
            Long salesChannelId, String externalBrandCode, String externalBrandName) {
        return SalesChannelBrand.forNew(
                salesChannelId, externalBrandCode, externalBrandName, CommonVoFixtures.now());
    }

    public static SalesChannelBrand activeSalesChannelBrand() {
        return SalesChannelBrand.reconstitute(
                SalesChannelBrandId.of(1L),
                1L,
                "BRAND-001",
                "활성 브랜드",
                SalesChannelBrandStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelBrand activeSalesChannelBrand(Long id) {
        return SalesChannelBrand.reconstitute(
                SalesChannelBrandId.of(id),
                1L,
                "BRAND-001",
                "활성 브랜드",
                SalesChannelBrandStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelBrand activeSalesChannelBrand(
            Long id, Long salesChannelId, String externalBrandCode) {
        return SalesChannelBrand.reconstitute(
                SalesChannelBrandId.of(id),
                salesChannelId,
                externalBrandCode,
                "활성 브랜드",
                SalesChannelBrandStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelBrand inactiveSalesChannelBrand() {
        return SalesChannelBrand.reconstitute(
                SalesChannelBrandId.of(2L),
                1L,
                "BRAND-002",
                "비활성 브랜드",
                SalesChannelBrandStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannelBrand inactiveSalesChannelBrand(Long id) {
        return SalesChannelBrand.reconstitute(
                SalesChannelBrandId.of(id),
                1L,
                "BRAND-002",
                "비활성 브랜드",
                SalesChannelBrandStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
