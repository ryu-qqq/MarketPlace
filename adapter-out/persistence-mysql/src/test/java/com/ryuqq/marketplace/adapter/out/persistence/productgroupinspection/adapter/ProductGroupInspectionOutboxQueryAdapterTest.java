package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.ProductGroupInspectionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.mapper.ProductGroupInspectionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.repository.ProductGroupInspectionOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.productgroupinspection.ProductGroupInspectionFixtures;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
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
 * ProductGroupInspectionOutboxQueryAdapterTest - 상품 그룹 검수 Outbox Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupInspectionOutboxQueryAdapter 단위 테스트")
class ProductGroupInspectionOutboxQueryAdapterTest {

    @Mock private ProductGroupInspectionOutboxQueryDslRepository queryDslRepository;

    @Mock private ProductGroupInspectionOutboxJpaEntityMapper mapper;

    @InjectMocks private ProductGroupInspectionOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태 Outbox 목록을 Domain으로 변환하여 반환합니다")
        void findPendingOutboxes_WithResults_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            ProductGroupInspectionOutboxJpaEntity entity1 =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(1L);
            ProductGroupInspectionOutboxJpaEntity entity2 =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(2L);
            ProductGroupInspectionOutbox domain1 = ProductGroupInspectionFixtures.pendingOutbox(1L);
            ProductGroupInspectionOutbox domain2 = ProductGroupInspectionFixtures.pendingOutbox(2L);

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ProductGroupInspectionOutbox> result =
                    queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, limit);
        }

        @Test
        @DisplayName("PENDING Outbox가 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit)).willReturn(List.of());

            // when
            List<ProductGroupInspectionOutbox> result =
                    queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("단일 PENDING Outbox를 Domain으로 변환하여 반환합니다")
        void findPendingOutboxes_WithSingleResult_ReturnsSingleDomain() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(1L);
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.pendingOutbox(1L);

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ProductGroupInspectionOutbox> result =
                    queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }
    }

    // ========================================================================
    // 2. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 Domain으로 변환하여 반환합니다")
        void findProcessingTimeoutOutboxes_WithResults_ReturnsDomainList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;

            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newProcessingEntity();
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.processingOutbox();

            given(queryDslRepository.findInProgressTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ProductGroupInspectionOutbox> result =
                    queryAdapter.findInProgressTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
            then(queryDslRepository)
                    .should()
                    .findInProgressTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃 Outbox가 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;

            given(queryDslRepository.findInProgressTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<ProductGroupInspectionOutbox> result =
                    queryAdapter.findInProgressTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 타임아웃 Outbox를 Domain으로 변환하여 반환합니다")
        void findProcessingTimeoutOutboxes_WithMultipleResults_ReturnsDomainList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 5;

            ProductGroupInspectionOutboxJpaEntity entity1 =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newProcessingEntity();
            ProductGroupInspectionOutboxJpaEntity entity2 =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newProcessingEntity();
            ProductGroupInspectionOutbox domain1 =
                    ProductGroupInspectionFixtures.processingOutbox(1L);
            ProductGroupInspectionOutbox domain2 =
                    ProductGroupInspectionFixtures.processingOutbox(2L);

            given(queryDslRepository.findInProgressTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ProductGroupInspectionOutbox> result =
                    queryAdapter.findInProgressTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }
    }
}
