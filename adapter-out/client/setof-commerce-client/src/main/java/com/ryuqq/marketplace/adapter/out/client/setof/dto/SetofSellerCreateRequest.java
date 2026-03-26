package com.ryuqq.marketplace.adapter.out.client.setof.dto;

/**
 * 세토프 셀러 등록 요청 DTO.
 *
 * <p>POST /api/v2/admin/sellers 요청의 중첩 구조(sellerInfo + businessInfo)에 대응합니다. 현재 MarketPlace에서
 * businessInfo를 보유하지 않으므로 null로 전달합니다.
 */
public record SetofSellerCreateRequest(
        SellerInfoRequest sellerInfo, BusinessInfoRequest businessInfo) {

    public record SellerInfoRequest(
            String sellerName, String displayName, String logoUrl, String description) {}

    public record BusinessInfoRequest(
            String companyName,
            String businessNumber,
            String representativeName,
            String businessType,
            String businessCategory) {}

    /** MarketPlace가 가진 셀러 정보만으로 등록 요청을 생성한다. businessInfo는 null. */
    public static SetofSellerCreateRequest ofSellerInfo(
            String sellerName, String displayName, String logoUrl, String description) {
        return new SetofSellerCreateRequest(
                new SellerInfoRequest(sellerName, displayName, logoUrl, description), null);
    }
}
