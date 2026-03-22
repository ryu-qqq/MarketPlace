package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderConversionOutboxJpaRepository;
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
 * LegacyOrderConversionOutboxCommandAdapterTest - 주문 Outbox Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyOrderConversionOutboxCommandAdapter 단위 테스트")
class LegacyOrderConversionOutboxCommandAdapterTest {

    @Mock private LegacyOrderConversionOutboxJpaRepository repository;

    @Mock private LegacyOrderConversionOutboxJpaEntityMapper mapper;

    @InjectMocks private LegacyOrderConversionOutboxCommandAdapter commandAdapter;

    private LegacyOrderConversionOutbox newPendingDomain() {
        return LegacyOrderConversionOutbox.forNew(10001L, 20001L, Instant.now());
    }

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

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidDomain_SavesAndReturnsId() {
            // given
            LegacyOrderConversionOutbox domain = newPendingDomain();
            LegacyOrderConversionOutboxJpaEntity entityToSave =
                    LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity();
            LegacyOrderConversionOutboxJpaEntity savedEntity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.saveAndFlush(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(savedEntity.getId());
            then(mapper).should().toEntity(domain);
            then(repository).should().saveAndFlush(entityToSave);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            LegacyOrderConversionOutbox domain = newPendingDomain();
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.saveAndFlush(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(org.mockito.Mockito.times(1)).toEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Domain을 일괄 저장하고 ID 목록을 반환합니다")
        void persistAll_WithMultipleDomains_SavesAllAndReturnsIds() {
            // given
            LegacyOrderConversionOutbox domain1 = newPendingDomain();
            LegacyOrderConversionOutbox domain2 = newPendingDomain();
            LegacyOrderConversionOutboxJpaEntity entity1 =
                    LegacyOrderConversionOutboxJpaEntityFixtures.pendingEntity();
            LegacyOrderConversionOutboxJpaEntity entity2 =
                    LegacyOrderConversionOutboxJpaEntityFixtures.processingEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(any())).willReturn(List.of(entity1, entity2));

            // when
            List<Long> savedIds = commandAdapter.persistAll(List.of(domain1, domain2));

            // then
            assertThat(savedIds).hasSize(2);
            then(repository).should().saveAll(any());
        }

        @Test
        @DisplayName("빈 목록 저장 시 빈 목록을 반환합니다")
        void persistAll_WithEmptyList_ReturnsEmptyList() {
            // given
            given(repository.saveAll(any())).willReturn(List.of());

            // when
            List<Long> savedIds = commandAdapter.persistAll(List.of());

            // then
            assertThat(savedIds).isEmpty();
        }
    }
}
