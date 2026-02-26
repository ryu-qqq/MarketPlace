package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.SellerAddressJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.mapper.SellerAddressJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository.SellerAddressJpaRepository;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
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
 * SellerAddressCommandAdapterTest - 셀러 주소 Command Adapter 단위 테스트.
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
@DisplayName("SellerAddressCommandAdapter 단위 테스트")
class SellerAddressCommandAdapterTest {

    @Mock private SellerAddressJpaRepository jpaRepository;

    @Mock private SellerAddressJpaEntityMapper mapper;

    @InjectMocks private SellerAddressCommandAdapter commandAdapter;

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
            SellerAddress domain = SellerAddressFixtures.newShippingAddress(1L);
            SellerAddressJpaEntity entityToSave =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(1L);
            SellerAddressJpaEntity savedEntity =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(100L, 1L, "본사 창고", true);

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
        @DisplayName("SHIPPING 주소를 저장합니다")
        void persist_WithShippingAddress_Saves() {
            // given
            SellerAddress domain = SellerAddressFixtures.newShippingAddress(1L);
            SellerAddressJpaEntity entityToSave =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(1L);
            SellerAddressJpaEntity savedEntity =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(100L, 1L, "본사 창고", true);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("RETURN 주소를 저장합니다")
        void persist_WithReturnAddress_Saves() {
            // given
            SellerAddress domain = SellerAddressFixtures.newReturnAddress(1L);
            SellerAddressJpaEntity entityToSave =
                    SellerAddressJpaEntityFixtures.defaultReturnEntity(1L);
            SellerAddressJpaEntity savedEntity =
                    SellerAddressJpaEntityFixtures.returnEntityWithId(100L, 1L, "반품 센터", true);

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
            SellerAddress domain = SellerAddressFixtures.newShippingAddress(1L);
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(1L);

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
            SellerAddress domain1 = SellerAddressFixtures.newShippingAddress(1L);
            SellerAddress domain2 = SellerAddressFixtures.newReturnAddress(1L);
            List<SellerAddress> domains = List.of(domain1, domain2);

            SellerAddressJpaEntity entity1 =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(1L);
            SellerAddressJpaEntity entity2 = SellerAddressJpaEntityFixtures.defaultReturnEntity(1L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerAddressJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<SellerAddressJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<SellerAddress> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerAddressJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            SellerAddress domain1 = SellerAddressFixtures.newShippingAddress(1L);
            SellerAddress domain2 = SellerAddressFixtures.newReturnAddress(1L);
            SellerAddress domain3 = SellerAddressFixtures.deletedAddress(1L, 1L);
            List<SellerAddress> domains = List.of(domain1, domain2, domain3);

            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(1L);
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(SellerAddress.class));
        }
    }
}
