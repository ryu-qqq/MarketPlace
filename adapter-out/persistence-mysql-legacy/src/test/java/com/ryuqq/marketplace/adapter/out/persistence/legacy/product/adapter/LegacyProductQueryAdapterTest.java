package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
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
 * LegacyProductQueryAdapterTest - 레거시 상품 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductQueryAdapter 단위 테스트")
class LegacyProductQueryAdapterTest {

    @Mock private LegacyProductGroupQueryDslRepository queryDslRepository;

    @InjectMocks private LegacyProductQueryAdapter queryAdapter;

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("상품이 있는 상품그룹 ID로 조회 시 상품 목록을 반환합니다")
        void findByProductGroupId_WithExistingId_ReturnsProducts() {
            // given
            long productGroupId = 1L;

            LegacyProductEntity productEntity =
                    LegacyProductEntity.create(10L, 1L, "N", "Y", 5, "N");
            LegacyProductOptionEntity optionEntity =
                    LegacyProductOptionEntity.create(10L, 100L, 1000L, 0L);

            given(queryDslRepository.findProductsByProductGroupId(1L))
                    .willReturn(List.of(productEntity));
            given(queryDslRepository.findProductOptionsByProductIds(List.of(10L)))
                    .willReturn(List.of(optionEntity));

            // when
            List<Product> results = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(results).hasSize(1);
            then(queryDslRepository).should().findProductsByProductGroupId(1L);
            then(queryDslRepository).should().findProductOptionsByProductIds(List.of(10L));
        }

        @Test
        @DisplayName("상품이 없는 상품그룹 ID로 조회 시 빈 목록을 반환합니다")
        void findByProductGroupId_WithNoProducts_ReturnsEmptyList() {
            // given
            long productGroupId = 999L;

            given(queryDslRepository.findProductsByProductGroupId(999L)).willReturn(List.of());

            // when
            List<Product> results = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(results).isEmpty();
            then(queryDslRepository).should().findProductsByProductGroupId(999L);
        }
    }
}
