package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceOrderMapper;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 네이버 실제 API 주문 조회 → Mapper 변환 → ExternalOrderPayload 검증 테스트.
 *
 * <p>실행: {@code NAVER_CLIENT_ID=... NAVER_CLIENT_SECRET=... ./gradlew
 * :adapter-out:client:naver-commerce-client:externalIntegrationTest --tests
 * "*NaverOrderPollingExternalTest*"}
 */
@Tag("external-integration")
@DisplayName("네이버 실제 주문 조회 → Mapper 변환 검증")
class NaverOrderPollingExternalTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final NaverCommerceOrderMapper mapper = new NaverCommerceOrderMapper();

    @Test
    @DisplayName("네이버 주문 폴링 (최근 7일) → ExternalOrderPayload 변환 검증")
    void fetchAndConvertOrders() throws Exception {
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);
        System.out.println("[OK] 네이버 토큰 발급 성공");

        // 1. 최근 7일 변경 주문 ID 수집
        Instant toTime = Instant.now();
        Instant fromTime = toTime.minus(1, ChronoUnit.DAYS);

        String fromStr = formatForNaver(fromTime);
        String toStr = formatForNaver(toTime);

        System.out.println("=== 네이버 주문 폴링 ===");
        System.out.println("기간: " + fromStr + " ~ " + toStr);

        List<String> productOrderIds = pollChangedProductOrderIds(token, fromStr, toStr);
        System.out.println("변경 상품주문 수: " + productOrderIds.size());

        if (productOrderIds.isEmpty()) {
            System.out.println("[INFO] 최근 7일간 변경된 주문이 없습니다. 테스트 종료.");
            return;
        }

        // 2. 상세 조회 (최대 300건씩)
        List<NaverProductOrderDetail> allDetails = new ArrayList<>();
        int batchSize = 300;
        for (int i = 0; i < productOrderIds.size(); i += batchSize) {
            List<String> batch =
                    productOrderIds.subList(i, Math.min(i + batchSize, productOrderIds.size()));
            List<NaverProductOrderDetail> details = queryProductOrders(token, batch);
            allDetails.addAll(details);
        }

        System.out.println("상세 조회 결과: " + allDetails.size() + "건");

        // 3. Mapper 변환
        List<ExternalOrderPayload> payloads = mapper.toExternalOrderPayloads(allDetails);

        System.out.println("\n=== ExternalOrderPayload 변환 결과 ===");
        System.out.println("변환된 주문 수: " + payloads.size());

        assertThat(payloads).as("변환된 주문이 존재해야 한다").isNotEmpty();

        // 4. 각 주문 검증
        int printLimit = Math.min(payloads.size(), 5);
        for (int i = 0; i < printLimit; i++) {
            ExternalOrderPayload order = payloads.get(i);
            System.out.println("\n--- 주문: " + order.externalOrderNo() + " ---");
            System.out.println("  주문자: " + order.buyerName());
            System.out.println("  결제금액: " + order.totalPaymentAmount());
            System.out.println("  주문일시: " + order.orderedAt());
            System.out.println("  아이템 수: " + order.items().size());

            assertThat(order.externalOrderNo()).as("주문번호").isNotBlank();
            assertThat(order.orderedAt()).as("주문일시").isNotNull();
            assertThat(order.items()).as("아이템").isNotEmpty();

            for (ExternalOrderItemPayload item : order.items()) {
                System.out.println(
                        "    - ["
                                + item.externalProductOrderId()
                                + "] "
                                + item.externalProductName()
                                + " | 수량: "
                                + item.quantity()
                                + " | 결제: "
                                + item.paymentAmount()
                                + " | 수취인: "
                                + item.receiverName());

                assertThat(item.externalProductOrderId()).as("외부 상품주문 ID").isNotBlank();
                assertThat(item.quantity()).as("수량").isGreaterThan(0);
            }
        }

        if (payloads.size() > printLimit) {
            System.out.println("\n... 외 " + (payloads.size() - printLimit) + "건 생략");
        }

        System.out.println("\n[PASS] 네이버 주문 " + payloads.size() + "건 변환 검증 완료");
    }

    // ===== Helper =====

    private List<String> pollChangedProductOrderIds(String token, String fromStr, String toStr)
            throws Exception {
        List<String> allIds = new ArrayList<>();
        String moreSequence = null;

        do {
            StringBuilder uriBuilder =
                    new StringBuilder(
                            NaverAuthHelper.BASE_URL
                                    + "/v1/pay-order/seller/product-orders/last-changed-statuses"
                                    + "?lastChangedType=PAYED"
                                    + "&lastChangedFrom="
                                    + java.net.URLEncoder.encode(fromStr, "UTF-8")
                                    + "&lastChangedTo="
                                    + java.net.URLEncoder.encode(toStr, "UTF-8")
                                    + "&limitCount=300");

            if (moreSequence != null) {
                uriBuilder.append("&moreSequence=").append(moreSequence);
            }

            HttpRequest req =
                    HttpRequest.newBuilder()
                            .uri(URI.create(uriBuilder.toString()))
                            .header("Authorization", "Bearer " + token)
                            .GET()
                            .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                System.out.println("last-changed-statuses 실패: " + resp.statusCode());
                System.out.println("응답: " + resp.body());
                break;
            }

            JsonNode root = objectMapper.readTree(resp.body());
            JsonNode data = root.get("data");
            if (data == null) {
                break;
            }

            JsonNode statuses = data.get("lastChangeStatuses");
            if (statuses != null && statuses.isArray()) {
                for (JsonNode status : statuses) {
                    allIds.add(status.get("productOrderId").asText());
                }
            }

            JsonNode more = data.get("more");
            moreSequence =
                    (more != null && more.has("moreSequence"))
                            ? more.get("moreSequence").asText()
                            : null;
        } while (moreSequence != null);

        return allIds;
    }

    @SuppressWarnings("unchecked")
    private List<NaverProductOrderDetail> queryProductOrders(
            String token, List<String> productOrderIds) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("productOrderIds", productOrderIds));

        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v1/pay-order/seller/product-orders/query"))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            System.out.println("product-orders/query 실패: " + resp.statusCode());
            return List.of();
        }

        JsonNode root = objectMapper.readTree(resp.body());
        JsonNode data = root.get("data");
        if (data == null || !data.isArray()) {
            return List.of();
        }

        List<NaverProductOrderDetail> details = new ArrayList<>();
        for (JsonNode node : data) {
            details.add(objectMapper.treeToValue(node, NaverProductOrderDetail.class));
        }
        return details;
    }

    private String formatForNaver(Instant instant) {
        return instant.atZone(KST)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
    }
}
