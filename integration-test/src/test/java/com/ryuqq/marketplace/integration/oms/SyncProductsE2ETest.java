package com.ryuqq.marketplace.integration.oms;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.OutboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OutboundProductJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.SellerSalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository.SellerSalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * syncProducts API E2E 통합 테스트.
 *
 * <p>테스트 대상: POST /oms/products/sync
 *
 * <p>기능 흐름: ManualSyncProductsService → Coordinator → ReadFacade(조회) → CommandFacade(생성) → 200 OK +
 * ManualSyncResult 응답
 *
 * <p>데이터 의존: Shop(salesChannelId), ProductGroup(sellerId), SellerSalesChannel(sellerId +
 * salesChannelId, CONNECTED)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("oms")
@Tag("command")
@DisplayName("syncProducts API E2E 테스트")
class SyncProductsE2ETest extends E2ETestBase {

    private static final String SYNC_URL = "/oms/products/sync";

    @Autowired private ShopJpaRepository shopRepository;
    @Autowired private ProductGroupJpaRepository productGroupRepository;
    @Autowired private SellerSalesChannelJpaRepository sellerSalesChannelRepository;
    @Autowired private OutboundProductJpaRepository outboundProductRepository;
    @Autowired private OutboundSyncOutboxJpaRepository syncOutboxRepository;

    private Long shopId;
    private Long productGroupId;
    private Long salesChannelId;

    @BeforeEach
    void setUp() {
        cleanUp();
        seedTestData();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        syncOutboxRepository.deleteAll();
        outboundProductRepository.deleteAll();
        sellerSalesChannelRepository.deleteAll();
        productGroupRepository.deleteAll();
        shopRepository.deleteAll();
    }

    /**
     * 기본 테스트 데이터 세팅.
     *
     * <p>Shop(salesChannelId=10) + ProductGroup(sellerId=1) + SellerSalesChannel(sellerId=1,
     * salesChannelId=10, CONNECTED)
     */
    private void seedTestData() {
        ShopJpaEntity shop =
                shopRepository.save(ShopJpaEntityFixtures.activeEntityWithSalesChannelId(10L));
        this.shopId = shop.getId();
        this.salesChannelId = 10L;

        ProductGroupJpaEntity pg =
                productGroupRepository.save(ProductGroupJpaEntityFixtures.activeEntity());
        this.productGroupId = pg.getId();

        sellerSalesChannelRepository.save(
                SellerSalesChannelJpaEntityFixtures.connectedEntityWithSellerId(
                        ProductGroupJpaEntityFixtures.DEFAULT_SELLER_ID));
    }

    // ========================================================================
    // 1. 정상 케이스 - CREATE
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/products/sync - CREATE 케이스")
    class SyncProductsCreateTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-S01] OutboundProduct 미존재 시 CREATE Outbox가 생성되고 200 OK를 반환한다")
        void syncProducts_noExistingProduct_createsOutboxAndReturns200() {
            // given: 기본 세팅 (OutboundProduct 없음)
            Map<String, Object> body =
                    Map.of("productIds", List.of(productGroupId), "shopId", shopId);

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.createCount", equalTo(1))
                    .body("data.updateCount", equalTo(0))
                    .body("data.skippedCount", equalTo(0))
                    .body("data.status", equalTo("ACCEPTED"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-S02] CREATE 후 DB에 OutboundProduct와 PENDING Outbox가 생성되어야 한다")
        void syncProducts_noExistingProduct_createsProductAndOutboxInDb() {
            // given
            Map<String, Object> body =
                    Map.of("productIds", List.of(productGroupId), "shopId", shopId);

            // when
            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: OutboundProduct 생성 확인
            boolean productExists =
                    outboundProductRepository.existsByProductGroupIdAndSalesChannelId(
                            productGroupId, salesChannelId);
            assertThat(productExists).isTrue();

            // then: OutboundSyncOutbox 생성 확인 (PENDING, SyncType=CREATE)
            List<OutboundSyncOutboxJpaEntity> outboxes =
                    syncOutboxRepository.findAll().stream()
                            .filter(e -> e.getProductGroupId().equals(productGroupId))
                            .filter(e -> e.getSalesChannelId().equals(salesChannelId))
                            .toList();
            assertThat(outboxes).hasSize(1);
            assertThat(outboxes.getFirst().getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
            assertThat(outboxes.getFirst().getSyncType())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.SyncType.CREATE);
        }
    }

    // ========================================================================
    // 2. 정상 케이스 - UPDATE
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/products/sync - UPDATE 케이스")
    class SyncProductsUpdateTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-S01] OutboundProduct 존재 시 UPDATE Outbox만 생성되고 200 OK를 반환한다")
        void syncProducts_existingProduct_createsUpdateOutboxAndReturns200() {
            // given: OutboundProduct 미리 생성
            outboundProductRepository.save(
                    OutboundProductJpaEntityFixtures.pendingEntityWith(
                            productGroupId, salesChannelId));

            Map<String, Object> body =
                    Map.of("productIds", List.of(productGroupId), "shopId", shopId);

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.createCount", equalTo(0))
                    .body("data.updateCount", equalTo(1))
                    .body("data.skippedCount", equalTo(0))
                    .body("data.status", equalTo("ACCEPTED"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-S02] UPDATE 후 DB에 OutboundSyncOutbox만 추가되고 OutboundProduct는 새로 생성되지 않는다")
        void syncProducts_existingProduct_onlyCreatesOutboxInDb() {
            // given: OutboundProduct 미리 생성
            outboundProductRepository.save(
                    OutboundProductJpaEntityFixtures.pendingEntityWith(
                            productGroupId, salesChannelId));
            long productCountBefore = outboundProductRepository.count();

            Map<String, Object> body =
                    Map.of("productIds", List.of(productGroupId), "shopId", shopId);

            // when
            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: OutboundProduct 수 변화 없음
            long productCountAfter = outboundProductRepository.count();
            assertThat(productCountAfter).isEqualTo(productCountBefore);

            // then: OutboundSyncOutbox의 SyncType이 UPDATE
            List<OutboundSyncOutboxJpaEntity> outboxes =
                    syncOutboxRepository.findAll().stream()
                            .filter(e -> e.getProductGroupId().equals(productGroupId))
                            .filter(e -> e.getSalesChannelId().equals(salesChannelId))
                            .toList();
            assertThat(outboxes).hasSize(1);
            assertThat(outboxes.getFirst().getSyncType())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.SyncType.UPDATE);
        }
    }

    // ========================================================================
    // 3. SKIP 케이스 - PENDING Outbox 이미 존재
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/products/sync - SKIP 케이스 (PENDING 중복)")
    class SyncProductsSkipPendingTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-S01] PENDING Outbox가 이미 존재하면 skippedCount가 1이고 새 Outbox는 생성되지 않는다")
        void syncProducts_pendingOutboxExists_skipsAndReturns200() {
            // given: PENDING Outbox 미리 생성
            syncOutboxRepository.save(
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                            productGroupId, salesChannelId));

            Map<String, Object> body =
                    Map.of("productIds", List.of(productGroupId), "shopId", shopId);

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.createCount", equalTo(0))
                    .body("data.updateCount", equalTo(0))
                    .body("data.skippedCount", equalTo(1))
                    .body("data.status", equalTo("ACCEPTED"));

            // then: Outbox 수가 1개로 유지 (새로 생성되지 않음)
            List<OutboundSyncOutboxJpaEntity> outboxes =
                    syncOutboxRepository.findAll().stream()
                            .filter(e -> e.getProductGroupId().equals(productGroupId))
                            .filter(e -> e.getSalesChannelId().equals(salesChannelId))
                            .toList();
            assertThat(outboxes).hasSize(1);
        }
    }

    // ========================================================================
    // 4. SKIP 케이스 - 셀러 미연결 채널
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/products/sync - SKIP 케이스 (셀러 미연결)")
    class SyncProductsSkipDisconnectedTest {

        @Test
        @Tag("P0")
        @DisplayName("[C4-S01] 셀러가 채널에 미연결이면 skippedCount가 1이다")
        void syncProducts_sellerNotConnected_skipsAndReturns200() {
            // given: 기존 CONNECTED SellerSalesChannel 삭제 후 DISCONNECTED로 교체
            sellerSalesChannelRepository.deleteAll();
            sellerSalesChannelRepository.save(
                    SellerSalesChannelJpaEntityFixtures.disconnectedEntityWithSellerId(
                            ProductGroupJpaEntityFixtures.DEFAULT_SELLER_ID));

            Map<String, Object> body =
                    Map.of("productIds", List.of(productGroupId), "shopId", shopId);

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.createCount", equalTo(0))
                    .body("data.updateCount", equalTo(0))
                    .body("data.skippedCount", equalTo(1))
                    .body("data.status", equalTo("ACCEPTED"));

            // then: OutboundProduct, OutboundSyncOutbox 모두 미생성
            assertThat(outboundProductRepository.count()).isZero();
            assertThat(syncOutboxRepository.count()).isZero();
        }
    }

    // ========================================================================
    // 5. 인증 테스트
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/products/sync - 인증 테스트")
    class SyncProductsAuthTest {

        @Test
        @Tag("P0")
        @DisplayName("[C5-A01] 비인증 요청 시 401 반환")
        void syncProducts_unauthenticated_returns401() {
            Map<String, Object> body =
                    Map.of("productIds", List.of(productGroupId), "shopId", shopId);

            given().spec(givenUnauthenticated())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // 6. 유효성 검증 테스트
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/products/sync - 요청 유효성 검증")
    class SyncProductsValidationTest {

        @Test
        @Tag("P0")
        @DisplayName("[C6-V01] productIds가 빈 리스트이면 400 반환")
        void syncProducts_emptyProductIds_returns400() {
            Map<String, Object> body = Map.of("productIds", List.of(), "shopId", shopId);

            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C6-V02] shopId가 null이면 400 반환")
        void syncProducts_nullShopId_returns400() {
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("productIds", List.of(productGroupId));
            body.put("shopId", null);

            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ========================================================================
    // 7. 전체 플로우 시나리오
    // ========================================================================

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 다수 ProductGroup에 대해 CREATE/UPDATE/SKIP이 혼합된 전체 플로우")
        void fullFlow_mixedScenario_createsUpdatesAndSkips() {
            // Step 1: 추가 ProductGroup 생성 (같은 sellerId)
            ProductGroupJpaEntity pg2 =
                    productGroupRepository.save(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity pg3 =
                    productGroupRepository.save(ProductGroupJpaEntityFixtures.activeEntity());

            // Step 2: pg2에 기존 OutboundProduct 생성 → UPDATE 대상
            outboundProductRepository.save(
                    OutboundProductJpaEntityFixtures.pendingEntityWith(
                            pg2.getId(), salesChannelId));

            // Step 3: pg3에 PENDING Outbox 생성 → SKIP 대상
            syncOutboxRepository.save(
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                            pg3.getId(), salesChannelId));

            // Step 4: sync 호출 (pg1=CREATE, pg2=UPDATE, pg3=SKIP)
            Map<String, Object> body =
                    Map.of(
                            "productIds",
                            List.of(productGroupId, pg2.getId(), pg3.getId()),
                            "shopId",
                            shopId);

            given().spec(givenAuthenticatedUser())
                    .body(body)
                    .when()
                    .post(SYNC_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.createCount", equalTo(1))
                    .body("data.updateCount", equalTo(1))
                    .body("data.skippedCount", equalTo(1))
                    .body("data.status", equalTo("ACCEPTED"));

            // Step 5: DB 상태 검증
            // pg1: OutboundProduct + CREATE Outbox 생성됨
            assertThat(
                            outboundProductRepository.existsByProductGroupIdAndSalesChannelId(
                                    productGroupId, salesChannelId))
                    .isTrue();

            // pg3: PENDING Outbox 1개만 유지 (새로 추가되지 않음)
            long pg3OutboxCount =
                    syncOutboxRepository.findAll().stream()
                            .filter(e -> e.getProductGroupId().equals(pg3.getId()))
                            .filter(e -> e.getSalesChannelId().equals(salesChannelId))
                            .count();
            assertThat(pg3OutboxCount).isEqualTo(1);
        }
    }
}
