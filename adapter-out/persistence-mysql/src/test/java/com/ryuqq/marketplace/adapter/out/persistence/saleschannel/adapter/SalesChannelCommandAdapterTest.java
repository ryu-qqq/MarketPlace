package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.mapper.SalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SalesChannelCommandAdapterTest - 판매 채널 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelCommandAdapter 단위 테스트")
class SalesChannelCommandAdapterTest {

    @Mock private SalesChannelJpaRepository jpaRepository;

    @Mock private SalesChannelJpaEntityMapper mapper;

    @InjectMocks private SalesChannelCommandAdapter commandAdapter;

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
            SalesChannel domain = SalesChannelFixtures.newSalesChannel();
            SalesChannelJpaEntity entityToSave = SalesChannelJpaEntityFixtures.newEntity();
            SalesChannelJpaEntity savedEntity = SalesChannelJpaEntityFixtures.activeEntity(100L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toEntity(domain);
            then(jpaRepository).should().save(entityToSave);
        }

        @Test
        @DisplayName("활성 상태 판매 채널을 저장합니다")
        void persist_WithActiveSalesChannel_Saves() {
            // given
            SalesChannel domain = SalesChannelFixtures.activeSalesChannel();
            SalesChannelJpaEntity entityToSave = SalesChannelJpaEntityFixtures.newEntity();
            SalesChannelJpaEntity savedEntity = SalesChannelJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("비활성 상태 판매 채널을 저장합니다")
        void persist_WithInactiveSalesChannel_Saves() {
            // given
            SalesChannel domain = SalesChannelFixtures.inactiveSalesChannel();
            SalesChannelJpaEntity entityToSave = SalesChannelJpaEntityFixtures.newEntity();
            SalesChannelJpaEntity savedEntity = SalesChannelJpaEntityFixtures.activeEntity(2L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            SalesChannel domain = SalesChannelFixtures.newSalesChannel();
            SalesChannelJpaEntity entity = SalesChannelJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }
}
