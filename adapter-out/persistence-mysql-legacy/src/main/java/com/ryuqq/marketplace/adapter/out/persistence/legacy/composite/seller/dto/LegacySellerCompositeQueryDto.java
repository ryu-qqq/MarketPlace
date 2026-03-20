package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.dto;

/**
 * 레거시 셀러 복합 조회 flat projection DTO.
 *
 * <p>seller + seller_business_info 2개 테이블 조인 결과.
 *
 * @param sellerId 셀러 ID (seller.seller_id)
 * @param sellerName 셀러명 (seller.seller_name)
 * @param sellerLogoUrl 로고 URL (seller.seller_logo_url)
 * @param sellerDescription 설명 (seller.seller_description)
 * @param commissionRate 수수료율 (seller.commission_rate)
 * @param registrationNumber 사업자등록번호 (seller_business_info.registration_number)
 * @param companyName 회사명 (seller_business_info.company_name)
 * @param representative 대표자 (seller_business_info.representative)
 * @param saleReportNumber 통신판매신고번호 (seller_business_info.sale_report_number)
 * @param businessAddressZipCode 사업장 우편번호 (seller_business_info.business_address_zip_code)
 * @param businessAddressLine1 사업장 주소1 (seller_business_info.business_address_line1)
 * @param businessAddressLine2 사업장 주소2 (seller_business_info.business_address_line2)
 * @param bankName 은행명 (seller_business_info.bank_name)
 * @param accountNumber 계좌번호 (seller_business_info.account_number)
 * @param accountHolderName 예금주 (seller_business_info.account_holder_name)
 * @param csNumber 대표번호 (seller_business_info.cs_number)
 * @param csPhoneNumber CS 전화번호 (seller_business_info.cs_phone_number)
 * @param csEmail CS 이메일 (seller_business_info.cs_email)
 */
public record LegacySellerCompositeQueryDto(
        long sellerId,
        String sellerName,
        String sellerLogoUrl,
        String sellerDescription,
        Double commissionRate,
        String registrationNumber,
        String companyName,
        String representative,
        String saleReportNumber,
        String businessAddressZipCode,
        String businessAddressLine1,
        String businessAddressLine2,
        String bankName,
        String accountNumber,
        String accountHolderName,
        String csNumber,
        String csPhoneNumber,
        String csEmail) {}
