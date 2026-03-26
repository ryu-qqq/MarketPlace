package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.RefundOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.RefundOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.mapper.RefundOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.refund.outbox.RefundOutboxFixtures;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
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
 * RefundOutboxQueryAdapterTest - 환불 아웃박스 Query Adapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefundOutboxQueryAdapter 단위 테스트")
class RefundOutboxQueryAdapterTest {

    @Mock private RefundOutboxQueryDslRepository queryDslRepository;
    @Mock private RefundOutboxJpaRepository jpaRepository;
    @Mock private RefundOutboxJpaEntityMapper mapper;

    @InjectMocks private RefundOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 아웃박스 목록을 반환합니다")
        void findPendingOutboxes_WithValidCondition_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 10;
            RefundOutboxJpaEntity entity1 = RefundOutboxJpaEntityFixtures.pendingEntity(1L);
            RefundOutboxJpaEntity entity2 = RefundOutboxJpaEntityFixtures.pendingEntity(2L);
            RefundOutbox domain1 = RefundOutboxFixtures.pendingRefundOutbox();
            RefundOutbox domain2 = RefundOutboxFixtures.pendingRefundOutbox();

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<RefundOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, batchSize);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            given(queryDslRepository.findPendingOutboxes(beforeTime, 10)).willReturn(List.of());

            // when
            List<RefundOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING 아웃박스 목록을 반환합니다")
        void findProcessingTimeoutOutboxes_WithValidCondition_ReturnsDomainList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 10;
            RefundOutboxJpaEntity entity = RefundOutboxJpaEntityFixtures.processingEntity();
            RefundOutbox domain = RefundOutboxFixtures.processingRefundOutbox();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<RefundOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
        }

        @Test
        @DisplayName("타임아웃 아웃박스가 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant timeoutBefore = Instant.now();
            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, 10))
                    .willReturn(List.of());

            // when
            List<RefundOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. getById 테스트
    // ========================================================================

    @Nested
    @DisplayName("getById 메서드 테스트")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void getById_WithExistingId_ReturnsDomain() {
            // given
            Long outboxId = RefundOutboxJpaEntityFixtures.DEFAULT_ID;
            RefundOutboxJpaEntity entity = RefundOutboxJpaEntityFixtures.pendingEntity();
            RefundOutbox domain = RefundOutboxFixtures.pendingRefundOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            RefundOutbox result = queryAdapter.getById(outboxId);

            // then
            assertThat(result).isEqualTo(domain);
            then(jpaRepository).should().findById(outboxId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 IllegalStateException을 던집니다")
        void getById_WithNonExistingId_ThrowsException() {
            // given
            Long outboxId = 999L;
            given(jpaRepository.findById(outboxId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> queryAdapter.getById(outboxId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("RefundOutbox를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("getById 호출 시 jpaRepository에 ID가 전달됩니다")
        void getById_DelegatesToJpaRepositoryWithId() {
            // given
            Long outboxId = RefundOutboxJpaEntityFixtures.DEFAULT_ID;
            RefundOutboxJpaEntity entity = RefundOutboxJpaEntityFixtures.pendingEntity();
            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(RefundOutboxFixtures.pendingRefundOutbox());

            // when
            queryAdapter.getById(outboxId);

            // then
            then(jpaRepository).should().findById(outboxId);
        }
    }
}
