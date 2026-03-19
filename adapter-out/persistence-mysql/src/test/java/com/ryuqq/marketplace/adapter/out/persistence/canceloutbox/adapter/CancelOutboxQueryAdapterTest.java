package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.CancelOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.mapper.CancelOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
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
 * CancelOutboxQueryAdapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository를 우선 사용하며, JpaRepository 보조 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOutboxQueryAdapter 단위 테스트")
class CancelOutboxQueryAdapterTest {

    @Mock private CancelOutboxQueryDslRepository queryDslRepository;
    @Mock private CancelOutboxJpaRepository jpaRepository;
    @Mock private CancelOutboxJpaEntityMapper mapper;

    @InjectMocks private CancelOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 아웃박스가 있으면 CancelOutbox 목록을 반환합니다")
        void findPendingOutboxes_WithPendingOutboxes_ReturnsList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 10;
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<CancelOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxes_WithNoPendingOutboxes_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 10;

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of());

            // when
            List<CancelOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findPendingOutboxes 호출 시 파라미터가 queryDslRepository에 그대로 전달됩니다")
        void findPendingOutboxes_DelegatesToQueryDslRepositoryWithCorrectParams() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(60);
            int batchSize = 50;

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of());

            // when
            queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, batchSize);
        }

        @Test
        @DisplayName("여러 PENDING 아웃박스가 있으면 모두 변환하여 반환합니다")
        void findPendingOutboxes_WithMultiplePendingOutboxes_ReturnsAllConverted() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 100;
            CancelOutboxJpaEntity entity1 =
                    CancelOutboxJpaEntityFixtures.pendingEntity(1L, "item-001");
            CancelOutboxJpaEntity entity2 =
                    CancelOutboxJpaEntityFixtures.pendingEntity(2L, "item-002");
            CancelOutbox domain1 = CancelFixtures.pendingCancelOutbox();
            CancelOutbox domain2 = CancelFixtures.pendingCancelOutbox();

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<CancelOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(2);
        }
    }

    // ========================================================================
    // 2. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING 아웃박스가 있으면 CancelOutbox 목록을 반환합니다")
        void findProcessingTimeoutOutboxes_WithTimeoutOutboxes_ReturnsList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 10;
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.processingEntity();
            CancelOutbox domain = CancelFixtures.processingCancelOutbox();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<CancelOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("타임아웃된 아웃박스가 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoTimeoutOutboxes_ReturnsEmptyList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 10;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of());

            // when
            List<CancelOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findProcessingTimeoutOutboxes 호출 시 파라미터가 queryDslRepository에 그대로 전달됩니다")
        void findProcessingTimeoutOutboxes_DelegatesToQueryDslRepositoryWithCorrectParams() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(180);
            int batchSize = 20;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of());

            // when
            queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
        }
    }

    // ========================================================================
    // 3. getById 테스트
    // ========================================================================

    @Nested
    @DisplayName("getById 메서드 테스트")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 CancelOutbox 도메인을 반환합니다")
        void getById_WithExistingId_ReturnsCancelOutbox() {
            // given
            Long outboxId = CancelOutboxJpaEntityFixtures.DEFAULT_ID;
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            CancelOutbox result = queryAdapter.getById(outboxId);

            // then
            assertThat(result).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 IllegalStateException을 던집니다")
        void getById_WithNonExistingId_ThrowsIllegalStateException() {
            // given
            Long outboxId = 9999L;

            given(jpaRepository.findById(outboxId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> queryAdapter.getById(outboxId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("CancelOutbox를 찾을 수 없습니다")
                    .hasMessageContaining(String.valueOf(outboxId));
        }

        @Test
        @DisplayName("getById 호출 시 jpaRepository에 id가 전달됩니다")
        void getById_DelegatesToJpaRepositoryWithId() {
            // given
            Long outboxId = CancelOutboxJpaEntityFixtures.DEFAULT_ID;
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.getById(outboxId);

            // then
            then(jpaRepository).should().findById(outboxId);
        }

        @Test
        @DisplayName("getById 호출 시 queryDslRepository는 사용하지 않습니다")
        void getById_DoesNotUseQueryDslRepository() {
            // given
            Long outboxId = CancelOutboxJpaEntityFixtures.DEFAULT_ID;
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();
            CancelOutbox domain = CancelFixtures.pendingCancelOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.getById(outboxId);

            // then
            then(queryDslRepository).shouldHaveNoInteractions();
        }
    }
}
