package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyProductIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyProductIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyProductIdMappingJpaRepository;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductIdMappingCommandAdapterTest - ID 매핑 Command Adapter 단위 테스트.
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
@DisplayName("LegacyProductIdMappingCommandAdapter 단위 테스트")
class LegacyProductIdMappingCommandAdapterTest {

    @Mock private LegacyProductIdMappingJpaRepository repository;

    @Mock private LegacyProductIdMappingJpaEntityMapper mapper;

    @InjectMocks private LegacyProductIdMappingCommandAdapter commandAdapter;

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
            LegacyProductIdMapping domain = LegacyConversionFixtures.newMapping();
            LegacyProductIdMappingJpaEntity entityToSave =
                    LegacyProductIdMappingJpaEntityFixtures.newEntity();
            LegacyProductIdMappingJpaEntity savedEntity =
                    LegacyProductIdMappingJpaEntityFixtures.entity(100L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            LegacyProductIdMapping domain = LegacyConversionFixtures.newMapping();
            LegacyProductIdMappingJpaEntity entity =
                    LegacyProductIdMappingJpaEntityFixtures.entity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Domain을 Entity로 변환 후 일괄 저장합니다")
        void persistAll_WithMultipleDomains_SavesAll() {
            // given
            LegacyProductIdMapping domain1 = LegacyConversionFixtures.newMapping(201L, 301L);
            LegacyProductIdMapping domain2 = LegacyConversionFixtures.newMapping(202L, 302L);
            List<LegacyProductIdMapping> domains = List.of(domain1, domain2);

            LegacyProductIdMappingJpaEntity entity1 =
                    LegacyProductIdMappingJpaEntityFixtures.entity(1L);
            LegacyProductIdMappingJpaEntity entity2 =
                    LegacyProductIdMappingJpaEntityFixtures.entity(2L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<LegacyProductIdMappingJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());

            List<LegacyProductIdMappingJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<LegacyProductIdMapping> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<LegacyProductIdMappingJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            LegacyProductIdMapping domain1 = LegacyConversionFixtures.newMapping(201L, 301L);
            LegacyProductIdMapping domain2 = LegacyConversionFixtures.newMapping(202L, 302L);
            LegacyProductIdMapping domain3 = LegacyConversionFixtures.newMapping(203L, 303L);
            List<LegacyProductIdMapping> domains = List.of(domain1, domain2, domain3);

            LegacyProductIdMappingJpaEntity entity =
                    LegacyProductIdMappingJpaEntityFixtures.entity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(LegacyProductIdMapping.class));
        }
    }
}
