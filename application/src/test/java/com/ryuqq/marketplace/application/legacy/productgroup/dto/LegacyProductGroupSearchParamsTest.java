package com.ryuqq.marketplace.application.legacy.productgroup.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyProductGroupSearchParams 단위 테스트")
class LegacyProductGroupSearchParamsTest {

    @Nested
    @DisplayName("of() - 팩토리 메서드 생성")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터가 null인 기본 파라미터를 생성한다")
        void of_AllNullParams_CreatesDefaultParams() {
            // when
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();

            // then
            assertThat(params.sellerId()).isNull();
            assertThat(params.brandId()).isNull();
            assertThat(params.categoryId()).isNull();
            assertThat(params.categoryIds()).isEmpty();
            assertThat(params.managementType()).isNull();
            assertThat(params.soldOutYn()).isNull();
            assertThat(params.displayYn()).isNull();
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("categoryId가 있으면 categoryIds를 단일 원소 목록으로 초기화한다")
        void of_WithCategoryId_InitializesCategoryIdsWithSingleElement() {
            // when
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(200L);

            // then
            assertThat(params.categoryId()).isEqualTo(200L);
            assertThat(params.categoryIds()).containsExactly(200L);
        }

        @Test
        @DisplayName("categoryId가 null이면 categoryIds를 빈 목록으로 초기화한다")
        void of_WithNullCategoryId_InitializesEmptyCategoryIds() {
            // when
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();

            // then
            assertThat(params.categoryId()).isNull();
            assertThat(params.categoryIds()).isEmpty();
        }

        @Test
        @DisplayName("sellerId 필터가 있는 파라미터를 생성한다")
        void of_WithSellerId_CreateParamsWithSellerId() {
            // when
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithSeller(1L);

            // then
            assertThat(params.sellerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("가격 범위 필터가 있는 파라미터를 생성한다")
        void of_WithPriceRange_CreateParamsWithPriceRange() {
            // when
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithPriceRange(10000L, 50000L);

            // then
            assertThat(params.minSalePrice()).isEqualTo(10000L);
            assertThat(params.maxSalePrice()).isEqualTo(50000L);
        }

        @Test
        @DisplayName("page, size 파라미터가 정상적으로 설정된다")
        void of_WithPageAndSize_SetsPageAndSize() {
            // when
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParams(2, 50);

            // then
            assertThat(params.page()).isEqualTo(2);
            assertThat(params.size()).isEqualTo(50);
        }

        @Test
        @DisplayName("검색 키워드와 검색어가 있는 파라미터를 생성한다")
        void of_WithSearchKeywordAndWord_CreatesSearchParams() {
            // when
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithSearchWord(
                            "PRODUCT_GROUP_NAME", "테스트 상품");

            // then
            assertThat(params.searchKeyword()).isEqualTo("PRODUCT_GROUP_NAME");
            assertThat(params.searchWord()).isEqualTo("테스트 상품");
        }

        @Test
        @DisplayName("날짜 범위가 있는 파라미터를 직접 생성한다")
        void of_WithDateRange_CreateParamsWithDateRange() {
            // given
            LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

            // when
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupSearchParams.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            startDate, endDate, 0, 20);

            // then
            assertThat(params.startDate()).isEqualTo(startDate);
            assertThat(params.endDate()).isEqualTo(endDate);
        }
    }

    @Nested
    @DisplayName("withCategoryIds() - 카테고리 ID 확장")
    class WithCategoryIdsTest {

        @Test
        @DisplayName("확장된 카테고리 ID 목록으로 새 인스턴스를 반환한다")
        void withCategoryIds_ValidList_ReturnsNewInstanceWithExpandedIds() {
            // given
            LegacyProductGroupSearchParams original =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(100L);
            List<Long> expandedIds = List.of(100L, 101L, 102L, 103L);

            // when
            LegacyProductGroupSearchParams expanded = original.withCategoryIds(expandedIds);

            // then
            assertThat(expanded.categoryIds()).containsExactlyInAnyOrder(100L, 101L, 102L, 103L);
            assertThat(expanded.categoryIds()).hasSize(4);
        }

        @Test
        @DisplayName("원본 인스턴스의 다른 필드는 변경되지 않는다")
        void withCategoryIds_RetainsOtherFields() {
            // given
            LegacyProductGroupSearchParams original =
                    LegacyProductGroupQueryFixtures.searchParamsWithSeller(1L);
            List<Long> expandedIds = List.of(10L, 20L);

            // when
            LegacyProductGroupSearchParams expanded = original.withCategoryIds(expandedIds);

            // then
            assertThat(expanded.sellerId()).isEqualTo(original.sellerId());
            assertThat(expanded.page()).isEqualTo(original.page());
            assertThat(expanded.size()).isEqualTo(original.size());
        }

        @Test
        @DisplayName("null 목록을 전달하면 빈 목록으로 설정한다")
        void withCategoryIds_NullList_SetsEmptyCategoryIds() {
            // given
            LegacyProductGroupSearchParams original =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(100L);

            // when
            LegacyProductGroupSearchParams expanded = original.withCategoryIds(null);

            // then
            assertThat(expanded.categoryIds()).isEmpty();
        }

        @Test
        @DisplayName("원본 categoryId는 withCategoryIds 후에도 유지된다")
        void withCategoryIds_PreservesOriginalCategoryId() {
            // given
            Long originalCategoryId = 100L;
            LegacyProductGroupSearchParams original =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(originalCategoryId);
            List<Long> expandedIds = List.of(100L, 101L, 102L);

            // when
            LegacyProductGroupSearchParams expanded = original.withCategoryIds(expandedIds);

            // then
            assertThat(expanded.categoryId()).isEqualTo(originalCategoryId);
        }

        @Test
        @DisplayName("withCategoryIds는 원본 인스턴스를 변경하지 않는다")
        void withCategoryIds_DoesNotMutateOriginal() {
            // given
            LegacyProductGroupSearchParams original =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(100L);
            List<Long> originalCategoryIds = original.categoryIds();

            // when
            original.withCategoryIds(List.of(100L, 101L, 102L));

            // then
            assertThat(original.categoryIds()).isEqualTo(originalCategoryIds);
        }
    }
}
