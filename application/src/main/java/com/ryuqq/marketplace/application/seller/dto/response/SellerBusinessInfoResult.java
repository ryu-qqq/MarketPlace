package com.ryuqq.marketplace.application.seller.dto.response;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import java.time.Instant;

/**
 * 사업자 정보 조회 결과.
 *
 * @param id 사업자 정보 ID
 * @param sellerId 셀러 ID
 * @param registrationNumber 사업자등록번호
 * @param companyName 회사명
 * @param representative 대표자명
 * @param saleReportNumber 통신판매업 신고번호
 * @param businessAddress 사업장 주소
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
public record SellerBusinessInfoResult(
        Long id,
        Long sellerId,
        String registrationNumber,
        String companyName,
        String representative,
        String saleReportNumber,
        AddressResult businessAddress,
        Instant createdAt,
        Instant updatedAt) {

    public static SellerBusinessInfoResult from(SellerBusinessInfo info) {
        return new SellerBusinessInfoResult(
                info.idValue(),
                info.sellerIdValue(),
                info.registrationNumberValue(),
                info.companyNameValue(),
                info.representativeValue(),
                info.saleReportNumberValue(),
                new AddressResult(
                        info.businessAddressZipCode(),
                        info.businessAddressRoad(),
                        info.businessAddressDetail()),
                info.createdAt(),
                info.updatedAt());
    }

    /** 주소 결과. */
    public record AddressResult(String zipCode, String line1, String line2) {}
}
