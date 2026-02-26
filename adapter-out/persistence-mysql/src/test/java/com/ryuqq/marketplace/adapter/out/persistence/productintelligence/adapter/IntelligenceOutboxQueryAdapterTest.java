package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.IntelligenceOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.IntelligenceOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
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
 * IntelligenceOutboxQueryAdapterTest - Intelligence Pipeline Outbox Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("IntelligenceOutboxQueryAdapter 단위 테스트")
class IntelligenceOutboxQueryAdapterTest {

    @Mock private IntelligenceOutboxQueryDslRepository queryDslRepository;

    @Mock private IntelligenceOutboxJpaEntityMapper mapper;

    @InjectMocks private IntelligenceOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태의 Outbox 목록을 조회합니다")
        void findPendingOutboxes_WithPendingOutboxes_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            IntelligenceOutboxJpaEntity entity1 =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            1L, 100L, "PI:100:1740556800000");
            IntelligenceOutboxJpaEntity entity2 =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            2L, 101L, "PI:101:1740556800001");
            IntelligenceOutbox domain1 =
                    ProductIntelligenceFixtures.existingPendingOutbox(1L, 100L);
            IntelligenceOutbox domain2 =
                    ProductIntelligenceFixtures.existingPendingOutbox(2L, 101L);

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<IntelligenceOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, limit);
        }

        @Test
        @DisplayName("PENDING Outbox가 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxes_WithNoOutboxes_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            given(queryDslRepository.findPendingOutboxes(beforeTime, limit)).willReturn(List.of());

            // when
            List<IntelligenceOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
            then(mapper).shouldHaveNoInteractions();
        }
    }

    // ========================================================================
    // 2. findInProgressTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findInProgressTimeoutOutboxes 메서드 테스트")
    class FindInProgressTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 SENT 상태의 Outbox 목록을 조회합니다")
        void findInProgressTimeoutOutboxes_WithTimedOutOutboxes_ReturnsDomainList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(3600);
            int limit = 10;
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.timeoutSentOutboxEntity(
                            1L, 100L, "PI:100:1740556800000");
            IntelligenceOutbox domain = ProductIntelligenceFixtures.existingPendingOutbox(1L, 100L);

            given(queryDslRepository.findInProgressTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<IntelligenceOutbox> result =
                    queryAdapter.findInProgressTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository)
                    .should()
                    .findInProgressTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃된 Outbox가 없으면 빈 리스트를 반환합니다")
        void findInProgressTimeoutOutboxes_WithNoTimeoutOutboxes_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(3600);
            int limit = 10;
            given(queryDslRepository.findInProgressTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<IntelligenceOutbox> result =
                    queryAdapter.findInProgressTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            Long outboxId = 1L;
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            outboxId, 100L, "PI:100:1740556800000");
            IntelligenceOutbox domain =
                    ProductIntelligenceFixtures.existingPendingOutbox(outboxId, 100L);

            given(queryDslRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<IntelligenceOutbox> result = queryAdapter.findById(outboxId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(outboxId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long outboxId = 999L;
            given(queryDslRepository.findById(outboxId)).willReturn(Optional.empty());

            // when
            Optional<IntelligenceOutbox> result = queryAdapter.findById(outboxId);

            // then
            assertThat(result).isEmpty();
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
