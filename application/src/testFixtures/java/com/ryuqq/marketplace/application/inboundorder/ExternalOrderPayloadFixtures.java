package com.ryuqq.marketplace.application.inboundorder;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** ExternalOrderPayload 테스트 Fixtures. */
public final class ExternalOrderPayloadFixtures {

    private ExternalOrderPayloadFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_OPTION_ID = "EXT-OPT-001";
    public static final String DEFAULT_BUYER_NAME = "홍길동";
    public static final String DEFAULT_PAYMENT_METHOD = "CARD";

    /** 기본 ExternalOrderItemPayload 생성. */
    public static ExternalOrderItemPayload defaultItemPayload() {
        return new ExternalOrderItemPayload(
                "EXT-PO-001",
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                "테스트 상품",
                "블랙/M",
                "https://example.com/image.jpg",
                25000,
                2,
                50000,
                0,
                0,
                50000,
                "김수령",
                "010-9876-5432",
                "06234",
                "서울시 강남구 테헤란로 123",
                "456호",
                "부재 시 문 앞에 놓아주세요",
                "PAYED");
    }

    /** 외부 상품 ID를 지정한 아이템 페이로드. */
    public static ExternalOrderItemPayload itemPayload(String externalProductId) {
        return new ExternalOrderItemPayload(
                "EXT-PO-" + externalProductId,
                externalProductId,
                DEFAULT_EXTERNAL_OPTION_ID,
                "테스트 상품",
                "블랙/M",
                "https://example.com/image.jpg",
                25000,
                1,
                25000,
                0,
                0,
                25000,
                "김수령",
                "010-9876-5432",
                "06234",
                "서울시 강남구 테헤란로 123",
                "456호",
                null,
                null);
    }

    /** 기본 ExternalOrderPayload 생성. */
    public static ExternalOrderPayload defaultPayload() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return new ExternalOrderPayload(
                "NAVER-ORD-" + seq,
                now,
                DEFAULT_BUYER_NAME,
                "buyer@example.com",
                "010-1234-5678",
                DEFAULT_PAYMENT_METHOD,
                50000,
                now,
                List.of(defaultItemPayload()));
    }

    /** 외부 주문번호를 지정한 페이로드. */
    public static ExternalOrderPayload payload(String externalOrderNo) {
        Instant now = Instant.now();
        return new ExternalOrderPayload(
                externalOrderNo,
                now,
                DEFAULT_BUYER_NAME,
                "buyer@example.com",
                "010-1234-5678",
                DEFAULT_PAYMENT_METHOD,
                50000,
                now,
                List.of(defaultItemPayload()));
    }

    /** 여러 아이템이 포함된 페이로드. */
    public static ExternalOrderPayload payloadWithItems(List<ExternalOrderItemPayload> items) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return new ExternalOrderPayload(
                "NAVER-ORD-" + seq,
                now,
                DEFAULT_BUYER_NAME,
                "buyer@example.com",
                "010-1234-5678",
                DEFAULT_PAYMENT_METHOD,
                items.stream().mapToInt(ExternalOrderItemPayload::paymentAmount).sum(),
                now,
                items);
    }
}
