package com.ryuqq.marketplace.adapter.out.persistence.exchange.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.ExchangeClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper.ExchangePersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.assertj.core.api.Assertions;
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
 * ExchangeCommandAdapterTest - 교환 클레임 Command Adapter 단위 테스트.
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
@DisplayName("ExchangeCommandAdapter 단위 테스트")
class ExchangeCommandAdapterTest {

    @Mock private ExchangeClaimJpaRepository claimRepository;
    @Mock private ExchangePersistenceMapper mapper;

    @InjectMocks private ExchangeCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장합니다")
        void persist_WithValidDomain_SavesEntity() {
            // given
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(claimRepository).should().save(entity);
        }

        @Test
        @DisplayName("REQUESTED 상태 교환을 저장합니다")
        void persist_WithRequestedClaim_Saves() {
            // given
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(claimRepository).should().save(entity);
        }

        @Test
        @DisplayName("COMPLETED 상태 교환을 저장합니다")
        void persist_WithCompletedClaim_Saves() {
            // given
            ExchangeClaim domain = ExchangeFixtures.completedExchangeClaim();
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(claimRepository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

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
            ExchangeClaim domain1 = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaim domain2 = ExchangeFixtures.collectingExchangeClaim();
            List<ExchangeClaim> domains = List.of(domain1, domain2);

            ExchangeClaimJpaEntity entity1 = ExchangeClaimJpaEntityFixtures.requestedEntity();
            ExchangeClaimJpaEntity entity2 = ExchangeClaimJpaEntityFixtures.collectingEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ExchangeClaimJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(claimRepository).should().saveAll(captor.capture());

            List<ExchangeClaimJpaEntity> savedEntities = captor.getValue();
            Assertions.assertThat(savedEntities).hasSize(2);
            Assertions.assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<ExchangeClaim> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ExchangeClaimJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(claimRepository).should().saveAll(captor.capture());
            Assertions.assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            ExchangeClaim domain1 = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaim domain2 = ExchangeFixtures.collectingExchangeClaim();
            ExchangeClaim domain3 = ExchangeFixtures.completedExchangeClaim();
            List<ExchangeClaim> domains = List.of(domain1, domain2, domain3);

            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(ExchangeClaim.class));
        }
    }
}
