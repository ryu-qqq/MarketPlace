package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupCompositeListReadManager;
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
@DisplayName("LegacyProductGroupListReadFacade 단위 테스트")
class LegacyProductGroupListReadFacadeTest {

    @InjectMocks private LegacyProductGroupListReadFacade sut;

    @Mock private LegacyProductGroupCompositeListReadManager compositeListReadManager;

    @Nested
    @DisplayName("getBundles() - 상품그룹 번들 목록 조회")
    class GetBundlesTest {

        @Test
        @DisplayName("Criteria로 CompositeListReadManager에 위임하여 번들 목록을 반환한다")
        void getBundles_ValidCriteria_ReturnsBundleList() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            List<LegacyProductGroupDetailBundle> expected =
                    List.of(
                            LegacyProductGroupQueryFixtures.detailBundle(1L),
                            LegacyProductGroupQueryFixtures.detailBundle(2L));

            given(compositeListReadManager.search(criteria)).willReturn(expected);

            // when
            List<LegacyProductGroupDetailBundle> result = sut.getBundles(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(compositeListReadManager).should().search(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void getBundles_EmptyResult_ReturnsEmptyList() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();

            given(compositeListReadManager.search(criteria)).willReturn(List.of());

            // when
            List<LegacyProductGroupDetailBundle> result = sut.getBundles(criteria);

            // then
            assertThat(result).isEmpty();
            then(compositeListReadManager).should().search(criteria);
        }

        @Test
        @DisplayName("제품 정보가 포함된 번들을 반환한다")
        void getBundles_BundleWithProducts_ReturnsCorrectBundle() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            LegacyProductGroupDetailBundle bundleWithProducts =
                    LegacyProductGroupQueryFixtures.detailBundleWithProducts(1L);

            given(compositeListReadManager.search(criteria))
                    .willReturn(List.of(bundleWithProducts));

            // when
            List<LegacyProductGroupDetailBundle> result = sut.getBundles(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).products()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("count() - 전체 건수 조회")
    class CountTest {

        @Test
        @DisplayName("Criteria로 CompositeListReadManager에 위임하여 전체 건수를 반환한다")
        void count_ValidCriteria_ReturnsTotalCount() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            long expectedCount = 42L;

            given(compositeListReadManager.count(criteria)).willReturn(expectedCount);

            // when
            long result = sut.count(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(compositeListReadManager).should().count(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환한다")
        void count_EmptyResult_ReturnsZero() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();

            given(compositeListReadManager.count(criteria)).willReturn(0L);

            // when
            long result = sut.count(criteria);

            // then
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("getBundles와 count를 각각 독립적으로 호출할 수 있다")
        void getBundlesAndCount_CalledIndependently_EachDelegatesToManager() {
            // given
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            List<LegacyProductGroupDetailBundle> bundles =
                    List.of(LegacyProductGroupQueryFixtures.detailBundle(1L));
            long totalCount = 1L;

            given(compositeListReadManager.search(criteria)).willReturn(bundles);
            given(compositeListReadManager.count(criteria)).willReturn(totalCount);

            // when
            List<LegacyProductGroupDetailBundle> resultBundles = sut.getBundles(criteria);
            long resultCount = sut.count(criteria);

            // then
            assertThat(resultBundles).hasSize(1);
            assertThat(resultCount).isEqualTo(1L);
            then(compositeListReadManager).should().search(criteria);
            then(compositeListReadManager).should().count(criteria);
        }
    }
}
