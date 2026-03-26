package com.ryuqq.marketplace.domain.legacy.productgroup.query;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyProductGroupSearchCriteria 단위 테스트")
class LegacyProductGroupSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            // given
            Long sellerId = 1L;
            Long brandId = 2L;
            List<Long> categoryIds = List.of(10L, 20L);
            String managementType = "MANUAL";
            String soldOutYn = "N";
            String displayYn = "Y";
            Long minSalePrice = 1000L;
            Long maxSalePrice = 99000L;
            Long minDiscountRate = 0L;
            Long maxDiscountRate = 50L;
            String searchKeyword = "PRODUCT_GROUP_NAME";
            String searchWord = "나이키";
            LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);
            int page = 0;
            int size = 20;

            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            sellerId,
                            brandId,
                            categoryIds,
                            managementType,
                            soldOutYn,
                            displayYn,
                            minSalePrice,
                            maxSalePrice,
                            minDiscountRate,
                            maxDiscountRate,
                            searchKeyword,
                            searchWord,
                            startDate,
                            endDate,
                            page,
                            size);

            // then
            assertThat(criteria).isNotNull();
            assertThat(criteria.sellerId()).isEqualTo(sellerId);
            assertThat(criteria.brandId()).isEqualTo(brandId);
            assertThat(criteria.categoryIds()).containsExactly(10L, 20L);
            assertThat(criteria.managementType()).isEqualTo(managementType);
            assertThat(criteria.soldOutYn()).isEqualTo(soldOutYn);
            assertThat(criteria.displayYn()).isEqualTo(displayYn);
            assertThat(criteria.minSalePrice()).isEqualTo(minSalePrice);
            assertThat(criteria.maxSalePrice()).isEqualTo(maxSalePrice);
            assertThat(criteria.minDiscountRate()).isEqualTo(minDiscountRate);
            assertThat(criteria.maxDiscountRate()).isEqualTo(maxDiscountRate);
            assertThat(criteria.searchKeyword()).isEqualTo(searchKeyword);
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.startDate()).isEqualTo(startDate);
            assertThat(criteria.endDate()).isEqualTo(endDate);
            assertThat(criteria.page()).isEqualTo(page);
            assertThat(criteria.size()).isEqualTo(size);
        }
    }

    @Nested
    @DisplayName("nullable 필드 처리 테스트")
    class NullableFieldTest {

        @Test
        @DisplayName("nullable 필드를 모두 null로 전달하면 그대로 null이 된다")
        void createWithAllNullableFieldsAsNull() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.sellerId()).isNull();
            assertThat(criteria.brandId()).isNull();
            assertThat(criteria.managementType()).isNull();
            assertThat(criteria.soldOutYn()).isNull();
            assertThat(criteria.displayYn()).isNull();
            assertThat(criteria.minSalePrice()).isNull();
            assertThat(criteria.maxSalePrice()).isNull();
            assertThat(criteria.minDiscountRate()).isNull();
            assertThat(criteria.maxDiscountRate()).isNull();
            assertThat(criteria.searchKeyword()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.startDate()).isNull();
            assertThat(criteria.endDate()).isNull();
        }

        @Test
        @DisplayName("categoryIds가 null이면 빈 리스트로 초기화된다")
        void nullCategoryIdsInitializesEmptyList() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.categoryIds()).isNotNull();
            assertThat(criteria.categoryIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("page/size 기본값 확인 테스트")
    class PageSizeTest {

        @Test
        @DisplayName("page=0, size=20으로 생성하면 해당 값이 그대로 반환된다")
        void createWithDefaultPageSize() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.page()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("page=2, size=50으로 생성하면 해당 값이 그대로 반환된다")
        void createWithCustomPageSize() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 2, 50);

            // then
            assertThat(criteria.page()).isEqualTo(2);
            assertThat(criteria.size()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("offset() 계산 테스트")
    class OffsetTest {

        @Test
        @DisplayName("page=0, size=20이면 offset은 0이다")
        void offsetIsZeroWhenPageIsZero() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.offset()).isEqualTo(0L);
        }

        @Test
        @DisplayName("page=1, size=20이면 offset은 20이다")
        void offsetIsCalculatedCorrectly_Page1Size20() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 1, 20);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page=3, size=10이면 offset은 30이다")
        void offsetIsCalculatedCorrectly_Page3Size10() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 3, 10);

            // then
            assertThat(criteria.offset()).isEqualTo(30L);
        }

        @Test
        @DisplayName("page=5, size=50이면 offset은 250이다")
        void offsetIsCalculatedCorrectly_Page5Size50() {
            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 5, 50);

            // then
            assertThat(criteria.offset()).isEqualTo(250L);
        }
    }

    @Nested
    @DisplayName("hasCategoryFilter() 메서드 테스트")
    class HasCategoryFilterTest {

        @Test
        @DisplayName("categoryIds가 비어 있지 않으면 true를 반환한다")
        void returnsTrueWhenCategoryIdsExist() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null,
                            null,
                            List.of(1L, 2L),
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

            // then
            assertThat(criteria.hasCategoryFilter()).isTrue();
        }

        @Test
        @DisplayName("categoryIds가 비어 있으면 false를 반환한다")
        void returnsFalseWhenCategoryIdsEmpty() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 0, 20);

            // then
            assertThat(criteria.hasCategoryFilter()).isFalse();
        }

        @Test
        @DisplayName("categoryIds가 null이면 false를 반환한다")
        void returnsFalseWhenCategoryIdsNull() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasCategoryFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasDateRange() 메서드 테스트")
    class HasDateRangeTest {

        @Test
        @DisplayName("startDate와 endDate가 모두 있으면 true를 반환한다")
        void returnsTrueWhenBothDatesExist() {
            // given
            LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            startDate, endDate, 0, 20);

            // then
            assertThat(criteria.hasDateRange()).isTrue();
        }

        @Test
        @DisplayName("startDate만 있으면 false를 반환한다")
        void returnsFalseWhenOnlyStartDateExists() {
            // given
            LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            startDate, null, 0, 20);

            // then
            assertThat(criteria.hasDateRange()).isFalse();
        }

        @Test
        @DisplayName("endDate만 있으면 false를 반환한다")
        void returnsFalseWhenOnlyEndDateExists() {
            // given
            LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, endDate, 0, 20);

            // then
            assertThat(criteria.hasDateRange()).isFalse();
        }

        @Test
        @DisplayName("startDate와 endDate가 모두 null이면 false를 반환한다")
        void returnsFalseWhenBothDatesNull() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasDateRange()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 메서드 테스트")
    class HasSearchConditionTest {

        @Test
        @DisplayName("searchWord가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
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
                            "PRODUCT_GROUP_NAME",
                            "나이키",
                            null,
                            null,
                            0,
                            20);

            // then
            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("searchWord가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("searchWord가 공백 문자열이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, "   ",
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasPriceRange() 메서드 테스트")
    class HasPriceRangeTest {

        @Test
        @DisplayName("minSalePrice만 있으면 true를 반환한다")
        void returnsTrueWhenMinSalePriceExists() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, 1000L, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasPriceRange()).isTrue();
        }

        @Test
        @DisplayName("maxSalePrice만 있으면 true를 반환한다")
        void returnsTrueWhenMaxSalePriceExists() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, 99000L, null, null, null,
                            null, null, null, 0, 20);

            // then
            assertThat(criteria.hasPriceRange()).isTrue();
        }

        @Test
        @DisplayName("minSalePrice와 maxSalePrice가 모두 null이면 false를 반환한다")
        void returnsFalseWhenBothPricesNull() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasPriceRange()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasDiscountRateRange() 메서드 테스트")
    class HasDiscountRateRangeTest {

        @Test
        @DisplayName("minDiscountRate만 있으면 true를 반환한다")
        void returnsTrueWhenMinDiscountRateExists() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, 10L, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasDiscountRateRange()).isTrue();
        }

        @Test
        @DisplayName("maxDiscountRate만 있으면 true를 반환한다")
        void returnsTrueWhenMaxDiscountRateExists() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, 50L, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasDiscountRateRange()).isTrue();
        }

        @Test
        @DisplayName("minDiscountRate와 maxDiscountRate가 모두 null이면 false를 반환한다")
        void returnsFalseWhenBothDiscountRatesNull() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, 0, 20);

            // then
            assertThat(criteria.hasDiscountRateRange()).isFalse();
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("categoryIds는 불변 리스트로 복사된다")
        void categoryIdsAreCopiedAsUnmodifiableList() {
            // given
            List<Long> mutableList = new ArrayList<>(List.of(1L, 2L, 3L));

            // when
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null,
                            null,
                            mutableList,
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

            mutableList.add(99L);

            // then
            assertThat(criteria.categoryIds()).containsExactly(1L, 2L, 3L);
            assertThat(criteria.categoryIds()).isUnmodifiable();
        }
    }
}
