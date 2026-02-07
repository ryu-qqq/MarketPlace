package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerSettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerSettlementJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerSettlementJpaRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
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
 * SellerSettlementCommandAdapterTest - 셀러 정산 정보 Command Adapter 단위 테스트.
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
@DisplayName("SellerSettlementCommandAdapter 단위 테스트")
class SellerSettlementCommandAdapterTest {

    @Mock private SellerSettlementJpaRepository jpaRepository;

    @Mock private SellerSettlementJpaEntityMapper mapper;

    @InjectMocks private SellerSettlementCommandAdapter commandAdapter;

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
            SellerSettlement domain = SellerFixtures.newSellerSettlement();
            SellerSettlementJpaEntity entityToSave = SellerSettlementJpaEntityFixtures.newEntity();
            SellerSettlementJpaEntity savedEntity =
                    SellerSettlementJpaEntityFixtures.verifiedEntity(100L);

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
        @DisplayName("검증된 상태 정산 정보를 저장합니다")
        void persist_WithVerifiedSettlement_Saves() {
            // given
            SellerSettlement domain = SellerFixtures.activeSellerSettlement();
            SellerSettlementJpaEntity entityToSave = SellerSettlementJpaEntityFixtures.newEntity();
            SellerSettlementJpaEntity savedEntity =
                    SellerSettlementJpaEntityFixtures.verifiedEntity(1L);

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
            SellerSettlement domain = SellerFixtures.newSellerSettlement();
            SellerSettlementJpaEntity entity = SellerSettlementJpaEntityFixtures.verifiedEntity();

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
            SellerSettlement domain1 = SellerFixtures.activeSellerSettlement();
            SellerSettlement domain2 = SellerFixtures.newSellerSettlement();
            List<SellerSettlement> domains = List.of(domain1, domain2);

            SellerSettlementJpaEntity entity1 = SellerSettlementJpaEntityFixtures.verifiedEntity();
            SellerSettlementJpaEntity entity2 =
                    SellerSettlementJpaEntityFixtures.unverifiedEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerSettlementJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<SellerSettlementJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<SellerSettlement> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerSettlementJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            SellerSettlement domain1 = SellerFixtures.activeSellerSettlement();
            SellerSettlement domain2 = SellerFixtures.newSellerSettlement();
            List<SellerSettlement> domains = List.of(domain1, domain2);

            SellerSettlementJpaEntity entity = SellerSettlementJpaEntityFixtures.verifiedEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(2))
                    .toEntity(org.mockito.ArgumentMatchers.any(SellerSettlement.class));
        }
    }
}
