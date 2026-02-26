package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.SellerSalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.mapper.SellerSalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository.SellerSalesChannelJpaRepository;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SellerSalesChannelCommandAdapterTest - 셀러 판매채널 Command Adapter 단위 테스트.
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
@DisplayName("SellerSalesChannelCommandAdapter 단위 테스트")
class SellerSalesChannelCommandAdapterTest {

    @Mock private SellerSalesChannelJpaRepository repository;

    @Mock private SellerSalesChannelJpaEntityMapper mapper;

    @InjectMocks private SellerSalesChannelCommandAdapter commandAdapter;

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
            SellerSalesChannel domain = SellerSalesChannelFixtures.newSellerSalesChannel();
            SellerSalesChannelJpaEntity entityToSave =
                    SellerSalesChannelJpaEntityFixtures.newEntity();
            SellerSalesChannelJpaEntity savedEntity =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(100L);

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
        @DisplayName("CONNECTED 상태 채널을 저장합니다")
        void persist_WithConnectedChannel_Saves() {
            // given
            SellerSalesChannel domain = SellerSalesChannelFixtures.connectedSellerSalesChannel();
            SellerSalesChannelJpaEntity entityToSave =
                    SellerSalesChannelJpaEntityFixtures.newEntity();
            SellerSalesChannelJpaEntity savedEntity =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("DISCONNECTED 상태 채널을 저장합니다")
        void persist_WithDisconnectedChannel_Saves() {
            // given
            SellerSalesChannel domain = SellerSalesChannelFixtures.disconnectedSellerSalesChannel();
            SellerSalesChannelJpaEntity entityToSave =
                    SellerSalesChannelJpaEntityFixtures.disconnectedEntity();
            SellerSalesChannelJpaEntity savedEntity =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(2L);

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
            SellerSalesChannel domain = SellerSalesChannelFixtures.newSellerSalesChannel();
            SellerSalesChannelJpaEntity entity =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("저장된 Entity의 ID를 반환합니다")
        void persist_ReturnsIdOfSavedEntity() {
            // given
            SellerSalesChannel domain = SellerSalesChannelFixtures.newSellerSalesChannel();
            SellerSalesChannelJpaEntity entityToSave =
                    SellerSalesChannelJpaEntityFixtures.newEntity();
            SellerSalesChannelJpaEntity savedEntity =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(42L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(domain);

            // then
            assertThat(result).isEqualTo(42L);
        }
    }
}
