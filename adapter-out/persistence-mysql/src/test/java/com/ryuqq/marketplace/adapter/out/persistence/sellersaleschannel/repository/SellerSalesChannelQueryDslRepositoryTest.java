package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.SellerSalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.condition.SellerSalesChannelConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * SellerSalesChannelQueryDslRepositoryTest - 셀러 판매채널 QueryDslRepository 통합 테스트.
 *
 * <p>CONNECTED 상태 필터 동작을 검증합니다.
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerSalesChannelQueryDslRepository 통합 테스트")
class SellerSalesChannelQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerSalesChannelQueryDslRepository repository() {
        return new SellerSalesChannelQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerSalesChannelConditionBuilder());
    }

    private SellerSalesChannelJpaEntity persist(SellerSalesChannelJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findConnectedBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findConnectedBySellerId")
    class FindConnectedBySellerIdTest {

        @Test
        @DisplayName("CONNECTED 상태 채널은 sellerId로 조회됩니다")
        void findConnectedBySellerId_WithConnectedChannel_ReturnsEntity() {
            // given
            Long sellerId = 10L;
            SellerSalesChannelJpaEntity connected =
                    persist(
                            SellerSalesChannelJpaEntityFixtures.connectedEntityWithSellerId(
                                    sellerId));

            // when
            List<SellerSalesChannelJpaEntity> result =
                    repository().findConnectedBySellerId(sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(connected.getId());
        }

        @Test
        @DisplayName("DISCONNECTED 상태 채널은 조회되지 않습니다")
        void findConnectedBySellerId_WithDisconnectedChannel_ReturnsEmpty() {
            // given
            Long sellerId = 11L;
            persist(SellerSalesChannelJpaEntityFixtures.disconnectedEntityWithSellerId(sellerId));

            // when
            List<SellerSalesChannelJpaEntity> result =
                    repository().findConnectedBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("SUSPENDED 상태 채널은 조회되지 않습니다")
        void findConnectedBySellerId_WithSuspendedChannel_ReturnsEmpty() {
            // given
            Long sellerId = 12L;
            persist(SellerSalesChannelJpaEntityFixtures.suspendedEntityWithSellerId(sellerId));

            // when
            List<SellerSalesChannelJpaEntity> result =
                    repository().findConnectedBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("CONNECTED 상태 채널만 필터링하여 반환합니다")
        void findConnectedBySellerId_WithMixedStatuses_ReturnsOnlyConnected() {
            // given
            Long sellerId = 13L;
            SellerSalesChannelJpaEntity connected =
                    persist(
                            SellerSalesChannelJpaEntityFixtures.connectedEntityWithSellerId(
                                    sellerId));
            persist(SellerSalesChannelJpaEntityFixtures.disconnectedEntityWithSellerId(sellerId));
            persist(SellerSalesChannelJpaEntityFixtures.suspendedEntityWithSellerId(sellerId));

            // when
            List<SellerSalesChannelJpaEntity> result =
                    repository().findConnectedBySellerId(sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(connected.getId());
            assertThat(result.get(0).getConnectionStatus())
                    .isEqualTo(SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED);
        }

        @Test
        @DisplayName("여러 CONNECTED 채널이 있으면 모두 반환합니다")
        void findConnectedBySellerId_WithMultipleConnectedChannels_ReturnsAll() {
            // given
            Long sellerId = 14L;
            SellerSalesChannelJpaEntity channel1 =
                    persist(
                            SellerSalesChannelJpaEntityFixtures.connectedEntityWithSellerId(
                                    sellerId));
            SellerSalesChannelJpaEntity channel2 =
                    persist(
                            SellerSalesChannelJpaEntityFixtures.connectedEntityWithSellerId(
                                    sellerId));

            // when
            List<SellerSalesChannelJpaEntity> result =
                    repository().findConnectedBySellerId(sellerId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(SellerSalesChannelJpaEntity::getId)
                    .containsExactlyInAnyOrder(channel1.getId(), channel2.getId());
        }

        @Test
        @DisplayName("null sellerId 입력 시 빈 리스트를 반환합니다")
        void findConnectedBySellerId_WithNullSellerId_ReturnsEmpty() {
            // when
            List<SellerSalesChannelJpaEntity> result = repository().findConnectedBySellerId(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 sellerId의 채널은 조회되지 않습니다")
        void findConnectedBySellerId_WithDifferentSellerId_DoesNotReturn() {
            // given
            Long sellerIdA = 20L;
            Long sellerIdB = 21L;
            persist(SellerSalesChannelJpaEntityFixtures.connectedEntityWithSellerId(sellerIdA));

            // when
            List<SellerSalesChannelJpaEntity> result =
                    repository().findConnectedBySellerId(sellerIdB);

            // then
            assertThat(result).isEmpty();
        }
    }
}
