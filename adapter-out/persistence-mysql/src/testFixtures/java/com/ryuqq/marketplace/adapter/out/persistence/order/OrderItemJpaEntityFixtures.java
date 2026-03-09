package com.ryuqq.marketplace.adapter.out.persistence.order;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/** OrderItemJpaEntity 테스트 Fixtures. */
public final class OrderItemJpaEntityFixtures {

    private OrderItemJpaEntityFixtures() {}

    public static final long DEFAULT_PRODUCT_GROUP_ID = 1000L;
    public static final long DEFAULT_PRODUCT_ID = 2000L;
    public static final long DEFAULT_SELLER_ID = 10L;
    public static final long DEFAULT_BRAND_ID = 50L;
    public static final String DEFAULT_SKU_CODE = "SKU-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_OPTION_ID = "EXT-OPT-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_NAME = "테스트 상품";
    public static final String DEFAULT_EXTERNAL_OPTION_NAME = "블랙/M";
    public static final String DEFAULT_EXTERNAL_IMAGE_URL = "https://example.com/img.jpg";
    public static final int DEFAULT_UNIT_PRICE = 29900;
    public static final int DEFAULT_QUANTITY = 1;
    public static final int DEFAULT_TOTAL_AMOUNT = 29900;
    public static final int DEFAULT_DISCOUNT_AMOUNT = 0;
    public static final int DEFAULT_PAYMENT_AMOUNT = 29900;
    public static final String DEFAULT_RECEIVER_NAME = "김수령";
    public static final String DEFAULT_RECEIVER_PHONE = "010-9876-5432";
    public static final String DEFAULT_RECEIVER_ZIPCODE = "06234";
    public static final String DEFAULT_RECEIVER_ADDRESS = "서울시 강남구 테헤란로 123";
    public static final String DEFAULT_RECEIVER_ADDRESS_DETAIL = "4층 401호";
    public static final String DEFAULT_DELIVERY_REQUEST = "부재시 경비실에 맡겨주세요";

    /** 기본 주문 상품 Entity 생성. */
    public static OrderItemJpaEntity defaultItem(String orderId) {
        Instant now = Instant.now();
        return OrderItemJpaEntity.create(
                null,
                orderId,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_PRODUCT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SKU_CODE,
                null,
                null,
                null,
                null,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                now,
                now);
    }

    /** 여러 개의 주문 상품 Entity 생성. */
    public static List<OrderItemJpaEntity> defaultItems(String orderId, int count) {
        return IntStream.range(0, count).mapToObj(i -> defaultItem(orderId)).toList();
    }

    /** 가격을 지정한 주문 상품 Entity 생성. */
    public static OrderItemJpaEntity itemWithPrice(String orderId, int unitPrice, int quantity) {
        Instant now = Instant.now();
        int totalAmount = unitPrice * quantity;
        return OrderItemJpaEntity.create(
                null,
                orderId,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_PRODUCT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SKU_CODE,
                null,
                null,
                null,
                null,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                unitPrice,
                quantity,
                totalAmount,
                DEFAULT_DISCOUNT_AMOUNT,
                totalAmount,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                now,
                now);
    }
}
