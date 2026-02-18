package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.ProductGroupDescriptionQueryDslRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
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
 * ProductGroupDescriptionQueryAdapterTest - 상품 그룹 상세설명 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupDescriptionQueryAdapter 단위 테스트")
class ProductGroupDescriptionQueryAdapterTest {

    @Mock private ProductGroupDescriptionQueryDslRepository queryDslRepository;

    @Mock private ProductGroupDescriptionJpaEntityMapper mapper;

    @InjectMocks private ProductGroupDescriptionQueryAdapter queryAdapter;

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
            Long id = 1L;
            ProductGroupDescriptionJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(id, 1L);
            List<DescriptionImageJpaEntity> images =
                    ProductGroupDescriptionJpaEntityFixtures.emptyImageEntities();
            ProductGroupDescription domain = ProductGroupFixtures.defaultProductGroupDescription();

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(queryDslRepository.findImagesByDescriptionId(entity.getId())).willReturn(images);
            given(mapper.toDomain(entity, images)).willReturn(domain);

            // when
            Optional<ProductGroupDescription> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(id);
            then(queryDslRepository).should().findImagesByDescriptionId(entity.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long id = 999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<ProductGroupDescription> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(id);
            then(queryDslRepository).shouldHaveNoMoreInteractions();
        }
    }

    // ========================================================================
    // 2. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("ProductGroupId로 조회 시 Domain을 반환합니다")
        void findByProductGroupId_WithExistingId_ReturnsDomain() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            ProductGroupDescriptionJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(1L, 1L);
            List<DescriptionImageJpaEntity> images =
                    ProductGroupDescriptionJpaEntityFixtures.emptyImageEntities();
            ProductGroupDescription domain = ProductGroupFixtures.defaultProductGroupDescription();

            given(queryDslRepository.findByProductGroupId(1L)).willReturn(Optional.of(entity));
            given(queryDslRepository.findImagesByDescriptionId(entity.getId())).willReturn(images);
            given(mapper.toDomain(entity, images)).willReturn(domain);

            // when
            Optional<ProductGroupDescription> result =
                    queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            then(queryDslRepository).should().findByProductGroupId(1L);
        }

        @Test
        @DisplayName("존재하지 않는 ProductGroupId로 조회 시 빈 Optional을 반환합니다")
        void findByProductGroupId_WithNonExistingId_ReturnsEmpty() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(999L);
            given(queryDslRepository.findByProductGroupId(999L)).willReturn(Optional.empty());

            // when
            Optional<ProductGroupDescription> result =
                    queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByPublishStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByPublishStatus 메서드 테스트")
    class FindByPublishStatusTest {

        @Test
        @DisplayName("PENDING 상태 상세설명 목록을 조회합니다")
        void findByPublishStatus_WithPendingStatus_ReturnsList() {
            // given
            DescriptionPublishStatus status = DescriptionPublishStatus.PENDING;
            int limit = 10;
            ProductGroupDescriptionJpaEntity entity1 =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(1L, 1L);
            ProductGroupDescriptionJpaEntity entity2 =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(2L, 2L);
            List<DescriptionImageJpaEntity> images =
                    ProductGroupDescriptionJpaEntityFixtures.emptyImageEntities();
            ProductGroupDescription domain = ProductGroupFixtures.defaultProductGroupDescription();

            given(queryDslRepository.findByPublishStatus("PENDING", limit))
                    .willReturn(List.of(entity1, entity2));
            given(queryDslRepository.findImagesByDescriptionId(entity1.getId())).willReturn(images);
            given(queryDslRepository.findImagesByDescriptionId(entity2.getId())).willReturn(images);
            given(mapper.toDomain(entity1, images)).willReturn(domain);
            given(mapper.toDomain(entity2, images)).willReturn(domain);

            // when
            List<ProductGroupDescription> result = queryAdapter.findByPublishStatus(status, limit);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByPublishStatus("PENDING", limit);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByPublishStatus_WithNoResults_ReturnsEmptyList() {
            // given
            DescriptionPublishStatus status = DescriptionPublishStatus.PUBLISHED;
            int limit = 5;
            given(queryDslRepository.findByPublishStatus("PUBLISHED", limit)).willReturn(List.of());

            // when
            List<ProductGroupDescription> result = queryAdapter.findByPublishStatus(status, limit);

            // then
            assertThat(result).isEmpty();
        }
    }
}
