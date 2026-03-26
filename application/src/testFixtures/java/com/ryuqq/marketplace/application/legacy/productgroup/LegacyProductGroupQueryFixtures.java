package com.ryuqq.marketplace.application.legacy.productgroup;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LegacyProductGroup Application Query 테스트 Fixtures.
 *
 * <p>LegacyProductGroup 관련 Query 파라미터·Criteria·번들·결과 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyProductGroupQueryFixtures {

    private LegacyProductGroupQueryFixtures() {}

    // ===== LegacyProductGroupSearchParams Fixtures =====

    public static LegacyProductGroupSearchParams searchParams() {
        return LegacyProductGroupSearchParams.of(
                null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                0, 20);
    }

    public static LegacyProductGroupSearchParams searchParams(int page, int size) {
        return LegacyProductGroupSearchParams.of(
                null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                page, size);
    }

    public static LegacyProductGroupSearchParams searchParamsWithSeller(Long sellerId) {
        return LegacyProductGroupSearchParams.of(
                sellerId, null, null, null, null, null, null, null, null, null, null, null, null,
                null, 0, 20);
    }

    public static LegacyProductGroupSearchParams searchParamsWithCategory(Long categoryId) {
        return LegacyProductGroupSearchParams.of(
                null,
                null,
                categoryId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                20);
    }

    public static LegacyProductGroupSearchParams searchParamsWithPriceRange(
            Long minSalePrice, Long maxSalePrice) {
        return LegacyProductGroupSearchParams.of(
                null,
                null,
                null,
                null,
                null,
                null,
                minSalePrice,
                maxSalePrice,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                20);
    }

    public static LegacyProductGroupSearchParams searchParamsWithSearchWord(
            String searchKeyword, String searchWord) {
        return LegacyProductGroupSearchParams.of(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                searchKeyword,
                searchWord,
                null,
                null,
                0,
                20);
    }

    public static LegacyProductGroupSearchParams searchParamsWithDisplayFilter(String displayYn) {
        return LegacyProductGroupSearchParams.of(
                null, null, null, null, null, displayYn, null, null, null, null, null, null, null,
                null, 0, 20);
    }

    // ===== LegacyProductGroupSearchCriteria Fixtures =====

    public static LegacyProductGroupSearchCriteria defaultCriteria() {
        return LegacyProductGroupSearchCriteria.of(
                null, null, List.of(), null, null, null, null, null, null, null, null, null, null,
                null, 0, 20);
    }

    public static LegacyProductGroupSearchCriteria criteriaWithCategoryIds(List<Long> categoryIds) {
        return LegacyProductGroupSearchCriteria.of(
                null,
                null,
                categoryIds,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                20);
    }

    // ===== LegacyProductGroupDetailBundle Fixtures =====

    public static LegacyProductGroupDetailBundle detailBundle(long productGroupId) {
        return LegacyProductGroupDetailBundle.of(compositeResult(productGroupId), List.of());
    }

    public static LegacyProductGroupDetailBundle detailBundleWithProducts(long productGroupId) {
        return LegacyProductGroupDetailBundle.of(
                compositeResult(productGroupId), List.of(productCompositeResult(productGroupId)));
    }

    public static LegacyProductGroupCompositeResult compositeResult(long productGroupId) {
        return new LegacyProductGroupCompositeResult(
                productGroupId,
                "테스트 상품그룹",
                1L,
                "테스트셀러",
                100L,
                "테스트브랜드",
                200L,
                "의류 > 상의",
                "COMBINATION",
                "AUTO",
                50000L,
                45000L,
                40000L,
                5000L,
                10,
                20,
                false,
                true,
                "NEW",
                "DOMESTIC",
                "STYLE-001",
                "admin",
                "admin",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 6, 1, 0, 0),
                List.of(
                        new LegacyProductGroupCompositeResult.ImageInfo(
                                "MAIN", "https://example.com/main.jpg")),
                "<p>상품 상세 설명</p>",
                new LegacyProductGroupCompositeResult.NoticeInfo(
                        "면", "블랙", "FREE", "제조사", "한국", "손세탁", "2024-01", "품질보증기준", "02-1234-5678"),
                new LegacyProductGroupCompositeResult.DeliveryInfo(
                        "DOMESTIC", 3000, 3, "RETURN_ADDRESS", "CJ대한통운", 5000, "서울시 강남구"));
    }

    public static LegacyProductCompositeResult productCompositeResult(long productGroupId) {
        return new LegacyProductCompositeResult(
                1L,
                productGroupId,
                100,
                false,
                List.of(
                        new LegacyProductCompositeResult.OptionMapping(10L, 101L, "색상", "블랙"),
                        new LegacyProductCompositeResult.OptionMapping(11L, 201L, "사이즈", "L")));
    }

    // ===== LegacyProductGroupDetailResult Fixtures =====

    public static LegacyProductGroupDetailResult detailResult(long productGroupId) {
        return new LegacyProductGroupDetailResult(
                productGroupId,
                "테스트 상품그룹",
                1L,
                "테스트셀러",
                100L,
                "테스트브랜드",
                200L,
                "의류 > 상의",
                "COMBINATION",
                "AUTO",
                50000L,
                45000L,
                40000L,
                5000L,
                10,
                20,
                false,
                true,
                "NEW",
                "DOMESTIC",
                "STYLE-001",
                "admin",
                "admin",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 6, 1, 0, 0),
                new LegacyProductGroupDetailResult.LegacyNoticeResult(
                        "면", "블랙", "FREE", "제조사", "한국", "손세탁", "2024-01", "품질보증기준", "02-1234-5678"),
                List.of(
                        new LegacyProductGroupDetailResult.LegacyImageResult(
                                "MAIN", "https://example.com/main.jpg")),
                "<p>상품 상세 설명</p>",
                new LegacyProductGroupDetailResult.LegacyDeliveryResult(
                        "DOMESTIC", 3000, 3, "RETURN_ADDRESS", "CJ대한통운", 5000, "서울시 강남구"),
                List.of());
    }

    // ===== LegacyProductGroupPageResult Fixtures =====

    public static LegacyProductGroupPageResult pageResult() {
        return LegacyProductGroupPageResult.of(List.of(detailResult(1L)), 1L, 0, 20);
    }

    public static LegacyProductGroupPageResult emptyPageResult() {
        return LegacyProductGroupPageResult.empty(0, 20);
    }
}
