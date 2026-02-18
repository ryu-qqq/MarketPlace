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
}
