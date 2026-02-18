package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.ChannelOptionMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.mapper.ChannelOptionMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.repository.ChannelOptionMappingJpaRepository;
import com.ryuqq.marketplace.domain.channeloptionmapping.ChannelOptionMappingFixtures;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ChannelOptionMappingCommandAdapterTest - ChannelOptionMapping Command Adapter 단위 테스트.
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
@DisplayName("ChannelOptionMappingCommandAdapter 단위 테스트")
class ChannelOptionMappingCommandAdapterTest {

    @Mock private ChannelOptionMappingJpaRepository repository;

    @Mock private ChannelOptionMappingJpaEntityMapper mapper;

    @InjectMocks private ChannelOptionMappingCommandAdapter commandAdapter;

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
            ChannelOptionMapping domain = ChannelOptionMappingFixtures.newChannelOptionMapping();
            ChannelOptionMappingJpaEntity entityToSave =
                    ChannelOptionMappingJpaEntityFixtures.newEntity();
            ChannelOptionMappingJpaEntity savedEntity =
                    ChannelOptionMappingJpaEntityFixtures.entity(100L);

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
        @DisplayName("새로운 ChannelOptionMapping을 저장합니다")
        void persist_WithNewChannelOptionMapping_Saves() {
            // given
            ChannelOptionMapping domain = ChannelOptionMappingFixtures.newChannelOptionMapping();
            ChannelOptionMappingJpaEntity entityToSave =
                    ChannelOptionMappingJpaEntityFixtures.newEntity();
            ChannelOptionMappingJpaEntity savedEntity =
                    ChannelOptionMappingJpaEntityFixtures.entity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("기존 ChannelOptionMapping을 저장합니다")
        void persist_WithExistingChannelOptionMapping_Saves() {
            // given
            ChannelOptionMapping domain =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();
            ChannelOptionMappingJpaEntity entityToSave =
                    ChannelOptionMappingJpaEntityFixtures.entity(1L);
            ChannelOptionMappingJpaEntity savedEntity =
                    ChannelOptionMappingJpaEntityFixtures.entity(1L);

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
            ChannelOptionMapping domain = ChannelOptionMappingFixtures.newChannelOptionMapping();
            ChannelOptionMappingJpaEntity entity = ChannelOptionMappingJpaEntityFixtures.entity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("저장 후 반환된 Entity의 ID를 반환합니다")
        void persist_ReturnsIdFromSavedEntity() {
            // given
            Long expectedId = 42L;
            ChannelOptionMapping domain = ChannelOptionMappingFixtures.newChannelOptionMapping();
            ChannelOptionMappingJpaEntity entityToSave =
                    ChannelOptionMappingJpaEntityFixtures.newEntity();
            ChannelOptionMappingJpaEntity savedEntity =
                    ChannelOptionMappingJpaEntityFixtures.entity(expectedId);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(expectedId);
        }
    }
}
