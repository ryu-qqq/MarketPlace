package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DetailAttribute;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OriginProduct;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.ProductInfoProvidedNotice;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryName;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldName;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverCommerceProductMapper 단위 테스트")
class NaverCommerceProductMapperTest {

    private final NaverCommerceProductMapper sut = new NaverCommerceProductMapper();

    private static final Long EXTERNAL_CATEGORY_ID = 50002322L;
    private static final Long EXTERNAL_BRAND_ID = 12345L;

    // ── 헬퍼 메서드 ──

    private ProductGroupDetailCompositeQueryResult createQueryResult(
            ShippingPolicyResult shippingPolicy) {
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
                "OPTION_ONE",
                "ACTIVE",
                Instant.now(),
                Instant.now(),
                shippingPolicy,
                null);
    }

    private ShippingPolicyResult freeShippingPolicy() {
        return new ShippingPolicyResult(
                1L,
                1L,
                "무료배송",
                true,
                true,
                "FREE",
                "무료",
                0L,
                null,
                null,
                null,
                3000L,
                5000L,
                1,
                3,
                null,
                Instant.now(),
                Instant.now());
    }

    private ShippingPolicyResult paidShippingPolicy() {
        return new ShippingPolicyResult(
                2L,
                1L,
                "유료배송",
                false,
                true,
                "PAID",
                "유료",
                3000L,
                null,
                3000L,
                5000L,
                3000L,
                5000L,
                1,
                3,
                null,
                Instant.now(),
                Instant.now());
    }

    private ShippingPolicyResult conditionalFreeShippingPolicy() {
        return new ShippingPolicyResult(
                3L,
                1L,
                "조건부무료",
                false,
                true,
                "CONDITIONAL_FREE",
                "조건부무료",
                3000L,
                50000L,
                3000L,
                5000L,
                3000L,
                5000L,
                1,
                3,
                null,
                Instant.now(),
                Instant.now());
    }

    private ProductGroupDetailBundle createBundle(
            ShippingPolicyResult shipping,
            Optional<ProductNotice> notice,
            Optional<NoticeCategory> noticeCategory) {
        return createBundle(shipping, notice, noticeCategory, Optional.empty());
    }

    private ProductGroupDetailBundle createBundle(
            ShippingPolicyResult shipping,
            Optional<ProductNotice> notice,
            Optional<NoticeCategory> noticeCategory,
            Optional<SellerCs> sellerCs) {
        ProductGroup group = ProductGroupFixtures.activeProductGroup();
        List<Product> products = List.of(ProductFixtures.activeProduct());
        return new ProductGroupDetailBundle(
                createQueryResult(shipping),
                group,
                products,
                Optional.empty(),
                notice,
                noticeCategory,
                sellerCs,
                Map.of());
    }

    /**
     * 특정 fieldId와 매칭되는 NoticeField를 가진 NoticeCategory를 생성한다. ProductNoticeEntry의 noticeFieldId와
     * 매칭시키기 위함.
     */
    private NoticeCategory clothingNoticeCategoryWithFields(List<Long> fieldIds) {
        List<NoticeField> fields =
                fieldIds.stream()
                        .map(
                                id ->
                                        NoticeField.reconstitute(
                                                NoticeFieldId.of(id),
                                                NoticeFieldCode.of("field_" + id),
                                                NoticeFieldName.of("필드명_" + id),
                                                true,
                                                id.intValue()))
                        .toList();

        return NoticeCategory.reconstitute(
                NoticeCategoryId.of(1L),
                NoticeCategoryCode.of("CLOTHING"),
                NoticeCategoryName.of("의류", "Clothing"),
                com.ryuqq.marketplace.domain.category.vo.CategoryGroup.CLOTHING,
                true,
                fields,
                Instant.now(),
                Instant.now());
    }

    // ── 테스트 ──

    @Nested
    @DisplayName("toRegistrationRequest")
    class ToRegistrationRequest {

        @Test
        @DisplayName("기본 등록 요청 변환 - 필수 필드가 모두 설정된다")
        void basicRegistrationRequest() {
            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            assertThat(result).isNotNull();
            assertThat(result.originProduct()).isNotNull();
            assertThat(result.smartstoreChannelProduct()).isNotNull();

            OriginProduct origin = result.originProduct();
            assertThat(origin.statusType()).isEqualTo("SALE");
            assertThat(origin.saleType()).isEqualTo("NEW");
            assertThat(origin.leafCategoryId()).isEqualTo(String.valueOf(EXTERNAL_CATEGORY_ID));
            assertThat(origin.name()).isEqualTo("테스트 상품 그룹");
            assertThat(origin.salePrice()).isGreaterThan(0);
            assertThat(origin.stockQuantity()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("DeliveryInfo 매핑")
    class DeliveryInfoMapping {

        @Test
        @DisplayName("무료 배송 - deliveryFeeType=FREE, deliveryFeePayType=PREPAID")
        void freeShipping() {
            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            DeliveryInfo delivery = result.originProduct().deliveryInfo();
            assertThat(delivery.deliveryFee().deliveryFeeType()).isEqualTo("FREE");
            assertThat(delivery.deliveryFee().deliveryFeePayType()).isEqualTo("PREPAID");
            assertThat(delivery.deliveryFee().baseFee()).isZero();
            assertThat(delivery.deliveryFee().freeConditionalAmount()).isNull();
        }

        @Test
        @DisplayName("유료 배송 - deliveryFeeType=PAID, baseFee 설정")
        void paidShipping() {
            ProductGroupDetailBundle bundle =
                    createBundle(paidShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            DeliveryInfo delivery = result.originProduct().deliveryInfo();
            assertThat(delivery.deliveryFee().deliveryFeeType()).isEqualTo("PAID");
            assertThat(delivery.deliveryFee().deliveryFeePayType()).isEqualTo("PREPAID");
            assertThat(delivery.deliveryFee().baseFee()).isEqualTo(3000);
            assertThat(delivery.deliveryFee().freeConditionalAmount()).isNull();
        }

        @Test
        @DisplayName("조건부 무료 - deliveryFeeType=CONDITIONAL_FREE, freeConditionalAmount 설정")
        void conditionalFreeShipping() {
            ProductGroupDetailBundle bundle =
                    createBundle(
                            conditionalFreeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            DeliveryInfo delivery = result.originProduct().deliveryInfo();
            assertThat(delivery.deliveryFee().deliveryFeeType()).isEqualTo("CONDITIONAL_FREE");
            assertThat(delivery.deliveryFee().deliveryFeePayType()).isEqualTo("PREPAID");
            assertThat(delivery.deliveryFee().baseFee()).isEqualTo(3000);
            assertThat(delivery.deliveryFee().freeConditionalAmount()).isEqualTo(50000L);
        }

        @Test
        @DisplayName("deliveryCompany는 기본값 CJGLS로 설정")
        void deliveryCompanyIsDefault() {
            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            DeliveryInfo delivery = result.originProduct().deliveryInfo();
            assertThat(delivery.deliveryCompany()).isEqualTo("CJGLS");
        }

        @Test
        @DisplayName("지역별 추가 배송비 - 제주/도서산간 추가비 설정")
        void regionalExtraFees() {
            ProductGroupDetailBundle bundle =
                    createBundle(paidShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            DeliveryInfo delivery = result.originProduct().deliveryInfo();
            assertThat(delivery.deliveryFeeByArea()).isNotNull();
            assertThat(delivery.deliveryFeeByArea().jejuAreaFee()).isEqualTo(3000L);
            assertThat(delivery.deliveryFeeByArea().isolatedAreaFee()).isEqualTo(5000L);
        }

        @Test
        @DisplayName("반품/교환 배송비 설정")
        void claimDeliveryInfo() {
            ProductGroupDetailBundle bundle =
                    createBundle(paidShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            DeliveryInfo delivery = result.originProduct().deliveryInfo();
            assertThat(delivery.claimDeliveryInfo()).isNotNull();
            assertThat(delivery.claimDeliveryInfo().returnDeliveryFee()).isEqualTo(3000L);
            assertThat(delivery.claimDeliveryInfo().exchangeDeliveryFee()).isEqualTo(5000L);
        }
    }

    @Nested
    @DisplayName("CertificationInfo 매핑")
    class CertificationInfoMapping {

        @Test
        @DisplayName(
                "기본 인증 정보 - productCertificationInfos null, certificationTargetExcludeContent 빈 객체")
        void defaultCertificationInfo() {
            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            DetailAttribute detailAttr = result.originProduct().detailAttribute();
            assertThat(detailAttr.productCertificationInfos()).isNull();
            assertThat(detailAttr.certificationTargetExcludeContent()).isNotNull();
        }
    }

    @Nested
    @DisplayName("ProductInfoProvidedNotice 매핑")
    class NoticeMapping {

        @Test
        @DisplayName("notice 없으면 detailAttribute.productInfoProvidedNotice가 null")
        void noNotice() {
            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            assertThat(result.originProduct().detailAttribute().productInfoProvidedNotice())
                    .isNull();
        }

        @Test
        @DisplayName("CLOTHING 카테고리 notice - type=WEAR로 매핑, 타입별 필드 구조 사용")
        void clothingNoticeMapping() {
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();
            List<Long> fieldIds =
                    notice.entries().stream().map(ProductNoticeEntry::noticeFieldIdValue).toList();

            NoticeCategory category = clothingNoticeCategoryWithFields(fieldIds);

            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.of(notice), Optional.of(category));

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            ProductInfoProvidedNotice noticeResult =
                    result.originProduct().detailAttribute().productInfoProvidedNotice();
            assertThat(noticeResult).isNotNull();
            assertThat(noticeResult.productInfoProvidedNoticeType()).isEqualTo("WEAR");
            assertThat(noticeResult.wear()).isNotNull();
            assertThat(noticeResult.wear()).containsKey("returnCostReason");
        }

        @Test
        @DisplayName("noticeCategory 없으면 type=ETC, 기본 필드 설정")
        void noticeCategoryMissing() {
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();

            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.of(notice), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            ProductInfoProvidedNotice noticeResult =
                    result.originProduct().detailAttribute().productInfoProvidedNotice();
            assertThat(noticeResult).isNotNull();
            assertThat(noticeResult.productInfoProvidedNoticeType()).isEqualTo("ETC");
            assertThat(noticeResult.etc()).isNotNull();
            assertThat(noticeResult.etc())
                    .containsKeys("returnCostReason", "itemName", "modelName", "manufacturer");
        }
    }

    @Nested
    @DisplayName("AfterServiceInfo 매핑")
    class AfterServiceInfoMapping {

        @Test
        @DisplayName("SellerCs 있으면 csPhone을 AS 전화번호로 사용")
        void sellerCsPhone() {
            SellerCs sellerCs =
                    SellerCs.defaultCs(
                            SellerId.of(1L),
                            CsContact.of("02-1234-5678", null, "cs@test.com"),
                            Instant.now());

            ProductGroupDetailBundle bundle =
                    createBundle(
                            freeShippingPolicy(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.of(sellerCs));

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            assertThat(
                            result.originProduct()
                                    .detailAttribute()
                                    .afterServiceInfo()
                                    .afterServiceTelephoneNumber())
                    .isEqualTo("02-1234-5678");
        }

        @Test
        @DisplayName("SellerCs phone 없으면 mobile로 폴백")
        void sellerCsMobileFallback() {
            SellerCs sellerCs =
                    SellerCs.defaultCs(
                            SellerId.of(1L),
                            CsContact.of(null, "010-9999-8888", "cs@test.com"),
                            Instant.now());

            ProductGroupDetailBundle bundle =
                    createBundle(
                            freeShippingPolicy(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.of(sellerCs));

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            assertThat(
                            result.originProduct()
                                    .detailAttribute()
                                    .afterServiceInfo()
                                    .afterServiceTelephoneNumber())
                    .isEqualTo("010-9999-8888");
        }

        @Test
        @DisplayName("SellerCs 없으면 기본값 사용")
        void noSellerCsDefaultPhone() {
            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            assertThat(
                            result.originProduct()
                                    .detailAttribute()
                                    .afterServiceInfo()
                                    .afterServiceTelephoneNumber())
                    .isEqualTo("1660-1126");
        }
    }

    @Nested
    @DisplayName("OriginAreaInfo 매핑")
    class OriginAreaInfoMapping {

        @Test
        @DisplayName("notice에 made_in 필드 있으면 해당 값을 원산지로 사용")
        void madeInFromNotice() {
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();
            List<ProductNoticeEntry> entries = notice.entries();

            // 첫 번째 entry의 noticeFieldId를 made_in으로 매핑
            Long madeInFieldId = entries.get(0).noticeFieldIdValue();

            List<NoticeField> fields =
                    List.of(
                            NoticeField.reconstitute(
                                    NoticeFieldId.of(madeInFieldId),
                                    NoticeFieldCode.of("made_in"),
                                    NoticeFieldName.of("제조국"),
                                    true,
                                    1));

            NoticeCategory category =
                    NoticeCategory.reconstitute(
                            NoticeCategoryId.of(1L),
                            NoticeCategoryCode.of("CLOTHING"),
                            NoticeCategoryName.of("의류", "Clothing"),
                            com.ryuqq.marketplace.domain.category.vo.CategoryGroup.CLOTHING,
                            true,
                            fields,
                            Instant.now(),
                            Instant.now());

            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.of(notice), Optional.of(category));

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            // 원산지 content는 entries 첫 번째의 fieldValue
            assertThat(result.originProduct().detailAttribute().originAreaInfo().content())
                    .isEqualTo(entries.get(0).fieldValueValue());
            assertThat(result.originProduct().detailAttribute().originAreaInfo().originAreaCode())
                    .isEqualTo("03");
        }

        @Test
        @DisplayName("notice 없으면 기본값 사용")
        void noNoticeDefaultOrigin() {
            ProductGroupDetailBundle bundle =
                    createBundle(freeShippingPolicy(), Optional.empty(), Optional.empty());

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            assertThat(result.originProduct().detailAttribute().originAreaInfo().content())
                    .isEqualTo("상세설명에 표시");
        }
    }

    @Nested
    @DisplayName("통합 변환 검증")
    class IntegrationConversion {

        @Test
        @DisplayName("모든 필드가 포함된 전체 등록 요청 - 네이버 API 필수 필드 누락 없음")
        void fullRegistrationRequest() {
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();
            List<Long> fieldIds =
                    notice.entries().stream().map(ProductNoticeEntry::noticeFieldIdValue).toList();
            NoticeCategory category = clothingNoticeCategoryWithFields(fieldIds);

            ProductGroupDetailBundle bundle =
                    createBundle(
                            conditionalFreeShippingPolicy(),
                            Optional.of(notice),
                            Optional.of(category));

            NaverProductRegistrationRequest result =
                    sut.toRegistrationRequest(bundle, EXTERNAL_CATEGORY_ID, EXTERNAL_BRAND_ID);

            OriginProduct origin = result.originProduct();

            // 1. 기본 필드
            assertThat(origin.statusType()).isEqualTo("SALE");
            assertThat(origin.name()).isNotBlank();
            assertThat(origin.images()).isNotNull();

            // 2. deliveryFeePayType (필수)
            assertThat(origin.deliveryInfo().deliveryFee().deliveryFeePayType())
                    .isEqualTo("PREPAID");

            // 3. freeConditionalAmount (CONDITIONAL_FREE일 때 필수)
            assertThat(origin.deliveryInfo().deliveryFee().freeConditionalAmount())
                    .isEqualTo(50000L);

            // 4. 인증 정보 (카테고리별 설정 필요)
            assertThat(origin.detailAttribute()).isNotNull();

            // 5. productInfoProvidedNotice (detailAttribute 내부)
            assertThat(origin.detailAttribute().productInfoProvidedNotice()).isNotNull();
            assertThat(
                            origin.detailAttribute()
                                    .productInfoProvidedNotice()
                                    .productInfoProvidedNoticeType())
                    .isEqualTo("WEAR");
            assertThat(origin.detailAttribute().productInfoProvidedNotice().wear()).isNotNull();
        }
    }
}
