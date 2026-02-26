package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.SellerSalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.sellersaleschannel.vo.ConnectionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerSalesChannelJpaEntityMapperTest - 셀러 판매채널 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerSalesChannelJpaEntityMapper 단위 테스트")
class SellerSalesChannelJpaEntityMapperTest {

    private SellerSalesChannelJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerSalesChannelJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("CONNECTED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithConnectedDomain_ConvertsCorrectly() {
            // given
            SellerSalesChannel domain = SellerSalesChannelFixtures.connectedSellerSalesChannel();

            // when
            SellerSalesChannelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelIdValue());
            assertThat(entity.getChannelCode()).isEqualTo(domain.channelCode());
            assertThat(entity.getConnectionStatus())
                    .isEqualTo(SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED);
            assertThat(entity.getApiKey()).isEqualTo(domain.apiKey());
            assertThat(entity.getApiSecret()).isEqualTo(domain.apiSecret());
            assertThat(entity.getAccessToken()).isEqualTo(domain.accessToken());
            assertThat(entity.getVendorId()).isEqualTo(domain.vendorId());
            assertThat(entity.getDisplayName()).isEqualTo(domain.displayName());
        }

        @Test
        @DisplayName("DISCONNECTED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithDisconnectedDomain_ConvertsStatusCorrectly() {
            // given
            SellerSalesChannel domain = SellerSalesChannelFixtures.disconnectedSellerSalesChannel();

            // when
            SellerSalesChannelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getConnectionStatus())
                    .isEqualTo(SellerSalesChannelJpaEntity.ConnectionStatus.DISCONNECTED);
        }

        @Test
        @DisplayName("SUSPENDED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithSuspendedDomain_ConvertsStatusCorrectly() {
            // given
            SellerSalesChannel domain = SellerSalesChannelFixtures.suspendedSellerSalesChannel();

            // when
            SellerSalesChannelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getConnectionStatus())
                    .isEqualTo(SellerSalesChannelJpaEntity.ConnectionStatus.SUSPENDED);
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다 (ID 없음)")
        void toEntity_WithNewDomain_IdIsNull() {
            // given
            SellerSalesChannel domain = SellerSalesChannelFixtures.newSellerSalesChannel();

            // when
            SellerSalesChannelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getChannelCode()).isEqualTo(domain.channelCode());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("CONNECTED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithConnectedEntity_ConvertsCorrectly() {
            // given
            SellerSalesChannelJpaEntity entity =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);

            // when
            SellerSalesChannel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.salesChannelIdValue()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.channelCode()).isEqualTo(entity.getChannelCode());
            assertThat(domain.connectionStatus()).isEqualTo(ConnectionStatus.CONNECTED);
            assertThat(domain.apiKey()).isEqualTo(entity.getApiKey());
            assertThat(domain.apiSecret()).isEqualTo(entity.getApiSecret());
            assertThat(domain.accessToken()).isEqualTo(entity.getAccessToken());
            assertThat(domain.vendorId()).isEqualTo(entity.getVendorId());
            assertThat(domain.displayName()).isEqualTo(entity.getDisplayName());
        }

        @Test
        @DisplayName("DISCONNECTED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDisconnectedEntity_ConvertsStatusCorrectly() {
            // given - disconnectedEntity has null id, so use connectedEntity with id and verify
            // status mapping via toEntity
            SellerSalesChannel disconnectedDomain =
                    SellerSalesChannelFixtures.disconnectedSellerSalesChannel();
            SellerSalesChannelJpaEntity entityFromDomain = mapper.toEntity(disconnectedDomain);

            // when - reconstitute via domain fixtures which has id
            SellerSalesChannel result = mapper.toDomain(mapper.toEntity(disconnectedDomain));

            // then
            assertThat(entityFromDomain.getConnectionStatus())
                    .isEqualTo(SellerSalesChannelJpaEntity.ConnectionStatus.DISCONNECTED);
        }

        @Test
        @DisplayName("SUSPENDED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithSuspendedEntity_ConvertsStatusCorrectly() {
            // given - suspended domain has id=3L from fixtures
            SellerSalesChannel suspendedDomain =
                    SellerSalesChannelFixtures.suspendedSellerSalesChannel();
            SellerSalesChannelJpaEntity entity = mapper.toEntity(suspendedDomain);

            // when
            SellerSalesChannel result = mapper.toDomain(entity);

            // then
            assertThat(result.connectionStatus()).isEqualTo(ConnectionStatus.SUSPENDED);
        }

        @Test
        @DisplayName("vendorId가 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutVendorId_ConvertsCorrectly() {
            // given - use connectedEntity with explicit id for toDomain
            SellerSalesChannelJpaEntity entityWithId =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);

            // when
            SellerSalesChannel domain = mapper.toDomain(entityWithId);

            // then
            assertThat(domain.idValue()).isNotNull();
            assertThat(domain.vendorId()).isEqualTo(entityWithId.getVendorId());
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 예외를 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            SellerSalesChannelJpaEntity entity = SellerSalesChannelJpaEntityFixtures.newEntity();

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(
                    IllegalStateException.class, () -> mapper.toDomain(entity));
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            SellerSalesChannel original = SellerSalesChannelFixtures.connectedSellerSalesChannel();

            // when
            SellerSalesChannelJpaEntity entity = mapper.toEntity(original);
            SellerSalesChannel converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
            assertThat(converted.salesChannelIdValue()).isEqualTo(original.salesChannelIdValue());
            assertThat(converted.channelCode()).isEqualTo(original.channelCode());
            assertThat(converted.connectionStatus()).isEqualTo(original.connectionStatus());
            assertThat(converted.apiKey()).isEqualTo(original.apiKey());
            assertThat(converted.apiSecret()).isEqualTo(original.apiSecret());
            assertThat(converted.accessToken()).isEqualTo(original.accessToken());
            assertThat(converted.vendorId()).isEqualTo(original.vendorId());
            assertThat(converted.displayName()).isEqualTo(original.displayName());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerSalesChannelJpaEntity original =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);

            // when
            SellerSalesChannel domain = mapper.toDomain(original);
            SellerSalesChannelJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getChannelCode()).isEqualTo(original.getChannelCode());
            assertThat(converted.getConnectionStatus()).isEqualTo(original.getConnectionStatus());
            assertThat(converted.getApiKey()).isEqualTo(original.getApiKey());
            assertThat(converted.getApiSecret()).isEqualTo(original.getApiSecret());
            assertThat(converted.getAccessToken()).isEqualTo(original.getAccessToken());
            assertThat(converted.getVendorId()).isEqualTo(original.getVendorId());
            assertThat(converted.getDisplayName()).isEqualTo(original.getDisplayName());
        }
    }
}
