package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderConversionOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
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
 * LegacyOrderConversionOutboxQueryAdapterTest - 주문 Outbox Query Adapter 단위 테스트.
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
@DisplayName("LegacyOrderConversionOutboxQueryAdapter 단위 테스트")
class LegacyOrderConversionOutboxQueryAdapterTest {

    @Mock private LegacyOrderConversionOutboxQueryDslRepository queryDslRepository;

    @Mock private LegacyOrderConversionOutboxJpaEntityMapper mapper;

    @InjectMocks private LegacyOrderConversionOutboxQueryAdapter queryAdapter;

    private LegacyOrderConversionOutbox pendingDomain() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(1L),
                10001L,
                20001L,
                LegacyConversionOutboxStatus.PENDING,
                0,
                3,
                now,
                now,
                null,
                null,
                0L);
    }

    private LegacyOrderConversionOutbox processingDomain() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(2L),
                10002L,
                20002L,
                LegacyConversionOutboxStatus.PROCESSING,
                0,
                3,
                now,
                now,
                null,
                null,
                1L);
    }

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
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.pendingEntity();
            LegacyOrderConversionOutbox domain = pendingDomain();

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<LegacyOrderConversionOutbox> result =
                    queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
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
            List<LegacyOrderConversionOutbox> result =
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
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.processingEntity();
            LegacyOrderConversionOutbox domain = processingDomain();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<LegacyOrderConversionOutbox> result =
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
            List<LegacyOrderConversionOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }
}
