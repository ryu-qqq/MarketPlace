package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyConversionOutboxJpaRepository;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyConversionOutboxCommandAdapterTest - Outbox Command Adapter 단위 테스트.
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
@DisplayName("LegacyConversionOutboxCommandAdapter 단위 테스트")
class LegacyConversionOutboxCommandAdapterTest {

    @Mock private LegacyConversionOutboxJpaRepository repository;

    @Mock private LegacyConversionOutboxJpaEntityMapper mapper;

    @InjectMocks private LegacyConversionOutboxCommandAdapter commandAdapter;

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
            LegacyConversionOutbox domain = LegacyConversionFixtures.newPendingOutbox();
            LegacyConversionOutboxJpaEntity entityToSave =
                    LegacyConversionOutboxJpaEntityFixtures.newPendingEntity();
            LegacyConversionOutboxJpaEntity savedEntity =
                    LegacyConversionOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(savedEntity.getId());
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            LegacyConversionOutbox domain = LegacyConversionFixtures.newPendingOutbox();
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(org.mockito.Mockito.times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("PENDING 상태 Outbox를 저장합니다")
        void persist_WithPendingOutbox_Saves() {
            // given
            LegacyConversionOutbox domain = LegacyConversionFixtures.pendingOutbox();
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }
    }
}
