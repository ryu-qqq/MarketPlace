package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Setof Commerce Client DTO ↔ Setof Commerce Server API 간 JSON Contract 검증.
 *
 * <p>클라이언트 DTO가 직렬화한 JSON 필드명/구조가 서버(setof-commerce adapter-in:rest-api-admin v2)가 기대하는 것과 일치하는지
 * 확인한다.
 *
 * <p>서버 API 스펙이 변경되면 이 테스트가 깨져서 불일치를 조기에 감지할 수 있다.
 */
@Tag("unit")
@DisplayName("Setof Commerce DTO JSON Contract 검증")
class SetofCommerceDtoContractTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ── 유틸리티 ──

    private static JsonNode toJsonNode(Object dto) {
        try {
            return objectMapper.readTree(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }

    private static Set<String> fieldNames(JsonNode node) {
        return StreamSupport.stream(
                        ((Iterable<String>) () -> node.fieldNames()).spliterator(), false)
                .collect(Collectors.toSet());
    }

    // ════════════════════════════════════════════════════════════════
    // Product DTOs
    // Server: PATCH /api/v2/admin/products/{productId}/price
    //         PATCH /api/v2/admin/products/{productId}/stock
    //         PATCH /api/v2/admin/products/product-groups/{productGroupId}
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SetofProductPriceUpdateRequest → UpdateProductPriceApiRequest")
    class ProductPriceUpdateContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var request = new SetofProductPriceUpdateRequest(10000, 8000);
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node)).containsExactlyInAnyOrder("regularPrice", "currentPrice");
            assertThat(node.get("regularPrice").asInt()).isEqualTo(10000);
            assertThat(node.get("currentPrice").asInt()).isEqualTo(8000);
        }
    }

    @Nested
    @DisplayName("SetofProductStockUpdateRequest → UpdateProductStockApiRequest")
    class ProductStockUpdateContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var request = new SetofProductStockUpdateRequest(50);
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node)).containsExactlyInAnyOrder("stockQuantity");
            assertThat(node.get("stockQuantity").asInt()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("SetofProductsUpdateRequest → UpdateProductsApiRequest")
    class ProductsUpdateContract {

        @Test
        @DisplayName("전체 필드가 포함된 JSON이 서버 계약과 일치한다")
        void fullFieldsShouldMatchServerContract() {
            var selectedOption = new SetofProductsUpdateRequest.SelectedOptionRequest("색상", "블랙");
            var product =
                    new SetofProductsUpdateRequest.ProductRequest(
                            100L, "SKU-001", 10000, 8000, 50, 1, List.of(selectedOption));
            var optionValue = new SetofProductsUpdateRequest.OptionValueRequest(200L, "블랙", 1);
            var optionGroup =
                    new SetofProductsUpdateRequest.OptionGroupRequest(
                            300L, "색상", 1, List.of(optionValue));
            var request = new SetofProductsUpdateRequest(List.of(optionGroup), List.of(product));

            JsonNode node = toJsonNode(request);

            // 최상위 필드
            assertThat(fieldNames(node)).containsExactlyInAnyOrder("optionGroups", "products");

            // OptionGroupRequest 필드
            JsonNode optionGroupNode = node.get("optionGroups").get(0);
            assertThat(fieldNames(optionGroupNode))
                    .containsExactlyInAnyOrder(
                            "sellerOptionGroupId", "optionGroupName", "sortOrder", "optionValues");

            // OptionValueRequest 필드
            JsonNode optionValueNode = optionGroupNode.get("optionValues").get(0);
            assertThat(fieldNames(optionValueNode))
                    .containsExactlyInAnyOrder(
                            "sellerOptionValueId", "optionValueName", "sortOrder");

            // ProductRequest 필드
            JsonNode productNode = node.get("products").get(0);
            assertThat(fieldNames(productNode))
                    .containsExactlyInAnyOrder(
                            "productId",
                            "skuCode",
                            "regularPrice",
                            "currentPrice",
                            "stockQuantity",
                            "sortOrder",
                            "selectedOptions");

            // SelectedOptionRequest 필드
            JsonNode selectedOptionNode = productNode.get("selectedOptions").get(0);
            assertThat(fieldNames(selectedOptionNode))
                    .containsExactlyInAnyOrder("optionGroupName", "optionValueName");
        }

        @Test
        @DisplayName("@JsonInclude(NON_NULL) - null 필드는 제외된다")
        void nullFieldsShouldBeExcluded() {
            var product =
                    new SetofProductsUpdateRequest.ProductRequest(
                            null, "SKU-001", 10000, 8000, 50, 1, List.of());
            var request = new SetofProductsUpdateRequest(null, List.of(product));

            JsonNode node = toJsonNode(request);

            // optionGroups가 null이면 제외
            assertThat(node.has("optionGroups")).isFalse();

            // productId가 null이면 제외
            JsonNode productNode = node.get("products").get(0);
            assertThat(productNode.has("productId")).isFalse();
        }
    }

    // ════════════════════════════════════════════════════════════════
    // ProductGroup DTOs
    // Server: POST /api/v2/admin/product-groups
    //         PUT  /api/v2/admin/product-groups/{productGroupId}
    //         PATCH /api/v2/admin/product-groups/{productGroupId}/basic-info
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SetofProductGroupRegistrationRequest → RegisterProductGroupApiRequest")
    class ProductGroupRegistrationContract {

        @Test
        @DisplayName("모든 필드가 포함된 JSON이 서버 계약과 일치한다")
        void fullFieldsShouldMatchServerContract() {
            var image =
                    new SetofProductGroupRegistrationRequest.ImageRequest(
                            "THUMBNAIL", "https://img.example.com/1.jpg", 1);
            var optionValue = new SetofProductGroupRegistrationRequest.OptionValueRequest("블랙", 1);
            var optionGroup =
                    new SetofProductGroupRegistrationRequest.OptionGroupRequest(
                            "색상", 1, List.of(optionValue));
            var selectedOption =
                    new SetofProductGroupRegistrationRequest.SelectedOptionRequest("색상", "블랙");
            var product =
                    new SetofProductGroupRegistrationRequest.ProductRequest(
                            null, "SKU-001", 10000, 8000, 50, 1, List.of(selectedOption));
            var descImage =
                    new SetofProductGroupRegistrationRequest.DescriptionImageRequest(
                            "https://img.example.com/desc.jpg", 1);
            var description =
                    new SetofProductGroupRegistrationRequest.DescriptionRequest(
                            "<p>상품 설명</p>", List.of(descImage));
            var noticeEntry =
                    new SetofProductGroupRegistrationRequest.NoticeEntryRequest(1L, "제조국", "대한민국");
            var notice =
                    new SetofProductGroupRegistrationRequest.NoticeRequest(List.of(noticeEntry));
            var request =
                    new SetofProductGroupRegistrationRequest(
                            1L,
                            100L,
                            200L,
                            300L,
                            400L,
                            "테스트 상품",
                            "COMBINATION",
                            10000,
                            8000,
                            List.of(image),
                            List.of(optionGroup),
                            List.of(product),
                            description,
                            notice);

            JsonNode node = toJsonNode(request);

            // 최상위 필드
            assertThat(fieldNames(node))
                    .containsExactlyInAnyOrder(
                            "productGroupId",
                            "brandId",
                            "categoryId",
                            "shippingPolicyId",
                            "refundPolicyId",
                            "productGroupName",
                            "optionType",
                            "regularPrice",
                            "currentPrice",
                            "images",
                            "optionGroups",
                            "products",
                            "description",
                            "notice");

            // ImageRequest 필드
            JsonNode imageNode = node.get("images").get(0);
            assertThat(fieldNames(imageNode))
                    .containsExactlyInAnyOrder("imageType", "imageUrl", "sortOrder");

            // OptionGroupRequest 필드 (등록용 - ID 없음)
            JsonNode optionGroupNode = node.get("optionGroups").get(0);
            assertThat(fieldNames(optionGroupNode))
                    .containsExactlyInAnyOrder("optionGroupName", "sortOrder", "optionValues");

            // OptionValueRequest 필드 (등록용 - ID 없음)
            JsonNode optionValueNode = optionGroupNode.get("optionValues").get(0);
            assertThat(fieldNames(optionValueNode))
                    .containsExactlyInAnyOrder("optionValueName", "sortOrder");

            // ProductRequest 필드 (등록용 - productId 없음)
            JsonNode productNode = node.get("products").get(0);
            assertThat(fieldNames(productNode))
                    .containsExactlyInAnyOrder(
                            "skuCode",
                            "regularPrice",
                            "currentPrice",
                            "stockQuantity",
                            "sortOrder",
                            "selectedOptions");

            // SelectedOptionRequest 필드
            JsonNode selectedOptionNode = productNode.get("selectedOptions").get(0);
            assertThat(fieldNames(selectedOptionNode))
                    .containsExactlyInAnyOrder("optionGroupName", "optionValueName");

            // DescriptionRequest 필드
            JsonNode descNode = node.get("description");
            assertThat(fieldNames(descNode))
                    .containsExactlyInAnyOrder("content", "descriptionImages");

            // NoticeRequest 필드
            JsonNode noticeNode = node.get("notice");
            assertThat(fieldNames(noticeNode)).containsExactlyInAnyOrder("entries");

            // NoticeEntryRequest 필드
            JsonNode entryNode = noticeNode.get("entries").get(0);
            assertThat(fieldNames(entryNode))
                    .containsExactlyInAnyOrder("noticeFieldId", "fieldName", "fieldValue");
        }

        @Test
        @DisplayName("@JsonInclude(NON_NULL) - optional 필드 null 시 제외된다")
        void optionalNullFieldsShouldBeExcluded() {
            var product =
                    new SetofProductGroupRegistrationRequest.ProductRequest(
                            null, "SKU-001", 10000, 8000, 50, 1, List.of());
            var image =
                    new SetofProductGroupRegistrationRequest.ImageRequest(
                            "THUMBNAIL", "https://img.example.com/1.jpg", 1);
            var request =
                    new SetofProductGroupRegistrationRequest(
                            null,
                            null,
                            200L,
                            null,
                            null,
                            "테스트 상품",
                            null,
                            10000,
                            8000,
                            List.of(image),
                            null,
                            List.of(product),
                            null,
                            null);

            JsonNode node = toJsonNode(request);

            // null 필드는 JSON에 포함되지 않아야 함
            assertThat(node.has("productGroupId")).isFalse();
            assertThat(node.has("brandId")).isFalse();
            assertThat(node.has("shippingPolicyId")).isFalse();
            assertThat(node.has("refundPolicyId")).isFalse();
            assertThat(node.has("optionType")).isFalse();
            assertThat(node.has("optionGroups")).isFalse();
            assertThat(node.has("description")).isFalse();
            assertThat(node.has("notice")).isFalse();

            // non-null 필드는 포함
            assertThat(node.has("categoryId")).isTrue();
            assertThat(node.has("productGroupName")).isTrue();
            assertThat(node.has("regularPrice")).isTrue();
            assertThat(node.has("currentPrice")).isTrue();
        }
    }

    @Nested
    @DisplayName("SetofProductGroupUpdateRequest → UpdateProductGroupFullApiRequest")
    class ProductGroupUpdateContract {

        @Test
        @DisplayName("수정용 필드(ID 포함)가 서버 계약과 일치한다")
        void updateFieldsWithIdsShouldMatchServerContract() {
            var image =
                    new SetofProductGroupUpdateRequest.ImageRequest(
                            "THUMBNAIL", "https://img.example.com/1.jpg", 1);
            var optionValue = new SetofProductGroupUpdateRequest.OptionValueRequest(10L, "블랙", 1);
            var optionGroup =
                    new SetofProductGroupUpdateRequest.OptionGroupRequest(
                            20L, "색상", 1, List.of(optionValue));
            var selectedOption =
                    new SetofProductGroupUpdateRequest.SelectedOptionRequest("색상", "블랙");
            var product =
                    new SetofProductGroupUpdateRequest.ProductRequest(
                            30L, "SKU-001", 10000, 8000, 50, 1, List.of(selectedOption));
            var request =
                    new SetofProductGroupUpdateRequest(
                            "수정된 상품",
                            100L,
                            200L,
                            300L,
                            400L,
                            "SINGLE",
                            10000,
                            8000,
                            List.of(image),
                            List.of(optionGroup),
                            List.of(product),
                            null,
                            null);

            JsonNode node = toJsonNode(request);

            // 최상위 필드 (sellerId 없음 - path variable로 전달)
            assertThat(fieldNames(node))
                    .containsExactlyInAnyOrder(
                            "productGroupName",
                            "brandId",
                            "categoryId",
                            "shippingPolicyId",
                            "refundPolicyId",
                            "optionType",
                            "regularPrice",
                            "currentPrice",
                            "images",
                            "optionGroups",
                            "products");

            // OptionGroupRequest - 수정용이므로 sellerOptionGroupId 포함
            JsonNode optionGroupNode = node.get("optionGroups").get(0);
            assertThat(fieldNames(optionGroupNode))
                    .containsExactlyInAnyOrder(
                            "sellerOptionGroupId", "optionGroupName", "sortOrder", "optionValues");

            // OptionValueRequest - 수정용이므로 sellerOptionValueId 포함
            JsonNode optionValueNode = optionGroupNode.get("optionValues").get(0);
            assertThat(fieldNames(optionValueNode))
                    .containsExactlyInAnyOrder(
                            "sellerOptionValueId", "optionValueName", "sortOrder");

            // ProductRequest - 수정용이므로 productId 포함
            JsonNode productNode = node.get("products").get(0);
            assertThat(fieldNames(productNode))
                    .containsExactlyInAnyOrder(
                            "productId",
                            "skuCode",
                            "regularPrice",
                            "currentPrice",
                            "stockQuantity",
                            "sortOrder",
                            "selectedOptions");
        }
    }

    @Nested
    @DisplayName("SetofProductGroupBasicInfoUpdateRequest → UpdateProductGroupBasicInfoApiRequest")
    class ProductGroupBasicInfoUpdateContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var request =
                    new SetofProductGroupBasicInfoUpdateRequest("수정된 상품명", 100L, 200L, 300L, 400L);
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node))
                    .containsExactlyInAnyOrder(
                            "productGroupName",
                            "brandId",
                            "categoryId",
                            "shippingPolicyId",
                            "refundPolicyId");
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Content DTOs (독립 엔드포인트)
    // Server: POST/PUT /api/v2/admin/product-groups/{id}/description
    //         POST/PUT /api/v2/admin/product-groups/{id}/images
    //         POST/PUT /api/v2/admin/product-groups/{id}/notice
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SetofDescriptionRequest → DescriptionApiRequest")
    class DescriptionContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var descImage =
                    new SetofDescriptionRequest.DescriptionImageRequest(
                            "https://img.example.com/desc.jpg", 1);
            var request = new SetofDescriptionRequest("<p>상세 설명</p>", List.of(descImage));
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node)).containsExactlyInAnyOrder("content", "descriptionImages");

            JsonNode imageNode = node.get("descriptionImages").get(0);
            assertThat(fieldNames(imageNode)).containsExactlyInAnyOrder("imageUrl", "sortOrder");
        }

        @Test
        @DisplayName("@JsonInclude(NON_NULL) - descriptionImages가 null이면 제외")
        void nullDescriptionImagesShouldBeExcluded() {
            var request = new SetofDescriptionRequest("<p>설명</p>", null);
            JsonNode node = toJsonNode(request);

            assertThat(node.has("content")).isTrue();
            assertThat(node.has("descriptionImages")).isFalse();
        }
    }

    @Nested
    @DisplayName("SetofImagesRequest → 서버 이미지 API")
    class ImagesContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var image =
                    new SetofImagesRequest.ImageRequest(
                            "DETAIL", "https://img.example.com/2.jpg", 2);
            var request = new SetofImagesRequest(List.of(image));
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node)).containsExactlyInAnyOrder("images");

            JsonNode imageNode = node.get("images").get(0);
            assertThat(fieldNames(imageNode))
                    .containsExactlyInAnyOrder("imageType", "imageUrl", "sortOrder");
        }
    }

    @Nested
    @DisplayName("SetofNoticeRequest → NoticeApiRequest")
    class NoticeContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var entry = new SetofNoticeRequest.NoticeEntryRequest(1L, "제조국", "대한민국");
            var request = new SetofNoticeRequest(List.of(entry));
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node)).containsExactlyInAnyOrder("entries");

            JsonNode entryNode = node.get("entries").get(0);
            assertThat(fieldNames(entryNode))
                    .containsExactlyInAnyOrder("noticeFieldId", "fieldName", "fieldValue");
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Seller Sync DTOs
    // Server: POST /api/v2/admin/sellers
    //         PUT  /api/v2/admin/sellers/{sellerId}
    //         POST/PUT/DELETE /api/v2/admin/seller-addresses/sellers/{sellerId}
    //         POST/PUT /api/v2/admin/sellers/{sellerId}/shipping-policies
    //         POST/PUT /api/v2/admin/sellers/{sellerId}/refund-policies
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SetofSellerSyncRequest → 서버 셀러 API")
    class SellerSyncContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var request =
                    new SetofSellerSyncRequest(
                            1L,
                            "테스트셀러",
                            "테스트 표시명",
                            "https://logo.example.com/logo.png",
                            "셀러 설명",
                            true);
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node))
                    .containsExactlyInAnyOrder(
                            "sellerId",
                            "sellerName",
                            "displayName",
                            "logoUrl",
                            "description",
                            "active");
        }
    }

    @Nested
    @DisplayName("SetofSellerAddressSyncRequest → 서버 셀러 주소 API")
    class SellerAddressSyncContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var request =
                    new SetofSellerAddressSyncRequest(
                            1L, 10L, "RETURN", "반품지", "06234", "서울시 강남구 테헤란로 123", "4층", true);
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node))
                    .containsExactlyInAnyOrder(
                            "id",
                            "sellerId",
                            "addressType",
                            "addressName",
                            "zipCode",
                            "roadAddress",
                            "detailAddress",
                            "defaultAddress");
        }
    }

    @Nested
    @DisplayName("SetofShippingPolicySyncRequest → 서버 배송 정책 API")
    class ShippingPolicySyncContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var leadTime = new SetofShippingPolicySyncRequest.LeadTimeRequest(1, 3, null);
            var request =
                    new SetofShippingPolicySyncRequest(
                            "기본배송", true, "PAID", 3000, 50000, 3000, 5000, 3000, 6000, leadTime);
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node))
                    .containsExactlyInAnyOrder(
                            "policyName",
                            "defaultPolicy",
                            "shippingFeeType",
                            "baseFee",
                            "freeThreshold",
                            "jejuExtraFee",
                            "islandExtraFee",
                            "returnFee",
                            "exchangeFee",
                            "leadTime");
        }
    }

    @Nested
    @DisplayName("SetofRefundPolicySyncRequest → 서버 환불 정책 API")
    class RefundPolicySyncContract {

        @Test
        @DisplayName("JSON 필드명이 서버 계약과 일치한다")
        void fieldNamesShouldMatchServerContract() {
            var request =
                    new SetofRefundPolicySyncRequest(
                            1L,
                            10L,
                            "기본환불",
                            true,
                            true,
                            7,
                            7,
                            List.of("OPENED", "DAMAGED"),
                            false,
                            true,
                            3,
                            "추가 안내");
            JsonNode node = toJsonNode(request);

            assertThat(fieldNames(node))
                    .containsExactlyInAnyOrder(
                            "id",
                            "sellerId",
                            "policyName",
                            "defaultPolicy",
                            "active",
                            "returnPeriodDays",
                            "exchangePeriodDays",
                            "nonReturnableConditions",
                            "partialRefundEnabled",
                            "inspectionRequired",
                            "inspectionPeriodDays",
                            "additionalInfo");
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Response DTOs (역직렬화 검증)
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SetofProductGroupRegistrationResponse ← 서버 응답")
    class RegistrationResponseContract {

        @Test
        @DisplayName("서버 응답 JSON을 올바르게 역직렬화한다")
        void shouldDeserializeServerResponse() {
            String serverJson =
                    """
                    {"data": {"productGroupId": 12345}, "timestamp": "2026-03-15T22:44:17+09:00"}
                    """;

            SetofProductGroupRegistrationResponse response =
                    fromJson(serverJson, SetofProductGroupRegistrationResponse.class);

            assertThat(response.productGroupId()).isEqualTo(12345L);
        }

        @Test
        @DisplayName("data 래퍼 내부의 productGroupId를 편의 접근자로 추출한다")
        void shouldExtractProductGroupIdFromDataWrapper() {
            String wrappedJson =
                    """
                    {"data": {"productGroupId": 67890}, "requestId": "abc-123"}
                    """;

            SetofProductGroupRegistrationResponse response =
                    fromJson(wrappedJson, SetofProductGroupRegistrationResponse.class);

            assertThat(response.productGroupId()).isEqualTo(67890L);
            assertThat(response.data()).isNotNull();
            assertThat(response.data().productGroupId()).isEqualTo(67890L);
        }
    }

    @Nested
    @DisplayName("SetofSyncApiResponse ← 서버 응답")
    class SyncApiResponseContract {

        @Test
        @DisplayName("성공 응답을 올바르게 역직렬화한다")
        void shouldDeserializeSuccessResponse() {
            String serverJson =
                    """
                    {"success": true, "errorCode": null, "errorMessage": null}
                    """;

            SetofSyncApiResponse response = fromJson(serverJson, SetofSyncApiResponse.class);

            assertThat(response.success()).isTrue();
            assertThat(response.errorCode()).isNull();
            assertThat(response.errorMessage()).isNull();
        }

        @Test
        @DisplayName("에러 응답을 올바르게 역직렬화한다")
        void shouldDeserializeErrorResponse() {
            String serverJson =
                    """
{"success": false, "errorCode": "SELLER_NOT_FOUND", "errorMessage": "셀러를 찾을 수 없습니다"}
""";

            SetofSyncApiResponse response = fromJson(serverJson, SetofSyncApiResponse.class);

            assertThat(response.success()).isFalse();
            assertThat(response.errorCode()).isEqualTo("SELLER_NOT_FOUND");
            assertThat(response.errorMessage()).isEqualTo("셀러를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("알 수 없는 필드가 추가되면 역직렬화 실패 여부를 확인한다")
        void shouldDetectUnknownFieldHandling() {
            String serverJson =
                    """
{"success": true, "errorCode": null, "errorMessage": null, "timestamp": "2026-03-14T10:00:00Z"}
""";

            // Java Record는 기본적으로 알 수 없는 필드에 대해 실패한다.
            // 서버가 새 필드를 추가할 경우 클라이언트가 깨질 수 있으므로
            // @JsonIgnoreProperties(ignoreUnknown = true) 적용을 고려해야 한다.
            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> fromJson(serverJson, SetofSyncApiResponse.class),
                    "Record DTO는 알 수 없는 필드에 대해 기본적으로 실패한다 - "
                            + "@JsonIgnoreProperties(ignoreUnknown = true) 적용을 고려하세요");
        }
    }

    // ════════════════════════════════════════════════════════════════
    // 타입 호환성 검증
    // ════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("타입 호환성")
    class TypeCompatibility {

        @Test
        @DisplayName("int/Integer 필드가 JSON number로 직렬화된다")
        void intFieldsShouldSerializeAsNumbers() {
            var request = new SetofProductPriceUpdateRequest(10000, 8000);
            JsonNode node = toJsonNode(request);

            assertThat(node.get("regularPrice").isNumber()).isTrue();
            assertThat(node.get("currentPrice").isNumber()).isTrue();
        }

        @Test
        @DisplayName("Long 필드가 JSON number로 직렬화된다")
        void longFieldsShouldSerializeAsNumbers() {
            var request =
                    new SetofProductGroupBasicInfoUpdateRequest("테스트", 100L, 200L, 300L, 400L);
            JsonNode node = toJsonNode(request);

            assertThat(node.get("brandId").isNumber()).isTrue();
            assertThat(node.get("categoryId").isNumber()).isTrue();
        }

        @Test
        @DisplayName("boolean 필드가 JSON boolean으로 직렬화된다")
        void booleanFieldsShouldSerializeAsBooleans() {
            var request = new SetofSellerSyncRequest(1L, "셀러", "표시명", "logo.png", "설명", true);
            JsonNode node = toJsonNode(request);

            assertThat(node.get("active").isBoolean()).isTrue();
            assertThat(node.get("active").asBoolean()).isTrue();
        }

        @Test
        @DisplayName("List<String> 필드가 JSON string array로 직렬화된다")
        void stringListShouldSerializeAsStringArray() {
            var request =
                    new SetofRefundPolicySyncRequest(
                            1L,
                            10L,
                            "기본환불",
                            true,
                            true,
                            7,
                            7,
                            List.of("OPENED", "DAMAGED"),
                            false,
                            true,
                            3,
                            "추가 안내");
            JsonNode node = toJsonNode(request);

            JsonNode conditions = node.get("nonReturnableConditions");
            assertThat(conditions.isArray()).isTrue();
            assertThat(conditions.get(0).isTextual()).isTrue();
            assertThat(conditions.get(0).asText()).isEqualTo("OPENED");
            assertThat(conditions.get(1).asText()).isEqualTo("DAMAGED");
        }
    }
}
