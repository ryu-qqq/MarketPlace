package com.ryuqq.marketplace.adapter.out.persistence.product.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.product.ProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.mapper.ProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.product.repository.ProductQueryDslRepository;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
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

/**
 * ProductQueryAdapterTest - 상품 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductQueryAdapter 단위 테스트")
class ProductQueryAdapterTest {

    @Mock private ProductQueryDslRepository queryDslRepository;

    @Mock private ProductJpaEntityMapper mapper;

    @InjectMocks private ProductQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            ProductId productId = ProductId.of(1L);
            ProductJpaEntity entity = ProductJpaEntityFixtures.activeEntity(1L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();
            Product domain = ProductFixtures.activeProduct(1L);

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(queryDslRepository.findOptionMappingsByProductId(1L)).willReturn(mappings);
            given(mapper.toDomain(entity, mappings)).willReturn(domain);

            // when
            Optional<Product> result = queryAdapter.findById(productId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            ProductId productId = ProductId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<Product> result = queryAdapter.findById(productId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("상품 그룹 ID로 상품 목록을 조회합니다")
        void findByProductGroupId_WithValidGroupId_ReturnsDomainList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            ProductJpaEntity entity1 = ProductJpaEntityFixtures.activeEntity(1L, 1L);
            ProductJpaEntity entity2 = ProductJpaEntityFixtures.activeEntity(2L, 1L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();
            Product domain1 = ProductFixtures.activeProduct(1L);
            Product domain2 = ProductFixtures.activeProduct(2L);

            given(queryDslRepository.findByProductGroupId(1L))
                    .willReturn(List.of(entity1, entity2));
            given(queryDslRepository.findOptionMappingsByProductIds(List.of(1L, 2L)))
                    .willReturn(mappings);
            given(mapper.toDomain(entity1, List.of())).willReturn(domain1);
            given(mapper.toDomain(entity2, List.of())).willReturn(domain2);

            // when
            List<Product> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByProductGroupId(1L);
        }

        @Test
        @DisplayName("상품이 없는 그룹 ID로 조회 시 빈 리스트를 반환합니다")
        void findByProductGroupId_WithNoProducts_ReturnsEmptyList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(999L);
            given(queryDslRepository.findByProductGroupId(999L)).willReturn(List.of());

            // when
            List<Product> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByProductGroupIdAndIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdAndIdIn 메서드 테스트")
    class FindByProductGroupIdAndIdInTest {

        @Test
        @DisplayName("그룹 ID와 상품 ID 목록으로 상품을 조회합니다")
        void findByProductGroupIdAndIdIn_WithValidParams_ReturnsDomainList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            List<ProductId> productIds = List.of(ProductId.of(1L), ProductId.of(2L));
            ProductJpaEntity entity1 = ProductJpaEntityFixtures.activeEntity(1L, 1L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();
            Product domain1 = ProductFixtures.activeProduct(1L);

            given(queryDslRepository.findByProductGroupIdAndIdIn(1L, List.of(1L, 2L)))
                    .willReturn(List.of(entity1));
            given(queryDslRepository.findOptionMappingsByProductIds(List.of(1L)))
                    .willReturn(mappings);
            given(mapper.toDomain(entity1, List.of())).willReturn(domain1);

            // when
            List<Product> result =
                    queryAdapter.findByProductGroupIdAndIdIn(productGroupId, productIds);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByProductGroupIdAndIdIn_WithNoResults_ReturnsEmptyList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            List<ProductId> productIds = List.of(ProductId.of(999L));

            given(queryDslRepository.findByProductGroupIdAndIdIn(1L, List.of(999L)))
                    .willReturn(List.of());

            // when
            List<Product> result =
                    queryAdapter.findByProductGroupIdAndIdIn(productGroupId, productIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findByProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdIn 메서드 테스트")
    class FindByProductGroupIdInTest {

        @Test
        @DisplayName("여러 ProductGroupId로 상품 목록을 배치 조회합니다")
        void findByProductGroupIdIn_WithValidIds_ReturnsDomainList() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(1L), ProductGroupId.of(2L));
            ProductJpaEntity entity1 = ProductJpaEntityFixtures.activeEntity(1L, 1L);
            ProductJpaEntity entity2 = ProductJpaEntityFixtures.activeEntity(2L, 2L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();
            Product domain1 = ProductFixtures.activeProduct(1L);
            Product domain2 = ProductFixtures.activeProduct(2L);

            given(queryDslRepository.findByProductGroupIdIn(List.of(1L, 2L)))
                    .willReturn(List.of(entity1, entity2));
            given(queryDslRepository.findOptionMappingsByProductIds(List.of(1L, 2L)))
                    .willReturn(mappings);
            given(mapper.toDomain(entity1, List.of())).willReturn(domain1);
            given(mapper.toDomain(entity2, List.of())).willReturn(domain2);

            // when
            List<Product> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByProductGroupIdIn(List.of(1L, 2L));
        }

        @Test
        @DisplayName("빈 ProductGroupId 목록 입력 시 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<ProductGroupId> productGroupIds = List.of();
            given(queryDslRepository.findByProductGroupIdIn(List.of())).willReturn(List.of());

            // when
            List<Product> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 상품이 없으면 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithNoMatchingProducts_ReturnsEmptyList() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(999L), ProductGroupId.of(1000L));
            given(queryDslRepository.findByProductGroupIdIn(List.of(999L, 1000L)))
                    .willReturn(List.of());

            // when
            List<Product> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByProductGroupIdIn(List.of(999L, 1000L));
            then(queryDslRepository).shouldHaveNoMoreInteractions();
        }
    }
}
