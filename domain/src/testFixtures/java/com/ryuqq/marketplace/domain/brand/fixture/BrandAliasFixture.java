package com.ryuqq.marketplace.domain.brand.fixture;

import com.ryuqq.marketplace.domain.brand.aggregate.brand.BrandAlias;
import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;

/**
 * BrandAlias Entity Fixture
 *
 * <p>BrandAlias 엔티티 테스트용 Fixture 클래스</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class BrandAliasFixture {

    private BrandAliasFixture() {
        // Utility class
    }

    // ==================== 기본 Fixture ====================

    /**
     * 기본 BrandAlias 생성 (신규, CONFIRMED 상태)
     */
    public static BrandAlias defaultAlias() {
        return BrandAlias.create(
            1L,
            AliasName.of("Nike Korea"),
            AliasSource.manual(),
            Confidence.certain(),
            AliasStatus.CONFIRMED
        );
    }

    /**
     * 신규 BrandAlias 생성 (PENDING_REVIEW 상태)
     */
    public static BrandAlias pendingAlias() {
        return BrandAlias.create(
            1L,
            AliasName.of("나이키"),
            AliasSource.manual(),
            Confidence.of(0.8),
            AliasStatus.PENDING_REVIEW
        );
    }

    /**
     * 자동 제안 BrandAlias 생성 (AUTO_SUGGESTED 상태)
     */
    public static BrandAlias autoSuggestedAlias() {
        return BrandAlias.create(
            1L,
            AliasName.of("NIKE INC"),
            AliasSource.system(),
            Confidence.of(0.6),
            AliasStatus.AUTO_SUGGESTED
        );
    }

    /**
     * 거부된 BrandAlias 생성 (REJECTED 상태)
     */
    public static BrandAlias rejectedAlias() {
        return BrandAlias.create(
            1L,
            AliasName.of("Invalid Alias"),
            AliasSource.manual(),
            Confidence.of(0.3),
            AliasStatus.REJECTED
        );
    }

    // ==================== 소스별 Fixture ====================

    /**
     * 셀러 소스 BrandAlias 생성
     */
    public static BrandAlias sellerAlias(Long sellerId) {
        return BrandAlias.create(
            1L,
            AliasName.of("Seller Nike Alias"),
            AliasSource.seller(sellerId),
            Confidence.of(0.7),
            AliasStatus.PENDING_REVIEW
        );
    }

    /**
     * 외부몰 소스 BrandAlias 생성
     */
    public static BrandAlias externalMallAlias(String mallCode) {
        return BrandAlias.create(
            1L,
            AliasName.of("External Mall Nike"),
            AliasSource.externalMall(mallCode),
            Confidence.of(0.5),
            AliasStatus.AUTO_SUGGESTED
        );
    }

    /**
     * 시스템 소스 BrandAlias 생성
     */
    public static BrandAlias systemAlias() {
        return BrandAlias.create(
            1L,
            AliasName.of("System Generated"),
            AliasSource.system(),
            Confidence.uncertain(),
            AliasStatus.AUTO_SUGGESTED
        );
    }

    // ==================== 재구성 Fixture ====================

    /**
     * 재구성된 BrandAlias (DB에서 로드된 상태)
     */
    public static BrandAlias reconstitutedAlias() {
        return BrandAlias.reconstitute(
            BrandAliasId.of(100L),
            1L,
            AliasName.of("Nike Korea"),
            AliasSource.manual(),
            Confidence.certain(),
            AliasStatus.CONFIRMED
        );
    }

    /**
     * 재구성된 BrandAlias (커스텀)
     */
    public static BrandAlias reconstitutedAlias(Long aliasId, Long brandId, String aliasOriginal) {
        return BrandAlias.reconstitute(
            BrandAliasId.of(aliasId),
            brandId,
            AliasName.of(aliasOriginal),
            AliasSource.manual(),
            Confidence.certain(),
            AliasStatus.CONFIRMED
        );
    }

    // ==================== 커스텀 빌더 ====================

    /**
     * 커스텀 BrandAlias 생성
     */
    public static BrandAlias customAlias(
        Long brandId,
        String aliasOriginal,
        AliasSource source,
        Confidence confidence,
        AliasStatus status
    ) {
        return BrandAlias.create(brandId, AliasName.of(aliasOriginal), source, confidence, status);
    }

    /**
     * 커스텀 재구성 BrandAlias
     */
    public static BrandAlias customReconstitutedAlias(
        Long aliasId,
        Long brandId,
        String aliasOriginal,
        AliasSource source,
        Confidence confidence,
        AliasStatus status
    ) {
        return BrandAlias.reconstitute(
            BrandAliasId.of(aliasId),
            brandId,
            AliasName.of(aliasOriginal),
            source,
            confidence,
            status
        );
    }
}
