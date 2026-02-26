package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.InboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.mapper.InboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.repository.InboundProductJpaRepository;
import com.ryuqq.marketplace.domain.inboundproduct.InboundProductFixtures;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
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
 * InboundProductCommandAdapterTest - InboundProduct Command Adapter 단위 테스트.
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
@DisplayName("InboundProductCommandAdapter 단위 테스트")
class InboundProductCommandAdapterTest {

    @Mock private InboundProductJpaRepository repository;

    @Mock private InboundProductJpaEntityMapper mapper;

    @InjectMocks private InboundProductCommandAdapter commandAdapter;

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
            InboundProduct domain = InboundProductFixtures.newInboundProduct();
            InboundProductJpaEntity entityToSave = InboundProductJpaEntityFixtures.receivedEntity();
            InboundProductJpaEntity savedEntity =
                    InboundProductJpaEntityFixtures.receivedEntity(100L);

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
        @DisplayName("RECEIVED 상태 Domain을 저장합니다")
        void persist_WithReceivedProduct_Saves() {
            // given
            InboundProduct domain = InboundProductFixtures.receivedProduct();
            InboundProductJpaEntity entityToSave = InboundProductJpaEntityFixtures.receivedEntity();
            InboundProductJpaEntity savedEntity =
                    InboundProductJpaEntityFixtures.receivedEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("MAPPED 상태 Domain을 저장합니다")
        void persist_WithMappedProduct_Saves() {
            // given
            InboundProduct domain = InboundProductFixtures.mappedProduct();
            InboundProductJpaEntity entityToSave = InboundProductJpaEntityFixtures.mappedEntity();
            InboundProductJpaEntity savedEntity = InboundProductJpaEntityFixtures.mappedEntity(2L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("CONVERTED 상태 Domain을 저장합니다")
        void persist_WithConvertedProduct_Saves() {
            // given
            InboundProduct domain = InboundProductFixtures.convertedProduct();
            InboundProductJpaEntity entityToSave =
                    InboundProductJpaEntityFixtures.convertedEntity();
            InboundProductJpaEntity savedEntity =
                    InboundProductJpaEntityFixtures.convertedEntity(3L);

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
            InboundProduct domain = InboundProductFixtures.newInboundProduct();
            InboundProductJpaEntity entity = InboundProductJpaEntityFixtures.receivedEntity(1L);

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
            InboundProduct domain1 = InboundProductFixtures.receivedProduct(1L);
            InboundProduct domain2 = InboundProductFixtures.receivedProduct(2L);
            List<InboundProduct> domains = List.of(domain1, domain2);

            InboundProductJpaEntity entity1 = InboundProductJpaEntityFixtures.receivedEntity(1L);
            InboundProductJpaEntity entity2 = InboundProductJpaEntityFixtures.receivedEntity(2L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            List<Long> savedIds = commandAdapter.persistAll(domains);

            // then
            assertThat(savedIds).hasSize(2);
            assertThat(savedIds).containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("빈 리스트를 저장하면 빈 ID 목록을 반환합니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<InboundProduct> emptyList = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            List<Long> result = commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<InboundProductJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            InboundProduct domain1 = InboundProductFixtures.receivedProduct(1L);
            InboundProduct domain2 = InboundProductFixtures.mappedProduct();
            InboundProduct domain3 = InboundProductFixtures.convertedProduct();
            List<InboundProduct> domains = List.of(domain1, domain2, domain3);

            InboundProductJpaEntity entity = InboundProductJpaEntityFixtures.entity();
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
                    .toEntity(org.mockito.ArgumentMatchers.any(InboundProduct.class));
        }

        @Test
        @DisplayName("저장된 Entity의 ID 목록을 반환합니다")
        void persistAll_ReturnsIdListFromSavedEntities() {
            // given
            InboundProduct domain1 = InboundProductFixtures.receivedProduct(1L);
            InboundProduct domain2 = InboundProductFixtures.receivedProduct(2L);
            List<InboundProduct> domains = List.of(domain1, domain2);

            InboundProductJpaEntity entity1 = InboundProductJpaEntityFixtures.entity(10L);
            InboundProductJpaEntity entity2 = InboundProductJpaEntityFixtures.entity(20L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            List<Long> result = commandAdapter.persistAll(domains);

            // then
            assertThat(result).containsExactly(10L, 20L);
        }
    }
}
