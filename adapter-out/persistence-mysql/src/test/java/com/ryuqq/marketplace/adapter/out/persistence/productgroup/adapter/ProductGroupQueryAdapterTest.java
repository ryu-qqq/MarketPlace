package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
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
 * ProductGroupQueryAdapterTest - 상품 그룹 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupQueryAdapter 단위 테스트")
class ProductGroupQueryAdapterTest {

    @Mock private ProductGroupQueryDslRepository queryDslRepository;

    @Mock private ProductGroupJpaEntityMapper mapper;

    @Mock private ProductGroupSearchCriteria criteria;

    @InjectMocks private ProductGroupQueryAdapter queryAdapter;

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
            ProductGroupId id = ProductGroupId.of(1L);
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.activeEntity(1L);
            ProductGroup domain = ProductGroupFixtures.activeProductGroup();

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(queryDslRepository.findImagesByProductGroupId(1L)).willReturn(List.of());
            given(queryDslRepository.findOptionGroupsByProductGroupId(1L)).willReturn(List.of());
            given(queryDslRepository.findOptionValuesByOptionGroupIds(List.of()))
                    .willReturn(List.of());
            given(
                            mapper.toDomain(
                                    entity,
                                    List.<ProductGroupImageJpaEntity>of(),
                                    List.<SellerOptionGroupJpaEntity>of(),
                                    List.<SellerOptionValueJpaEntity>of()))
                    .willReturn(domain);

            // when
            Optional<ProductGroup> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            ProductGroupId id = ProductGroupId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<ProductGroup> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 상품 그룹 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            ProductGroupJpaEntity entity1 = ProductGroupJpaEntityFixtures.activeEntity(1L);
            ProductGroupJpaEntity entity2 = ProductGroupJpaEntityFixtures.activeEntity(2L);
            ProductGroup domain1 = ProductGroupFixtures.activeProductGroup();
            ProductGroup domain2 = ProductGroupFixtures.activeProductGroup();

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(queryDslRepository.findImagesByProductGroupIds(List.of(1L, 2L)))
                    .willReturn(List.of());
            given(queryDslRepository.findOptionGroupsByProductGroupIds(List.of(1L, 2L)))
                    .willReturn(List.of());
            given(queryDslRepository.findOptionValuesByOptionGroupIds(List.of()))
                    .willReturn(List.of());
            given(
                            mapper.toDomain(
                                    entity1,
                                    List.<ProductGroupImageJpaEntity>of(),
                                    List.<SellerOptionGroupJpaEntity>of(),
                                    List.<SellerOptionValueJpaEntity>of()))
                    .willReturn(domain1);
            given(
                            mapper.toDomain(
                                    entity2,
                                    List.<ProductGroupImageJpaEntity>of(),
                                    List.<SellerOptionGroupJpaEntity>of(),
                                    List.<SellerOptionValueJpaEntity>of()))
                    .willReturn(domain2);

            // when
            List<ProductGroup> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<ProductGroup> result = queryAdapter.findByCriteria(criteria);

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
            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
