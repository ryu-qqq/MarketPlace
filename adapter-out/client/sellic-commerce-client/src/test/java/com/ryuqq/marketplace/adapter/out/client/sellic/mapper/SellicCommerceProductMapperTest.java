package com.ryuqq.marketplace.adapter.out.client.sellic.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductUpdateRequest;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellicCommerceProductMapper 단위 테스트")
class SellicCommerceProductMapperTest {

    private final SellicCommerceProductMapper sut = new SellicCommerceProductMapper();

    private static final String CUSTOMER_ID = "test-customer";
    private static final String API_KEY = "test-api-key";

    // ── 헬퍼 메서드 ──

    private ProductGroupSyncData createSyncData() {
        return createSyncData(
                ProductGroupFixtures.activeProductGroup(),
                List.of(ProductFixtures.activeProduct()));
    }

    private ProductGroupSyncData createSyncData(ProductGroup group, List<Product> products) {
        var queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        1L,
                        1L,
                        "테스트셀러",
                        100L,
                        "테스트브랜드",
                        200L,
                        "테스트카테고리",
                        "상의 > 긴팔",
                        "1/200",
                        "테스트 상품 그룹",
                        "NONE",
                        "ACTIVE",
                        Instant.now(),
                        Instant.now(),
                        null,
                        null);
        var bundle =
                new ProductGroupDetailBundle(
                        queryResult,
                        group,
                        products,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Map.of());
        return ProductGroupSyncData.from(bundle);
    }

    @Nested
    @DisplayName("toRegistrationRequest()")
    class ToRegistrationRequestTest {

        @Test
        @DisplayName("기본 등록 요청을 생성한다")
        void createsBasicRegistrationRequest() {
            var syncData = createSyncData();

            SellicProductRegistrationRequest result =
                    sut.toRegistrationRequest(syncData, CUSTOMER_ID, API_KEY);

            assertThat(result.customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.apiKey()).isEqualTo(API_KEY);
            assertThat(result.productName()).isEqualTo("테스트 상품 그룹");
            assertThat(result.brand()).isEqualTo("테스트브랜드");
            assertThat(result.saleStatus()).isEqualTo(2000);
            assertThat(result.deliveryChargeType()).isEqualTo(1296);
            assertThat(result.tax()).isEqualTo(1286);
            assertThat(result.salePrice()).isGreaterThan(0);
            assertThat(result.marketPrice()).isGreaterThan(0);
        }

        @Test
        @DisplayName("옵션이 없으면 기본 옵션명이 '단품'이다")
        void defaultOptionNameIsDanpum() {
            var syncData = createSyncData();

            SellicProductRegistrationRequest result =
                    sut.toRegistrationRequest(syncData, CUSTOMER_ID, API_KEY);

            assertThat(result.optionName1()).isEqualTo("단품");
        }

        @Test
        @DisplayName("재고 목록이 상품 수만큼 생성된다")
        void stockCountMatchesProducts() {
            var syncData = createSyncData();

            SellicProductRegistrationRequest result =
                    sut.toRegistrationRequest(syncData, CUSTOMER_ID, API_KEY);

            assertThat(result.productStocks()).hasSize(1);
            assertThat(result.productStocks().get(0).presentStock())
                    .isEqualTo(ProductFixtures.DEFAULT_STOCK_QUANTITY);
        }
    }

    @Nested
    @DisplayName("toUpdateRequest()")
    class ToUpdateRequestTest {

        @Test
        @DisplayName("수정 요청을 생성한다")
        void createsUpdateRequest() {
            var syncData = createSyncData();

            SellicProductUpdateRequest result =
                    sut.toUpdateRequest(syncData, "EXT001", CUSTOMER_ID, API_KEY);

            assertThat(result.customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.apiKey()).isEqualTo(API_KEY);
            assertThat(result.productId()).isEqualTo("EXT001");
            assertThat(result.productName()).isEqualTo("테스트 상품 그룹");
            assertThat(result.saleStatus()).isEqualTo(2000);
        }
    }

    @Nested
    @DisplayName("toDeleteRequest()")
    class ToDeleteRequestTest {

        @Test
        @DisplayName("삭제 요청은 saleStatus=2004(판매종료)로 설정된다")
        void deleteSetsTerminatedStatus() {
            SellicProductUpdateRequest result = sut.toDeleteRequest("EXT001", CUSTOMER_ID, API_KEY);

            assertThat(result.customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.apiKey()).isEqualTo(API_KEY);
            assertThat(result.productId()).isEqualTo("EXT001");
            assertThat(result.saleStatus()).isEqualTo(2004);
            assertThat(result.productName()).isNull();
        }
    }

    @Nested
    @DisplayName("toStockUpdateRequest()")
    class ToStockUpdateRequestTest {

        @Test
        @DisplayName("재고 수정 요청을 생성한다")
        void createsStockUpdateRequest() {
            var syncData = createSyncData();

            SellicProductStockUpdateRequest result =
                    sut.toStockUpdateRequest(syncData, "EXT001", CUSTOMER_ID, API_KEY);

            assertThat(result.customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.apiKey()).isEqualTo(API_KEY);
            assertThat(result.productId()).isEqualTo("EXT001");
            assertThat(result.optionName1()).isEqualTo("단품");
            assertThat(result.productStocks()).hasSize(1);
        }
    }
}
