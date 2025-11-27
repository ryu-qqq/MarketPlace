package com.ryuqq.marketplace.domain.brand.fixture;

import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasSourceType;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.CanonicalName;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;
import com.ryuqq.marketplace.domain.brand.vo.Country;
import com.ryuqq.marketplace.domain.brand.vo.DataQuality;
import com.ryuqq.marketplace.domain.brand.vo.DataQualityLevel;
import com.ryuqq.marketplace.domain.brand.vo.Department;

/**
 * Brand VO Fixture
 *
 * <p>Brand 도메인의 Value Object 테스트용 Fixture 클래스</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class BrandVoFixture {

    private BrandVoFixture() {
        // Utility class
    }

    // ==================== BrandId ====================

    public static BrandId brandId() {
        return BrandId.of(1L);
    }

    public static BrandId brandId(Long value) {
        return BrandId.of(value);
    }

    public static BrandId newBrandId() {
        return BrandId.forNew();
    }

    // ==================== BrandAliasId ====================

    public static BrandAliasId brandAliasId() {
        return BrandAliasId.of(1L);
    }

    public static BrandAliasId brandAliasId(Long value) {
        return BrandAliasId.of(value);
    }

    public static BrandAliasId newBrandAliasId() {
        return BrandAliasId.forNew();
    }

    // ==================== BrandCode ====================

    public static BrandCode brandCode() {
        return BrandCode.of("NIKE");
    }

    public static BrandCode brandCode(String value) {
        return BrandCode.of(value);
    }

    // ==================== CanonicalName ====================

    public static CanonicalName canonicalName() {
        return CanonicalName.of("Nike");
    }

    public static CanonicalName canonicalName(String value) {
        return CanonicalName.of(value);
    }

    // ==================== BrandName ====================

    public static BrandName brandName() {
        return BrandName.of("나이키", "Nike", "NK");
    }

    public static BrandName brandNameKorean() {
        return BrandName.ofKorean("나이키");
    }

    public static BrandName brandNameEnglish() {
        return BrandName.ofEnglish("Nike");
    }

    public static BrandName brandName(String nameKo, String nameEn, String shortName) {
        return BrandName.of(nameKo, nameEn, shortName);
    }

    // ==================== Country ====================

    public static Country country() {
        return Country.of("US");
    }

    public static Country countryKorea() {
        return Country.of("KR");
    }

    public static Country country(String code) {
        return Country.of(code);
    }

    // ==================== Department ====================

    public static Department department() {
        return Department.FASHION;
    }

    public static Department departmentBeauty() {
        return Department.BEAUTY;
    }

    // ==================== BrandStatus ====================

    public static BrandStatus statusActive() {
        return BrandStatus.ACTIVE;
    }

    public static BrandStatus statusInactive() {
        return BrandStatus.INACTIVE;
    }

    public static BrandStatus statusBlocked() {
        return BrandStatus.BLOCKED;
    }

    // ==================== BrandMeta ====================

    public static BrandMeta brandMeta() {
        return BrandMeta.of(
            "https://www.nike.com",
            "https://cdn.example.com/nike-logo.png",
            "글로벌 스포츠 브랜드"
        );
    }

    public static BrandMeta brandMetaEmpty() {
        return BrandMeta.empty();
    }

    public static BrandMeta brandMeta(String website, String logoUrl, String description) {
        return BrandMeta.of(website, logoUrl, description);
    }

    // ==================== DataQuality ====================

    public static DataQuality dataQualityHigh() {
        return DataQuality.of(DataQualityLevel.HIGH, 85);
    }

    public static DataQuality dataQualityMid() {
        return DataQuality.of(DataQualityLevel.MID, 55);
    }

    public static DataQuality dataQualityLow() {
        return DataQuality.of(DataQualityLevel.LOW, 25);
    }

    public static DataQuality dataQualityUnknown() {
        return DataQuality.unknown();
    }

    public static DataQuality dataQuality(DataQualityLevel level, int score) {
        return DataQuality.of(level, score);
    }

    // ==================== Confidence ====================

    public static Confidence confidenceHigh() {
        return Confidence.of(0.9);
    }

    public static Confidence confidenceMid() {
        return Confidence.of(0.5);
    }

    public static Confidence confidenceLow() {
        return Confidence.of(0.3);
    }

    public static Confidence confidenceCertain() {
        return Confidence.certain();
    }

    public static Confidence confidence(double value) {
        return Confidence.of(value);
    }

    // ==================== AliasName ====================

    public static AliasName aliasName() {
        return AliasName.of("Nike Korea");
    }

    public static AliasName aliasName(String original) {
        return AliasName.of(original);
    }

    // ==================== AliasSource ====================

    public static AliasSource aliasSourceManual() {
        return AliasSource.manual();
    }

    public static AliasSource aliasSourceSeller(Long sellerId) {
        return AliasSource.seller(sellerId);
    }

    public static AliasSource aliasSourceExternalMall(String mallCode) {
        return AliasSource.externalMall(mallCode);
    }

    public static AliasSource aliasSourceSystem() {
        return AliasSource.system();
    }

    public static AliasSource aliasSource(AliasSourceType type, Long sellerId, String mallCode) {
        return AliasSource.of(type, sellerId, mallCode);
    }

    // ==================== AliasStatus ====================

    public static AliasStatus aliasStatusConfirmed() {
        return AliasStatus.CONFIRMED;
    }

    public static AliasStatus aliasStatusPending() {
        return AliasStatus.PENDING_REVIEW;
    }

    public static AliasStatus aliasStatusRejected() {
        return AliasStatus.REJECTED;
    }

    public static AliasStatus aliasStatusAutoSuggested() {
        return AliasStatus.AUTO_SUGGESTED;
    }
}
