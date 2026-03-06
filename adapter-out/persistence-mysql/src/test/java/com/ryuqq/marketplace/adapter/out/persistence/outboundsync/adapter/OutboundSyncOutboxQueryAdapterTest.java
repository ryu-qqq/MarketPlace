package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OutboundSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
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

    // ========================================================================
    // 2. findPendingByProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingByProductGroupIds 메서드 테스트")
    class FindPendingByProductGroupIdsTest {

        @Test
        @DisplayName("여러 상품그룹 ID로 PENDING 상태 Outbox를 일괄 조회하여 Domain 리스트로 변환합니다")
        void findPendingByProductGroupIds_WithValidIds_ReturnsDomainList() {
            // given
            java.util.Collection<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(100L), ProductGroupId.of(200L));
            OutboundSyncOutboxJpaEntity entity1 =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();
            OutboundSyncOutboxJpaEntity entity2 =
                    OutboundSyncOutboxJpaEntityFixtures.pendingUpdateEntity();
            List<OutboundSyncOutboxJpaEntity> entities = List.of(entity1, entity2);

            OutboundSyncOutbox domain1 = OutboundSyncOutboxFixtures.pendingOutbox();
            OutboundSyncOutbox domain2 = OutboundSyncOutboxFixtures.pendingOutbox();

            given(queryDslRepository.findPendingByProductGroupIds(List.of(100L, 200L)))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<OutboundSyncOutbox> result =
                    queryAdapter.findPendingByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
        }

        @Test
        @DisplayName("빈 결과가 반환되면 빈 리스트를 반환합니다")
        void findPendingByProductGroupIds_WithEmptyResult_ReturnsEmptyList() {
            // given
            java.util.Collection<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(9999L));

            given(queryDslRepository.findPendingByProductGroupIds(List.of(9999L)))
                    .willReturn(List.of());

            // when
            List<OutboundSyncOutbox> result =
                    queryAdapter.findPendingByProductGroupIds(productGroupIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("ProductGroupId가 Long 값으로 변환되어 Repository에 전달됩니다")
        void findPendingByProductGroupIds_ConvertsProductGroupIdsToLong() {
            // given
            java.util.Collection<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(10L), ProductGroupId.of(20L), ProductGroupId.of(30L));

            given(queryDslRepository.findPendingByProductGroupIds(List.of(10L, 20L, 30L)))
                    .willReturn(List.of());

            // when
            queryAdapter.findPendingByProductGroupIds(productGroupIds);

            // then
            then(queryDslRepository).should().findPendingByProductGroupIds(List.of(10L, 20L, 30L));
        }
    }

    // ========================================================================
    // 3. getById 테스트 (retrySyncHistory 흐름)
    // ========================================================================

    @Nested
    @DisplayName("getById 메서드 테스트")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 FAILED 상태 Outbox를 조회하여 Domain으로 변환합니다")
        void getById_WithExistingFailedEntity_ReturnsDomain() {
            // given
            Long outboxId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;
            OutboundSyncOutboxJpaEntity failedEntity =
                    OutboundSyncOutboxJpaEntityFixtures.failedEntity();
            OutboundSyncOutbox failedDomain = OutboundSyncOutboxFixtures.failedOutbox();

            given(queryDslRepository.findById(outboxId)).willReturn(failedEntity);
            given(mapper.toDomain(failedEntity)).willReturn(failedDomain);

            // when
            OutboundSyncOutbox result = queryAdapter.getById(outboxId);

            // then
            assertThat(result).isEqualTo(failedDomain);
            assertThat(result.status()).isEqualTo(SyncStatus.FAILED);
            then(queryDslRepository).should().findById(outboxId);
            then(mapper).should().toDomain(failedEntity);
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 IllegalStateException을 던집니다")
        void getById_WithNonExistentId_ThrowsIllegalStateException() {
            // given
            Long nonExistentId = 9999L;

            given(queryDslRepository.findById(nonExistentId)).willReturn(null);

            // when / then
            assertThatThrownBy(() -> queryAdapter.getById(nonExistentId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("OutboundSyncOutbox를 찾을 수 없습니다")
                    .hasMessageContaining(String.valueOf(nonExistentId));
        }

        @Test
        @DisplayName("null ID 입력 시 NullPointerException을 던집니다")
        void getById_WithNullId_ThrowsNullPointerException() {
            // when / then
            assertThatThrownBy(() -> queryAdapter.getById(null))
                    .isInstanceOf(NullPointerException.class);

            then(queryDslRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("PENDING 상태 Outbox도 ID로 조회할 수 있습니다")
        void getById_WithPendingEntity_ReturnsDomain() {
            // given
            Long outboxId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;
            OutboundSyncOutboxJpaEntity pendingEntity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();
            OutboundSyncOutbox pendingDomain = OutboundSyncOutboxFixtures.pendingOutbox();

            given(queryDslRepository.findById(outboxId)).willReturn(pendingEntity);
            given(mapper.toDomain(pendingEntity)).willReturn(pendingDomain);

            // when
            OutboundSyncOutbox result = queryAdapter.getById(outboxId);

            // then
            assertThat(result.status()).isEqualTo(SyncStatus.PENDING);
            then(mapper).should().toDomain(pendingEntity);
        }

        @Test
        @DisplayName("조회 후 Domain에서 retry()를 호출하면 PENDING 상태로 전이합니다")
        void getById_ThenRetry_TransitionsToPending() {
            // given
            Long outboxId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;
            OutboundSyncOutboxJpaEntity failedEntity =
                    OutboundSyncOutboxJpaEntityFixtures.failedEntity();
            OutboundSyncOutbox failedDomain = OutboundSyncOutboxFixtures.failedOutbox();

            given(queryDslRepository.findById(outboxId)).willReturn(failedEntity);
            given(mapper.toDomain(failedEntity)).willReturn(failedDomain);

            // when
            OutboundSyncOutbox result = queryAdapter.getById(outboxId);
            result.retry(java.time.Instant.now());

            // then
            assertThat(result.status()).isEqualTo(SyncStatus.PENDING);
            assertThat(result.retryCount()).isEqualTo(0);
            assertThat(result.errorMessage()).isNull();
        }
    }
}
