package com.ryuqq.marketplace.application.legacy.productgroup.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.port.out.query.LegacyProductGroupCompositeListQueryPort;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
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
@DisplayName("LegacyProductGroupCompositeListReadManager 단위 테스트")
class LegacyProductGroupCompositeListReadManagerTest {

    @InjectMocks private LegacyProductGroupCompositeListReadManager sut;

    @Mock private LegacyProductGroupCompositeListQueryPort compositeListQueryPort;

    @Nested
    @DisplayName("search() - 상품그룹 목록 조회")
    class SearchTest {

        @Test
        @DisplayName("Criteria로 Port에 위임하여 상품그룹 번들 목록을 반환한다")
        void search_ValidCriteria_ReturnsBundleList() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            List<LegacyProductGroupDetailBundle> expected = List.of(
                    LegacyProductGroupQueryFixtures.detailBundle(1L),
                    LegacyProductGroupQueryFixtures.detailBundle(2L));

            given(compositeListQueryPort.searchProductGroups(criteria)).willReturn(expected);

            // when
            List<LegacyProductGroupDetailBundle> result = sut.search(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(compositeListQueryPort).should().searchProductGroups(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void search_EmptyResult_ReturnsEmptyList() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();

            given(compositeListQueryPort.searchProductGroups(criteria)).willReturn(List.of());

            // when
            List<LegacyProductGroupDetailBundle> result = sut.search(criteria);

            // then
            assertThat(result).isEmpty();
            then(compositeListQueryPort).should().searchProductGroups(criteria);
        }

        @Test
        @DisplayName("카테고리 필터가 있는 Criteria로 Port를 호출한다")
        void search_WithCategoryFilter_CallsPortWithCriteria() {
            // given
            List<Long> categoryIds = List.of(200L, 201L, 202L);
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.criteriaWithCategoryIds(categoryIds);
            List<LegacyProductGroupDetailBundle> expected = List.of(
                    LegacyProductGroupQueryFixtures.detailBundle(1L));

            given(compositeListQueryPort.searchProductGroups(criteria)).willReturn(expected);

            // when
            List<LegacyProductGroupDetailBundle> result = sut.search(criteria);

            // then
            assertThat(result).hasSize(1);
            then(compositeListQueryPort).should().searchProductGroups(criteria);
        }

        @Test
        @DisplayName("제품 정보가 포함된 번들을 그대로 반환한다")
        void search_BundleWithProducts_ReturnsCompleteBundle() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            LegacyProductGroupDetailBundle bundleWithProducts =
                    LegacyProductGroupQueryFixtures.detailBundleWithProducts(1L);

            given(compositeListQueryPort.searchProductGroups(criteria))
                    .willReturn(List.of(bundleWithProducts));

            // when
            List<LegacyProductGroupDetailBundle> result = sut.search(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).products()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("count() - 전체 건수 조회")
    class CountTest {

        @Test
        @DisplayName("Criteria로 Port에 위임하여 전체 건수를 반환한다")
        void count_ValidCriteria_ReturnsTotalCount() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            long expectedCount = 100L;

            given(compositeListQueryPort.count(criteria)).willReturn(expectedCount);

            // when
            long result = sut.count(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(compositeListQueryPort).should().count(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환한다")
        void count_NoResults_ReturnsZero() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();

            given(compositeListQueryPort.count(criteria)).willReturn(0L);

            // when
            long result = sut.count(criteria);

            // then
            assertThat(result).isZero();
            then(compositeListQueryPort).should().count(criteria);
        }

        @Test
        @DisplayName("카테고리 필터가 있는 Criteria로 count Port를 호출한다")
        void count_WithCategoryFilter_CallsPortWithCriteria() {
            // given
            List<Long> categoryIds = List.of(200L, 201L);
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.criteriaWithCategoryIds(categoryIds);

            given(compositeListQueryPort.count(criteria)).willReturn(5L);

            // when
            long result = sut.count(criteria);

            // then
            assertThat(result).isEqualTo(5L);
            then(compositeListQueryPort).should().count(criteria);
        }
    }
}
