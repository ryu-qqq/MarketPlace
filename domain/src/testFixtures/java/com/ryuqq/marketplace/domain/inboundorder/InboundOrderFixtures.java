package com.ryuqq.marketplace.domain.inboundorder;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.inboundorder.id.InboundOrderId;
import com.ryuqq.marketplace.domain.inboundorder.id.InboundOrderItemId;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.List;

/** InboundOrder 도메인 테스트 Fixtures. */
public final class InboundOrderFixtures {

    private InboundOrderFixtures() {}

    public static final long DEFAULT_SALES_CHANNEL_ID = 100L;
    public static final long DEFAULT_SHOP_ID = 0L;
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_EXTERNAL_ORDER_NO = "NAVER-ORD-001";
    public static final String DEFAULT_BUYER_NAME = "홍길동";
    public static final String DEFAULT_BUYER_EMAIL = "buyer@example.com";
    public static final String DEFAULT_BUYER_PHONE = "010-1234-5678";
    public static final String DEFAULT_PAYMENT_METHOD = "CARD";
    public static final int DEFAULT_TOTAL_PAYMENT_AMOUNT = 50000;
    public static final String DEFAULT_INTERNAL_ORDER_ID = "order-uuid-001";

    public static final String DEFAULT_EXTERNAL_PRODUCT_ORDER_ID = "EXT-PO-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_OPTION_ID = "EXT-OPT-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_NAME = "테스트 상품";
    public static final String DEFAULT_EXTERNAL_OPTION_NAME = "블랙/M";
    public static final String DEFAULT_EXTERNAL_IMAGE_URL = "https://example.com/image.jpg";
    public static final int DEFAULT_UNIT_PRICE = 25000;
    public static final int DEFAULT_QUANTITY = 2;
    public static final int DEFAULT_TOTAL_AMOUNT = 50000;
    public static final int DEFAULT_DISCOUNT_AMOUNT = 0;
    public static final int DEFAULT_PAYMENT_AMOUNT = 50000;
    public static final String DEFAULT_RECEIVER_NAME = "김수령";
    public static final String DEFAULT_RECEIVER_PHONE = "010-9876-5432";
    public static final String DEFAULT_RECEIVER_ZIPCODE = "06234";
    public static final String DEFAULT_RECEIVER_ADDRESS = "서울시 강남구 테헤란로 123";
    public static final String DEFAULT_RECEIVER_ADDRESS_DETAIL = "456호";
    public static final String DEFAULT_DELIVERY_REQUEST = "부재 시 문 앞에 놓아주세요";

    // ===== Item Fixtures =====

    /** 신규 아이템 (매핑 전). */
    public static InboundOrderItem newItem() {
        return InboundOrderItem.forNew(
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                0,
                DEFAULT_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST);
    }

    /** 외부 상품 ID를 지정한 신규 아이템. */
    public static InboundOrderItem newItem(String externalProductId) {
        return InboundOrderItem.forNew(
                "EXT-PO-" + externalProductId,
                externalProductId,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                0,
                DEFAULT_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST);
    }

    /** 매핑 완료된 아이템 (reconstitute). */
    public static InboundOrderItem mappedItem(Long id) {
        return InboundOrderItem.reconstitute(
                InboundOrderItemId.of(id),
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                0,
                DEFAULT_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                300L,
                400L,
                DEFAULT_SELLER_ID,
                500L,
                "SKU-001",
                "테스트 상품그룹",
                true);
    }

    // ===== Order Fixtures =====

    /** 신규 수신 InboundOrder (RECEIVED 상태). */
    public static InboundOrder newReceivedOrder() {
        Instant now = CommonVoFixtures.now();
        return InboundOrder.forNew(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                now,
                List.of(newItem()),
                now);
    }

    /** RECEIVED 상태로 복원된 InboundOrder. */
    public static InboundOrder receivedOrder() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return InboundOrder.reconstitute(
                InboundOrderId.of(1L),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                yesterday,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                yesterday,
                InboundOrderStatus.RECEIVED,
                null,
                null,
                List.of(newItem()),
                yesterday,
                yesterday);
    }

    /** ID를 지정한 RECEIVED 상태 InboundOrder. */
    public static InboundOrder receivedOrder(Long id) {
        Instant yesterday = CommonVoFixtures.yesterday();
        return InboundOrder.reconstitute(
                InboundOrderId.of(id),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                yesterday,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                yesterday,
                InboundOrderStatus.RECEIVED,
                null,
                null,
                List.of(newItem()),
                yesterday,
                yesterday);
    }

    /** PENDING_MAPPING 상태 InboundOrder. */
    public static InboundOrder pendingMappingOrder() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return InboundOrder.reconstitute(
                InboundOrderId.of(2L),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                "NAVER-ORD-PENDING",
                yesterday,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                yesterday,
                InboundOrderStatus.PENDING_MAPPING,
                null,
                null,
                List.of(newItem()),
                yesterday,
                yesterday);
    }

    /** MAPPED 상태 InboundOrder. */
    public static InboundOrder mappedOrder() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return InboundOrder.reconstitute(
                InboundOrderId.of(3L),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                yesterday,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                yesterday,
                InboundOrderStatus.MAPPED,
                null,
                null,
                List.of(mappedItem(10L)),
                yesterday,
                yesterday);
    }

    /** CONVERTED 상태 InboundOrder. */
    public static InboundOrder convertedOrder() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return InboundOrder.reconstitute(
                InboundOrderId.of(4L),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                yesterday,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                yesterday,
                InboundOrderStatus.CONVERTED,
                DEFAULT_INTERNAL_ORDER_ID,
                null,
                List.of(mappedItem(10L)),
                yesterday,
                yesterday);
    }
}
