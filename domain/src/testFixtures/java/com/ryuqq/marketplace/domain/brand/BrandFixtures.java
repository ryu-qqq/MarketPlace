package com.ryuqq.marketplace.domain.brand;

import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.LogoUrl;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;

/**
 * Brand 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Brand 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class BrandFixtures {

    private BrandFixtures() {}

    // ===== ID Fixtures =====
    public static BrandId defaultBrandId() {
        return BrandId.of(1L);
    }

    public static BrandId brandId(Long value) {
        return BrandId.of(value);
    }

    public static BrandId newBrandId() {
        return BrandId.forNew();
    }

    // ===== VO Fixtures =====
    public static BrandCode defaultBrandCode() {
        return BrandCode.of("TEST_BRAND");
    }

    public static BrandCode brandCode(String value) {
        return BrandCode.of(value);
    }

    public static BrandName defaultBrandName() {
        return BrandName.of("테스트 브랜드", "Test Brand", "테브");
    }

    public static BrandName brandName(String nameKo, String nameEn, String shortName) {
        return BrandName.of(nameKo, nameEn, shortName);
    }

    public static BrandName emptyBrandName() {
        return BrandName.empty();
    }

    public static LogoUrl defaultLogoUrl() {
        return LogoUrl.of("https://example.com/logo.png");
    }

    public static LogoUrl logoUrl(String value) {
        return LogoUrl.of(value);
    }

    public static LogoUrl emptyLogoUrl() {
        return LogoUrl.empty();
    }

    // ===== Aggregate Fixtures =====
    public static Brand newBrand() {
        return Brand.forNew(
                defaultBrandCode(), defaultBrandName(), defaultLogoUrl(), CommonVoFixtures.now());
    }

    public static Brand newBrand(String code, String nameKo, String logoUrl) {
        return Brand.forNew(
                BrandCode.of(code),
                BrandName.of(nameKo, null, null),
                LogoUrl.of(logoUrl),
                CommonVoFixtures.now());
    }

    public static Brand activeBrand() {
        return Brand.reconstitute(
                BrandId.of(1L),
                defaultBrandCode(),
                defaultBrandName(),
                BrandStatus.ACTIVE,
                defaultLogoUrl(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Brand activeBrand(Long id) {
        return Brand.reconstitute(
                BrandId.of(id),
                defaultBrandCode(),
                defaultBrandName(),
                BrandStatus.ACTIVE,
                defaultLogoUrl(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Brand activeBrand(Long id, String code) {
        return Brand.reconstitute(
                BrandId.of(id),
                BrandCode.of(code),
                defaultBrandName(),
                BrandStatus.ACTIVE,
                defaultLogoUrl(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Brand inactiveBrand() {
        return Brand.reconstitute(
                BrandId.of(2L),
                BrandCode.of("INACTIVE_BRAND"),
                BrandName.of("비활성 브랜드", "Inactive Brand", null),
                BrandStatus.INACTIVE,
                defaultLogoUrl(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Brand inactiveBrand(Long id) {
        return Brand.reconstitute(
                BrandId.of(id),
                BrandCode.of("INACTIVE_BRAND"),
                BrandName.of("비활성 브랜드", "Inactive Brand", null),
                BrandStatus.INACTIVE,
                defaultLogoUrl(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Brand deletedBrand() {
        return Brand.reconstitute(
                BrandId.of(3L),
                BrandCode.of("DELETED_BRAND"),
                BrandName.of("삭제된 브랜드", "Deleted Brand", null),
                BrandStatus.INACTIVE,
                defaultLogoUrl(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Brand deletedBrand(Long id) {
        return Brand.reconstitute(
                BrandId.of(id),
                BrandCode.of("DELETED_BRAND"),
                BrandName.of("삭제된 브랜드", "Deleted Brand", null),
                BrandStatus.INACTIVE,
                defaultLogoUrl(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
