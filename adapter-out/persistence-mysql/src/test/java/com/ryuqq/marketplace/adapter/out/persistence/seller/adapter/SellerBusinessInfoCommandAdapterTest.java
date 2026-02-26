package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerBusinessInfoJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoJpaRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
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
 * SellerBusinessInfoCommandAdapterTest - 셀러 사업자 정보 Command Adapter 단위 테스트.
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
@DisplayName("SellerBusinessInfoCommandAdapter 단위 테스트")
class SellerBusinessInfoCommandAdapterTest {

    @Mock private SellerBusinessInfoJpaRepository jpaRepository;

    @Mock private SellerBusinessInfoJpaEntityMapper mapper;

    @InjectMocks private SellerBusinessInfoCommandAdapter commandAdapter;

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
            SellerBusinessInfo domain = SellerFixtures.newSellerBusinessInfo();
            SellerBusinessInfoJpaEntity entityToSave =
                    SellerBusinessInfoJpaEntityFixtures.newEntity();
            SellerBusinessInfoJpaEntity savedEntity =
                    SellerBusinessInfoJpaEntityFixtures.activeEntity(100L);

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
        @DisplayName("활성 상태 사업자 정보를 저장합니다")
        void persist_WithActiveBusinessInfo_Saves() {
            // given
            SellerBusinessInfo domain = SellerFixtures.activeSellerBusinessInfo();
            SellerBusinessInfoJpaEntity entityToSave =
                    SellerBusinessInfoJpaEntityFixtures.newEntity();
            SellerBusinessInfoJpaEntity savedEntity =
                    SellerBusinessInfoJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("삭제된 상태 사업자 정보를 저장합니다")
        void persist_WithDeletedBusinessInfo_Saves() {
            // given
            SellerBusinessInfo domain = SellerFixtures.deletedSellerBusinessInfo();
            SellerBusinessInfoJpaEntity entityToSave =
                    SellerBusinessInfoJpaEntityFixtures.newEntity();
            SellerBusinessInfoJpaEntity savedEntity =
                    SellerBusinessInfoJpaEntityFixtures.activeEntity(2L);

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
            SellerBusinessInfo domain = SellerFixtures.newSellerBusinessInfo();
            SellerBusinessInfoJpaEntity entity = SellerBusinessInfoJpaEntityFixtures.activeEntity();

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
            SellerBusinessInfo domain1 = SellerFixtures.activeSellerBusinessInfo();
            SellerBusinessInfo domain2 = SellerFixtures.deletedSellerBusinessInfo();
            List<SellerBusinessInfo> domains = List.of(domain1, domain2);

            SellerBusinessInfoJpaEntity entity1 =
                    SellerBusinessInfoJpaEntityFixtures.activeEntity();
            SellerBusinessInfoJpaEntity entity2 =
                    SellerBusinessInfoJpaEntityFixtures.deletedEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerBusinessInfoJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<SellerBusinessInfoJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<SellerBusinessInfo> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerBusinessInfoJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            SellerBusinessInfo domain1 = SellerFixtures.activeSellerBusinessInfo();
            SellerBusinessInfo domain2 = SellerFixtures.deletedSellerBusinessInfo();
            List<SellerBusinessInfo> domains = List.of(domain1, domain2);

            SellerBusinessInfoJpaEntity entity = SellerBusinessInfoJpaEntityFixtures.activeEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(2))
                    .toEntity(org.mockito.ArgumentMatchers.any(SellerBusinessInfo.class));
        }
    }
}
