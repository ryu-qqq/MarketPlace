package com.ryuqq.marketplace.application.sellerapplication.dto.response;

import java.time.Instant;

/**
 * 셀러 입점 신청 조회 결과.
 *
 * @param id 신청 ID
 * @param sellerInfo 셀러 기본 정보
 * @param businessInfo 사업자 정보
 * @param csContact CS 연락처
 * @param agreement 동의 정보
 * @param status 신청 상태
 * @param appliedAt 신청 일시
 * @param processedAt 처리 일시 (null 가능)
 * @param processedBy 처리자 (null 가능)
 * @param rejectionReason 거절 사유 (null 가능)
 * @param approvedSellerId 승인된 셀러 ID (null 가능)
 * @author ryu-qqq
 */
public record SellerApplicationResult(
        Long id,
        SellerInfoResult sellerInfo,
        BusinessInfoResult businessInfo,
        CsContactResult csContact,
        ContactInfoResult contactInfo,
        AgreementResult agreement,
        String status,
        Instant appliedAt,
        Instant processedAt,
        String processedBy,
        String rejectionReason,
        Long approvedSellerId) {

    /**
     * 셀러 기본 정보 결과.
     *
     * @param sellerName 셀러명
     * @param displayName 표시명
     * @param logoUrl 로고 URL
     * @param description 설명
     */
    public record SellerInfoResult(
            String sellerName, String displayName, String logoUrl, String description) {}

    /**
     * 사업자 정보 결과.
     *
     * @param registrationNumber 사업자등록번호
     * @param companyName 회사명
     * @param representative 대표자명
     * @param saleReportNumber 통신판매업 신고번호
     * @param businessAddress 사업장 주소
     */
    public record BusinessInfoResult(
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            AddressResult businessAddress) {}

    /**
     * CS 연락처 결과.
     *
     * @param phone 전화번호
     * @param email 이메일
     * @param mobile 휴대폰번호
     */
    public record CsContactResult(String phone, String email, String mobile) {}

    /**
     * 담당자 연락처 결과.
     *
     * @param name 담당자명
     * @param phone 담당자 전화번호
     * @param email 담당자 이메일
     */
    public record ContactInfoResult(String name, String phone, String email) {}

    /**
     * 동의 정보 결과.
     *
     * @param agreedAt 동의 일시
     * @param termsAgreed 이용약관 동의 여부
     * @param privacyAgreed 개인정보 처리방침 동의 여부
     */
    public record AgreementResult(Instant agreedAt, boolean termsAgreed, boolean privacyAgreed) {}

    /**
     * 주소 결과.
     *
     * @param zipCode 우편번호
     * @param line1 주소1
     * @param line2 주소2
     */
    public record AddressResult(String zipCode, String line1, String line2) {}
}
