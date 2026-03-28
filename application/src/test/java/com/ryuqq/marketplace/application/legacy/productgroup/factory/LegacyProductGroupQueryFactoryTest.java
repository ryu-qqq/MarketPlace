package com.ryuqq.marketplace.application.legacy.productgroup.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyBrandIdResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyCategoryIdResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacySellerIdResolver;
import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupQueryFactory 단위 테스트")
class LegacyProductGroupQueryFactoryTest {

    @InjectMocks private LegacyProductGroupQueryFactory sut;

    @Mock private LegacySellerIdResolver sellerIdResolver;
    @Mock private LegacyBrandIdResolver brandIdResolver;
    @Mock private LegacyCategoryIdResolver categoryIdResolver;

    @Nested
    @DisplayName("createCriteria() - 검색 파라미터를 Criteria로 변환")
    class CreateCriteriaTest {

        @Test
        @DisplayName("기본 파라미터로 Criteria를 생성하면 nullable 필드는 null이다")
        void createCriteria_DefaultParams_ReturnsDefaultCriteria() {
            // given
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria).isNotNull();
            assertThat(criteria.sellerId()).isNull();
            assertThat(criteria.brandId()).isNull();
            assertThat(criteria.categoryIds()).isEmpty();
            assertThat(criteria.managementType()).isNull();
            assertThat(criteria.soldOutYn()).isNull();
            assertThat(criteria.displayYn()).isNull();
            assertThat(criteria.minSalePrice()).isNull();
            assertThat(criteria.maxSalePrice()).isNull();
            assertThat(criteria.searchKeyword()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.page()).isZero();
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("sellerId가 있는 파라미터로 Criteria를 생성하면 sellerId가 매핑된다")
        void createCriteria_WithSellerId_MapsSellerId() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithSeller(1L);

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria.sellerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("categoryId가 있는 파라미터로 Criteria를 생성하면 categoryIds로 매핑된다")
        void createCriteria_WithCategoryId_MapsCategoryIds() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(200L);

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria.categoryIds()).containsExactly(200L);
        }

        @Test
        @DisplayName("확장된 카테고리 ID 목록이 있는 파라미터로 Criteria를 생성한다")
        void createCriteria_WithExpandedCategoryIds_MapsAllCategoryIds() {
            // given
            List<Long> expandedIds = List.of(200L, 201L, 202L);
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(200L)
                            .withCategoryIds(expandedIds);

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria.categoryIds()).containsExactlyInAnyOrder(200L, 201L, 202L);
        }

        @Test
        @DisplayName("가격 범위가 있는 파라미터로 Criteria를 생성하면 가격 범위가 매핑된다")
        void createCriteria_WithPriceRange_MapsPriceRange() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithPriceRange(10000L, 50000L);

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria.minSalePrice()).isEqualTo(10000L);
            assertThat(criteria.maxSalePrice()).isEqualTo(50000L);
        }

        @Test
        @DisplayName("검색어가 있는 파라미터로 Criteria를 생성하면 검색어가 매핑된다")
        void createCriteria_WithSearchWord_MapsSearchWord() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithSearchWord(
                            "PRODUCT_GROUP_NAME", "테스트 상품");

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria.searchKeyword()).isEqualTo("PRODUCT_GROUP_NAME");
            assertThat(criteria.searchWord()).isEqualTo("테스트 상품");
        }

        @Test
        @DisplayName("displayYn 필터가 있는 파라미터로 Criteria를 생성하면 displayYn이 매핑된다")
        void createCriteria_WithDisplayYn_MapsDisplayYn() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithDisplayFilter("Y");

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria.displayYn()).isEqualTo("Y");
        }

        @Test
        @DisplayName("page, size 파라미터가 Criteria에 정상적으로 매핑된다")
        void createCriteria_WithPageAndSize_MapsPageAndSize() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParams(2, 50);

            // when
            LegacyProductGroupSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria.page()).isEqualTo(2);
            assertThat(criteria.size()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("toStandardSearchParams() - 레거시 파라미터 → 표준 ProductGroupSearchParams 변환")
    class ToStandardSearchParamsTest {

        @Test
        @DisplayName("기본 파라미터를 표준 SearchParams로 변환한다")
        void toStandardSearchParams_DefaultParams_ReturnsStandardParams() {
            // given
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).isEmpty();
            assertThat(result.sellerIds()).isEmpty();
            assertThat(result.brandIds()).isEmpty();
        }

        @Test
        @DisplayName("displayYn=Y이면 statuses에 ACTIVE가 포함된다")
        void toStandardSearchParams_DisplayYnY_HasActiveStatus() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithDisplayFilter("Y");

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result.statuses()).contains("ACTIVE");
        }

        @Test
        @DisplayName("displayYn=N이면 statuses에 INACTIVE가 포함된다")
        void toStandardSearchParams_DisplayYnN_HasInactiveStatus() {
            // given
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithDisplayFilter("N");

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result.statuses()).contains("INACTIVE");
        }

        @Test
        @DisplayName("sellerId가 있으면 sellerIds에 포함된다")
        void toStandardSearchParams_WithSellerId_HasSellerIds() {
            // given
            Long sellerId = 1L;
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithSeller(sellerId);

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result.sellerIds()).containsExactly(sellerId);
        }

        @Test
        @DisplayName("sellerId가 null이면 sellerIds는 비어 있다")
        void toStandardSearchParams_NullSellerId_EmptySellerIds() {
            // given
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result.sellerIds()).isEmpty();
        }

        @Test
        @DisplayName("카테고리 ID 목록이 resolve 후 표준 SearchParams에 반영된다")
        void toStandardSearchParams_WithCategoryIds_ReflectsInResult() {
            // given
            List<Long> categoryIds = List.of(200L, 201L);
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(200L)
                            .withCategoryIds(categoryIds);
            org.mockito.BDDMockito.given(categoryIdResolver.resolve(200L)).willReturn(300L);
            org.mockito.BDDMockito.given(categoryIdResolver.resolve(201L)).willReturn(301L);

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result.categoryIds()).containsExactlyInAnyOrder(300L, 301L);
        }

        @Test
        @DisplayName("검색 키워드/검색어가 표준 SearchParams에 반영된다")
        void toStandardSearchParams_WithSearchWord_ReflectsInResult() {
            // given
            String searchKeyword = "productGroupName";
            String searchWord = "테스트";
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithSearchWord(
                            searchKeyword, searchWord);

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result.searchField()).isEqualTo(searchKeyword);
            assertThat(result.searchWord()).isEqualTo(searchWord);
        }

        @Test
        @DisplayName("페이지/사이즈 파라미터가 표준 SearchParams에 반영된다")
        void toStandardSearchParams_WithCustomPage_ReflectsPageInResult() {
            // given
            int page = 2;
            int size = 10;
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParams(page, size);

            // when
            ProductGroupSearchParams result = sut.toStandardSearchParams(params);

            // then
            assertThat(result.page()).isEqualTo(page);
            assertThat(result.size()).isEqualTo(size);
        }
    }
}
