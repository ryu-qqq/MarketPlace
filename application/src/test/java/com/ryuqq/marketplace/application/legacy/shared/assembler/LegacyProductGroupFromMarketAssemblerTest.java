package com.ryuqq.marketplace.application.legacy.shared.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyProductGroupFromMarketAssembler 단위 테스트")
class LegacyProductGroupFromMarketAssemblerTest {

    private LegacyProductGroupFromMarketAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new LegacyProductGroupFromMarketAssembler();
    }

    @Nested
    @DisplayName("toDetailResult() - 표준 상세 결과 → 레거시 상세 결과 변환")
    class ToDetailResultTest {

        @Test
        @DisplayName("표준 CompositeResult를 LegacyProductGroupDetailResult로 변환한다")
        void toDetailResult_ValidComposite_ReturnsLegacyDetailResult() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIds();
            ProductGroupDetailCompositeResult composite = createCompositeResult(200L, now);

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(resolved.requestProductGroupId());
            assertThat(result.productGroupName()).isEqualTo(composite.productGroupName());
            assertThat(result.sellerId()).isEqualTo(composite.sellerId());
            assertThat(result.brandId()).isEqualTo(composite.brandId());
            assertThat(result.categoryId()).isEqualTo(composite.categoryId());
        }

        @Test
        @DisplayName("products가 없는 경우 가격은 0으로 설정된다")
        void toDetailResult_NoProducts_PricesAreZero() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite = createCompositeResult(200L, now);

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result.regularPrice()).isZero();
            assertThat(result.currentPrice()).isZero();
        }

        @Test
        @DisplayName("status=ACTIVE이면 displayed=true, soldOut=false로 변환한다")
        void toDetailResult_ActiveStatus_DisplayedTrueAndSoldOutFalse() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite =
                    createCompositeResultWithStatus(200L, now, "ACTIVE");

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result.displayed()).isTrue();
            assertThat(result.soldOut()).isFalse();
        }

        @Test
        @DisplayName("status=SOLD_OUT이면 soldOut=true로 변환한다")
        void toDetailResult_SoldOutStatus_SoldOutTrue() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite =
                    createCompositeResultWithStatus(200L, now, "SOLD_OUT");

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result.soldOut()).isTrue();
            assertThat(result.displayed()).isFalse();
        }

        @Test
        @DisplayName("optionType=NONE이면 SINGLE로 변환한다")
        void toDetailResult_OptionTypeNone_ReturnsSingle() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite =
                    createCompositeResultWithOptionType(200L, now, "NONE");

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result.optionType()).isEqualTo("SINGLE");
        }

        @Test
        @DisplayName("optionType=SINGLE이면 OPTION_ONE으로 변환한다")
        void toDetailResult_OptionTypeSingle_ReturnsOptionOne() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite =
                    createCompositeResultWithOptionType(200L, now, "SINGLE");

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result.optionType()).isEqualTo("OPTION_ONE");
        }

        @Test
        @DisplayName("optionType=COMBINATION이면 OPTION_TWO로 변환한다")
        void toDetailResult_OptionTypeCombination_ReturnsOptionTwo() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite =
                    createCompositeResultWithOptionType(200L, now, "COMBINATION");

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result.optionType()).isEqualTo("OPTION_TWO");
        }

        @Test
        @DisplayName("고시정보(notice)가 null이면 빈 LegacyNoticeResult를 반환한다")
        void toDetailResult_NullNotice_ReturnsEmptyNotice() {
            // given
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite = createCompositeResult(200L, now);

            // when
            LegacyProductGroupDetailResult result = sut.toDetailResult(composite, resolved);

            // then
            assertThat(result.notice()).isNotNull();
            assertThat(result.notice().material()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 표준 목록 결과 → 레거시 목록 결과 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("ProductGroupPageResult를 LegacyProductGroupPageResult로 변환한다")
        void toPageResult_ValidPageResult_ReturnsLegacyPageResult() {
            // given
            ProductGroupPageResult pageResult = createPageResult();
            int page = 0;
            int size = 20;

            // when
            LegacyProductGroupPageResult result = sut.toPageResult(pageResult, page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.items()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(pageResult.pageMeta().totalElements());
            assertThat(result.page()).isEqualTo(page);
            assertThat(result.size()).isEqualTo(size);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 포함한 PageResult를 반환한다")
        void toPageResult_EmptyResults_ReturnsEmptyList() {
            // given
            ProductGroupPageResult emptyPageResult =
                    ProductGroupPageResult.of(List.of(), 0, 20, 0L);
            int page = 0;
            int size = 20;

            // when
            LegacyProductGroupPageResult result = sut.toPageResult(emptyPageResult, page, size);

            // then
            assertThat(result.items()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }
    }

    // ===== 헬퍼 메서드 =====

    private ProductGroupDetailCompositeResult createCompositeResult(
            Long productGroupId, Instant now) {
        return new ProductGroupDetailCompositeResult(
                productGroupId,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "의류",
                "의류 > 상의",
                "200",
                "테스트 상품그룹",
                "SINGLE",
                "ACTIVE",
                now,
                now,
                List.of(),
                null,
                null,
                null,
                null,
                null);
    }

    private ProductGroupDetailCompositeResult createCompositeResultWithStatus(
            Long productGroupId, Instant now, String status) {
        return new ProductGroupDetailCompositeResult(
                productGroupId,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "의류",
                "의류 > 상의",
                "200",
                "테스트 상품그룹",
                "SINGLE",
                status,
                now,
                now,
                List.of(),
                null,
                null,
                null,
                null,
                null);
    }

    private ProductGroupDetailCompositeResult createCompositeResultWithOptionType(
            Long productGroupId, Instant now, String optionType) {
        return new ProductGroupDetailCompositeResult(
                productGroupId,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "의류",
                "의류 > 상의",
                "200",
                "테스트 상품그룹",
                optionType,
                "ACTIVE",
                now,
                now,
                List.of(),
                null,
                null,
                null,
                null,
                null);
    }

    private ProductGroupPageResult createPageResult() {
        ProductGroupListCompositeResult item = createListCompositeResult(1L);
        return ProductGroupPageResult.of(List.of(item), 0, 20, 1L);
    }

    private ProductGroupListCompositeResult createListCompositeResult(Long id) {
        Instant now = Instant.now();
        return ProductGroupListCompositeResult.ofBase(
                id,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "의류",
                "의류 > 상의",
                String.valueOf(ProductGroupFixtures.DEFAULT_CATEGORY_ID),
                1,
                null,
                null,
                "테스트 상품그룹",
                "SINGLE",
                "ACTIVE",
                null,
                2,
                now,
                now);
    }
}
