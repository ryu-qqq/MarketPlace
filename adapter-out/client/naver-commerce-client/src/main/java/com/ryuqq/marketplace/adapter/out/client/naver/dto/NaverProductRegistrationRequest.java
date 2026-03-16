package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

/**
 * 네이버 커머스 상품 등록/수정 요청 DTO.
 *
 * <p>POST /v2/products (등록), PUT /v2/products/origin-products/{id} (수정) 요청 본문.
 *
 * @see <a href="https://apicenter.commerce.naver.com/ko/basic/commerce-api">Naver Commerce API</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverProductRegistrationRequest(
        OriginProduct originProduct, SmartstoreChannelProduct smartstoreChannelProduct) {

    /** 원상품 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OriginProduct(
            String statusType,
            String saleType,
            String leafCategoryId,
            String name,
            Images images,
            DetailAttribute detailAttribute,
            int salePrice,
            int stockQuantity,
            String detailContent,
            DeliveryInfo deliveryInfo) {}

    /** 이미지 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Images(
            RepresentativeImage representativeImage, List<OptionalImage> optionalImages) {

        /** 방어적 복사. */
        public Images {
            optionalImages = optionalImages == null ? null : List.copyOf(optionalImages);
        }

        /** 대표 이미지. */
        public record RepresentativeImage(String url) {}

        /** 추가 이미지. */
        public record OptionalImage(String url) {}
    }

    /** 상세 속성. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DetailAttribute(
            NaverShoppingSearchInfo naverShoppingSearchInfo,
            OptionInfo optionInfo,
            AfterServiceInfo afterServiceInfo,
            OriginAreaInfo originAreaInfo,
            Boolean minorPurchasable,
            List<ProductCertificationInfo> productCertificationInfos,
            CertificationTargetExcludeContent certificationTargetExcludeContent,
            ProductInfoProvidedNotice productInfoProvidedNotice) {

        /** 방어적 복사. */
        public DetailAttribute {
            productCertificationInfos =
                    productCertificationInfos == null
                            ? null
                            : List.copyOf(productCertificationInfos);
        }

        /** 팩토리 메서드 - 카테고리/브랜드 기반. */
        public static DetailAttribute of(
                Long naverCategoryId,
                OptionInfo optionInfo,
                Long brandId,
                String manufacturerName,
                AfterServiceInfo afterServiceInfo,
                OriginAreaInfo originAreaInfo,
                Boolean minorPurchasable,
                List<ProductCertificationInfo> productCertificationInfos,
                CertificationTargetExcludeContent certificationTargetExcludeContent,
                ProductInfoProvidedNotice productInfoProvidedNotice) {
            NaverShoppingSearchInfo searchInfo =
                    new NaverShoppingSearchInfo(
                            manufacturerName,
                            manufacturerName,
                            null,
                            naverCategoryId,
                            brandId,
                            false);
            return new DetailAttribute(
                    searchInfo,
                    optionInfo,
                    afterServiceInfo,
                    originAreaInfo,
                    minorPurchasable,
                    productCertificationInfos,
                    certificationTargetExcludeContent,
                    productInfoProvidedNotice);
        }
    }

    /** KC 인증 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductCertificationInfo(
            String certificationKindType,
            String name,
            String certificationNumber,
            Boolean certificationMark,
            String companyName,
            String certificationDate) {}

    /** KC 인증 대상 제외 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CertificationTargetExcludeContent(
            String kcCertifiedProductExclusionYn,
            String kcExemptionType,
            String childCertifiedProductExclusionYn,
            String greenCertifiedProductExclusionYn) {

        /** KC 인증 면제 팩토리 메서드. */
        public static CertificationTargetExcludeContent kcExempt() {
            return new CertificationTargetExcludeContent("TRUE", null, null, null);
        }
    }

    /** 네이버 쇼핑 검색 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record NaverShoppingSearchInfo(
            String manufacturerName,
            String brandName,
            String modelName,
            Long categoryId,
            Long brandId,
            Boolean catalogMatchingYn) {}

    /** 네이버 쇼핑 검색 속성 (카테고리별 PRIMARY 속성). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductAttribute(Long attributeSeq, Long attributeValueSeq) {}

    /** A/S 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AfterServiceInfo(
            String afterServiceTelephoneNumber, String afterServiceGuideContent) {}

    /** 원산지 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OriginAreaInfo(String originAreaCode, String content) {}

    /** 옵션 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OptionInfo(
            String optionCombinationSortType,
            OptionCombinationGroupNames optionCombinationGroupNames,
            List<OptionCombination> optionCombinations,
            List<OptionCustom> optionCustom) {

        /** 방어적 복사. */
        public OptionInfo {
            optionCombinations =
                    optionCombinations == null ? null : List.copyOf(optionCombinations);
            optionCustom = optionCustom == null ? null : List.copyOf(optionCustom);
        }

        /** 옵션 조합 그룹명 (최대 4개). */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record OptionCombinationGroupNames(
                String optionGroupName1,
                String optionGroupName2,
                String optionGroupName3,
                String optionGroupName4) {

            public static OptionCombinationGroupNames of(List<String> groupNames) {
                return new OptionCombinationGroupNames(
                        groupNames.size() > 0 ? groupNames.get(0) : null,
                        groupNames.size() > 1 ? groupNames.get(1) : null,
                        groupNames.size() > 2 ? groupNames.get(2) : null,
                        groupNames.size() > 3 ? groupNames.get(3) : null);
            }
        }

        /** 옵션 조합 (개별 SKU). */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record OptionCombination(
                Long id,
                String optionName1,
                String optionName2,
                String optionName3,
                int stockQuantity,
                int price,
                String sellerManagerCode,
                boolean usable) {}

        /** 직접 입력형 옵션 (구매자가 주문 시 직접 텍스트 입력). */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record OptionCustom(Long id, String groupName, boolean usable) {}
    }

    /** 배송 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DeliveryInfo(
            String deliveryType,
            String deliveryAttributeType,
            DeliveryFee deliveryFee,
            String deliveryCompany,
            DeliveryFeeByArea deliveryFeeByArea,
            ClaimDeliveryInfo claimDeliveryInfo) {

        /** 배송비 정보. */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DeliveryFee(
                String deliveryFeeType,
                String deliveryFeePayType,
                int baseFee,
                Long freeConditionalAmount) {}

        /** 지역별 추가 배송비. */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DeliveryFeeByArea(
                Long jejuAreaFee, Long isolatedAreaFee, String deliveryAreaType) {}

        /** 반품/교환 배송비. */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ClaimDeliveryInfo(Long returnDeliveryFee, Long exchangeDeliveryFee) {}
    }

    /**
     * 상품정보제공고시.
     *
     * <p>네이버 API는 타입별 고정 필드 구조를 사용합니다. productInfoProvidedNoticeType에 해당하는 필드만 값을 설정하고 나머지는 null로
     * 둡니다.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductInfoProvidedNotice(
            String productInfoProvidedNoticeType,
            Map<String, String> wear,
            Map<String, String> shoes,
            Map<String, String> bag,
            Map<String, String> fashionItems,
            Map<String, String> sleepingGear,
            Map<String, String> furniture,
            Map<String, String> imageAppliances,
            Map<String, String> homeAppliances,
            Map<String, String> seasonAppliances,
            Map<String, String> kids,
            Map<String, String> etc) {

        /** 방어적 복사. */
        public ProductInfoProvidedNotice {
            wear = wear == null ? null : Map.copyOf(wear);
            shoes = shoes == null ? null : Map.copyOf(shoes);
            bag = bag == null ? null : Map.copyOf(bag);
            fashionItems = fashionItems == null ? null : Map.copyOf(fashionItems);
            sleepingGear = sleepingGear == null ? null : Map.copyOf(sleepingGear);
            furniture = furniture == null ? null : Map.copyOf(furniture);
            imageAppliances = imageAppliances == null ? null : Map.copyOf(imageAppliances);
            homeAppliances = homeAppliances == null ? null : Map.copyOf(homeAppliances);
            seasonAppliances = seasonAppliances == null ? null : Map.copyOf(seasonAppliances);
            kids = kids == null ? null : Map.copyOf(kids);
            etc = etc == null ? null : Map.copyOf(etc);
        }

        /** 타입별 팩토리 메서드. 지정된 타입의 필드에만 내용을 설정한다. */
        public static ProductInfoProvidedNotice of(String type, Map<String, String> contents) {
            return new ProductInfoProvidedNotice(
                    type,
                    "WEAR".equals(type) ? contents : null,
                    "SHOES".equals(type) ? contents : null,
                    "BAG".equals(type) ? contents : null,
                    "FASHION_ITEMS".equals(type) ? contents : null,
                    "SLEEPING_GEAR".equals(type) ? contents : null,
                    "FURNITURE".equals(type) ? contents : null,
                    "IMAGE_APPLIANCES".equals(type) ? contents : null,
                    "HOME_APPLIANCES".equals(type) ? contents : null,
                    "SEASON_APPLIANCES".equals(type) ? contents : null,
                    "KIDS".equals(type) ? contents : null,
                    "ETC".equals(type) ? contents : null);
        }
    }

    /** 스마트스토어 채널 상품 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SmartstoreChannelProduct(
            String channelProductName,
            String channelProductDisplayStatusType,
            Boolean naverShoppingRegistration,
            Long storeKeepExclusiveProduct) {}
}
