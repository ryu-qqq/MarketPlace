package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupCompositionQueryDslRepository;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.OptionGroupSummaryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelBaseBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductGroupCompositionQueryAdapterTest - 상품 그룹 Composition Query Adapter 단위 테스트.
 *
 * <p>크로스 도메인 JOIN 기반 목록/상세 조회 Adapter를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupCompositionQueryAdapter 단위 테스트")
class ProductGroupCompositionQueryAdapterTest {

    @Mock private ProductGroupCompositionQueryDslRepository compositionRepository;

    @Mock private ProductGroupSearchCriteria criteria;

    @InjectMocks private ProductGroupCompositionQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findCompositeById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findCompositeById 메서드 테스트")
    class FindCompositeByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 CompositeResult를 반환합니다")
        void findCompositeById_WithExistingId_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            ProductGroupListCompositeResult expected = buildListCompositeResult(productGroupId);

            given(compositionRepository.findCompositeById(productGroupId))
                    .willReturn(Optional.of(expected));

            // when
            Optional<ProductGroupListCompositeResult> result =
                    queryAdapter.findCompositeById(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(productGroupId);
            then(compositionRepository).should().findCompositeById(productGroupId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findCompositeById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long productGroupId = 999L;
            given(compositionRepository.findCompositeById(productGroupId))
                    .willReturn(Optional.empty());

            // when
            Optional<ProductGroupListCompositeResult> result =
                    queryAdapter.findCompositeById(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(compositionRepository).should().findCompositeById(productGroupId);
        }
    }

    // ========================================================================
    // 2. findCompositeByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findCompositeByCriteria 메서드 테스트")
    class FindCompositeByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 상품 그룹 목록을 조회합니다")
        void findCompositeByCriteria_WithValidCriteria_ReturnsList() {
            // given
            List<ProductGroupListCompositeResult> expected =
                    List.of(buildListCompositeResult(1L), buildListCompositeResult(2L));

            given(compositionRepository.findCompositeByCriteria(criteria)).willReturn(expected);

            // when
            List<ProductGroupListCompositeResult> result =
                    queryAdapter.findCompositeByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(compositionRepository).should().findCompositeByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findCompositeByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(compositionRepository.findCompositeByCriteria(criteria)).willReturn(List.of());

            // when
            List<ProductGroupListCompositeResult> result =
                    queryAdapter.findCompositeByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 상품 그룹 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(compositionRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
            then(compositionRepository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(compositionRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // 4. findEnrichmentsByProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findEnrichmentsByProductGroupIds 메서드 테스트")
    class FindEnrichmentsByProductGroupIdsTest {

        @Test
        @DisplayName("ID 목록으로 Enrichment 데이터를 배치 조회합니다")
        void findEnrichmentsByProductGroupIds_WithValidIds_ReturnsEnrichments() {
            // given
            List<Long> productGroupIds = List.of(1L, 2L, 3L);
            List<ProductGroupEnrichmentResult> expected =
                    List.of(
                            new ProductGroupEnrichmentResult(
                                    1L, 10000, 20000, 25000, 10000, 10, List.of()),
                            new ProductGroupEnrichmentResult(
                                    2L, 15000, 30000, 40000, 15000, 20, List.of()),
                            new ProductGroupEnrichmentResult(
                                    3L, 5000, 5000, 6000, 5000, 0, List.of()));

            given(compositionRepository.findEnrichmentsByProductGroupIds(productGroupIds))
                    .willReturn(expected);

            // when
            List<ProductGroupEnrichmentResult> result =
                    queryAdapter.findEnrichmentsByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).productGroupId()).isEqualTo(1L);
            then(compositionRepository).should().findEnrichmentsByProductGroupIds(productGroupIds);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회하면 빈 리스트를 반환합니다")
        void findEnrichmentsByProductGroupIds_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<Long> emptyIds = List.of();
            given(compositionRepository.findEnrichmentsByProductGroupIds(emptyIds))
                    .willReturn(List.of());

            // when
            List<ProductGroupEnrichmentResult> result =
                    queryAdapter.findEnrichmentsByProductGroupIds(emptyIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("옵션 그룹이 포함된 Enrichment 데이터를 정상 반환합니다")
        void findEnrichmentsByProductGroupIds_WithOptionGroups_ReturnsEnrichmentWithOptions() {
            // given
            List<Long> productGroupIds = List.of(1L);
            List<OptionGroupSummaryResult> optionGroups =
                    List.of(
                            new OptionGroupSummaryResult("색상", List.of("블랙", "화이트")),
                            new OptionGroupSummaryResult("사이즈", List.of("S", "M", "L")));
            ProductGroupEnrichmentResult enrichment =
                    new ProductGroupEnrichmentResult(
                            1L, 10000, 50000, 60000, 10000, 30, optionGroups);

            given(compositionRepository.findEnrichmentsByProductGroupIds(productGroupIds))
                    .willReturn(List.of(enrichment));

            // when
            List<ProductGroupEnrichmentResult> result =
                    queryAdapter.findEnrichmentsByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).optionGroups()).hasSize(2);
        }
    }

    // ========================================================================
    // 5. findDetailCompositeById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findDetailCompositeById 메서드 테스트")
    class FindDetailCompositeByIdTest {

        @Test
        @DisplayName("존재하는 ID로 상세 Composite를 조회합니다")
        void findDetailCompositeById_WithExistingId_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroupDetailCompositeQueryResult expected =
                    new ProductGroupDetailCompositeQueryResult(
                            productGroupId,
                            1L,
                            "테스트 셀러",
                            100L,
                            "테스트 브랜드",
                            200L,
                            "상의",
                            "패션 > 상의",
                            "1/200",
                            "테스트 상품 그룹",
                            "NONE",
                            "ACTIVE",
                            now,
                            now,
                            null,
                            null);

            given(compositionRepository.findDetailCompositeById(productGroupId))
                    .willReturn(Optional.of(expected));

            // when
            Optional<ProductGroupDetailCompositeQueryResult> result =
                    queryAdapter.findDetailCompositeById(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(productGroupId);
            then(compositionRepository).should().findDetailCompositeById(productGroupId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findDetailCompositeById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long productGroupId = 999L;
            given(compositionRepository.findDetailCompositeById(productGroupId))
                    .willReturn(Optional.empty());

            // when
            Optional<ProductGroupDetailCompositeQueryResult> result =
                    queryAdapter.findDetailCompositeById(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(compositionRepository).should().findDetailCompositeById(productGroupId);
        }
    }

    // ========================================================================
    // 6. findExcelBaseBundleByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findExcelBaseBundleByCriteria 메서드 테스트")
    class FindExcelBaseBundleByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 엑셀 기본 번들을 조회합니다")
        void findExcelBaseBundleByCriteria_WithValidCriteria_ReturnsBundle() {
            // given
            ProductGroupExcelBaseBundle expected =
                    new ProductGroupExcelBaseBundle(
                            List.of(buildListCompositeResult(1L)),
                            Map.of(1L, "https://cdn.example.com/desc"),
                            1L);

            given(compositionRepository.findExcelBaseBundleByCriteria(criteria))
                    .willReturn(expected);

            // when
            ProductGroupExcelBaseBundle result =
                    queryAdapter.findExcelBaseBundleByCriteria(criteria);

            // then
            assertThat(result.composites()).hasSize(1);
            assertThat(result.descriptionCdnUrlByProductGroupId()).containsKey(1L);
            assertThat(result.totalElements()).isEqualTo(1L);
            then(compositionRepository).should().findExcelBaseBundleByCriteria(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 빈 번들을 반환합니다")
        void findExcelBaseBundleByCriteria_WithNoResults_ReturnsEmptyBundle() {
            // given
            ProductGroupExcelBaseBundle expected =
                    new ProductGroupExcelBaseBundle(List.of(), Map.of(), 0);

            given(compositionRepository.findExcelBaseBundleByCriteria(criteria))
                    .willReturn(expected);

            // when
            ProductGroupExcelBaseBundle result =
                    queryAdapter.findExcelBaseBundleByCriteria(criteria);

            // then
            assertThat(result.composites()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }
    }

    // ========================================================================
    // 7. findProductsWithOptionNamesByProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProductsWithOptionNamesByProductGroupIds 메서드 테스트")
    class FindProductsWithOptionNamesByProductGroupIdsTest {

        @Test
        @DisplayName("ID 목록으로 옵션 이름이 포함된 상품 데이터를 배치 조회합니다")
        void findProductsWithOptionNames_WithValidIds_ReturnsMap() {
            // given
            List<Long> productGroupIds = List.of(1L, 2L);
            Instant now = Instant.now();
            ProductResult productResult =
                    new ProductResult(
                            10L, 1L, "SKU-001", 30000, 25000, null, 16, 100, "ACTIVE", 1, List.of(),
                            now, now);
            Map<Long, List<ProductResult>> expected = Map.of(1L, List.of(productResult));

            given(
                            compositionRepository.findProductsWithOptionNamesByProductGroupIds(
                                    productGroupIds))
                    .willReturn(expected);

            // when
            Map<Long, List<ProductResult>> result =
                    queryAdapter.findProductsWithOptionNamesByProductGroupIds(productGroupIds);

            // then
            assertThat(result).containsKey(1L);
            assertThat(result.get(1L)).hasSize(1);
            then(compositionRepository)
                    .should()
                    .findProductsWithOptionNamesByProductGroupIds(productGroupIds);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회하면 빈 Map을 반환합니다")
        void findProductsWithOptionNames_WithEmptyIds_ReturnsEmptyMap() {
            // given
            List<Long> emptyIds = List.of();
            given(compositionRepository.findProductsWithOptionNamesByProductGroupIds(emptyIds))
                    .willReturn(Map.of());

            // when
            Map<Long, List<ProductResult>> result =
                    queryAdapter.findProductsWithOptionNamesByProductGroupIds(emptyIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // Helper
    // ========================================================================

    private ProductGroupListCompositeResult buildListCompositeResult(Long id) {
        Instant now = Instant.now();
        return ProductGroupListCompositeResult.ofBase(
                id,
                1L,
                "테스트 셀러",
                100L,
                "테스트 브랜드",
                200L,
                "상의",
                "패션 > 상의",
                "1/200",
                2,
                "패션",
                "상의",
                "테스트 상품 그룹 " + id,
                "NONE",
                "ACTIVE",
                null,
                0,
                now,
                now);
    }
}
