package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyConversionOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
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
 * LegacyConversionOutboxQueryAdapterTest - Outbox Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyConversionOutboxQueryAdapter 단위 테스트")
class LegacyConversionOutboxQueryAdapterTest {

    @Mock private LegacyConversionOutboxQueryDslRepository queryDslRepository;

    @Mock private LegacyConversionOutboxJpaEntityMapper mapper;

    @InjectMocks private LegacyConversionOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING Outbox 목록을 반환합니다")
        void findPendingOutboxes_WithValidConditions_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            LegacyConversionOutboxJpaEntity entity1 =
                    LegacyConversionOutboxJpaEntityFixtures.pendingEntity();
            LegacyConversionOutbox domain1 = LegacyConversionFixtures.pendingOutbox();

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity1));
            given(mapper.toDomain(entity1)).willReturn(domain1);

            // when
            List<LegacyConversionOutbox> result =
                    queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain1);
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, limit);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            given(queryDslRepository.findPendingOutboxes(beforeTime, limit)).willReturn(List.of());

            // when
            List<LegacyConversionOutbox> result =
                    queryAdapter.findPendingOutboxes(beforeTime, limit);

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
        @DisplayName("PROCESSING 타임아웃 Outbox 목록을 반환합니다")
        void findProcessingTimeoutOutboxes_WithTimeoutThreshold_ReturnsDomainList() {
            // given
            Instant timeoutThreshold = Instant.now();
            int limit = 10;
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.processingEntity();
            LegacyConversionOutbox domain = LegacyConversionFixtures.processingOutbox();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<LegacyConversionOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃 결과가 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now();
            int limit = 10;
            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<LegacyConversionOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. existsPendingByLegacyProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsPendingByLegacyProductGroupId 메서드 테스트")
    class ExistsPendingByLegacyProductGroupIdTest {

        @Test
        @DisplayName("PENDING Outbox가 존재하면 true를 반환합니다")
        void existsPendingByLegacyProductGroupId_WithExistingPending_ReturnsTrue() {
            // given
            long legacyProductGroupId = 100L;
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.pendingEntity();

            given(queryDslRepository.findPendingByLegacyProductGroupId(legacyProductGroupId))
                    .willReturn(Optional.of(entity));

            // when
            boolean result = queryAdapter.existsPendingByLegacyProductGroupId(legacyProductGroupId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("PENDING Outbox가 없으면 false를 반환합니다")
        void existsPendingByLegacyProductGroupId_WithNoPending_ReturnsFalse() {
            // given
            long legacyProductGroupId = 999L;
            given(queryDslRepository.findPendingByLegacyProductGroupId(legacyProductGroupId))
                    .willReturn(Optional.empty());

            // when
            boolean result = queryAdapter.existsPendingByLegacyProductGroupId(legacyProductGroupId);

            // then
            assertThat(result).isFalse();
        }
    }
}
