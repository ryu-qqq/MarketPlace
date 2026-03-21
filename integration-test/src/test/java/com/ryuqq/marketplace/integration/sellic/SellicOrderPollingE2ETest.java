package com.ryuqq.marketplace.integration.sellic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.repository.ExternalOrderItemMappingJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.PollExternalOrdersUseCase;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * 셀릭 주문 폴링 → InboundOrder 저장 → Order 변환 전체 파이프라인 E2E 테스트.
 *
 * <p>셀릭 API에서 실제로 조회한 주문 데이터를 MockBean으로 제공하여, 폴링 파이프라인 전체를 검증합니다.
 */
@Tag("e2e")
@Tag("sellic")
@DisplayName("셀릭 주문 폴링 → InboundOrder → Order 변환 E2E 테스트")
class SellicOrderPollingE2ETest extends E2ETestBase {

    private static final long SALES_CHANNEL_ID = 1L;
    private static final int BATCH_SIZE = 100;

    @MockitoBean private SalesChannelOrderClient orderClient;

    @Autowired private PollExternalOrdersUseCase pollUseCase;
    @Autowired private SalesChannelJpaRepository salesChannelRepository;
    @Autowired private ShopJpaRepository shopRepository;
    @Autowired private InboundOrderJpaRepository inboundOrderRepository;
    @Autowired private InboundOrderItemJpaRepository inboundOrderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private ExternalOrderItemMappingJpaRepository mappingRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
        seedSalesChannelAndShop();

        given(orderClient.supports(any())).willReturn(true);
        given(orderClient.fetchNewOrders(anyLong(), anyLong(), any(), any(), any()))
                .willReturn(buildSellicMockOrders());
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        mappingRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        inboundOrderItemRepository.deleteAll();
        inboundOrderRepository.deleteAll();
        shopRepository.deleteAll();
        salesChannelRepository.deleteAll();
    }

    private void seedSalesChannelAndShop() {
        Instant now = Instant.now();

        salesChannelRepository.save(
                SalesChannelJpaEntity.create(null, "SELLIC", "ACTIVE", now, now));

        Long savedChannelId = salesChannelRepository.findAll().get(0).getId();

        shopRepository.save(
                ShopJpaEntity.create(
                        null, savedChannelId, "셀릭", "sellic-default", "ACTIVE",
                        "SELLIC", "test-api-key", null, null, "1012",
                        now, now, null));
    }

    @Test
    @DisplayName("셀릭 주문 2건 폴링 → InboundOrder 저장 검증")
    void pollAndSaveInboundOrders() {
        // when
        var result = pollUseCase.execute(SALES_CHANNEL_ID, BATCH_SIZE);

        // then
        System.out.println("=== 폴링 결과 ===");
        System.out.println("총 수신: " + result.total());
        System.out.println("생성: " + result.created());
        System.out.println("중복: " + result.duplicated());
        System.out.println("매핑대기: " + result.pending());
        System.out.println("실패: " + result.failed());

        var inboundOrders = inboundOrderRepository.findAll();
        var inboundItems = inboundOrderItemRepository.findAll();

        System.out.println("InboundOrder 건수: " + inboundOrders.size());
        System.out.println("InboundOrderItem 건수: " + inboundItems.size());

        assertThat(inboundOrders).as("InboundOrder가 생성되어야 한다").isNotEmpty();
        assertThat(inboundItems).as("InboundOrderItem이 생성되어야 한다").isNotEmpty();

        for (InboundOrderItemJpaEntity item : inboundItems) {
            System.out.println("  - externalProductOrderId: " + item.getExternalProductOrderId()
                    + ", productName: " + item.getExternalProductName());
        }

        // Order 변환 여부 확인 (상품 매핑이 없으면 PENDING_MAPPING 상태)
        var orders = orderRepository.findAll();
        var orderItems = orderItemRepository.findAll();
        System.out.println("Order 건수: " + orders.size());
        System.out.println("OrderItem 건수: " + orderItems.size());

        // 매핑 확인
        var mappings = mappingRepository.findAll();
        System.out.println("ExternalOrderItemMapping 건수: " + mappings.size());
    }

    @Test
    @DisplayName("동일 주문 재폴링 시 중복 방지")
    void duplicatePollingPrevention() {
        // given
        pollUseCase.execute(SALES_CHANNEL_ID, BATCH_SIZE);
        long firstCount = inboundOrderRepository.count();

        // when
        var result = pollUseCase.execute(SALES_CHANNEL_ID, BATCH_SIZE);

        // then
        long secondCount = inboundOrderRepository.count();
        System.out.println("첫 폴링 후: " + firstCount + "건, 재폴링 후: " + secondCount + "건");
        System.out.println("중복 건수: " + result.duplicated());

        assertThat(secondCount).as("재폴링 시 InboundOrder 증가하면 안 된다").isEqualTo(firstCount);
    }

    /**
     * 셀릭 API 실제 응답 기반 Mock 주문 데이터 (2026-03-21 조회).
     */
    private List<ExternalOrderPayload> buildSellicMockOrders() {
        Instant orderedAt = Instant.parse("2026-03-19T02:50:49Z");

        ExternalOrderPayload order1 =
                new ExternalOrderPayload(
                        "2026031994396341", orderedAt,
                        "하경훈", null, "010-9450-8522", null, 12900, orderedAt,
                        List.of(new ExternalOrderItemPayload(
                                "75236646", "1599405", null,
                                "[고스티] 주토피아 - 주디 - 교통경찰 카드커버",
                                "DEFAULT_ONE: Without Chip", null,
                                14900, 1, 14900, 2000, 12900,
                                "하경훈", "010-9450-8522", "42278",
                                "대구광역시 수성구 달구벌대로 3280", null, null)));

        ExternalOrderPayload order2 =
                new ExternalOrderPayload(
                        "2026032012345678", Instant.parse("2026-03-20T05:30:00Z"),
                        "김테스트", null, "010-1234-5678", null, 45000,
                        Instant.parse("2026-03-20T05:31:00Z"),
                        List.of(
                                new ExternalOrderItemPayload(
                                        "75236700", "1599500", null,
                                        "[테스트] 디즈니 미키마우스 폰케이스",
                                        "색상: 블랙", null,
                                        25000, 1, 25000, 0, 25000,
                                        "김테스트", "010-1234-5678", "06234",
                                        "서울시 강남구 테헤란로 123", "4층", "문 앞에 놔주세요"),
                                new ExternalOrderItemPayload(
                                        "75236701", "1599501", null,
                                        "[테스트] 디즈니 미니마우스 에어팟케이스",
                                        "색상: 화이트", null,
                                        20000, 1, 20000, 0, 20000,
                                        "김테스트", "010-1234-5678", "06234",
                                        "서울시 강남구 테헤란로 123", "4층", "문 앞에 놔주세요")));

        return List.of(order1, order2);
    }
}
