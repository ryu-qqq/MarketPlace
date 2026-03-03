package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 네이버 커머스 상품 등록 요청 DTO.
 *
 * <p>POST /v2/products 요청 본문. 네이버 커머스 일반상품 등록 API 스펙에 맞는 중첩 record 구조입니다.
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
            DeliveryInfo deliveryInfo,
            ProductInfoProvidedNotice productInfoProvidedNotice) {}

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
            Long naverShoppingSearchInfo, OptionInfo optionInfo, Long brandId) {

        /** 네이버 쇼핑 검색 정보 (categoryId). */
        public static DetailAttribute of(
                Long naverCategoryId, OptionInfo optionInfo, Long brandId) {
            return new DetailAttribute(naverCategoryId, optionInfo, brandId);
        }
    }

    /** 옵션 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OptionInfo(
            String optionCombinationSortType,
            List<OptionCombinationGroupNames> optionCombinationGroupNames,
            List<OptionCombination> optionCombinations) {

        /** 방어적 복사. */
        public OptionInfo {
            optionCombinationGroupNames =
                    optionCombinationGroupNames == null
                            ? null
                            : List.copyOf(optionCombinationGroupNames);
            optionCombinations =
                    optionCombinations == null ? null : List.copyOf(optionCombinations);
        }

        /** 옵션 조합 그룹명. */
        public record OptionCombinationGroupNames(String optionGroupName) {}

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
    }

    /** 배송 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DeliveryInfo(
            String deliveryType,
            String deliveryAttributeType,
            String deliveryFee,
            DeliveryFeeByArea deliveryFeeByArea,
            ClaimDeliveryInfo claimDeliveryInfo) {

        /** 지역별 추가 배송비. */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record DeliveryFeeByArea(
                Long jejuAreaFee, Long isolatedAreaFee, String deliveryAreaType) {}

        /** 반품/교환 배송비. */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ClaimDeliveryInfo(Long returnDeliveryFee, Long exchangeDeliveryFee) {}
    }

    /** 상품정보제공고시. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductInfoProvidedNotice(
            String productInfoProvidedNoticeType,
            List<ProductInfoProvidedNoticeContent> productInfoProvidedNoticeContents) {

        /** 방어적 복사. */
        public ProductInfoProvidedNotice {
            productInfoProvidedNoticeContents =
                    productInfoProvidedNoticeContents == null
                            ? null
                            : List.copyOf(productInfoProvidedNoticeContents);
        }

        /** 고시정보 항목. */
        public record ProductInfoProvidedNoticeContent(int order, String title, String content) {}
    }

    /** 스마트스토어 채널 상품 정보. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SmartstoreChannelProduct(
            String channelProductName, Long storeKeepExclusiveProduct) {}
}
