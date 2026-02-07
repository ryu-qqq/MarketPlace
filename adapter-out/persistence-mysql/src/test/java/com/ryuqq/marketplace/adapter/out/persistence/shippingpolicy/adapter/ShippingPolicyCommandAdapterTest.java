package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.mapper.ShippingPolicyJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository.ShippingPolicyJpaRepository;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
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
 * ShippingPolicyCommandAdapterTest - 배송 정책 Command Adapter 단위 테스트.
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
@DisplayName("ShippingPolicyCommandAdapter 단위 테스트")
class ShippingPolicyCommandAdapterTest {

    @Mock private ShippingPolicyJpaRepository jpaRepository;

    @Mock private ShippingPolicyJpaEntityMapper mapper;

    @InjectMocks private ShippingPolicyCommandAdapter commandAdapter;

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
            ShippingPolicy domain = ShippingPolicyFixtures.newFreeShippingPolicy();
            ShippingPolicyJpaEntity entityToSave = ShippingPolicyJpaEntityFixtures.newEntity();
            ShippingPolicyJpaEntity savedEntity =
                    ShippingPolicyJpaEntityFixtures.activeEntity(100L);

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
        @DisplayName("무료배송 정책을 저장합니다")
        void persist_WithFreeShippingPolicy_Saves() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.newFreeShippingPolicy();
            ShippingPolicyJpaEntity entityToSave = ShippingPolicyJpaEntityFixtures.newEntity();
            ShippingPolicyJpaEntity savedEntity = ShippingPolicyJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("유료배송 정책을 저장합니다")
        void persist_WithPaidShippingPolicy_Saves() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.newPaidShippingPolicy();
            ShippingPolicyJpaEntity entityToSave = ShippingPolicyJpaEntityFixtures.newEntity();
            ShippingPolicyJpaEntity savedEntity = ShippingPolicyJpaEntityFixtures.activeEntity(2L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("조건부 무료배송 정책을 저장합니다")
        void persist_WithConditionalFreeShippingPolicy_Saves() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();
            ShippingPolicyJpaEntity entityToSave = ShippingPolicyJpaEntityFixtures.newEntity();
            ShippingPolicyJpaEntity savedEntity = ShippingPolicyJpaEntityFixtures.activeEntity(3L);

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
            ShippingPolicy domain = ShippingPolicyFixtures.newFreeShippingPolicy();
            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity();

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
            ShippingPolicy domain1 = ShippingPolicyFixtures.newFreeShippingPolicy();
            ShippingPolicy domain2 = ShippingPolicyFixtures.newPaidShippingPolicy();
            List<ShippingPolicy> domains = List.of(domain1, domain2);

            ShippingPolicyJpaEntity entity1 = ShippingPolicyJpaEntityFixtures.freeShippingEntity();
            ShippingPolicyJpaEntity entity2 = ShippingPolicyJpaEntityFixtures.paidShippingEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ShippingPolicyJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<ShippingPolicyJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<ShippingPolicy> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ShippingPolicyJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            ShippingPolicy domain1 = ShippingPolicyFixtures.newFreeShippingPolicy();
            ShippingPolicy domain2 = ShippingPolicyFixtures.newPaidShippingPolicy();
            ShippingPolicy domain3 = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();
            List<ShippingPolicy> domains = List.of(domain1, domain2, domain3);

            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(ShippingPolicy.class));
        }
    }
}
