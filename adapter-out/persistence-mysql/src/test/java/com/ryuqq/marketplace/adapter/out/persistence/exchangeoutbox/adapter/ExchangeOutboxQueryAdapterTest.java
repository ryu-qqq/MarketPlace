package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.ExchangeOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.mapper.ExchangeOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.exchange.outbox.ExchangeOutboxFixtures;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
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
 * ExchangeOutboxQueryAdapterTest - 교환 아웃박스 Query Adapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeOutboxQueryAdapter 단위 테스트")
class ExchangeOutboxQueryAdapterTest {

    @Mock private ExchangeOutboxQueryDslRepository queryDslRepository;
    @Mock private ExchangeOutboxJpaRepository jpaRepository;
    @Mock private ExchangeOutboxJpaEntityMapper mapper;

    @InjectMocks private ExchangeOutboxQueryAdapter queryAdapter;

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
            ExchangeOutboxJpaEntity entity1 = ExchangeOutboxJpaEntityFixtures.pendingEntity(1L);
            ExchangeOutboxJpaEntity entity2 = ExchangeOutboxJpaEntityFixtures.pendingEntity(2L);
            ExchangeOutbox domain1 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutbox domain2 = ExchangeOutboxFixtures.pendingExchangeOutbox();

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ExchangeOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

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
            List<ExchangeOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, 10);

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
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.processingEntity();
            ExchangeOutbox domain = ExchangeOutboxFixtures.processingExchangeOutbox();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ExchangeOutbox> result =
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
            List<ExchangeOutbox> result =
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
            Long outboxId = ExchangeOutboxJpaEntityFixtures.DEFAULT_ID;
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();
            ExchangeOutbox domain = ExchangeOutboxFixtures.pendingExchangeOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            ExchangeOutbox result = queryAdapter.getById(outboxId);

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
                    .hasMessageContaining("ExchangeOutbox를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("getById 호출 시 jpaRepository에 ID가 전달됩니다")
        void getById_DelegatesToJpaRepositoryWithId() {
            // given
            Long outboxId = ExchangeOutboxJpaEntityFixtures.DEFAULT_ID;
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();
            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity))
                    .willReturn(ExchangeOutboxFixtures.pendingExchangeOutbox());

            // when
            queryAdapter.getById(outboxId);

            // then
            then(jpaRepository).should().findById(outboxId);
        }
    }
}
