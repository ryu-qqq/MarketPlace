package com.ryuqq.marketplace.application.productgroup.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupCompositionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
@DisplayName("ProductGroupCompositionReadManager 단위 테스트")
class ProductGroupCompositionReadManagerTest {

    @InjectMocks private ProductGroupCompositionReadManager sut;

    @Mock private ProductGroupCompositionQueryPort compositionQueryPort;

    @Nested
    @DisplayName("getCompositeById() - ID로 목록용 Composite 단건 조회")
    class GetCompositeByIdTest {

        @Test
        @DisplayName("존재하는 ID로 ProductGroupListCompositeResult를 반환한다")
        void getCompositeById_Exists_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            ProductGroupListCompositeResult expected = createListCompositeResult(productGroupId);

            given(compositionQueryPort.findCompositeById(productGroupId))
                    .willReturn(Optional.of(expected));

            // when
            ProductGroupListCompositeResult result = sut.getCompositeById(productGroupId);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findCompositeById(productGroupId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 ProductGroupNotFoundException을 던진다")
        void getCompositeById_NotExists_ThrowsProductGroupNotFoundException() {
            // given
            Long productGroupId = 999L;

            given(compositionQueryPort.findCompositeById(productGroupId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getCompositeById(productGroupId))
                    .isInstanceOf(ProductGroupNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getDetailCompositeById() - ID로 상세용 Composite 단건 조회")
    class GetDetailCompositeByIdTest {

        @Test
        @DisplayName("존재하는 ID로 ProductGroupDetailCompositeQueryResult를 반환한다")
        void getDetailCompositeById_Exists_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            ProductGroupDetailCompositeQueryResult expected =
                    createDetailCompositeQueryResult(productGroupId);

            given(compositionQueryPort.findDetailCompositeById(productGroupId))
                    .willReturn(Optional.of(expected));

            // when
            ProductGroupDetailCompositeQueryResult result =
                    sut.getDetailCompositeById(productGroupId);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findDetailCompositeById(productGroupId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 상세 조회 시 ProductGroupNotFoundException을 던진다")
        void getDetailCompositeById_NotExists_ThrowsProductGroupNotFoundException() {
            // given
            Long productGroupId = 999L;

            given(compositionQueryPort.findDetailCompositeById(productGroupId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getDetailCompositeById(productGroupId))
                    .isInstanceOf(ProductGroupNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findCompositeByCriteria() - 검색 조건으로 목록 조회")
    class FindCompositeByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 ProductGroupListCompositeResult 목록을 반환한다")
        void findCompositeByCriteria_ValidCriteria_ReturnsResults() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            List<ProductGroupListCompositeResult> expected =
                    List.of(createListCompositeResult(1L), createListCompositeResult(2L));

            given(compositionQueryPort.findCompositeByCriteria(criteria)).willReturn(expected);

            // when
            List<ProductGroupListCompositeResult> result = sut.findCompositeByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findCompositeByCriteria(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void findCompositeByCriteria_NoResults_ReturnsEmptyList() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            given(compositionQueryPort.findCompositeByCriteria(criteria)).willReturn(List.of());

            // when
            List<ProductGroupListCompositeResult> result = sut.findCompositeByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 건수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 전체 건수를 반환한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            long expected = 42L;

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환한다")
        void countByCriteria_NoResults_ReturnsZero() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("findEnrichments() - productGroupIds로 enrichment 배치 조회")
    class FindEnrichmentsTest {

        @Test
        @DisplayName("productIds 목록으로 enrichment 결과를 반환한다")
        void findEnrichments_ValidIds_ReturnsEnrichments() {
            // given
            List<Long> productGroupIds = List.of(1L, 2L, 3L);
            List<ProductGroupEnrichmentResult> expected =
                    List.of(
                            new ProductGroupEnrichmentResult(
                                    1L, 10000, 20000, 20000, 10000, 10, List.of()),
                            new ProductGroupEnrichmentResult(
                                    2L, 15000, 25000, 25000, 15000, 15, List.of()),
                            new ProductGroupEnrichmentResult(
                                    3L, 5000, 10000, 10000, 5000, 5, List.of()));

            given(compositionQueryPort.findEnrichmentsByProductGroupIds(productGroupIds))
                    .willReturn(expected);

            // when
            List<ProductGroupEnrichmentResult> result = sut.findEnrichments(productGroupIds);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findEnrichmentsByProductGroupIds(productGroupIds);
        }

        @Test
        @DisplayName("빈 productIds 목록이면 쿼리를 실행하지 않고 빈 목록을 반환한다")
        void findEnrichments_EmptyIds_ReturnsEmptyWithoutQuery() {
            // given
            List<Long> emptyIds = List.of();

            // when
            List<ProductGroupEnrichmentResult> result = sut.findEnrichments(emptyIds);

            // then
            assertThat(result).isEmpty();
            then(compositionQueryPort).shouldHaveNoInteractions();
        }
    }

    // ===== 헬퍼 메서드 =====

    private ProductGroupListCompositeResult createListCompositeResult(Long productGroupId) {
        return ProductGroupListCompositeResult.ofBase(
                productGroupId,
                1L,
                "테스트셀러",
                100L,
                "테스트브랜드",
                200L,
                "테스트카테고리",
                "상의 > 긴팔",
                "1/200",
                2,
                "FASHION",
                "TOPS",
                "테스트 상품 그룹",
                "NONE",
                "ACTIVE",
                "https://example.com/thumb.jpg",
                1,
                Instant.now(),
                Instant.now());
    }

    private ProductGroupDetailCompositeQueryResult createDetailCompositeQueryResult(
            Long productGroupId) {
        return new ProductGroupDetailCompositeQueryResult(
                productGroupId,
                1L,
                "테스트셀러",
                100L,
                "테스트브랜드",
                200L,
                "테스트카테고리",
                "상의 > 긴팔",
                "1/200",
                "테스트 상품 그룹",
                "NONE",
                "ACTIVE",
                Instant.now(),
                Instant.now(),
                null,
                null);
    }
}
