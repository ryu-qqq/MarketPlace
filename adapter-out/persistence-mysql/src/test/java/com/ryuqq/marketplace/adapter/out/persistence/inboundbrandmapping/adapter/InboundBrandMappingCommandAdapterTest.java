package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.InboundBrandMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.mapper.InboundBrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.repository.InboundBrandMappingJpaRepository;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
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
 * InboundBrandMappingCommandAdapterTest - InboundBrandMapping Command Adapter 단위 테스트.
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
@DisplayName("InboundBrandMappingCommandAdapter 단위 테스트")
class InboundBrandMappingCommandAdapterTest {

    @Mock private InboundBrandMappingJpaRepository repository;

    @Mock private InboundBrandMappingJpaEntityMapper mapper;

    @InjectMocks private InboundBrandMappingCommandAdapter commandAdapter;

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
            InboundBrandMapping domain = InboundBrandMappingFixtures.newMapping();
            InboundBrandMappingJpaEntity entityToSave =
                    InboundBrandMappingJpaEntityFixtures.newEntity();
            InboundBrandMappingJpaEntity savedEntity =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(100L);

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
        @DisplayName("활성 상태 매핑을 저장합니다")
        void persist_WithActiveMapping_Saves() {
            // given
            InboundBrandMapping domain = InboundBrandMappingFixtures.activeMapping();
            InboundBrandMappingJpaEntity entityToSave =
                    InboundBrandMappingJpaEntityFixtures.newEntity();
            InboundBrandMappingJpaEntity savedEntity =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("비활성 상태 매핑을 저장합니다")
        void persist_WithInactiveMapping_Saves() {
            // given
            InboundBrandMapping domain = InboundBrandMappingFixtures.inactiveMapping();
            InboundBrandMappingJpaEntity entityToSave =
                    InboundBrandMappingJpaEntityFixtures.newEntity();
            InboundBrandMappingJpaEntity savedEntity =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(2L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            InboundBrandMapping domain = InboundBrandMappingFixtures.newMapping();
            InboundBrandMappingJpaEntity entity =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);

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
            InboundBrandMapping domain1 = InboundBrandMappingFixtures.activeMapping(1L);
            InboundBrandMapping domain2 = InboundBrandMappingFixtures.inactiveMapping();
            List<InboundBrandMapping> domains = List.of(domain1, domain2);

            InboundBrandMappingJpaEntity entity1 =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);
            InboundBrandMappingJpaEntity entity2 =
                    InboundBrandMappingJpaEntityFixtures.inactiveEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            List<Long> savedIds = commandAdapter.persistAll(domains);

            // then
            assertThat(savedIds).hasSize(2);
            then(repository).should().saveAll(List.of(entity1, entity2));
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<InboundBrandMapping> emptyList = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            List<Long> savedIds = commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<InboundBrandMappingJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
            assertThat(savedIds).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            InboundBrandMapping domain1 = InboundBrandMappingFixtures.activeMapping(1L);
            InboundBrandMapping domain2 = InboundBrandMappingFixtures.inactiveMapping();
            InboundBrandMapping domain3 = InboundBrandMappingFixtures.newMapping();
            List<InboundBrandMapping> domains = List.of(domain1, domain2, domain3);

            InboundBrandMappingJpaEntity entity =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);
            given(repository.saveAll(org.mockito.ArgumentMatchers.anyList()))
                    .willReturn(List.of(entity, entity, entity));

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(InboundBrandMapping.class));
        }
    }
}
