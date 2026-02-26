package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OutboundSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
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
 * OutboundSyncOutboxQueryAdapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity → Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboundSyncOutboxQueryAdapter 단위 테스트")
class OutboundSyncOutboxQueryAdapterTest {

    @Mock private OutboundSyncOutboxQueryDslRepository queryDslRepository;

    @Mock private OutboundSyncOutboxJpaEntityMapper mapper;

    @InjectMocks private OutboundSyncOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingByProductGroupId 메서드 테스트")
    class FindPendingByProductGroupIdTest {

        @Test
        @DisplayName("상품그룹 ID로 PENDING 상태 Outbox를 조회하여 Domain 리스트로 변환합니다")
        void findPendingByProductGroupId_WithValidId_ReturnsDomainList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(100L);
            OutboundSyncOutboxJpaEntity entity1 =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();
            OutboundSyncOutboxJpaEntity entity2 =
                    OutboundSyncOutboxJpaEntityFixtures.pendingUpdateEntity();
            List<OutboundSyncOutboxJpaEntity> entities = List.of(entity1, entity2);

            OutboundSyncOutbox domain1 = OutboundSyncOutboxFixtures.pendingOutbox();
            OutboundSyncOutbox domain2 = OutboundSyncOutboxFixtures.pendingOutbox();

            given(queryDslRepository.findPendingByProductGroupId(productGroupId.value()))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<OutboundSyncOutbox> result =
                    queryAdapter.findPendingByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
            then(queryDslRepository).should().findPendingByProductGroupId(productGroupId.value());
        }

        @Test
        @DisplayName("PENDING 상태가 없으면 빈 리스트를 반환합니다")
        void findPendingByProductGroupId_WithNoPending_ReturnsEmptyList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(9999L);

            given(queryDslRepository.findPendingByProductGroupId(productGroupId.value()))
                    .willReturn(List.of());

            // when
            List<OutboundSyncOutbox> result =
                    queryAdapter.findPendingByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findPendingByProductGroupId(productGroupId.value());
        }

        @Test
        @DisplayName("Mapper가 각 Entity에 대해 호출됩니다")
        void findPendingByProductGroupId_CallsMapperForEachEntity() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(100L);
            OutboundSyncOutboxJpaEntity entity1 =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();
            OutboundSyncOutboxJpaEntity entity2 =
                    OutboundSyncOutboxJpaEntityFixtures.pendingUpdateEntity();
            OutboundSyncOutboxJpaEntity entity3 =
                    OutboundSyncOutboxJpaEntityFixtures.pendingDeleteEntity();
            List<OutboundSyncOutboxJpaEntity> entities = List.of(entity1, entity2, entity3);

            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.pendingOutbox();
            given(queryDslRepository.findPendingByProductGroupId(productGroupId.value()))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain);
            given(mapper.toDomain(entity2)).willReturn(domain);
            given(mapper.toDomain(entity3)).willReturn(domain);

            // when
            queryAdapter.findPendingByProductGroupId(productGroupId);

            // then
            then(mapper)
                    .should(org.mockito.Mockito.times(3))
                    .toDomain(org.mockito.ArgumentMatchers.any(OutboundSyncOutboxJpaEntity.class));
        }

        @Test
        @DisplayName("단건 결과도 리스트로 반환합니다")
        void findPendingByProductGroupId_WithSingleResult_ReturnsSingletonList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(100L);
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.pendingOutbox();

            given(queryDslRepository.findPendingByProductGroupId(productGroupId.value()))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<OutboundSyncOutbox> result =
                    queryAdapter.findPendingByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }
    }
}
