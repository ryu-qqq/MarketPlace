package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.OutboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper.OutboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OutboundProductJpaRepository;
import com.ryuqq.marketplace.domain.outboundproduct.OutboundProductFixtures;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
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
 * OutboundProductCommandAdapterTest - OutboundProduct Command Adapter 단위 테스트.
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
@DisplayName("OutboundProductCommandAdapter 단위 테스트")
class OutboundProductCommandAdapterTest {

    @Mock private OutboundProductJpaRepository jpaRepository;

    @Mock private OutboundProductJpaEntityMapper mapper;

    @InjectMocks private OutboundProductCommandAdapter commandAdapter;

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
            OutboundProduct domain = OutboundProductFixtures.newPendingProduct();
            OutboundProductJpaEntity entityToSave =
                    OutboundProductJpaEntityFixtures.pendingEntity();
            OutboundProductJpaEntity savedEntity =
                    OutboundProductJpaEntityFixtures.pendingEntity(100L);

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
        @DisplayName("PENDING 상태 OutboundProduct를 저장합니다")
        void persist_WithPendingProduct_Saves() {
            // given
            OutboundProduct domain = OutboundProductFixtures.pendingProduct();
            OutboundProductJpaEntity entityToSave =
                    OutboundProductJpaEntityFixtures.pendingEntity();
            OutboundProductJpaEntity savedEntity =
                    OutboundProductJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("REGISTERED 상태 OutboundProduct를 저장합니다")
        void persist_WithRegisteredProduct_Saves() {
            // given
            OutboundProduct domain = OutboundProductFixtures.registeredProduct();
            OutboundProductJpaEntity entityToSave =
                    OutboundProductJpaEntityFixtures.registeredEntity();
            OutboundProductJpaEntity savedEntity =
                    OutboundProductJpaEntityFixtures.registeredEntity(2L);

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
            OutboundProduct domain = OutboundProductFixtures.newPendingProduct();
            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(entity);

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
            OutboundProduct domain1 = OutboundProductFixtures.pendingProduct();
            OutboundProduct domain2 = OutboundProductFixtures.registeredProduct();
            List<OutboundProduct> domains = List.of(domain1, domain2);

            OutboundProductJpaEntity entity1 = OutboundProductJpaEntityFixtures.pendingEntity(1L);
            OutboundProductJpaEntity entity2 =
                    OutboundProductJpaEntityFixtures.registeredEntity(2L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<OutboundProductJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<OutboundProductJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<OutboundProduct> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<OutboundProductJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            OutboundProduct domain1 = OutboundProductFixtures.pendingProduct();
            OutboundProduct domain2 = OutboundProductFixtures.registeredProduct();
            OutboundProduct domain3 = OutboundProductFixtures.failedProduct();
            List<OutboundProduct> domains = List.of(domain1, domain2, domain3);

            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.entity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(OutboundProduct.class));
        }
    }
}
