package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 기존 등록된 네이버 상품의 KC 인증 정보를 조회하는 테스트.
 *
 * <p>환경변수: NAVER_CLIENT_ID, NAVER_CLIENT_SECRET
 */
@Tag("external-integration")
@DisplayName("네이버 기존 상품 인증정보 조회")
class NaverExistingProductCertCheckTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("기존 상품 목록 조회 → 상세 조회 → 인증정보 출력")
    void checkExistingProductCertification() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);
        System.out.println("[OK] 토큰 발급 성공");

        // 1. 상품 목록 조회 (첫 페이지)
        String searchBody =
                """
                {"page":1,"size":10}""";

        HttpRequest searchReq =
                HttpRequest.newBuilder()
                        .uri(URI.create(NaverAuthHelper.BASE_URL + "/v1/products/search"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(searchBody))
                        .build();

        HttpResponse<String> searchResp =
                httpClient.send(searchReq, HttpResponse.BodyHandlers.ofString());
        JsonNode searchResult = objectMapper.readTree(searchResp.body());

        System.out.println("=== 상품 목록 (첫 10개) ===");
        System.out.println("전체 상품 수: " + searchResult.path("totalElements").asInt());

        JsonNode contents = searchResult.path("contents");
        if (!contents.isArray() || contents.isEmpty()) {
            System.out.println("등록된 상품이 없습니다.");
            return;
        }

        // 상품 목록 출력
        for (JsonNode content : contents) {
            long originProductNo = content.path("originProductNo").asLong();
            JsonNode channelProducts = content.path("channelProducts");
            String name =
                    channelProducts.isArray() && !channelProducts.isEmpty()
                            ? channelProducts.get(0).path("name").asText("")
                            : "";
            String status =
                    channelProducts.isArray() && !channelProducts.isEmpty()
                            ? channelProducts.get(0).path("statusType").asText("")
                            : "";
            System.out.printf("  원상품번호: %d | 상태: %s | 이름: %s%n", originProductNo, status, name);
        }

        // 2. 보조배터리(13184147202)와 USB드라이브(13174797666) 포함 주요 상품 상세 조회
        System.out.println("\n=== 상품 상세 (인증정보) ===");
        // 보조배터리와 USB 드라이브 상세 확인 (전자제품 - KC 인증 필요 가능성)
        long[] targetProducts = {13184147202L, 13174797666L, 13184185075L};
        int count = 0;
        for (long originProductNo : targetProducts) {
            HttpRequest detailReq =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            NaverAuthHelper.BASE_URL
                                                    + "/v2/products/origin-products/"
                                                    + originProductNo))
                            .header("Authorization", "Bearer " + token)
                            .GET()
                            .build();

            HttpResponse<String> detailResp =
                    httpClient.send(detailReq, HttpResponse.BodyHandlers.ofString());
            JsonNode detail = objectMapper.readTree(detailResp.body());

            JsonNode detailAttr = detail.path("originProduct").path("detailAttribute");
            JsonNode certInfos = detailAttr.path("productCertificationInfos");
            JsonNode certExclude = detailAttr.path("certificationTargetExcludeContent");
            JsonNode notice = detailAttr.path("productInfoProvidedNotice");

            String productName = detail.path("originProduct").path("name").asText("");
            String categoryId = detail.path("originProduct").path("leafCategoryId").asText("");

            System.out.printf(
                    "%n--- 상품 %d: %s (카테고리: %s) ---%n", originProductNo, productName, categoryId);

            System.out.println("  [productCertificationInfos]");
            if (certInfos.isArray() && !certInfos.isEmpty()) {
                System.out.println("  " + objectMapper.writeValueAsString(certInfos));
            } else {
                System.out.println("  (없음)");
            }

            System.out.println("  [certificationTargetExcludeContent]");
            if (!certExclude.isMissingNode() && !certExclude.isNull()) {
                System.out.println("  " + objectMapper.writeValueAsString(certExclude));
            } else {
                System.out.println("  (없음)");
            }

            System.out.println("  [productInfoProvidedNotice.type]");
            if (!notice.isMissingNode() && !notice.isNull()) {
                System.out.println(
                        "  type: " + notice.path("productInfoProvidedNoticeType").asText(""));
            } else {
                System.out.println("  (없음)");
            }

            JsonNode searchInfo = detailAttr.path("naverShoppingSearchInfo");
            System.out.println("  [naverShoppingSearchInfo]");
            if (!searchInfo.isMissingNode() && !searchInfo.isNull()) {
                System.out.println("  " + objectMapper.writeValueAsString(searchInfo));
            } else {
                System.out.println("  (없음)");
            }

            // productAttributes 위치 확인
            JsonNode prodAttrs = detailAttr.path("productAttributes");
            if (prodAttrs.isArray() && !prodAttrs.isEmpty()) {
                System.out.println(
                        "  [detailAttribute.productAttributes] "
                                + objectMapper.writeValueAsString(prodAttrs));
            }
            JsonNode searchAttrs = searchInfo.path("productAttributes");
            if (searchAttrs.isArray() && !searchAttrs.isEmpty()) {
                System.out.println(
                        "  [naverShoppingSearchInfo.productAttributes] "
                                + objectMapper.writeValueAsString(searchAttrs));
            }

            count++;
        }
    }

    @Test
    @DisplayName("최소 요청으로 상품 등록 시도 (productAttributes 포함)")
    void tryMinimalRegistration() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // 카테고리 50000973 (담요) PRIMARY 속성:
        // 10014604 주요소재 → 10574774 면
        // 10014605 패턴 → 10040049 무지
        String json =
                """
{
  "originProduct": {
    "statusType": "SALE",
    "saleType": "NEW",
    "leafCategoryId": "50001615",
    "name": "네이버 속성 테스트 USB 드라이브",
    "images": {
      "representativeImage": {
        "url": "https://shop-phinf.pstatic.net/20260313_294/17733696875206wuci_JPEG/288351393492979_1382318577.jpg"
      }
    },
    "detailAttribute": {
      "naverShoppingSearchInfo": {
        "brandName": "테스트"
      },
      "afterServiceInfo": {
        "afterServiceTelephoneNumber": "01051304844",
        "afterServiceGuideContent": "상세페이지 참조"
      },
      "originAreaInfo": {
        "originAreaCode": "03",
        "content": "중국"
      },
      "minorPurchasable": true,
      "certificationTargetExcludeContent": {},
      "productAttributes": [
        {"attributeSeq": 1895, "attributeValueSeq": 10422},
        {"attributeSeq": 10015514, "attributeValueSeq": 10517836},
        {"attributeSeq": 9000504, "attributeValueSeq": 10030656}
      ],
      "productInfoProvidedNotice": {
        "productInfoProvidedNoticeType": "ETC",
        "etc": {
          "returnCostReason": "상품 상세 참조",
          "noRefundReason": "상품 상세 참조",
          "qualityAssuranceStandard": "상품 상세 참조",
          "compensationProcedure": "상품 상세 참조",
          "troubleShootingContents": "상품 상세 참조",
          "itemName": "USB 드라이브",
          "modelName": "상품 상세 참조",
          "manufacturer": "테스트",
          "afterServiceDirector": "상품 상세 참조"
        }
      }
    },
    "salePrice": 12000,
    "stockQuantity": 50,
    "detailContent": "<p>테스트 상품입니다</p>",
    "deliveryInfo": {
      "deliveryType": "DELIVERY",
      "deliveryAttributeType": "NORMAL",
      "deliveryFee": {
        "deliveryFeeType": "FREE",
        "deliveryFeePayType": "PREPAID",
        "baseFee": 0
      },
      "deliveryCompany": "CJGLS",
      "claimDeliveryInfo": {
        "returnDeliveryFee": 3000,
        "exchangeDeliveryFee": 6000
      }
    }
  },
  "smartstoreChannelProduct": {
    "channelProductName": "네이버 속성 테스트 USB 드라이브",
    "channelProductDisplayStatusType": "ON",
    "naverShoppingRegistration": true
  }
}
""";

        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(URI.create(NaverAuthHelper.BASE_URL + "/v2/products"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("=== 최소 등록 요청 응답 ===");
        System.out.println("Status: " + resp.statusCode());
        try {
            System.out.println(objectMapper.writeValueAsString(objectMapper.readTree(resp.body())));
        } catch (Exception e) {
            System.out.println(resp.body());
        }
    }

    @Test
    @DisplayName("테스트 상품 삭제")
    void deleteTestProduct() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        long[] productNos = {13190261267L, 13190261314L, 13190261354L};
        for (long productNo : productNos) {
            HttpRequest deleteReq =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            NaverAuthHelper.BASE_URL
                                                    + "/v2/products/origin-products/"
                                                    + productNo))
                            .header("Authorization", "Bearer " + token)
                            .DELETE()
                            .build();

            HttpResponse<String> resp =
                    httpClient.send(deleteReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("삭제 " + productNo + " -> Status: " + resp.statusCode());
            if (resp.statusCode() != 200) {
                System.out.println("  " + resp.body());
            }
            Thread.sleep(500);
        }
    }

    @Test
    @DisplayName("카테고리 50000973 필수 속성 조회")
    void checkCategoryAttributes() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // 카테고리 50001615 PRIMARY 속성의 값 조회
        long categoryId = 50001615L;
        HttpRequest valReq =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v1/product-attributes/attribute-values?categoryId="
                                                + categoryId))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
        HttpResponse<String> valResp =
                httpClient.send(valReq, HttpResponse.BodyHandlers.ofString());
        JsonNode valResult = objectMapper.readTree(valResp.body());

        // PRIMARY 속성 seq: 1895(인터페이스), 10015514(무상AS), 9000504(캡방식)
        long[] primarySeqs = {1895L, 10015514L, 9000504L};
        for (long seq : primarySeqs) {
            System.out.printf("=== 속성 %d 값들 ===%n", seq);
            if (valResult.isArray()) {
                for (JsonNode val : valResult) {
                    if (val.path("attributeSeq").asLong() == seq) {
                        System.out.printf(
                                "  valueSeq=%s, value=%s%n",
                                val.path("attributeValueSeq").asText(),
                                val.path("minAttributeValue").asText(""));
                    }
                }
            }
        }
    }
}
