package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite.SyncHistoryCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.condition.OutboundSyncOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySortKey;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * OmsSyncHistoryCompositionQueryDslRepositoryTest - 연동 이력 Composition QueryDSL 레포지토리 통합 테스트.
 *
 * <p>outbound_sync_outboxes LEFT JOIN seller_sales_channels LEFT JOIN shop LEFT JOIN
 * outbound_products 조인 쿼리 + 필터 조회 검증.
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
@DisplayName("OmsSyncHistoryCompositionQueryDslRepository 통합 테스트")
class OmsSyncHistoryCompositionQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private OmsSyncHistoryCompositionQueryDslRepository repository;

    // 공통 테스트 데이터
    private static final Long PRODUCT_GROUP_ID = 100L;
    private static final Long SALES_CHANNEL_ID = 1L;
    private static final Long SELLER_ID = 1L;

    private ShopJpaEntity shop;

    @BeforeEach
    void setUp() {
        repository =
                new OmsSyncHistoryCompositionQueryDslRepository(
                        new JPAQueryFactory(entityManager),
                        new OutboundSyncOutboxConditionBuilder());

        Instant now = Instant.now();

        shop = persist(createShop(null, SALES_CHANNEL_ID, "테스트 외부몰", "test-account-001", now));
        persist(createSellerSalesChannel(null, SELLER_ID, SALES_CHANNEL_ID, shop.getId(), now));
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private ShopJpaEntity createShop(
            Long id, Long salesChannelId, String shopName, String accountId, Instant now) {
        return ShopJpaEntity.create(
                id,
                salesChannelId,
                shopName,
                accountId,
                "ACTIVE",
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null);
    }

    private SellerSalesChannelJpaEntity createSellerSalesChannel(
            Long id, Long sellerId, Long salesChannelId, long shopId, Instant now) {
        return SellerSalesChannelJpaEntity.create(
                id,
                sellerId,
                salesChannelId,
                "TEST_CHANNEL",
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED,
                null,
                null,
                null,
                null,
                "테스트 채널",
                shopId,
                now,
                now);
    }

    private OutboundSyncOutboxJpaEntity createOutbox(
            Long productGroupId,
            Long salesChannelId,
            Long sellerId,
            OutboundSyncOutboxJpaEntity.Status status,
            Instant processedAt,
            String idempotencyKey) {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                salesChannelId,
                sellerId,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                status,
                "{}",
                0,
                3,
                now,
                now,
                processedAt,
                null,
                0L,
                idempotencyKey);
    }

    private OutboundSyncOutboxJpaEntity createFailedOutbox(
            Long productGroupId, Long salesChannelId, Long sellerId, String idempotencyKey) {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                salesChannelId,
                sellerId,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.FAILED,
                "{}",
                3,
                3,
                now,
                now,
                now.minusSeconds(1800),
                "외부 채널 연동 최대 재시도 초과",
                0L,
                idempotencyKey);
    }

    private OutboundProductJpaEntity createOutboundProduct(
            Long productGroupId, Long salesChannelId, String externalProductId) {
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                null, productGroupId, salesChannelId, externalProductId, "REGISTERED", now, now);
    }

    private SyncHistorySearchCriteria defaultCriteria(Long productGroupId) {
        QueryContext<SyncHistorySortKey> queryContext =
                QueryContext.of(
                        SyncHistorySortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
        return new SyncHistorySearchCriteria(productGroupId, null, queryContext);
    }

    private SyncHistorySearchCriteria criteriaWithStatus(Long productGroupId, SyncStatus status) {
        QueryContext<SyncHistorySortKey> queryContext =
                QueryContext.of(
                        SyncHistorySortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
        return new SyncHistorySearchCriteria(productGroupId, status, queryContext);
    }

    // ========================================================================
    // 1. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("해당 상품그룹의 연동 이력을 모두 반환합니다")
        void findByCriteria_WithMatchingProductGroup_ReturnsAllHistories() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-" + UUID.randomUUID()));
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.FAILED,
                            Instant.now().minusSeconds(7200),
                            "key-" + UUID.randomUUID()));

            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("다른 상품그룹의 이력은 조회되지 않습니다")
        void findByCriteria_WithDifferentProductGroup_ReturnsEmpty() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-" + UUID.randomUUID()));

            // when
            Long otherProductGroupId = 999L;
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(defaultCriteria(otherProductGroupId));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태 필터 적용 시 해당 상태만 반환합니다")
        void findByCriteria_WithCompletedStatusFilter_ReturnsOnlyCompleted() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-completed-" + UUID.randomUUID()));
            persist(
                    createFailedOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            "key-failed-" + UUID.randomUUID()));

            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(
                            criteriaWithStatus(PRODUCT_GROUP_ID, SyncStatus.COMPLETED));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("FAILED 상태 필터 적용 시 실패 이력만 반환합니다")
        void findByCriteria_WithFailedStatusFilter_ReturnsOnlyFailed() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-completed-" + UUID.randomUUID()));
            persist(
                    createFailedOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            "key-failed-" + UUID.randomUUID()));

            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(
                            criteriaWithStatus(PRODUCT_GROUP_ID, SyncStatus.FAILED));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("FAILED");
            assertThat(result.get(0).errorMessage()).isEqualTo("외부 채널 연동 최대 재시도 초과");
        }

        @Test
        @DisplayName("shop 정보가 LEFT JOIN으로 조회되어 shopName과 accountId가 반환됩니다")
        void findByCriteria_WithShopJoin_ReturnsShopInfo() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-" + UUID.randomUUID()));

            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).shopName()).isEqualTo("테스트 외부몰");
            assertThat(result.get(0).accountId()).isEqualTo("test-account-001");
        }

        @Test
        @DisplayName("outbound_products와 LEFT JOIN으로 externalProductId가 조회됩니다")
        void findByCriteria_WithOutboundProductJoin_ReturnsExternalProductId() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-" + UUID.randomUUID()));
            persist(createOutboundProduct(PRODUCT_GROUP_ID, SALES_CHANNEL_ID, "EXT-PROD-001"));

            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalProductId()).isEqualTo("EXT-PROD-001");
        }

        @Test
        @DisplayName("outbound_products가 없으면 externalProductId는 null을 반환합니다")
        void findByCriteria_WithoutOutboundProduct_ReturnsNullExternalProductId() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.PENDING,
                            null,
                            "key-" + UUID.randomUUID()));

            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalProductId()).isNull();
        }

        @Test
        @DisplayName("결과는 createdAt 내림차순으로 정렬됩니다")
        void findByCriteria_ResultsAreOrderedByCreatedAtDesc() {
            // given: 순차적으로 생성하여 createdAt 차이를 확보
            OutboundSyncOutboxJpaEntity older =
                    persist(
                            createOutbox(
                                    PRODUCT_GROUP_ID,
                                    SALES_CHANNEL_ID,
                                    SELLER_ID,
                                    OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                                    Instant.now().minusSeconds(7200),
                                    "key-older-" + UUID.randomUUID()));
            OutboundSyncOutboxJpaEntity newer =
                    persist(
                            createOutbox(
                                    PRODUCT_GROUP_ID,
                                    SALES_CHANNEL_ID,
                                    SELLER_ID,
                                    OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                                    Instant.now().minusSeconds(3600),
                                    "key-newer-" + UUID.randomUUID()));

            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(result).hasSize(2);
            // newer가 먼저 조회되어야 함 (createdAt DESC)
            assertThat(result.get(0).outboxId()).isEqualTo(newer.getId());
            assertThat(result.get(1).outboxId()).isEqualTo(older.getId());
        }

        @Test
        @DisplayName("페이지네이션이 정상 동작합니다")
        void findByCriteria_WithPagination_ReturnsPagedResults() {
            // given
            for (int i = 0; i < 3; i++) {
                persist(
                        createOutbox(
                                PRODUCT_GROUP_ID,
                                SALES_CHANNEL_ID,
                                SELLER_ID,
                                OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                                Instant.now().minusSeconds(3600L * (i + 1)),
                                "key-page-" + i + "-" + UUID.randomUUID()));
            }

            QueryContext<SyncHistorySortKey> pageContext =
                    QueryContext.of(
                            SyncHistorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 2));
            SyncHistorySearchCriteria criteria =
                    new SyncHistorySearchCriteria(PRODUCT_GROUP_ID, null, pageContext);

            // when
            List<SyncHistoryCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("이력이 없으면 빈 목록을 반환합니다")
        void findByCriteria_WithNoHistory_ReturnsEmptyList() {
            // when
            List<SyncHistoryCompositeDto> result =
                    repository.findByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("전체 이력 개수를 반환합니다")
        void countByCriteria_WithMultipleHistories_ReturnsTotalCount() {
            // given
            for (int i = 0; i < 3; i++) {
                persist(
                        createOutbox(
                                PRODUCT_GROUP_ID,
                                SALES_CHANNEL_ID,
                                SELLER_ID,
                                OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                                Instant.now().minusSeconds(3600L * (i + 1)),
                                "key-count-" + i + "-" + UUID.randomUUID()));
            }

            // when
            long count = repository.countByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(count).isEqualTo(3L);
        }

        @Test
        @DisplayName("COMPLETED 상태 필터 적용 시 해당 개수를 반환합니다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-completed-" + UUID.randomUUID()));
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(7200),
                            "key-completed2-" + UUID.randomUUID()));
            persist(
                    createFailedOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            "key-failed-" + UUID.randomUUID()));

            // when
            long count =
                    repository.countByCriteria(
                            criteriaWithStatus(PRODUCT_GROUP_ID, SyncStatus.COMPLETED));

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("다른 상품그룹의 이력은 집계되지 않습니다")
        void countByCriteria_WithDifferentProductGroup_ReturnsZero() {
            // given
            persist(
                    createOutbox(
                            PRODUCT_GROUP_ID,
                            SALES_CHANNEL_ID,
                            SELLER_ID,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            Instant.now().minusSeconds(3600),
                            "key-" + UUID.randomUUID()));

            // when
            long count = repository.countByCriteria(defaultCriteria(999L));

            // then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("이력이 없으면 0을 반환합니다")
        void countByCriteria_WithNoHistory_ReturnsZero() {
            // when
            long count = repository.countByCriteria(defaultCriteria(PRODUCT_GROUP_ID));

            // then
            assertThat(count).isZero();
        }
    }
}
