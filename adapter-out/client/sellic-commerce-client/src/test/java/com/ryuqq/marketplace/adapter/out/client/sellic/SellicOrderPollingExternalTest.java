package com.ryuqq.marketplace.adapter.out.client.sellic;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicOrderQueryResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.mapper.SellicCommerceOrderMapper;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 셀릭 실제 API 주문 조회 → Mapper 변환 → ExternalOrderPayload 검증 테스트.
 *
 * <p>실행: {@code ./gradlew :adapter-out:client:sellic-commerce-client:externalIntegrationTest
 * --tests "*SellicOrderPollingExternalTest*"}
 */
@Tag("external-integration")
@DisplayName("셀릭 실제 주문 조회 → Mapper 변환 검증")
class SellicOrderPollingExternalTest {

    private static final String BASE_URL = "http://api.sellic.co.kr";
    private static final String CUSTOMER_ID = "1012";
    private static final String API_KEY = "REDACTED_API_KEY";

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final HttpClient httpClient =
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private final SellicCommerceOrderMapper mapper = new SellicCommerceOrderMapper();

    @Test
    @DisplayName("셀릭 주문 조회 → ExternalOrderPayload 변환 검증")
    void fetchAndConvertOrders() throws Exception {
        // 1. 셀릭 API 호출
        String requestJson =
                objectMapper.writeValueAsString(
                        new java.util.LinkedHashMap<>() {
                            {
                                put("customer_id", CUSTOMER_ID);
                                put("api_key", API_KEY);
                                put("s_date", "2026-03-01");
                                put("e_date", "2026-03-21");
                            }
                        });

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/openapi/get_order"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                        .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).as("HTTP 200").isEqualTo(200);

        SellicOrderQueryResponse sellicResponse =
                objectMapper.readValue(response.body(), SellicOrderQueryResponse.class);

        assertThat(sellicResponse.isSuccess()).as("API 성공").isTrue();
        assertThat(sellicResponse.datas()).as("주문 데이터 존재").isNotEmpty();

        System.out.println("=== 셀릭 원본 주문 ===");
        System.out.println("총 건수: " + sellicResponse.datas().size());

        // 2. Mapper 변환
        List<ExternalOrderPayload> payloads =
                mapper.toExternalOrderPayloads(sellicResponse.datas());

        System.out.println("\n=== ExternalOrderPayload 변환 결과 ===");
        System.out.println("변환된 주문 수: " + payloads.size());

        assertThat(payloads).as("변환된 주문이 존재해야 한다").isNotEmpty();

        // 3. 각 주문 검증
        for (ExternalOrderPayload order : payloads) {
            System.out.println("\n--- 주문: " + order.externalOrderNo() + " ---");
            System.out.println("  주문자: " + order.buyerName());
            System.out.println("  연락처: " + order.buyerPhone());
            System.out.println("  결제금액: " + order.totalPaymentAmount());
            System.out.println("  주문일시: " + order.orderedAt());
            System.out.println("  아이템 수: " + order.items().size());

            assertThat(order.externalOrderNo()).as("주문번호").isNotBlank();
            assertThat(order.buyerName()).as("주문자명").isNotBlank();
            assertThat(order.orderedAt()).as("주문일시").isNotNull();
            assertThat(order.items()).as("아이템").isNotEmpty();

            for (ExternalOrderItemPayload item : order.items()) {
                System.out.println(
                        "    - ["
                                + item.externalProductOrderId()
                                + "] "
                                + item.externalProductName()
                                + " | 옵션: "
                                + item.externalOptionName()
                                + " | 수량: "
                                + item.quantity()
                                + " | 결제: "
                                + item.paymentAmount()
                                + " | 수취인: "
                                + item.receiverName());

                assertThat(item.externalProductOrderId()).as("외부 상품주문 ID").isNotBlank();
                assertThat(item.externalProductName()).as("상품명").isNotBlank();
                assertThat(item.quantity()).as("수량").isGreaterThan(0);
                assertThat(item.receiverName()).as("수취인명").isNotBlank();
            }
        }

        System.out.println("\n[PASS] 셀릭 주문 " + payloads.size() + "건 변환 검증 완료");
    }
}
