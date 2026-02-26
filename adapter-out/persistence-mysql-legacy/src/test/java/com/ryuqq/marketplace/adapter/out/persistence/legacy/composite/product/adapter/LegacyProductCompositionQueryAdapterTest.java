package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
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
 * LegacyProductCompositionQueryAdapterTest - 레거시 상품 Composition Query Adapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductCompositionQueryAdapter 단위 테스트")
class LegacyProductCompositionQueryAdapterTest {

    @Mock private LegacyProductCompositeQueryDslRepository repository;
    @Mock private LegacyProductCompositeMapper mapper;

    @InjectMocks private LegacyProductCompositionQueryAdapter queryAdapter;

    @Nested
    @DisplayName("findProductsByProductGroupId 메서드 테스트")
    class FindProductsByProductGroupIdTest {

        @Test
        @DisplayName("상품그룹 ID로 조회 시 Composite 결과 목록을 반환합니다")
        void findProductsByProductGroupId_WithExistingId_ReturnsCompositeResults() {
            // given
            long productGroupId = 10L;
            List<LegacyProductOptionQueryDto> rows =
                    List.of(
                            new LegacyProductOptionQueryDto(
                                    1L, productGroupId, "N", 5, 100L, 200L, "색상", "빨강"),
                            new LegacyProductOptionQueryDto(
                                    2L, productGroupId, "N", 3, 100L, 201L, "색상", "파랑"));

            LegacyProductCompositeResult result1 =
                    new LegacyProductCompositeResult(1L, productGroupId, 5, false, List.of());
            LegacyProductCompositeResult result2 =
                    new LegacyProductCompositeResult(2L, productGroupId, 3, false, List.of());

            given(repository.fetchProductsWithOptions(productGroupId)).willReturn(rows);
            given(mapper.toCompositeResults(rows)).willReturn(List.of(result1, result2));

            // when
            List<LegacyProductCompositeResult> results =
                    queryAdapter.findProductsByProductGroupId(productGroupId);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).productId()).isEqualTo(1L);
            assertThat(results.get(1).productId()).isEqualTo(2L);
            then(repository).should().fetchProductsWithOptions(productGroupId);
            then(mapper).should().toCompositeResults(rows);
        }

        @Test
        @DisplayName("상품이 없는 경우 빈 목록을 반환합니다")
        void findProductsByProductGroupId_WithNoProducts_ReturnsEmptyList() {
            // given
            long productGroupId = 99L;
            given(repository.fetchProductsWithOptions(productGroupId)).willReturn(List.of());
            given(mapper.toCompositeResults(List.of())).willReturn(List.of());

            // when
            List<LegacyProductCompositeResult> results =
                    queryAdapter.findProductsByProductGroupId(productGroupId);

            // then
            assertThat(results).isEmpty();
            then(repository).should().fetchProductsWithOptions(productGroupId);
        }
    }
}
