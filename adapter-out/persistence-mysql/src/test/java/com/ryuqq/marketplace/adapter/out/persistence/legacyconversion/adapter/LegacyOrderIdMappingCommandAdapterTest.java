package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderIdMappingJpaRepository;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
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
 * LegacyOrderIdMappingCommandAdapterTest - 주문 ID 매핑 Command Adapter 단위 테스트.
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
@DisplayName("LegacyOrderIdMappingCommandAdapter 단위 테스트")
class LegacyOrderIdMappingCommandAdapterTest {

    @Mock private LegacyOrderIdMappingJpaRepository repository;

    @Mock private LegacyOrderIdMappingJpaEntityMapper mapper;

    @InjectMocks private LegacyOrderIdMappingCommandAdapter commandAdapter;

    private LegacyOrderIdMapping newDomain() {
        return LegacyOrderIdMapping.forNew(
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_LEGACY_ORDER_ID,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_LEGACY_PAYMENT_ID,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_INTERNAL_ORDER_ID, 1001L,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_CHANNEL_NAME,
                Instant.now());
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
            LegacyOrderIdMapping domain = newDomain();
            LegacyOrderIdMappingJpaEntity entityToSave =
                    LegacyOrderIdMappingJpaEntityFixtures.newEntity();
            LegacyOrderIdMappingJpaEntity savedEntity =
                    LegacyOrderIdMappingJpaEntityFixtures.entity();

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
            LegacyOrderIdMapping domain = newDomain();
            LegacyOrderIdMappingJpaEntity entity = LegacyOrderIdMappingJpaEntityFixtures.entity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

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
            LegacyOrderIdMapping domain1 = newDomain();
            LegacyOrderIdMapping domain2 = newDomain();
            LegacyOrderIdMappingJpaEntity entity1 =
                    LegacyOrderIdMappingJpaEntityFixtures.entity(1L);
            LegacyOrderIdMappingJpaEntity entity2 =
                    LegacyOrderIdMappingJpaEntityFixtures.entity(2L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(any())).willReturn(List.of(entity1, entity2));

            // when
            List<Long> savedIds = commandAdapter.persistAll(List.of(domain1, domain2));

            // then
            assertThat(savedIds).hasSize(2);
            assertThat(savedIds).containsExactly(1L, 2L);
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
