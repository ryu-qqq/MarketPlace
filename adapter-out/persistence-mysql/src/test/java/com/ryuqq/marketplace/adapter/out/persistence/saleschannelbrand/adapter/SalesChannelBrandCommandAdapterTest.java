package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.SalesChannelBrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.mapper.SalesChannelBrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository.SalesChannelBrandJpaRepository;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SalesChannelBrandCommandAdapterTest - SalesChannelBrand Command Adapter 단위 테스트.
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
@DisplayName("SalesChannelBrandCommandAdapter 단위 테스트")
class SalesChannelBrandCommandAdapterTest {

    @Mock private SalesChannelBrandJpaRepository repository;

    @Mock private SalesChannelBrandJpaEntityMapper mapper;

    @InjectMocks private SalesChannelBrandCommandAdapter commandAdapter;

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
            SalesChannelBrand domain = SalesChannelBrandFixtures.newSalesChannelBrand();
            SalesChannelBrandJpaEntity entityToSave =
                    SalesChannelBrandJpaEntityFixtures.newEntity();
            SalesChannelBrandJpaEntity savedEntity =
                    SalesChannelBrandJpaEntityFixtures.activeEntity(100L);

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
        @DisplayName("활성 상태 브랜드를 저장합니다")
        void persist_WithActiveBrand_Saves() {
            // given
            SalesChannelBrand domain = SalesChannelBrandFixtures.activeSalesChannelBrand();
            SalesChannelBrandJpaEntity entityToSave =
                    SalesChannelBrandJpaEntityFixtures.activeEntity();
            SalesChannelBrandJpaEntity savedEntity =
                    SalesChannelBrandJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("비활성 상태 브랜드를 저장합니다")
        void persist_WithInactiveBrand_Saves() {
            // given
            SalesChannelBrand domain = SalesChannelBrandFixtures.inactiveSalesChannelBrand();
            SalesChannelBrandJpaEntity entityToSave =
                    SalesChannelBrandJpaEntityFixtures.inactiveEntity();
            SalesChannelBrandJpaEntity savedEntity =
                    SalesChannelBrandJpaEntityFixtures.inactiveEntity(2L);

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
            SalesChannelBrand domain = SalesChannelBrandFixtures.newSalesChannelBrand();
            SalesChannelBrandJpaEntity entity = SalesChannelBrandJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("특정 salesChannelId를 가진 브랜드를 저장합니다")
        void persist_WithSpecificSalesChannel_Saves() {
            // given
            Long salesChannelId = 100L;
            SalesChannelBrand domain =
                    SalesChannelBrandFixtures.newSalesChannelBrand(
                            salesChannelId, "CODE-100", "테스트 브랜드");
            SalesChannelBrandJpaEntity entityToSave =
                    SalesChannelBrandJpaEntityFixtures.activeEntityWithSalesChannel(salesChannelId);
            SalesChannelBrandJpaEntity savedEntity =
                    SalesChannelBrandJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
            then(mapper).should().toEntity(domain);
        }

        @Test
        @DisplayName("Repository가 정확히 한 번 호출됩니다")
        void persist_CallsRepositoryOnce() {
            // given
            SalesChannelBrand domain = SalesChannelBrandFixtures.newSalesChannelBrand();
            SalesChannelBrandJpaEntity entity = SalesChannelBrandJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should(times(1)).save(entity);
        }
    }
}
