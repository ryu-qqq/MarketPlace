package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SellerApplicationApiResponse - 셀러 입점 신청 조회 응답 DTO.
 *
 * <p>셀러 입점 신청 정보를 포함한 응답.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-004: createdAt/updatedAt 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 *
 * <p>API-DTO-006: 복잡한 구조는 중첩 Record로 표현.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 입점 신청 응답 DTO")
public record SellerApplicationApiResponse(
        @Schema(description = "신청 ID", example = "1") Long id,
        @Schema(description = "셀러 기본 정보") SellerInfo sellerInfo,
        @Schema(description = "사업자 정보") BusinessInfo businessInfo,
        @Schema(description = "CS 연락처 정보") CsContactInfo csContact,
        @Schema(description = "담당자 연락처") ContactInfo contactInfo,
        @Schema(description = "동의 정보") AgreementInfo agreement,
        @Schema(description = "신청 상태 (PENDING, APPROVED, REJECTED)", example = "PENDING")
                String status,
        @Schema(description = "신청 일시 (KST)", example = "2025-01-23 10:30:00")
                String appliedAt,
        @Schema(description = "처리 일시 (KST)", example = "2025-01-23 10:30:00")
                String processedAt,
        @Schema(description = "처리자", example = "admin@example.com") String processedBy,
        @Schema(description = "거절 사유", example = "서류 미비") String rejectionReason,
        @Schema(description = "승인된 셀러 ID", example = "100") Long approvedSellerId) {

    @Schema(description = "셀러 기본 정보")
    public record SellerInfo(
            @Schema(description = "셀러명", example = "테스트셀러") String sellerName,
            @Schema(description = "표시명", example = "테스트 브랜드") String displayName,
            @Schema(description = "로고 URL", example = "https://example.com/logo.png")
                    String logoUrl,
            @Schema(description = "설명", example = "테스트 셀러 설명입니다.") String description) {}

    @Schema(description = "사업자 정보")
    public record BusinessInfo(
            @Schema(description = "사업자등록번호", example = "123-45-67890") String registrationNumber,
            @Schema(description = "회사명", example = "테스트컴퍼니") String companyName,
            @Schema(description = "대표자명", example = "홍길동") String representative,
            @Schema(description = "통신판매업 신고번호", example = "제2025-서울강남-1234호")
                    String saleReportNumber,
            @Schema(description = "사업장 주소 정보") AddressDetail businessAddress) {}

    @Schema(description = "CS 연락처 정보")
    public record CsContactInfo(
            @Schema(description = "전화번호", example = "02-1234-5678") String phone,
            @Schema(description = "이메일", example = "cs@example.com") String email,
            @Schema(description = "휴대폰번호", example = "010-1234-5678") String mobile) {}

    @Schema(description = "담당자 연락처")
    public record ContactInfo(
            @Schema(description = "담당자명", example = "김담당") String name,
            @Schema(description = "연락처", example = "010-9876-5432") String phone,
            @Schema(description = "이메일", example = "contact@example.com") String email) {}

    @Schema(description = "주소 상세")
    public record AddressDetail(
            @Schema(description = "우편번호", example = "12345") String zipCode,
            @Schema(description = "주소1", example = "서울시 강남구") String line1,
            @Schema(description = "주소2", example = "테헤란로 123") String line2) {}

    @Schema(description = "동의 정보")
    public record AgreementInfo(
            @Schema(description = "동의 일시 (KST)", example = "2025-01-23 10:30:00")
                    String agreedAt,
            @Schema(description = "이용약관 동의", example = "true") boolean termsAgreed,
            @Schema(description = "개인정보 처리방침 동의", example = "true") boolean privacyAgreed) {}
}
