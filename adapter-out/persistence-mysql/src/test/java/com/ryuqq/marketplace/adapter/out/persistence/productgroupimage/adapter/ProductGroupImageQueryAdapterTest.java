package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.ProductGroupImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository.ProductGroupImageQueryDslRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
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
 * ProductGroupImageQueryAdapterTest - 상품 그룹 이미지 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupImageQueryAdapter 단위 테스트")
class ProductGroupImageQueryAdapterTest {

    @Mock private ProductGroupImageQueryDslRepository queryDslRepository;

    @Mock private ProductGroupJpaEntityMapper mapper;

    @InjectMocks private ProductGroupImageQueryAdapter queryAdapter;

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
            ProductGroupImageJpaEntity entity =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(id, 1L);
            ProductGroupImage domain = ProductGroupFixtures.thumbnailImage();

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toImageDomain(entity)).willReturn(domain);

            // when
            Optional<ProductGroupImage> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(id);
            then(mapper).should().toImageDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long id = 999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<ProductGroupImage> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(id);
        }
    }

    // ========================================================================
    // 2. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("ProductGroupId로 이미지 목록을 조회합니다")
        void findByProductGroupId_WithValidId_ReturnsDomainList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            ProductGroupImageJpaEntity entity1 =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(1L, 1L);
            ProductGroupImageJpaEntity entity2 =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(2L, 1L);
            ProductGroupImage domain1 = ProductGroupFixtures.thumbnailImage();
            ProductGroupImage domain2 = ProductGroupFixtures.thumbnailImage();

            given(queryDslRepository.findByProductGroupId(1L))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toImageDomain(entity1)).willReturn(domain1);
            given(mapper.toImageDomain(entity2)).willReturn(domain2);

            // when
            List<ProductGroupImage> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByProductGroupId(1L);
        }

        @Test
        @DisplayName("이미지가 없으면 빈 리스트를 반환합니다")
        void findByProductGroupId_WithNoImages_ReturnsEmptyList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(999L);
            given(queryDslRepository.findByProductGroupId(999L)).willReturn(List.of());

            // when
            List<ProductGroupImage> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdIn 메서드 테스트")
    class FindByProductGroupIdInTest {

        @Test
        @DisplayName("여러 ProductGroupId로 이미지 목록을 배치 조회합니다")
        void findByProductGroupIdIn_WithValidIds_ReturnsDomainList() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(1L), ProductGroupId.of(2L));
            ProductGroupImageJpaEntity entity1 =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(1L, 1L);
            ProductGroupImageJpaEntity entity2 =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(2L, 2L);
            ProductGroupImage domain1 = ProductGroupFixtures.thumbnailImage();
            ProductGroupImage domain2 = ProductGroupFixtures.thumbnailImage();

            given(queryDslRepository.findByProductGroupIdIn(List.of(1L, 2L)))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toImageDomain(entity1)).willReturn(domain1);
            given(mapper.toImageDomain(entity2)).willReturn(domain2);

            // when
            List<ProductGroupImage> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

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
            List<ProductGroupImage> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 이미지가 없으면 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithNoMatchingImages_ReturnsEmptyList() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(999L), ProductGroupId.of(1000L));
            given(queryDslRepository.findByProductGroupIdIn(List.of(999L, 1000L)))
                    .willReturn(List.of());

            // when
            List<ProductGroupImage> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByProductGroupIdIn(List.of(999L, 1000L));
        }
    }
}
