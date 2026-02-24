package com.ryuqq.marketplace.adapter.out.persistence.productnotice.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.ProductNoticeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper.ProductNoticeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeQueryDslRepository;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
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
 * ProductNoticeQueryAdapterTest - 상품 고시정보 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductNoticeQueryAdapter 단위 테스트")
class ProductNoticeQueryAdapterTest {

    @Mock private ProductNoticeQueryDslRepository queryDslRepository;

    @Mock private ProductNoticeJpaEntityMapper mapper;

    @InjectMocks private ProductNoticeQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("존재하는 ProductGroupId로 조회 시 Domain을 반환합니다")
        void findByProductGroupId_WithExistingId_ReturnsDomain() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            ProductNoticeJpaEntity entity = ProductNoticeJpaEntityFixtures.activeEntity(1L);
            List<ProductNoticeEntryJpaEntity> entries =
                    ProductNoticeJpaEntityFixtures.defaultEntryEntities(1L);
            ProductNotice domain = ProductNoticeFixtures.existingProductNotice();

            given(queryDslRepository.findByProductGroupId(1L)).willReturn(Optional.of(entity));
            given(queryDslRepository.findEntriesByProductNoticeId(entity.getId()))
                    .willReturn(entries);
            given(mapper.toDomain(entity, entries)).willReturn(domain);

            // when
            Optional<ProductNotice> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findByProductGroupId(1L);
            then(queryDslRepository).should().findEntriesByProductNoticeId(entity.getId());
            then(mapper).should().toDomain(entity, entries);
        }

        @Test
        @DisplayName("존재하지 않는 ProductGroupId로 조회 시 빈 Optional을 반환합니다")
        void findByProductGroupId_WithNonExistingId_ReturnsEmpty() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(999L);
            given(queryDslRepository.findByProductGroupId(999L)).willReturn(Optional.empty());

            // when
            Optional<ProductNotice> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByProductGroupId(999L);
            then(queryDslRepository).shouldHaveNoMoreInteractions();
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Entry가 없는 경우에도 빈 Entry 목록으로 Domain을 반환합니다")
        void findByProductGroupId_WithNoEntries_ReturnsDomainWithEmptyEntries() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(2L);
            ProductNoticeJpaEntity entity = ProductNoticeJpaEntityFixtures.activeEntity(2L);
            List<ProductNoticeEntryJpaEntity> emptyEntries =
                    ProductNoticeJpaEntityFixtures.emptyEntries();
            ProductNotice domain = ProductNoticeFixtures.existingProductNotice(2L);

            given(queryDslRepository.findByProductGroupId(2L)).willReturn(Optional.of(entity));
            given(queryDslRepository.findEntriesByProductNoticeId(entity.getId()))
                    .willReturn(emptyEntries);
            given(mapper.toDomain(entity, emptyEntries)).willReturn(domain);

            // when
            Optional<ProductNotice> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
        }
    }

    // ========================================================================
    // 2. findByProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdIn 메서드 테스트")
    class FindByProductGroupIdInTest {

        @Test
        @DisplayName("여러 ProductGroupId로 Notice 목록을 배치 조회하며 Entry도 함께 로드합니다")
        void findByProductGroupIdIn_WithValidIds_ReturnsDomainListWithEntries() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(1L), ProductGroupId.of(2L));
            ProductNoticeJpaEntity entity1 = ProductNoticeJpaEntityFixtures.activeEntity(1L, 1L);
            ProductNoticeJpaEntity entity2 = ProductNoticeJpaEntityFixtures.activeEntity(2L, 2L);
            List<ProductNoticeEntryJpaEntity> allEntries =
                    ProductNoticeJpaEntityFixtures.defaultEntryEntities(1L);
            ProductNotice domain1 = ProductNoticeFixtures.existingProductNotice(1L);
            ProductNotice domain2 = ProductNoticeFixtures.existingProductNotice(2L);

            given(queryDslRepository.findByProductGroupIdIn(List.of(1L, 2L)))
                    .willReturn(List.of(entity1, entity2));
            given(queryDslRepository.findEntriesByProductNoticeIds(List.of(1L, 2L)))
                    .willReturn(allEntries);
            given(
                            mapper.toDomain(
                                    entity1,
                                    List.of(
                                            allEntries.get(0),
                                            allEntries.get(1),
                                            allEntries.get(2))))
                    .willReturn(domain1);
            given(mapper.toDomain(entity2, List.of())).willReturn(domain2);

            // when
            List<ProductNotice> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByProductGroupIdIn(List.of(1L, 2L));
            then(queryDslRepository).should().findEntriesByProductNoticeIds(List.of(1L, 2L));
        }

        @Test
        @DisplayName("빈 ProductGroupId 목록 입력 시 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<ProductGroupId> productGroupIds = List.of();
            given(queryDslRepository.findByProductGroupIdIn(List.of())).willReturn(List.of());

            // when
            List<ProductNotice> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 Notice가 없으면 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithNoMatchingNotices_ReturnsEmptyList() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(999L), ProductGroupId.of(1000L));
            given(queryDslRepository.findByProductGroupIdIn(List.of(999L, 1000L)))
                    .willReturn(List.of());

            // when
            List<ProductNotice> result = queryAdapter.findByProductGroupIdIn(productGroupIds);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByProductGroupIdIn(List.of(999L, 1000L));
            then(queryDslRepository).shouldHaveNoMoreInteractions();
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
