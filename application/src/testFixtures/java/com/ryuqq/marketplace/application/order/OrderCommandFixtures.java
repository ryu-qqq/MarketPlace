package com.ryuqq.marketplace.application.order;

import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderItemCommand;
import java.time.Instant;
import java.util.List;

/**
 * Order Application Command 테스트 Fixtures.
 *
 * <p>Order 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OrderCommandFixtures {

    private OrderCommandFixtures() {}

    // ===== CreateOrderCommand Fixtures =====

    public static CreateOrderCommand createOrderCommand() {
        return new CreateOrderCommand(
                1L,
                10L,
                "NAVER",
                "네이버 스마트스토어",
                "EXT-ORDER-20260218-001",
                Instant.parse("2026-02-18T10:00:00Z"),
                "홍길동",
                "buyer@example.com",
                "010-1234-5678",
                "CARD",
                20000,
                Instant.parse("2026-02-18T10:05:00Z"),
                List.of(createOrderItemCommand()),
                "system");
    }

    public static CreateOrderCommand createOrderCommandWithoutOptionals() {
        return new CreateOrderCommand(
                1L,
                10L,
                null,
                null,
                "EXT-ORDER-20260218-002",
                Instant.parse("2026-02-18T10:00:00Z"),
                "홍길동",
                "buyer@example.com",
                "010-1234-5678",
                null,
                20000,
                null,
                List.of(createOrderItemCommand()),
                "system");
    }

    public static CreateOrderCommand createOrderCommandWithItems(
            List<CreateOrderItemCommand> items) {
        return new CreateOrderCommand(
                1L,
                10L,
                "NAVER",
                "네이버 스마트스토어",
                "EXT-ORDER-20260218-003",
                Instant.parse("2026-02-18T10:00:00Z"),
                "홍길동",
                "buyer@example.com",
                "010-1234-5678",
                "CARD",
                items.stream().mapToInt(CreateOrderItemCommand::paymentAmount).sum(),
                Instant.parse("2026-02-18T10:05:00Z"),
                items,
                "system");
    }

    // ===== CreateOrderItemCommand Fixtures =====

    public static CreateOrderItemCommand createOrderItemCommand() {
        return new CreateOrderItemCommand(
                100L,
                200L,
                1L,
                5L,
                "SKU-TEST-0001",
                "테스트 상품그룹",
                "테스트 브랜드",
                "테스트 셀러",
                "https://example.com/images/main.jpg",
                "EXT-PROD-001",
                "EXT-OPT-001",
                "테스트 상품명",
                "블랙 / L",
                "https://example.com/images/product.jpg",
                10000,
                2,
                20000,
                0,
                20000,
                "김수령",
                "010-9876-5432",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호",
                "부재시 문앞에 놓아주세요");
    }

    public static CreateOrderItemCommand createOrderItemCommandWithoutMappings() {
        return new CreateOrderItemCommand(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "EXT-PROD-002",
                "EXT-OPT-002",
                "미매핑 상품명",
                "레드 / M",
                "https://example.com/images/product2.jpg",
                15000,
                1,
                15000,
                0,
                15000,
                "이수령",
                "010-1111-2222",
                "54321",
                "서울시 마포구 홍대로 10",
                "202호",
                null);
    }
}
