package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 셀릭→네이버 상품 동기화 도구.
 *
 * <h3>배경</h3>
 *
 * <p>레거시 운영 기간 동안 디즈니 상품은 셀릭→네이버 경로로 등록됩니다. 셀릭이 네이버에 상품을 등록하면 네이버 상품
 * ID(originProductNo)가 생기는데, 이 ID가 우리 market DB에 없습니다. 이 도구는 네이버 API에서 상품을 조회하여
 * sellerManagementCode(= luxurydb product_group_id)로 매칭하고, 우리 outbound_products(네이버)에 등록합니다.
 *
 * <h3>매칭 키</h3>
 *
 * <p>셀릭이 네이버에 상품을 등록할 때 {@code sellerManagementCode}에 luxurydb product_group_id를 넣습니다. 이 값은
 * 네이버 상품 상세 API({@code /v2/products/origin-products/{id}})의 {@code
 * originProduct.detailAttribute.sellerCodeInfo.sellerManagementCode}에서 확인할 수 있습니다.
 *
 * <h3>최적화</h3>
 *
 * <p>매번 전체 상품을 상세 조회하지 않습니다:
 *
 * <ol>
 *   <li>네이버 상품 목록 API(search)로 전체 originProductNo 수집 — 빠름(페이지당 100건)
 *   <li>이미 outbound_products(네이버)에 등록된 originProductNo를 제외
 *   <li>신규분만 상세 조회하여 sellerManagementCode 추출
 * </ol>
 *
 * <h3>실행 방법</h3>
 *
 * <pre>
 * # 환경변수 설정 (shop 테이블의 네이버 api_key/api_secret 참조)
 * NAVER_CLIENT_ID={shop.api_key} \
 * NAVER_CLIENT_SECRET={shop.api_secret} \
 * ./gradlew :adapter-out:client:naver-commerce-client:externalIntegrationTest \
 *   --tests "*NaverSellicProductSyncTest.syncNewProducts"
 *
 * # 출력에서 INSERT SQL을 복사하여 market DB에 실행
 * </pre>
 *
 * <h3>DB 연결 없이 동작</h3>
 *
 * <p>이 도구는 DB 접속 없이 네이버 API만 호출합니다. 이미 등록된 네이버 상품 ID는 환경변수
 * {@code KNOWN_NAVER_PRODUCT_IDS}로 전달하거나, 실행 전에 DB에서 추출합니다:
 *
 * <pre>
 * # market DB에서 이미 등록된 네이버 상품 ID 추출
 * mysql -e "SELECT external_product_id FROM market.outbound_products WHERE sales_channel_id = 2"
 * </pre>
 */
@Tag("external-integration")
@DisplayName("셀릭→네이버 상품 동기화")
class NaverSellicProductSyncTest {

    private static final int SEARCH_PAGE_SIZE = 100;
    private static final long SEARCH_API_DELAY_MS = 200;
    private static final long DETAIL_API_DELAY_MS = 300;
    private static final int CONNECT_TIMEOUT_SECONDS = 5;
    private static final int REQUEST_TIMEOUT_SECONDS = 30;
    private static final int MAX_RETRY = 5;

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * 증분 동기화 — 신규 상품만 조회하여 INSERT SQL 생성.
     *
     * <p>실행 전 KNOWN_NAVER_PRODUCT_IDS 환경변수에 이미 등록된 네이버 상품 ID를 콤마로 전달하면 해당 상품은
     * 상세 조회를 건너뜁니다.
     *
     * <pre>
     * # 이미 등록된 ID 추출 후 실행
     * KNOWN=$(mysql -NBe "SELECT GROUP_CONCAT(external_product_id) FROM market.outbound_products WHERE sales_channel_id=2")
     * KNOWN_NAVER_PRODUCT_IDS=$KNOWN \
     * NAVER_CLIENT_ID={shop.api_key} NAVER_CLIENT_SECRET={shop.api_secret} \
     * ./gradlew :adapter-out:client:naver-commerce-client:externalIntegrationTest \
     *   --tests "*NaverSellicProductSyncTest.syncNewProducts"
     * </pre>
     */
    @Test
    @DisplayName("증분 동기화: 신규 네이버 상품 → sellerManagementCode 매핑 → INSERT SQL 출력")
    void syncNewProducts() throws Exception {
        HttpClient httpClient = buildHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);
        System.out.println("[OK] 토큰 발급 성공");

        Set<Long> knownIds = loadKnownNaverProductIds();
        System.out.printf("이미 등록된 네이버 상품 ID: %d건%n", knownIds.size());

        List<Long> allProductNos = fetchAllOriginProductNos(httpClient, token);
        System.out.printf("네이버 전체 상품: %d건%n", allProductNos.size());

        List<Long> newProductNos =
                allProductNos.stream().filter(id -> !knownIds.contains(id)).toList();
        System.out.printf("신규 (상세 조회 대상): %d건%n", newProductNos.size());

        if (newProductNos.isEmpty()) {
            System.out.println("동기화할 신규 상품이 없습니다.");
            return;
        }

        List<NaverProductMapping> mappings = fetchProductDetails(httpClient, token, newProductNos);
        System.out.printf("sellerManagementCode 매칭: %d건%n", mappings.size());

        printMappingData(mappings);
        printInsertSql(mappings);
    }

    /**
     * 전체 동기화 — 모든 네이버 상품을 조회하여 매핑 데이터 출력.
     *
     * <p>초기 셋업이나 전체 검증 시 사용합니다.
     */
    @Test
    @DisplayName("전체 동기화: 모든 네이버 상품 상세 조회 → 매핑 데이터 출력")
    void syncAllProducts() throws Exception {
        HttpClient httpClient = buildHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);
        System.out.println("[OK] 토큰 발급 성공");

        List<Long> allProductNos = fetchAllOriginProductNos(httpClient, token);
        System.out.printf("네이버 전체 상품: %d건%n", allProductNos.size());

        List<NaverProductMapping> mappings = fetchProductDetails(httpClient, token, allProductNos);
        System.out.printf("sellerManagementCode 매칭: %d건%n", mappings.size());

        printMappingData(mappings);
        printInsertSql(mappings);
    }

    // ==================== 내부 메서드 ====================

    private HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build();
    }

    private Set<Long> loadKnownNaverProductIds() {
        Set<Long> ids = new HashSet<>();
        String env = System.getenv("KNOWN_NAVER_PRODUCT_IDS");
        if (env != null && !env.isBlank()) {
            for (String part : env.split(",")) {
                try {
                    ids.add(Long.parseLong(part.trim()));
                } catch (NumberFormatException e) {
                    System.err.printf("경고: 유효하지 않은 상품 ID '%s'를 무시합니다.%n", part.trim());
                }
            }
        }
        return ids;
    }

    private List<Long> fetchAllOriginProductNos(HttpClient httpClient, String token)
            throws Exception {
        List<Long> productNos = new ArrayList<>();
        int page = 1;
        int totalPages = 1;

        while (page <= totalPages) {
            String searchBody =
                    String.format("{\"page\":%d,\"size\":%d}", page, SEARCH_PAGE_SIZE);
            HttpRequest req =
                    HttpRequest.newBuilder()
                            .uri(URI.create(NaverAuthHelper.BASE_URL + "/v1/products/search"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + token)
                            .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                            .POST(HttpRequest.BodyPublishers.ofString(searchBody))
                            .build();

            HttpResponse<String> resp =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                throw new IllegalStateException(
                        "네이버 상품 목록 조회 실패: status=" + resp.statusCode() + " body=" + resp.body());
            }

            JsonNode result = objectMapper.readTree(resp.body());

            if (page == 1) {
                int total = result.path("totalElements").asInt();
                totalPages = (total + SEARCH_PAGE_SIZE - 1) / SEARCH_PAGE_SIZE;
            }

            JsonNode contents = result.path("contents");
            if (!contents.isArray()) break;

            for (JsonNode c : contents) {
                productNos.add(c.path("originProductNo").asLong());
            }
            page++;
            Thread.sleep(SEARCH_API_DELAY_MS);
        }
        return productNos;
    }

    private List<NaverProductMapping> fetchProductDetails(
            HttpClient httpClient, String token, List<Long> productNos) throws Exception {
        List<NaverProductMapping> mappings = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        int count = 0;

        for (Long productNo : productNos) {
            try {
                HttpResponse<String> resp = requestWithRetry(httpClient, token, productNo);

                if (resp == null || resp.statusCode() != 200) {
                    int status = resp != null ? resp.statusCode() : -1;
                    System.err.printf("WARN: %d 조회 실패 (status=%d) — 누락됩니다.%n", productNo, status);
                    failedIds.add(productNo);
                    continue;
                }

                JsonNode detail = objectMapper.readTree(resp.body());
                JsonNode origin = detail.path("originProduct");
                String code =
                        origin.path("detailAttribute")
                                .path("sellerCodeInfo")
                                .path("sellerManagementCode")
                                .asText("");

                if (code.isBlank()) {
                    continue;
                }

                // sellerManagementCode는 숫자형 product_group_id여야 함 — 검증
                try {
                    Long.parseLong(code);
                } catch (NumberFormatException e) {
                    System.err.printf(
                            "WARN: %d의 sellerManagementCode '%s'가 숫자가 아닙니다. 건너뜁니다.%n",
                            productNo, code);
                    continue;
                }

                mappings.add(
                        new NaverProductMapping(
                                productNo,
                                code,
                                origin.path("name").asText(""),
                                origin.path("statusType").asText("")));

                count++;
                if (count % 50 == 0) {
                    System.out.printf(
                            "  상세 조회 진행: %d/%d (매칭: %d)%n",
                            count, productNos.size(), mappings.size());
                }
                Thread.sleep(DETAIL_API_DELAY_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("조회 중 인터럽트 발생. 중단합니다.", e);
            } catch (Exception e) {
                System.err.printf("ERROR: %d → %s%n", productNo, e.getMessage());
                failedIds.add(productNo);
            }
        }

        if (!failedIds.isEmpty()) {
            System.err.printf(
                    "%n경고: %d건 조회 실패. 누락된 ID: %s%n", failedIds.size(), failedIds);
        }

        return mappings;
    }

    private HttpResponse<String> requestWithRetry(
            HttpClient httpClient, String token, long productNo) throws Exception {
        for (int retry = 0; retry < MAX_RETRY; retry++) {
            HttpRequest req =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            NaverAuthHelper.BASE_URL
                                                    + "/v2/products/origin-products/"
                                                    + productNo))
                            .header("Authorization", "Bearer " + token)
                            .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                            .GET()
                            .build();

            HttpResponse<String> resp =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 429) {
                long wait = (long) Math.pow(2, retry) * 1000;
                System.err.printf("429: %d → %dms 대기%n", productNo, wait);
                Thread.sleep(wait);
                continue;
            }
            return resp;
        }
        return null;
    }

    private void printMappingData(List<NaverProductMapping> mappings) {
        System.out.println("\n=== NAVER_PRODUCT_MAPPING_START ===");
        System.out.println(
                "-- 형식: originProductNo|sellerManagementCode(=legacyProductGroupId)|status|name");
        for (NaverProductMapping m : mappings) {
            System.out.printf(
                    "%d|%s|%s|%s%n",
                    m.originProductNo, m.legacyProductGroupId, m.status, m.name);
        }
        System.out.println("=== NAVER_PRODUCT_MAPPING_END ===");
    }

    private void printInsertSql(List<NaverProductMapping> mappings) {
        if (mappings.isEmpty()) return;

        System.out.println("\n=== INSERT_SQL_START ===");
        System.out.println("-- outbound_products에 네이버 채널로 INSERT");
        System.out.println(
                "-- product_group_id는 legacy_product_id_mappings에서 internal_product_group_id로 변환");
        System.out.println();
        System.out.println(
                "INSERT INTO market.outbound_products"
                        + " (product_group_id, sales_channel_id, shop_id, external_product_id, status, created_at, updated_at)");
        System.out.println(
                "SELECT lm.internal_product_group_id, 2, 0, v.naver_id, 'REGISTERED', NOW(6), NOW(6)");
        System.out.println("FROM (VALUES");

        for (int i = 0; i < mappings.size(); i++) {
            NaverProductMapping m = mappings.get(i);
            String comma = (i < mappings.size() - 1) ? "," : "";
            // legacyProductGroupId는 숫자 검증을 통과한 값만 도달하므로 안전하게 숫자로 출력
            System.out.printf("  ROW(%d, '%d')%s%n",
                    Long.parseLong(m.legacyProductGroupId), m.originProductNo, comma);
        }

        System.out.println(") AS v(legacy_pgid, naver_id)");
        System.out.println(
                "JOIN market.legacy_product_id_mappings lm"
                        + " ON lm.legacy_product_group_id = v.legacy_pgid");
        System.out.println("WHERE NOT EXISTS (");
        System.out.println(
                "  SELECT 1 FROM market.outbound_products op"
                        + " WHERE op.sales_channel_id = 2 AND op.external_product_id = v.naver_id");
        System.out.println(");");
        System.out.println("=== INSERT_SQL_END ===");
    }

    record NaverProductMapping(
            long originProductNo, String legacyProductGroupId, String name, String status) {}
}
