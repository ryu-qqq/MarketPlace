package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/**
 * Order Composite Projection DTO 테스트 Fixtures.
 *
 * <p>PaymentProjectionDto, ProductOrderListProjectionDto, ProductOrderDetailProjectionDto 생성용.
 * paymentId(String), paymentNumber 필드 변경 이후 기준.
 */
public final class OrderCompositeProjectionDtoFixtures {

    private OrderCompositeProjectionDtoFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ORDER_ID = "01944b2a-1234-7fff-8888-abcdef012345";
    public static final String DEFAULT_ORDER_NUMBER = "ORD-20260218-0001";
    public static final String DEFAULT_STATUS = "ORDERED";
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final long DEFAULT_SHOP_ID = 100L;
    public static final String DEFAULT_SHOP_CODE = "MUSINSA";
    public static final String DEFAULT_SHOP_NAME = "무신사";
    public static final String DEFAULT_EXTERNAL_ORDER_NO = "EXT-ORD-001";
    public static final String DEFAULT_BUYER_NAME = "홍길동";
    public static final String DEFAULT_BUYER_EMAIL = "buyer@example.com";
    public static final String DEFAULT_BUYER_PHONE = "010-1234-5678";

    public static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final long DEFAULT_PRODUCT_ID = 200L;
    public static final long DEFAULT_SELLER_ID = 10L;
    public static final long DEFAULT_BRAND_ID = 50L;
    public static final String DEFAULT_SKU_CODE = "SKU-001";
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "테스트 상품";
    public static final String DEFAULT_BRAND_NAME = "테스트 브랜드";
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";
    public static final String DEFAULT_MAIN_IMAGE_URL = "https://example.com/main.jpg";
    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_OPTION_ID = "EXT-OPT-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_NAME = "외부 상품명";
    public static final String DEFAULT_EXTERNAL_OPTION_NAME = "블랙/M";
    public static final String DEFAULT_EXTERNAL_IMAGE_URL = "https://example.com/ext.jpg";
    public static final int DEFAULT_UNIT_PRICE = 10000;
    public static final int DEFAULT_QUANTITY = 2;
    public static final int DEFAULT_TOTAL_AMOUNT = 20000;
    public static final int DEFAULT_DISCOUNT_AMOUNT = 0;
    public static final int DEFAULT_ITEM_PAYMENT_AMOUNT = 20000;
    public static final String DEFAULT_RECEIVER_NAME = "김수령";
    public static final String DEFAULT_RECEIVER_PHONE = "010-9876-5432";
    public static final String DEFAULT_RECEIVER_ZIPCODE = "06234";
    public static final String DEFAULT_RECEIVER_ADDRESS = "서울시 강남구 테헤란로 123";
    public static final String DEFAULT_RECEIVER_ADDRESS_DETAIL = "4층 401호";
    public static final String DEFAULT_DELIVERY_REQUEST = "부재시 경비실에 맡겨주세요";
    public static final String DEFAULT_DELIVERY_STATUS = "READY";

    // payment 필드 (String ID, paymentNumber 포함)
    public static final String DEFAULT_PAYMENT_ID = "01944b2a-aaaa-7fff-8888-000000000001";
    public static final String DEFAULT_PAYMENT_NUMBER = "PAY-20260218-0001";
    public static final String DEFAULT_PAYMENT_STATUS = "COMPLETED";
    public static final String DEFAULT_PAYMENT_METHOD = "CARD";
    public static final String DEFAULT_PAYMENT_AGENCY_ID = "PG-TXN-00001";
    public static final int DEFAULT_PAYMENT_AMOUNT = 20000;

    // ========================================================================
    // PaymentProjectionDto Fixtures
    // ========================================================================

    /** 기본 COMPLETED 상태의 PaymentProjectionDto. */
    public static PaymentProjectionDto completedPaymentProjection() {
        Instant now = Instant.now();
        return new PaymentProjectionDto(
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_ORDER_ID,
                DEFAULT_PAYMENT_STATUS,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(60),
                null);
    }

    /** paymentId와 paymentNumber를 지정한 PaymentProjectionDto. */
    public static PaymentProjectionDto paymentProjection(
            String paymentId, String paymentNumber, String orderId) {
        Instant now = Instant.now();
        return new PaymentProjectionDto(
                paymentId,
                paymentNumber,
                orderId,
                DEFAULT_PAYMENT_STATUS,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(60),
                null);
    }

    /** 취소된 PaymentProjectionDto. */
    public static PaymentProjectionDto canceledPaymentProjection(String paymentId) {
        Instant now = Instant.now();
        return new PaymentProjectionDto(
                paymentId,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_ORDER_ID,
                "CANCELED",
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(3600),
                now.minusSeconds(60));
    }

    // ========================================================================
    // ProductOrderListProjectionDto Fixtures
    // ========================================================================

    /** 결제 정보를 포함한 기본 ProductOrderListProjectionDto. */
    public static ProductOrderListProjectionDto defaultListProjection() {
        Instant now = Instant.now();
        return new ProductOrderListProjectionDto(
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SHOP_CODE,
                DEFAULT_SHOP_NAME,
                DEFAULT_EXTERNAL_ORDER_NO,
                now.minusSeconds(3600),
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                now.minusSeconds(3600),
                now,
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20260218-0001-001",
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_PRODUCT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SKU_CODE,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_BRAND_NAME,
                DEFAULT_SELLER_NAME,
                DEFAULT_MAIN_IMAGE_URL,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_ITEM_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                DEFAULT_DELIVERY_STATUS,
                null,
                null,
                null,
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_PAYMENT_STATUS,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(60),
                null);
    }

    /** 결제 정보가 없는 ProductOrderListProjectionDto (LEFT JOIN null). */
    public static ProductOrderListProjectionDto listProjectionWithoutPayment() {
        Instant now = Instant.now();
        return new ProductOrderListProjectionDto(
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SHOP_CODE,
                DEFAULT_SHOP_NAME,
                DEFAULT_EXTERNAL_ORDER_NO,
                now.minusSeconds(3600),
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                now.minusSeconds(3600),
                now,
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20260218-0001-001",
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_PRODUCT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SKU_CODE,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_BRAND_NAME,
                DEFAULT_SELLER_NAME,
                DEFAULT_MAIN_IMAGE_URL,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_ITEM_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                DEFAULT_DELIVERY_STATUS,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null);
    }

    // ========================================================================
    // ProductOrderDetailProjectionDto Fixtures
    // ========================================================================

    /** 결제 정보를 포함한 기본 ProductOrderDetailProjectionDto. */
    public static ProductOrderDetailProjectionDto defaultDetailProjection() {
        Instant now = Instant.now();
        return new ProductOrderDetailProjectionDto(
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SHOP_CODE,
                DEFAULT_SHOP_NAME,
                DEFAULT_EXTERNAL_ORDER_NO,
                now.minusSeconds(3600),
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                now.minusSeconds(3600),
                now,
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20260218-0001-001",
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_PRODUCT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SKU_CODE,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_BRAND_NAME,
                DEFAULT_SELLER_NAME,
                DEFAULT_MAIN_IMAGE_URL,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_ITEM_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                DEFAULT_DELIVERY_STATUS,
                null,
                null,
                null,
                // 정산 필드
                0,
                0,
                0,
                0,
                0,
                null,
                null,
                // payment 필드
                DEFAULT_PAYMENT_ID,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_PAYMENT_STATUS,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(60),
                null);
    }

    /** 결제 정보가 없는 ProductOrderDetailProjectionDto. */
    public static ProductOrderDetailProjectionDto detailProjectionWithoutPayment() {
        Instant now = Instant.now();
        return new ProductOrderDetailProjectionDto(
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SHOP_CODE,
                DEFAULT_SHOP_NAME,
                DEFAULT_EXTERNAL_ORDER_NO,
                now.minusSeconds(3600),
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                now.minusSeconds(3600),
                now,
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20260218-0001-001",
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_PRODUCT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SKU_CODE,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_BRAND_NAME,
                DEFAULT_SELLER_NAME,
                DEFAULT_MAIN_IMAGE_URL,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_ITEM_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                DEFAULT_DELIVERY_STATUS,
                null,
                null,
                null,
                // 정산 필드
                0,
                0,
                0,
                0,
                0,
                null,
                null,
                // payment 필드 (모두 null)
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null);
    }
}
