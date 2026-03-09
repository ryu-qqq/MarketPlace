package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.DefaultRefundPolicyNotFoundException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.DefaultShippingPolicyNotFoundException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 변환 사전 해소기.
 *
 * <p>레거시 brandId/categoryId를 SETOF 인바운드 소스 매핑을 통해 내부 ID로 변환하고, 셀러의 기본 배송/환불 정책과 고시정보 카테고리를 조회합니다.
 */
@Component
public class LegacyConversionPreResolver {

    private static final long SETOF_SOURCE_ID = 2L;

    /** luxurydb seller_id → market sellers.id 매핑 (1회성 레거시 변환용). */
    private static final Map<Long, Long> LEGACY_SELLER_ID_MAP =
            Map.ofEntries(
                    Map.entry(1L, 1L), // admin → 없음 (skip)
                    Map.entry(2L, 1L), // bellitalia
                    Map.entry(4L, 2L), // bino2345
                    Map.entry(5L, 3L), // buono_dj
                    Map.entry(6L, 4L), // carino
                    Map.entry(7L, 5L), // ccapsule1
                    Map.entry(8L, 6L), // dmont
                    Map.entry(9L, 7L), // fixedone
                    Map.entry(10L, 8L), // italiagom
                    Map.entry(11L, 9L), // L-ONE
                    Map.entry(12L, 10L), // LIKEASTAR
                    Map.entry(13L, 11L), // LOUI
                    Map.entry(14L, 12L), // LUXNHOLIC
                    Map.entry(15L, 13L), // maisonparco
                    Map.entry(16L, 14L), // myaria
                    Map.entry(17L, 15L), // noblecoco_
                    Map.entry(18L, 16L), // RUNNERSELL
                    Map.entry(19L, 17L), // SUBIR
                    Map.entry(20L, 18L), // thefactor2
                    Map.entry(22L, 22L), // THEGRANDE
                    Map.entry(23L, 19L), // viaitalia
                    Map.entry(24L, 20L), // VIVIANO
                    Map.entry(25L, 21L), // wdrobe
                    Map.entry(26L, 23L), // winsome1978
                    Map.entry(27L, 24L), // winusavenue
                    Map.entry(29L, 26L), // ALLIWANT
                    Map.entry(30L, 27L), // UPSET
                    Map.entry(31L, 25L), // trexi
                    Map.entry(34L, 29L), // BONTANO
                    Map.entry(35L, 30L), // AmericanNeedle
                    Map.entry(36L, 31L), // Bensimon
                    Map.entry(37L, 32L), // Timberland
                    Map.entry(40L, 34L), // daomcor
                    Map.entry(42L, 35L), // wingsfoot
                    Map.entry(43L, 36L), // footmart
                    Map.entry(56L, 37L), // IKNOWK
                    Map.entry(57L, 38L), // origo
                    Map.entry(58L, 39L), // WEOinc
                    Map.entry(59L, 40L), // BESTON
                    Map.entry(60L, 41L), // SOSOHANBIT
                    Map.entry(61L, 42L), // marvelcollection
                    Map.entry(62L, 43L), // GMLS
                    Map.entry(63L, 44L), // MEYER
                    Map.entry(64L, 45L), // Cardmon
                    Map.entry(65L, 46L), // keshop
                    Map.entry(66L, 47L), // koreaboardgames
                    Map.entry(67L, 48L), // carif
                    Map.entry(68L, 49L), // bumkins
                    Map.entry(69L, 50L), // BLITZWAY
                    Map.entry(70L, 51L), // gaia
                    Map.entry(71L, 52L), // Asolution
                    Map.entry(72L, 53L), // marhenj
                    Map.entry(73L, 54L), // megoskorea
                    Map.entry(74L, 55L), // klepton
                    Map.entry(75L, 56L), // RSCholdings
                    Map.entry(76L, 57L), // GOODORVERYGOOD
                    Map.entry(77L, 58L), // backpacker
                    Map.entry(78L, 59L), // CLAPS
                    Map.entry(79L, 60L), // THEART
                    Map.entry(80L, 61L), // NationalGeographicMobileAccessory
                    Map.entry(81L, 62L), // MAYTON
                    Map.entry(82L, 63L), // oneplusdesign
                    Map.entry(83L, 64L), // ModernHouse
                    Map.entry(84L, 65L), // LILFANT
                    Map.entry(85L, 66L), // goldpang
                    Map.entry(86L, 67L), // Gosty
                    Map.entry(87L, 68L), // SCRUB DADDY
                    Map.entry(88L, 69L), // NLIZE
                    Map.entry(89L, 70L), // sweetjoy
                    Map.entry(90L, 71L), // MadeInKorea2025
                    Map.entry(91L, 72L), // molancano
                    Map.entry(92L, 73L), // TINYVILLE
                    Map.entry(93L, 74L), // slko
                    Map.entry(94L, 75L) // HOHOPE1977
                    );

    private final InboundProductMappingResolver mappingResolver;
    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;
    private final CategoryNoticeResolver categoryNoticeResolver;

    public LegacyConversionPreResolver(
            InboundProductMappingResolver mappingResolver,
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager,
            CategoryNoticeResolver categoryNoticeResolver) {
        this.mappingResolver = mappingResolver;
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
        this.categoryNoticeResolver = categoryNoticeResolver;
    }

    /**
     * 레거시 상품 그룹 데이터에서 내부 시스템 ID를 해소합니다.
     *
     * @param composite 레거시 상품 그룹 composite
     * @return 해소된 내부 ID 컨텍스트
     * @throws IllegalStateException 브랜드 또는 카테고리 매핑 미발견 시
     * @throws DefaultShippingPolicyNotFoundException 셀러 기본 배송정책 미존재 시
     * @throws DefaultRefundPolicyNotFoundException 셀러 기본 환불정책 미존재 시
     */
    public LegacyConversionResolvedContext resolve(LegacyProductGroupCompositeResult composite) {
        BrandId brandId = resolveBrand(composite.brandId());
        CategoryId categoryId = resolveCategory(composite.categoryId());
        SellerId sellerId = resolveSellerId(composite.sellerId());
        ShippingPolicyId shippingPolicyId = resolveShippingPolicy(sellerId);
        RefundPolicyId refundPolicyId = resolveRefundPolicy(sellerId);
        Optional<NoticeCategory> noticeCategory =
                categoryNoticeResolver.resolve(categoryId.value());

        return new LegacyConversionResolvedContext(
                brandId, categoryId, shippingPolicyId, refundPolicyId, noticeCategory);
    }

    private BrandId resolveBrand(long legacyBrandId) {
        return mappingResolver
                .resolveInternalBrandId(SETOF_SOURCE_ID, String.valueOf(legacyBrandId))
                .map(BrandId::of)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "SETOF 브랜드 매핑 미발견: legacyBrandId=" + legacyBrandId));
    }

    private CategoryId resolveCategory(long legacyCategoryId) {
        return mappingResolver
                .resolveInternalCategoryId(SETOF_SOURCE_ID, String.valueOf(legacyCategoryId))
                .map(CategoryId::of)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "SETOF 카테고리 매핑 미발견: legacyCategoryId=" + legacyCategoryId));
    }

    private SellerId resolveSellerId(long legacySellerId) {
        Long marketSellerId = LEGACY_SELLER_ID_MAP.get(legacySellerId);
        if (marketSellerId == null) {
            throw new IllegalStateException("레거시 셀러 매핑 미발견: legacySellerId=" + legacySellerId);
        }
        return SellerId.of(marketSellerId);
    }

    private ShippingPolicyId resolveShippingPolicy(SellerId sellerId) {
        return shippingPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(ShippingPolicy::id)
                .orElseThrow(() -> new DefaultShippingPolicyNotFoundException(sellerId.value()));
    }

    private RefundPolicyId resolveRefundPolicy(SellerId sellerId) {
        return refundPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(RefundPolicy::id)
                .orElseThrow(() -> new DefaultRefundPolicyNotFoundException(sellerId.value()));
    }
}
