package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupListQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductGroupListQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductGroupCompositeListQueryAdapterTest - 레거시 상품그룹 Composite 목록 조회 Adapter 단위 테스트.
 *
 * <p>3-Phase Query 흐름 및 빈 결과 조기 반환 등을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupCompositeListQueryAdapter 단위 테스트")
class LegacyProductGroupCompositeListQueryAdapterTest {

    @Mock private LegacyProductGroupListQueryDslRepository listQueryDslRepository;
    @Mock private LegacyProductCompositeMapper productCompositeMapper;

    @InjectMocks private LegacyProductGroupCompositeListQueryAdapter queryAdapter;

    private LegacyProductGroupSearchCriteria defaultCriteria() {
        return LegacyProductGroupSearchCriteria.of(
                null, null, List.of(), null, null, null,
                null, null, null, null, null, null,
                null, null, 0, 10);
    }

    private LegacyProductGroupListQueryDto buildListQueryDto(long productGroupId) {
        return new LegacyProductGroupListQueryDto(
                productGroupId,
                "테스트 상품그룹",
                10L,
                "테스트 셀러",
                20L,
                "나이키",
                30L,
                "패션>의류>상의",
                "SINGLE",
                "SYSTEM",
                10000L,
                9000L,
                8000L,
                500L,
                5,
                10,
                "N",
                "Y",
                "NEW",
                "DOMESTIC",
                "STYLE001",
                "admin",
                "admin",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 6, 1, 0, 0),
                "https://cdn.example.com/main.jpg");
    }

    private LegacyProductOptionQueryDto buildOptionDto(long productId, long productGroupId) {
        return new LegacyProductOptionQueryDto(
                productId, productGroupId, "N", 5, 100L, 200L, "색상", "블랙");
    }

    // ========================================================================
    // 1. searchProductGroups 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchProductGroups 메서드 테스트")
    class SearchProductGroupsTest {

        @Test
        @DisplayName("상품그룹 ID 목록 조회 후 상세 및 상품 데이터를 조립하여 반환합니다")
        void searchProductGroups_WithExistingData_ReturnsBundleList() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();
            long productGroupId = 1L;

            List<Long> productGroupIds = List.of(productGroupId);
            LegacyProductGroupListQueryDto detail = buildListQueryDto(productGroupId);
            LegacyProductOptionQueryDto optionRow = buildOptionDto(10L, productGroupId);

            LegacyProductCompositeResult compositeResult =
                    new LegacyProductCompositeResult(10L, productGroupId, 5, false, List.of());

            given(listQueryDslRepository.fetchProductGroupIds(criteria))
                    .willReturn(productGroupIds);
            given(listQueryDslRepository.fetchProductGroupDetails(productGroupIds))
                    .willReturn(List.of(detail));
            given(listQueryDslRepository.fetchProductsWithOptions(productGroupIds))
                    .willReturn(List.of(optionRow));
            given(productCompositeMapper.toCompositeResults(List.of(optionRow)))
                    .willReturn(List.of(compositeResult));

            // when
            List<LegacyProductGroupDetailBundle> result =
                    queryAdapter.searchProductGroups(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).composite().productGroupId()).isEqualTo(productGroupId);
            assertThat(result.get(0).composite().productGroupName()).isEqualTo("테스트 상품그룹");
            assertThat(result.get(0).products()).hasSize(1);
            assertThat(result.get(0).products().get(0).productId()).isEqualTo(10L);

            then(listQueryDslRepository).should().fetchProductGroupIds(criteria);
            then(listQueryDslRepository).should().fetchProductGroupDetails(productGroupIds);
            then(listQueryDslRepository).should().fetchProductsWithOptions(productGroupIds);
        }

        @Test
        @DisplayName("Phase 1에서 빈 ID 목록이 반환되면 Phase 2/3를 호출하지 않고 빈 목록을 반환합니다")
        void searchProductGroups_WithEmptyIds_ReturnsEmptyListWithoutPhase2And3() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();

            given(listQueryDslRepository.fetchProductGroupIds(criteria)).willReturn(List.of());

            // when
            List<LegacyProductGroupDetailBundle> result =
                    queryAdapter.searchProductGroups(criteria);

            // then
            assertThat(result).isEmpty();
            then(listQueryDslRepository).should().fetchProductGroupIds(criteria);
            then(listQueryDslRepository).should(never()).fetchProductGroupDetails(List.of());
            then(listQueryDslRepository).should(never()).fetchProductsWithOptions(List.of());
            then(productCompositeMapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("상품(Phase 3)이 없는 경우 상품 목록이 빈 번들을 반환합니다")
        void searchProductGroups_WithNoProducts_ReturnsBundleWithEmptyProducts() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();
            long productGroupId = 2L;

            List<Long> productGroupIds = List.of(productGroupId);
            LegacyProductGroupListQueryDto detail = buildListQueryDto(productGroupId);

            given(listQueryDslRepository.fetchProductGroupIds(criteria))
                    .willReturn(productGroupIds);
            given(listQueryDslRepository.fetchProductGroupDetails(productGroupIds))
                    .willReturn(List.of(detail));
            given(listQueryDslRepository.fetchProductsWithOptions(productGroupIds))
                    .willReturn(List.of());

            // when
            List<LegacyProductGroupDetailBundle> result =
                    queryAdapter.searchProductGroups(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).products()).isEmpty();
        }

        @Test
        @DisplayName("여러 상품그룹 ID의 순서가 Phase 1 결과 순서와 동일하게 유지됩니다")
        void searchProductGroups_WithMultipleIds_PreservesPhase1Order() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();
            long id1 = 5L;
            long id2 = 3L;
            long id3 = 1L;

            List<Long> productGroupIds = List.of(id1, id2, id3);
            LegacyProductGroupListQueryDto detail1 = buildListQueryDto(id1);
            LegacyProductGroupListQueryDto detail2 = buildListQueryDto(id2);
            LegacyProductGroupListQueryDto detail3 = buildListQueryDto(id3);

            given(listQueryDslRepository.fetchProductGroupIds(criteria))
                    .willReturn(productGroupIds);
            given(listQueryDslRepository.fetchProductGroupDetails(productGroupIds))
                    .willReturn(List.of(detail3, detail1, detail2));
            given(listQueryDslRepository.fetchProductsWithOptions(productGroupIds))
                    .willReturn(List.of());

            // when
            List<LegacyProductGroupDetailBundle> result =
                    queryAdapter.searchProductGroups(criteria);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).composite().productGroupId()).isEqualTo(id1);
            assertThat(result.get(1).composite().productGroupId()).isEqualTo(id2);
            assertThat(result.get(2).composite().productGroupId()).isEqualTo(id3);
        }

        @Test
        @DisplayName("메인 이미지 URL이 null이면 이미지 목록이 빈 상태로 반환됩니다")
        void searchProductGroups_WithNullMainImageUrl_ReturnsBundleWithEmptyImages() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();
            long productGroupId = 3L;

            LegacyProductGroupListQueryDto detailWithNoImage =
                    new LegacyProductGroupListQueryDto(
                            productGroupId,
                            "이미지 없는 상품그룹",
                            10L,
                            "테스트 셀러",
                            20L,
                            "나이키",
                            30L,
                            "패션>의류>상의",
                            "SINGLE",
                            "SYSTEM",
                            10000L,
                            9000L,
                            8000L,
                            500L,
                            5,
                            10,
                            "N",
                            "Y",
                            "NEW",
                            "DOMESTIC",
                            "STYLE001",
                            "admin",
                            "admin",
                            LocalDateTime.of(2025, 1, 1, 0, 0),
                            LocalDateTime.of(2025, 6, 1, 0, 0),
                            null);

            List<Long> productGroupIds = List.of(productGroupId);

            given(listQueryDslRepository.fetchProductGroupIds(criteria))
                    .willReturn(productGroupIds);
            given(listQueryDslRepository.fetchProductGroupDetails(productGroupIds))
                    .willReturn(List.of(detailWithNoImage));
            given(listQueryDslRepository.fetchProductsWithOptions(productGroupIds))
                    .willReturn(List.of());

            // when
            List<LegacyProductGroupDetailBundle> result =
                    queryAdapter.searchProductGroups(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).composite().images()).isEmpty();
        }

        @Test
        @DisplayName("soldOutYn = 'Y' 상품그룹은 soldOut = true로 변환됩니다")
        void searchProductGroups_WithSoldOutY_ReturnsSoldOutTrue() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();
            long productGroupId = 4L;

            LegacyProductGroupListQueryDto soldOutDetail =
                    new LegacyProductGroupListQueryDto(
                            productGroupId,
                            "품절 상품그룹",
                            10L,
                            "테스트 셀러",
                            20L,
                            "나이키",
                            30L,
                            "패션>의류>상의",
                            "SINGLE",
                            "SYSTEM",
                            10000L,
                            9000L,
                            8000L,
                            0L,
                            0,
                            0,
                            "Y",
                            "Y",
                            "NEW",
                            "DOMESTIC",
                            "STYLE001",
                            "admin",
                            "admin",
                            LocalDateTime.of(2025, 1, 1, 0, 0),
                            LocalDateTime.of(2025, 6, 1, 0, 0),
                            "https://cdn.example.com/main.jpg");

            given(listQueryDslRepository.fetchProductGroupIds(criteria))
                    .willReturn(List.of(productGroupId));
            given(listQueryDslRepository.fetchProductGroupDetails(List.of(productGroupId)))
                    .willReturn(List.of(soldOutDetail));
            given(listQueryDslRepository.fetchProductsWithOptions(List.of(productGroupId)))
                    .willReturn(List.of());

            // when
            List<LegacyProductGroupDetailBundle> result =
                    queryAdapter.searchProductGroups(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).composite().soldOut()).isTrue();
        }
    }

    // ========================================================================
    // 2. count 테스트
    // ========================================================================

    @Nested
    @DisplayName("count 메서드 테스트")
    class CountTest {

        @Test
        @DisplayName("검색 조건에 맞는 전체 건수를 반환합니다")
        void count_WithCriteria_ReturnsCount() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();
            given(listQueryDslRepository.count(criteria)).willReturn(42L);

            // when
            long result = queryAdapter.count(criteria);

            // then
            assertThat(result).isEqualTo(42L);
            then(listQueryDslRepository).should().count(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void count_WithNoCriteria_ReturnsZero() {
            // given
            LegacyProductGroupSearchCriteria criteria = defaultCriteria();
            given(listQueryDslRepository.count(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.count(criteria);

            // then
            assertThat(result).isZero();
            then(listQueryDslRepository).should().count(criteria);
        }
    }
}
