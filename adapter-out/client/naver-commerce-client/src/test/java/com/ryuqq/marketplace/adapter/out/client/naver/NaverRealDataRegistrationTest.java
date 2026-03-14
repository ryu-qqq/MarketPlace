package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImage;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryName;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldName;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeEntryId;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Stage DB의 실제 데이터를 기반으로 NaverCommerceProductMapper를 통해 변환 후 네이버 API에 등록하는 통합 테스트.
 *
 * <p>실행 전 필요 사항:
 *
 * <ul>
 *   <li>Stage DB 포트포워딩 (port 13308)
 *   <li>환경변수: NAVER_CLIENT_ID, NAVER_CLIENT_SECRET
 * </ul>
 *
 * <p>실행: {@code ./gradlew :adapter-out:client:naver-commerce-client:externalIntegrationTest --tests
 * "*NaverRealDataRegistrationTest*"}
 */
@Tag("external-integration")
@DisplayName("네이버 실제 데이터 등록 테스트 (Stage DB → Mapper → Naver API)")
class NaverRealDataRegistrationTest {

    private static final long PRODUCT_GROUP_ID = 30825L;
    private static final long NAVER_CATEGORY_ID = 50000973L; // 담요/이불 (기존 등록 성공 카테고리)
    private static final Long NAVER_BRAND_ID = null; // 브랜드 매핑 없음

    private final NaverCommerceProductMapper mapper = new NaverCommerceProductMapper();
    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("Stage DB 데이터 → Mapper 변환 → JSON 출력 (API 호출 없이 변환 결과만 확인)")
    void convertOnly_PrintsRequestJson() throws Exception {
        ProductGroupDetailBundle bundle = buildBundleFromStageData();
        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(bundle, NAVER_CATEGORY_ID, NAVER_BRAND_ID);

        String json = objectMapper.writeValueAsString(request);
        System.out.println("=== 변환된 네이버 등록 요청 JSON ===");
        System.out.println(json);

        // 기본 검증
        assertThat(request.originProduct()).isNotNull();
        assertThat(request.originProduct().name()).isEqualTo("네이버 자동등록 테스트 여성 티셔츠");
        assertThat(request.originProduct().salePrice()).isEqualTo(12000);
        assertThat(request.originProduct().images().representativeImage()).isNotNull();
        assertThat(request.originProduct().deliveryInfo()).isNotNull();
        assertThat(request.originProduct().deliveryInfo().deliveryFee().deliveryFeeType())
                .isEqualTo("CONDITIONAL_FREE");
        assertThat(request.originProduct().detailAttribute().productInfoProvidedNotice())
                .isNotNull();

        // AfterServiceInfo - 실제 seller_cs 데이터 반영 확인
        assertThat(
                        request.originProduct()
                                .detailAttribute()
                                .afterServiceInfo()
                                .afterServiceTelephoneNumber())
                .isEqualTo("01051304844");

        // OriginAreaInfo - notice에서 made_in 추출 확인
        assertThat(request.originProduct().detailAttribute().originAreaInfo().content())
                .isEqualTo("중국");

        System.out.println("[PASS] 변환 검증 완료");
    }

    @Test
    @DisplayName("Stage DB 데이터 → 이미지 업로드 → Mapper 변환 → 네이버 API 실제 등록")
    void registerToNaverApi() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, new ObjectMapper());
        System.out.println("[OK] 토큰 발급 성공");

        // 1. 이미지 업로드
        ProductGroupDetailBundle bundle = buildBundleFromStageData();
        List<String> originUrls =
                bundle.group().images().stream()
                        .map(
                                img -> {
                                    String uploaded = img.uploadedUrlValue();
                                    return uploaded != null ? uploaded : img.originUrlValue();
                                })
                        .toList();

        System.out.println("=== 이미지 업로드 시작 ===");
        ResolvedExternalImages resolvedImages = uploadImagesToNaver(token, bundle);
        System.out.println("[OK] 이미지 업로드 완료");
        System.out.println("  썸네일: " + resolvedImages.thumbnailUrl());
        System.out.println("  상세: " + resolvedImages.detailUrls());

        // 2. Mapper 변환 (업로드된 pstatic URL 사용)
        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(
                        bundle, NAVER_CATEGORY_ID, NAVER_BRAND_ID, resolvedImages);

        String requestJson = objectMapper.writeValueAsString(request);
        System.out.println("=== 등록 요청 JSON ===");
        System.out.println(requestJson);

        // 3. 상품 등록 API 호출
        HttpRequest apiRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(NaverAuthHelper.BASE_URL + "/v2/products"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                        .build();

        HttpResponse<String> response =
                httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("=== 네이버 API 응답 ===");
        System.out.println("Status: " + response.statusCode());
        try {
            Object responseObj = objectMapper.readValue(response.body(), Object.class);
            System.out.println(objectMapper.writeValueAsString(responseObj));
        } catch (Exception e) {
            System.out.println(response.body());
        }

        if (response.statusCode() == 200) {
            System.out.println("[PASS] 상품 등록 성공!");
        } else {
            System.out.println("[FAIL] 상품 등록 실패: HTTP " + response.statusCode());
        }

        assertThat(response.statusCode()).as("API 응답 확인 - 상세 에러는 위 로그 참조").isGreaterThan(0);
    }

    /**
     * 상품 이미지를 네이버 이미지 호스팅 API에 업로드하고 ResolvedExternalImages로 반환.
     *
     * <p>API: POST /v1/product-images/upload (multipart/form-data)
     */
    private ResolvedExternalImages uploadImagesToNaver(
            String token, ProductGroupDetailBundle bundle) throws Exception {
        List<ResolvedExternalImage> resolvedList = new ArrayList<>();

        for (var image : bundle.group().images()) {
            String originUrl =
                    image.uploadedUrlValue() != null
                            ? image.uploadedUrlValue()
                            : image.originUrlValue();

            // 이미지 다운로드
            byte[] imageBytes = downloadImage(originUrl);
            String filename = extractFilename(originUrl);
            String contentType = guessContentType(filename);

            System.out.println("  다운로드 완료: " + filename + " (" + imageBytes.length + " bytes)");

            // 네이버 이미지 호스팅에 multipart 업로드
            String naverUrl = uploadMultipart(token, imageBytes, filename, contentType);
            System.out.println("  업로드 완료: " + naverUrl);

            resolvedList.add(
                    new ResolvedExternalImage(
                            naverUrl,
                            image.isThumbnail() ? ImageType.THUMBNAIL : ImageType.DETAIL,
                            image.sortOrder()));
        }

        return new ResolvedExternalImages(resolvedList);
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) URI.create(imageUrl).toURL().openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(30_000);
        try (InputStream in = conn.getInputStream()) {
            return in.readAllBytes();
        } finally {
            conn.disconnect();
        }
    }

    private String uploadMultipart(
            String token, byte[] imageBytes, String filename, String contentType) throws Exception {
        String boundary = "----NaverTestBoundary" + System.currentTimeMillis();

        HttpURLConnection conn =
                (HttpURLConnection)
                        URI.create(NaverAuthHelper.BASE_URL + "/v1/product-images/upload")
                                .toURL()
                                .openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(30_000);

        try (OutputStream out = conn.getOutputStream()) {
            // multipart body
            String partHeader =
                    "--"
                            + boundary
                            + "\r\n"
                            + "Content-Disposition: form-data; name=\"imageFiles\"; filename=\""
                            + filename
                            + "\"\r\n"
                            + "Content-Type: "
                            + contentType
                            + "\r\n\r\n";
            out.write(partHeader.getBytes(StandardCharsets.UTF_8));
            out.write(imageBytes);
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));

            String ending = "--" + boundary + "--\r\n";
            out.write(ending.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }

        int status = conn.getResponseCode();
        String responseBody;
        try (InputStream in = status >= 400 ? conn.getErrorStream() : conn.getInputStream()) {
            responseBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        conn.disconnect();

        if (status != 200) {
            throw new RuntimeException("이미지 업로드 실패: HTTP " + status + " " + responseBody);
        }

        // {"images":[{"url":"https://shop-phinf.pstatic.net/..."}]}
        @SuppressWarnings("unchecked")
        Map<String, Object> parsed = objectMapper.readValue(responseBody, Map.class);
        @SuppressWarnings("unchecked")
        List<Map<String, String>> images = (List<Map<String, String>>) parsed.get("images");
        return images.get(0).get("url");
    }

    private String extractFilename(String imageUrl) {
        String path = URI.create(imageUrl).getPath();
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : "image.jpg";
    }

    private String guessContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".bmp")) {
            return "image/bmp";
        }
        return "image/jpeg";
    }

    /**
     * Stage DB product_group 30825 데이터를 하드코딩으로 구성.
     *
     * <p>실제 DB 조회 결과를 그대로 반영:
     *
     * <ul>
     *   <li>product_groups: id=30825, seller_id=25, brand_id=421, category_id=52, option_type=NONE
     *   <li>products: id=594, regular=15000, current=12000, sale=12000, stock=50
     *   <li>images: id=122675, THUMBNAIL, origin/uploaded 동일 URL
     *   <li>notice: category_id=11 (DIGITAL), 8 entries including made_in=중국
     *   <li>seller_cs: phone=01051304844, email=eys1@trexi.co.kr
     *   <li>shipping: CONDITIONAL_FREE, base=3000, threshold=50000
     *   <li>description: PUBLISHED, CDN path 있음
     * </ul>
     */
    private ProductGroupDetailBundle buildBundleFromStageData() {
        Instant now = Instant.parse("2026-03-13T07:54:51Z");

        // 1. ProductGroup (NONE option, no option groups)
        ProductGroup group =
                ProductGroup.reconstitute(
                        ProductGroupId.of(PRODUCT_GROUP_ID),
                        SellerId.of(25L),
                        BrandId.of(421L),
                        CategoryId.of(52L),
                        ShippingPolicyId.of(36L),
                        RefundPolicyId.of(35L),
                        ProductGroupName.of("네이버 자동등록 테스트 여성 티셔츠"),
                        OptionType.NONE,
                        ProductGroupStatus.ACTIVE,
                        List.of(
                                ProductGroupImage.reconstitute(
                                        ProductGroupImageId.of(122675L),
                                        ProductGroupId.of(PRODUCT_GROUP_ID),
                                        ImageUrl.of(
                                                "https://stage-cdn.set-of.com/public/2026/03/019ce630-e584-7c2b-8b1e-1e38864f7f4e.jpg"),
                                        ImageUrl.of(
                                                "https://stage-cdn.set-of.com/public/2026/03/019ce630-e584-7c2b-8b1e-1e38864f7f4e.jpg"),
                                        ImageType.THUMBNAIL,
                                        0,
                                        DeletionStatus.active())),
                        List.of(), // no option groups
                        now,
                        now);

        // 2. Products (1 ACTIVE product)
        List<Product> products =
                List.of(
                        Product.reconstitute(
                                ProductId.of(594L),
                                ProductGroupId.of(PRODUCT_GROUP_ID),
                                SkuCode.of(null),
                                Money.of(15000),
                                Money.of(12000),
                                Money.of(12000),
                                20,
                                50,
                                ProductStatus.ACTIVE,
                                1,
                                List.of(),
                                now,
                                now));

        // 3. Notice (CLOTHING category → WEAR type)
        List<NoticeField> noticeFields =
                List.of(
                        NoticeField.reconstitute(
                                NoticeFieldId.of(1L),
                                NoticeFieldCode.of("material"),
                                NoticeFieldName.of("소재"),
                                true,
                                1),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(2L),
                                NoticeFieldCode.of("color"),
                                NoticeFieldName.of("색상"),
                                true,
                                2),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(3L),
                                NoticeFieldCode.of("size"),
                                NoticeFieldName.of("사이즈"),
                                true,
                                3),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(4L),
                                NoticeFieldCode.of("manufacturer"),
                                NoticeFieldName.of("제조자/수입자"),
                                true,
                                4),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(5L),
                                NoticeFieldCode.of("made_in"),
                                NoticeFieldName.of("제조국"),
                                true,
                                5),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(6L),
                                NoticeFieldCode.of("caution"),
                                NoticeFieldName.of("세탁방법 및 취급시 주의사항"),
                                true,
                                6),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(7L),
                                NoticeFieldCode.of("quality_assurance"),
                                NoticeFieldName.of("품질보증기준"),
                                true,
                                7));

        NoticeCategory noticeCategory =
                NoticeCategory.reconstitute(
                        NoticeCategoryId.of(1L),
                        NoticeCategoryCode.of("CLOTHING"),
                        NoticeCategoryName.of("의류", "Clothing"),
                        CategoryGroup.CLOTHING,
                        true,
                        noticeFields,
                        Instant.parse("2026-02-11T06:52:38Z"),
                        Instant.parse("2026-02-11T06:52:38Z"));

        List<ProductNoticeEntry> noticeEntries =
                List.of(
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(212891L),
                                ProductNoticeId.of(30381L),
                                NoticeFieldId.of(1L),
                                NoticeFieldValue.of("면 100%")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(212892L),
                                ProductNoticeId.of(30381L),
                                NoticeFieldId.of(2L),
                                NoticeFieldValue.of("블랙")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(212893L),
                                ProductNoticeId.of(30381L),
                                NoticeFieldId.of(3L),
                                NoticeFieldValue.of("FREE")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(212894L),
                                ProductNoticeId.of(30381L),
                                NoticeFieldId.of(4L),
                                NoticeFieldValue.of("테스트 제조사")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(212895L),
                                ProductNoticeId.of(30381L),
                                NoticeFieldId.of(5L),
                                NoticeFieldValue.of("중국")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(212896L),
                                ProductNoticeId.of(30381L),
                                NoticeFieldId.of(6L),
                                NoticeFieldValue.of("손세탁 권장")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(212897L),
                                ProductNoticeId.of(30381L),
                                NoticeFieldId.of(7L),
                                NoticeFieldValue.of("구입 후 1년간 품질보증")));

        ProductNotice productNotice =
                ProductNotice.reconstitute(
                        ProductNoticeId.of(30381L),
                        ProductGroupId.of(PRODUCT_GROUP_ID),
                        NoticeCategoryId.of(1L),
                        noticeEntries,
                        Instant.parse("2026-03-13T03:54:52Z"),
                        now);

        // 4. SellerCs
        SellerCs sellerCs =
                SellerCs.reconstitute(
                        SellerCsId.of(92L),
                        SellerId.of(25L),
                        CsContact.of("01051304844", null, "eys1@trexi.co.kr"),
                        OperatingHours.of(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                        "MON,TUE,WED,THU,FRI",
                        null,
                        Instant.parse("2024-01-18T19:15:55Z"),
                        Instant.parse("2024-01-18T19:15:55Z"));

        // 5. Description
        ProductGroupDescription description =
                ProductGroupDescription.reconstitute(
                        ProductGroupDescriptionId.of(30623L),
                        ProductGroupId.of(PRODUCT_GROUP_ID),
                        DescriptionHtml.of(
                                "<p>네이버 커머스 자동 등록 테스트 상품입니다. 배포 후 NAVER/CREATE 전략으로 자동 등록되는지"
                                        + " 확인용.</p>"),
                        CdnPath.of(
                                "https://stage-cdn.set-of.com/public/2026/03/019ce630-f948-7289-bd93-853d026fc083.html"),
                        DescriptionPublishStatus.PUBLISHED,
                        List.of(),
                        Instant.parse("2026-03-13T03:54:52Z"),
                        now);

        // 6. ShippingPolicyResult (from shipping_policies table)
        ShippingPolicyResult shippingPolicy =
                new ShippingPolicyResult(
                        36L,
                        25L,
                        "기본 배송정책",
                        true,
                        true,
                        "CONDITIONAL_FREE",
                        "조건부 무료배송",
                        3000L,
                        50000L,
                        3000L,
                        5000L,
                        3000L,
                        6000L,
                        1,
                        3,
                        LocalTime.of(14, 0),
                        Instant.parse("2024-01-18T19:15:55Z"),
                        Instant.parse("2024-01-18T19:15:55Z"));

        // 7. QueryResult
        ProductGroupDetailCompositeQueryResult queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        PRODUCT_GROUP_ID,
                        25L,
                        "테스트 셀러",
                        421L,
                        "테스트 브랜드",
                        52L,
                        "테스트 카테고리",
                        "패션의류 > 여성의류 > 티셔츠",
                        "52",
                        "네이버 자동등록 테스트 여성 티셔츠",
                        "NONE",
                        "ACTIVE",
                        now,
                        now,
                        shippingPolicy,
                        null // refundPolicy
                        );

        return new ProductGroupDetailBundle(
                queryResult,
                group,
                products,
                Optional.of(description),
                Optional.of(productNotice),
                Optional.of(noticeCategory),
                Optional.of(sellerCs),
                Map.of());
    }
}
