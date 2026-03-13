package com.ryuqq.marketplace.integration.oms;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OutboundProductJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository.SellerSalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * OMS 상품 조회 API E2E 테스트.
 *
 * <p>테스트 대상: - GET /oms/products - OMS 상품 목록 조회 - GET /oms/products/{productGroupId} - OMS 상품 상세 조회
 * - GET /oms/products/{productGroupId}/sync-history - 연동 이력 조회
 *
 * <p>의존 테이블 구조: product_groups WHERE EXISTS(outbound_products) LEFT JOIN sellers LEFT JOIN
 * outbound_sync_outboxes
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("oms")
@Tag("query")
@DisplayName("OMS 상품 조회 API E2E 테스트")
class OmsProductQueryE2ETest extends E2ETestBase {

    private static final String PRODUCTS_URL = "/oms/products";
    private static final String SYNC_HISTORY_SUFFIX = "/sync-history";

    @Autowired private ProductGroupJpaRepository productGroupRepository;
    @Autowired private OutboundProductJpaRepository outboundProductRepository;
    @Autowired private OutboundSyncOutboxJpaRepository syncOutboxRepository;
    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private ShopJpaRepository shopRepository;
    @Autowired private SellerSalesChannelJpaRepository sellerSalesChannelRepository;

    private ShopJpaEntity testShop;

    @BeforeEach
    void setUp() {
        syncOutboxRepository.deleteAll();
        outboundProductRepository.deleteAll();
        productGroupRepository.deleteAll();
        sellerSalesChannelRepository.deleteAll();
        shopRepository.deleteAll();
        sellerRepository.deleteAll();

        // outbound_products INNER JOIN shops 를 위한 Shop 엔티티 생성
        testShop = shopRepository.save(ShopJpaEntityFixtures.newEntity());
    }

    @AfterEach
    void tearDown() {
        syncOutboxRepository.deleteAll();
        outboundProductRepository.deleteAll();
        productGroupRepository.deleteAll();
        sellerSalesChannelRepository.deleteAll();
        shopRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    // ===== 테스트 데이터 헬퍼 메서드 =====

    /**
     * OMS 상품 목록 조회 쿼리의 기본 데이터 셋업.
     *
     * <p>product_group + outbound_product 조합을 생성합니다. OMS 상품은 outbound_products에 연결된 product_group만
     * 조회됩니다.
     *
     * @param sellerId 셀러 ID
     * @param salesChannelId 판매채널 ID
     * @return 저장된 ProductGroupJpaEntity
     */
    private ProductGroupJpaEntity saveOmsProduct(Long sellerId, Long salesChannelId) {
        var pg =
                productGroupRepository.save(
                        ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, sellerId));
        outboundProductRepository.save(
                createOutboundProduct(pg.getId(), salesChannelId, "PENDING_REGISTRATION"));
        return pg;
    }

    /**
     * seller + productGroup + outboundProduct 풀셋을 저장합니다.
     *
     * @return 저장된 ProductGroupJpaEntity
     */
    private ProductGroupJpaEntity saveFullOmsProduct() {
        var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
        var pg =
                productGroupRepository.save(
                        ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, seller.getId()));
        outboundProductRepository.save(
                createOutboundProduct(pg.getId(), 10L, "REGISTERED"));
        return pg;
    }

    private static final AtomicLong EXT_PROD_SEQ = new AtomicLong(1);

    private OutboundProductJpaEntity createOutboundProduct(
            Long productGroupId, Long salesChannelId, String status) {
        Instant now = Instant.now();
        String externalId = "REGISTERED".equals(status)
                ? "EXT-PROD-" + EXT_PROD_SEQ.getAndIncrement() : null;
        return OutboundProductJpaEntity.create(
                null, productGroupId, salesChannelId, testShop.getId(), externalId, status, now,
                now);
    }

    // ========================================================================
    // 1. GET /oms/products - OMS 상품 목록 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /oms/products - OMS 상품 목록 조회")
    class SearchOmsProductsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S01] outbound_product가 연결된 상품만 목록에 조회된다")
        void searchOmsProducts_onlyProductsWithOutboundProduct_areReturned() {
            // given: outbound_product 연결된 상품 2건, 미연결 상품 1건
            var pg1 = saveFullOmsProduct();
            var pg2 = saveFullOmsProduct();
            // outbound_product 미연결 상품 (조회 제외 대상)
            productGroupRepository.save(ProductGroupJpaEntityFixtures.activeEntity());

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2))
                    .body("data.content", hasSize(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S02] 데이터 없을 때 빈 목록 반환")
        void searchOmsProducts_noData_returnsEmptyList() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S03] 페이징 파라미터 적용 시 지정한 크기만큼 반환")
        void searchOmsProducts_withPaging_returnsCorrectSize() {
            // given: 5건 저장
            for (int i = 0; i < 5; i++) {
                saveFullOmsProduct();
            }

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(2))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S04] DELETED 상태 상품은 목록에 조회되지 않는다")
        void searchOmsProducts_deletedProduct_notReturned() {
            // given: ACTIVE 상품 1건, DELETED 상품 1건
            var activePg = saveFullOmsProduct();
            var deletedPg =
                    productGroupRepository.save(ProductGroupJpaEntityFixtures.deletedEntity());
            outboundProductRepository.save(
                    createOutboundProduct(deletedPg.getId(), 10L, "PENDING_REGISTRATION"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S05] partnerIds 필터로 특정 셀러 상품만 조회")
        void searchOmsProducts_byPartnerId_returnsFilteredResults() {
            // given: 셀러 A, B 각각 상품 저장
            var sellerA = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            var sellerB = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            saveOmsProduct(sellerA.getId(), 10L);
            saveOmsProduct(sellerA.getId(), 10L);
            saveOmsProduct(sellerB.getId(), 10L);

            // when & then: 셀러 A 상품만 조회
            given().spec(givenAuthenticatedUser())
                    .queryParam("partnerIds", sellerA.getId())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S06] searchField=PRODUCT_NAME, searchWord로 상품명 검색")
        void searchOmsProducts_byProductName_returnsFilteredResults() {
            // given: 특정 이름의 상품 저장 후 OMS 연결
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            var targetPg =
                    productGroupRepository.save(
                            ProductGroupJpaEntityFixtures.activeEntityWithSeller(
                                    null, seller.getId()));
            outboundProductRepository.save(
                    createOutboundProduct(targetPg.getId(), 10L, "REGISTERED"));
            // 다른 상품
            saveFullOmsProduct();

            // when & then: productGroupName에 포함된 기본 키워드로 검색
            given().spec(givenAuthenticatedUser())
                    .queryParam("searchField", "PRODUCT_NAME")
                    .queryParam("searchWord", targetPg.getProductGroupName())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-A01] 비인증 요청 시 401 반환")
        void searchOmsProducts_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S07] 응답에 id, productName, status 포함")
        void searchOmsProducts_responseContainsRequiredFields() {
            // given
            saveFullOmsProduct();

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].id", notNullValue())
                    .body("data.content[0].productName", notNullValue())
                    .body("data.content[0].status", notNullValue());
        }
    }

    // ========================================================================
    // 2. GET /oms/products/{productGroupId} - OMS 상품 상세 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /oms/products/{productGroupId} - OMS 상품 상세 조회")
    class GetOmsProductDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2-S01] 존재하는 상품그룹 ID로 상세 조회 성공")
        void getOmsProductDetail_existingId_returns200() {
            // given
            var pg = saveFullOmsProduct();

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}", pg.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-F01] 존재하지 않는 상품그룹 ID 조회 시 404 반환")
        void getOmsProductDetail_nonExistingId_returns404() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}", 99999L)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-A01] 비인증 요청 시 401 반환")
        void getOmsProductDetail_unauthenticated_returns401() {
            // given
            var pg = saveFullOmsProduct();

            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}", pg.getId())
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // 3. GET /oms/products/{productGroupId}/sync-history - 연동 이력 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /oms/products/{productGroupId}/sync-history - 연동 이력 조회")
    class SearchSyncHistoryTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q3-S01] 연동 이력이 있는 상품그룹 조회 시 이력 반환")
        void searchSyncHistory_withHistory_returnsResults() {
            // given: productGroup + outboundProduct + syncOutbox 저장
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            var pg =
                    productGroupRepository.save(
                            ProductGroupJpaEntityFixtures.activeEntityWithSeller(
                                    null, seller.getId()));
            outboundProductRepository.save(
                    createOutboundProduct(pg.getId(), 10L, "REGISTERED"));
            syncOutboxRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    pg.getId(), 10L),
                            OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity()));

            // when & then: productGroupId에 해당하는 이력만 조회
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}" + SYNC_HISTORY_SUFFIX, pg.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q3-S02] 연동 이력이 없는 상품그룹 조회 시 빈 목록 반환")
        void searchSyncHistory_noHistory_returnsEmptyList() {
            // given: syncOutbox 없이 productGroup만 저장
            var pg = saveFullOmsProduct();

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}" + SYNC_HISTORY_SUFFIX, pg.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q3-S03] status 필터로 특정 상태의 이력만 조회")
        void searchSyncHistory_withStatusFilter_returnsFilteredResults() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            var pg =
                    productGroupRepository.save(
                            ProductGroupJpaEntityFixtures.activeEntityWithSeller(
                                    null, seller.getId()));
            outboundProductRepository.save(
                    createOutboundProduct(pg.getId(), 10L, "REGISTERED"));
            // PENDING 2건, COMPLETED 1건
            syncOutboxRepository.saveAll(
                    List.of(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    pg.getId(), 10L),
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                    pg.getId(), 20L)));

            // when & then: PENDING 상태만 필터
            given().spec(givenAuthenticatedUser())
                    .queryParam("status", "PENDING")
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}" + SYNC_HISTORY_SUFFIX, pg.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q3-S04] 페이징 파라미터 적용 시 지정한 크기만큼 반환")
        void searchSyncHistory_withPaging_returnsCorrectSize() {
            // given: sync 이력 5건
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            var pg =
                    productGroupRepository.save(
                            ProductGroupJpaEntityFixtures.activeEntityWithSeller(
                                    null, seller.getId()));
            outboundProductRepository.save(
                    createOutboundProduct(pg.getId(), 10L, "REGISTERED"));
            for (int i = 0; i < 5; i++) {
                syncOutboxRepository.save(
                        OutboundSyncOutboxJpaEntityFixtures.newPendingEntityWith(
                                pg.getId(), (long) (10 + i)));
            }

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}" + SYNC_HISTORY_SUFFIX, pg.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q3-A01] 비인증 요청 시 401 반환")
        void searchSyncHistory_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}" + SYNC_HISTORY_SUFFIX, 1L)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // 4. 전체 플로우 시나리오
    // ========================================================================

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 목록 조회 → 상세 조회 → 연동 이력 조회 플로우")
        void fullFlow_listToDetailToSyncHistory() {
            // Step 1: 테스트 데이터 준비
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            var pg =
                    productGroupRepository.save(
                            ProductGroupJpaEntityFixtures.activeEntityWithSeller(
                                    null, seller.getId()));
            outboundProductRepository.save(
                    createOutboundProduct(pg.getId(), 10L, "REGISTERED"));
            syncOutboxRepository.save(OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity());

            // Step 2: 목록 조회
            var listResponse =
                    given().spec(givenAuthenticatedUser())
                            .when()
                            .get(PRODUCTS_URL)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .body("data.totalElements", equalTo(1))
                            .extract()
                            .response();

            Long extractedId = listResponse.jsonPath().getLong("data.content[0].id");
            assertThat(extractedId).isEqualTo(pg.getId());

            // Step 3: 상세 조회
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}", extractedId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());

            // Step 4: 연동 이력 조회 (productGroup에 해당하는 이력이 없어도 200 OK)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL + "/{productGroupId}" + SYNC_HISTORY_SUFFIX, extractedId)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[F2] 여러 셀러 상품 등록 후 파트너별 필터링 조회")
        void fullFlow_multipleSellerProducts_filterByPartner() {
            // Step 1: 셀러 A, B 각각 상품 2건씩 저장
            var sellerA = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            var sellerB = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());

            saveOmsProduct(sellerA.getId(), 10L);
            saveOmsProduct(sellerA.getId(), 10L);
            saveOmsProduct(sellerB.getId(), 10L);

            // Step 2: 전체 목록 조회 (3건)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));

            // Step 3: 셀러 A만 필터 조회 (2건)
            given().spec(givenAuthenticatedUser())
                    .queryParam("partnerIds", sellerA.getId())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));

            // Step 4: 셀러 B만 필터 조회 (1건)
            given().spec(givenAuthenticatedUser())
                    .queryParam("partnerIds", sellerB.getId())
                    .when()
                    .get(PRODUCTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));
        }
    }
}
