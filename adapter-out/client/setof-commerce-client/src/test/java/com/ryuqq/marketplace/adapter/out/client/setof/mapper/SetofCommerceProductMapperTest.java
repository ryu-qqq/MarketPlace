package com.ryuqq.marketplace.adapter.out.client.setof.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofNoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SetofCommerceProductMapper 단위 테스트")
class SetofCommerceProductMapperTest {

    private final SetofCommerceProductMapper sut = new SetofCommerceProductMapper();

    // ── 헬퍼 메서드 ──

    private ProductGroupDetailCompositeQueryResult createQueryResult(
            ShippingPolicyResult shippingPolicy, RefundPolicyResult refundPolicy) {
        return new ProductGroupDetailCompositeQueryResult(
                1L,
                1L,
                "테스트셀러",
                100L,
                "테스트브랜드",
                200L,
                "테스트카테고리",
                "상의 > 긴팔",
                "1/200",
                "테스트 상품 그룹",
                "NONE",
                "ACTIVE",
                Instant.now(),
                Instant.now(),
                shippingPolicy,
                refundPolicy);
    }

    private ProductGroupDetailBundle createBundle(
            ProductGroup group,
            List<Product> products,
            Optional<ProductGroupDescription> description,
            Optional<ProductNotice> notice) {
        return new ProductGroupDetailBundle(
                createQueryResult(null, null),
                group,
                products,
                description,
                notice,
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }

    private ProductGroupDetailBundle createBundleWithPolicies(
            ShippingPolicyResult shippingPolicy, RefundPolicyResult refundPolicy) {
        return new ProductGroupDetailBundle(
                createQueryResult(shippingPolicy, refundPolicy),
                ProductGroupFixtures.activeProductGroup(),
                List.of(ProductFixtures.activeProduct()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }

    @Nested
    @DisplayName("toDeleteRequest()")
    class ToDeleteRequestTest {

        @Test
        @DisplayName("모든 필드가 null 또는 0으로 반환된다")
        void returnsNullFields() {
            SetofProductGroupUpdateRequest result = sut.toDeleteRequest();

            assertThat(result.productGroupName()).isNull();
            assertThat(result.brandId()).isNull();
            assertThat(result.categoryId()).isNull();
            assertThat(result.shippingPolicyId()).isNull();
            assertThat(result.refundPolicyId()).isNull();
            assertThat(result.optionType()).isNull();
            assertThat(result.regularPrice()).isZero();
            assertThat(result.currentPrice()).isZero();
            assertThat(result.images()).isNull();
            assertThat(result.optionGroups()).isNull();
            assertThat(result.products()).isNull();
            assertThat(result.description()).isNull();
            assertThat(result.notice()).isNull();
        }
    }

    @Nested
    @DisplayName("toNoticeRequest()")
    class ToNoticeRequestTest {

        @Test
        @DisplayName("notice가 null이면 null 반환")
        void nullNoticeReturnsNull() {
            SetofNoticeRequest result = sut.toNoticeRequest(null, null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("notice가 null이고 fieldNameMap이 null이면 null 반환")
        void bothNullReturnsNull() {
            SetofNoticeRequest result = sut.toNoticeRequest(null, null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("notice가 있으면 entries를 변환한다")
        void validNoticeReturnsEntries() {
            ProductNotice notice = ProductNoticeFixtures.newProductNotice();

            SetofNoticeRequest result = sut.toNoticeRequest(notice, null);

            assertThat(result).isNotNull();
            assertThat(result.entries()).hasSize(3);
            // fieldNameMap이 null이면 빈 문자열로 대체
            assertThat(result.entries().get(0).fieldName()).isEmpty();
        }

        @Test
        @DisplayName("fieldNameMap이 있으면 fieldName을 매핑한다")
        void fieldNameMapApplied() {
            ProductNotice notice = ProductNoticeFixtures.newProductNotice();
            Map<Long, String> fieldNameMap = Map.of(100L, "제조국", 101L, "제조사");

            SetofNoticeRequest result = sut.toNoticeRequest(notice, fieldNameMap);

            assertThat(result).isNotNull();
            assertThat(result.entries()).hasSize(3);
            // fieldNameMap에 있는 필드는 매핑된 이름 사용
            var entry100 =
                    result.entries().stream()
                            .filter(e -> e.noticeFieldId().equals(100L))
                            .findFirst()
                            .orElseThrow();
            assertThat(entry100.fieldName()).isEqualTo("제조국");
        }
    }

    @Nested
    @DisplayName("toDescriptionRequest()")
    class ToDescriptionRequestTest {

        @Test
        @DisplayName("description이 없으면 null 반환")
        void emptyDescriptionReturnsNull() {
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(ProductFixtures.activeProduct()),
                            Optional.empty(),
                            Optional.empty());

            SetofDescriptionRequest result = sut.toDescriptionRequest(bundle);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("description이 있으면 content를 변환한다")
        void validDescriptionReturnsContent() {
            var description = ProductGroupFixtures.defaultProductGroupDescription();
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(ProductFixtures.activeProduct()),
                            Optional.of(description),
                            Optional.empty());

            SetofDescriptionRequest result = sut.toDescriptionRequest(bundle);

            assertThat(result).isNotNull();
            assertThat(result.content()).isEqualTo(description.contentValue());
        }
    }

    @Nested
    @DisplayName("toImagesRequest()")
    class ToImagesRequestTest {

        @Test
        @DisplayName("이미지 목록을 변환한다")
        void convertsImages() {
            List<ProductGroupImage> images =
                    List.of(
                            ProductGroupFixtures.thumbnailImage(),
                            ProductGroupFixtures.detailImage(1));

            SetofImagesRequest result = sut.toImagesRequest(images);

            assertThat(result).isNotNull();
            assertThat(result.images()).hasSize(2);
            assertThat(result.images().get(0).imageType()).isEqualTo("THUMBNAIL");
            assertThat(result.images().get(1).imageType()).isEqualTo("DETAIL");
        }

        @Test
        @DisplayName("업로드된 이미지는 업로드 URL을 사용한다")
        void usesUploadedUrlWhenAvailable() {
            ProductGroupImage uploadedImage = ProductGroupFixtures.uploadedImage();

            SetofImagesRequest result = sut.toImagesRequest(List.of(uploadedImage));

            assertThat(result.images().get(0).imageUrl()).contains("uploaded");
        }

        @Test
        @DisplayName("업로드 안 된 이미지는 원본 URL을 사용한다")
        void usesOriginUrlWhenNotUploaded() {
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();

            SetofImagesRequest result = sut.toImagesRequest(List.of(image));

            assertThat(result.images().get(0).imageUrl()).isEqualTo(image.originUrlValue());
        }

        @Test
        @DisplayName("빈 이미지 목록이면 빈 리스트 반환")
        void emptyImagesReturnsEmptyList() {
            SetofImagesRequest result = sut.toImagesRequest(List.of());

            assertThat(result.images()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toBasicInfoUpdateRequest()")
    class ToBasicInfoUpdateRequestTest {

        @Test
        @DisplayName("기본 정보 수정 요청을 생성한다")
        void createsBasicInfoRequest() {
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(ProductFixtures.activeProduct()),
                            Optional.empty(),
                            Optional.empty());

            SetofProductGroupBasicInfoUpdateRequest result =
                    sut.toBasicInfoUpdateRequest(bundle, 500L, 600L);

            assertThat(result.productGroupName()).isEqualTo("테스트 상품 그룹");
            assertThat(result.brandId()).isEqualTo(600L);
            assertThat(result.categoryId()).isEqualTo(500L);
        }

        @Test
        @DisplayName("shippingPolicy가 있으면 policyId가 설정된다")
        void withShippingPolicy() {
            var shippingPolicy =
                    new ShippingPolicyResult(
                            10L,
                            1L,
                            "기본배송",
                            true,
                            true,
                            "PAID",
                            "유료배송",
                            3000L,
                            50000L,
                            3000L,
                            5000L,
                            3000L,
                            6000L,
                            1,
                            3,
                            null,
                            Instant.now(),
                            Instant.now());
            var bundle = createBundleWithPolicies(shippingPolicy, null);

            SetofProductGroupBasicInfoUpdateRequest result =
                    sut.toBasicInfoUpdateRequest(bundle, 500L, 600L);

            assertThat(result.shippingPolicyId()).isEqualTo(10L);
            assertThat(result.refundPolicyId()).isNull();
        }

        @Test
        @DisplayName("refundPolicy가 있으면 policyId가 설정된다")
        void withRefundPolicy() {
            var refundPolicy =
                    new RefundPolicyResult(
                            20L,
                            1L,
                            "기본환불",
                            true,
                            true,
                            7,
                            7,
                            List.of(),
                            false,
                            false,
                            0,
                            "환불 규정 상세",
                            Instant.now(),
                            Instant.now());
            var bundle = createBundleWithPolicies(null, refundPolicy);

            SetofProductGroupBasicInfoUpdateRequest result =
                    sut.toBasicInfoUpdateRequest(bundle, 500L, 600L);

            assertThat(result.shippingPolicyId()).isNull();
            assertThat(result.refundPolicyId()).isEqualTo(20L);
        }
    }

    @Nested
    @DisplayName("toProductsUpdateRequest()")
    class ToProductsUpdateRequestTest {

        @Test
        @DisplayName("옵션 없는 상품 목록을 변환한다")
        void convertsProductsWithoutOptions() {
            var products = List.of(ProductFixtures.activeProduct());

            SetofProductsUpdateRequest result =
                    sut.toProductsUpdateRequest(products, List.of(), null);

            assertThat(result.products()).hasSize(1);
            assertThat(result.optionGroups()).isEmpty();
            var product = result.products().get(0);
            assertThat(product.regularPrice()).isEqualTo(ProductFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(product.currentPrice()).isEqualTo(ProductFixtures.DEFAULT_CURRENT_PRICE);
            assertThat(product.stockQuantity()).isEqualTo(ProductFixtures.DEFAULT_STOCK_QUANTITY);
        }

        @Test
        @DisplayName("옵션이 있는 상품 그룹을 변환한다")
        void convertsProductsWithOptions() {
            var group = ProductGroupFixtures.newProductGroupWithSingleOption();
            var products = List.of(ProductFixtures.activeProduct());

            SetofProductsUpdateRequest result =
                    sut.toProductsUpdateRequest(products, group.sellerOptionGroups(), null);

            assertThat(result.optionGroups()).hasSize(1);
            assertThat(result.optionGroups().get(0).optionGroupName()).isEqualTo("색상");
        }
    }

    @Nested
    @DisplayName("toRegistrationRequest()")
    class ToRegistrationRequestTest {

        @Test
        @DisplayName("등록 요청을 생성한다")
        void createsRegistrationRequest() {
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(ProductFixtures.activeProduct()),
                            Optional.empty(),
                            Optional.empty());

            SetofProductGroupRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, 500L, 600L, 99L);

            assertThat(result).isNotNull();
            assertThat(result.productGroupName()).isEqualTo("테스트 상품 그룹");
            assertThat(result.brandId()).isEqualTo(600L);
            assertThat(result.categoryId()).isEqualTo(500L);
            assertThat(result.regularPrice()).isEqualTo(ProductFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(result.currentPrice()).isEqualTo(ProductFixtures.DEFAULT_CURRENT_PRICE);
        }

        @Test
        @DisplayName("여러 상품이 있으면 최소 가격이 대표 가격이 된다")
        void minPriceAsRepresentativePrice() {
            var product1 = ProductFixtures.activeProduct(1L);
            var product2 =
                    ProductFixtures.soldOutProduct(); // regularPrice=100000, currentPrice=80000
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(product1, product2),
                            Optional.empty(),
                            Optional.empty());

            SetofProductGroupRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, 500L, 600L, 99L);

            // 두 상품 모두 같은 가격이므로 그대로
            assertThat(result.regularPrice()).isEqualTo(ProductFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(result.currentPrice()).isEqualTo(ProductFixtures.DEFAULT_CURRENT_PRICE);
        }

        @Test
        @DisplayName("description이 있으면 포함된다")
        void withDescription() {
            var desc = ProductGroupFixtures.defaultProductGroupDescription();
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(ProductFixtures.activeProduct()),
                            Optional.of(desc),
                            Optional.empty());

            SetofProductGroupRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, 500L, 600L, 99L);

            assertThat(result.description()).isNotNull();
            assertThat(result.description().content()).isEqualTo(desc.contentValue());
        }

        @Test
        @DisplayName("notice가 있으면 포함된다")
        void withNotice() {
            var notice = ProductNoticeFixtures.newProductNotice();
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(ProductFixtures.activeProduct()),
                            Optional.empty(),
                            Optional.of(notice));

            SetofProductGroupRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, 500L, 600L, 99L);

            assertThat(result.notice()).isNotNull();
            assertThat(result.notice().entries()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("toUpdateRequest()")
    class ToUpdateRequestTest {

        @Test
        @DisplayName("수정 요청을 생성한다")
        void createsUpdateRequest() {
            var bundle =
                    createBundle(
                            ProductGroupFixtures.activeProductGroup(),
                            List.of(ProductFixtures.activeProduct()),
                            Optional.empty(),
                            Optional.empty());

            SetofProductGroupUpdateRequest result = sut.toUpdateRequest(bundle, 500L, 600L, null);

            assertThat(result).isNotNull();
            assertThat(result.productGroupName()).isEqualTo("테스트 상품 그룹");
            assertThat(result.brandId()).isEqualTo(600L);
            assertThat(result.categoryId()).isEqualTo(500L);
            assertThat(result.regularPrice()).isEqualTo(ProductFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(result.currentPrice()).isEqualTo(ProductFixtures.DEFAULT_CURRENT_PRICE);
        }

        @Test
        @DisplayName("이미지, 옵션, 상품이 모두 포함된다")
        void includesAllFields() {
            var group = ProductGroupFixtures.newProductGroupWithSingleOption();
            var bundle =
                    createBundle(
                            group,
                            List.of(ProductFixtures.activeProduct()),
                            Optional.of(ProductGroupFixtures.defaultProductGroupDescription()),
                            Optional.of(ProductNoticeFixtures.newProductNotice()));

            SetofProductGroupUpdateRequest result = sut.toUpdateRequest(bundle, 500L, 600L, null);

            assertThat(result.images()).isNotEmpty();
            assertThat(result.optionGroups()).hasSize(1);
            assertThat(result.products()).hasSize(1);
            assertThat(result.description()).isNotNull();
            assertThat(result.notice()).isNotNull();
        }
    }
}
