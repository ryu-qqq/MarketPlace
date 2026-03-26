package com.ryuqq.marketplace.adapter.in.rest.seller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SellerDetailApiResponse - 셀러 상세 조회 응답 DTO.
 *
 * <p>셀러 기본 정보, 사업자 정보, CS 정보, 계약 정보, 정산 정보를 포함한 상세 응답.
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 조회합니다.
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
@Schema(description = "셀러 상세 조회 응답 DTO")
public record SellerDetailApiResponse(
        @Schema(description = "셀러 기본 정보") SellerInfo seller,
        @Schema(description = "사업자 정보") BusinessInfo businessInfo,
        @Schema(description = "CS 정보") CsInfo csInfo,
        @Schema(description = "계약 정보") ContractInfo contractInfo,
        @Schema(description = "정산 정보") SettlementInfo settlementInfo) {

    @Schema(description = "셀러 기본 정보")
    public record SellerInfo(
            @Schema(description = "셀러 ID", example = "1") Long id,
            @Schema(description = "셀러명", example = "테스트셀러") String sellerName,
            @Schema(description = "표시명", example = "테스트 브랜드") String displayName,
            @Schema(description = "로고 URL", example = "https://example.com/logo.png")
                    String logoUrl,
            @Schema(description = "설명", example = "테스트 셀러 설명입니다.") String description,
            @Schema(description = "활성화 여부", example = "true") boolean active,
            @Schema(description = "생성일시 (KST)", example = "2025-01-23 10:30:00") String createdAt,
            @Schema(description = "수정일시 (KST)", example = "2025-01-23 10:30:00")
                    String updatedAt) {}

    @Schema(description = "사업자 정보")
    public record BusinessInfo(
            @Schema(description = "사업자 정보 ID", example = "1") Long id,
            @Schema(description = "사업자등록번호", example = "123-45-67890") String registrationNumber,
            @Schema(description = "회사명", example = "테스트컴퍼니") String companyName,
            @Schema(description = "대표자명", example = "홍길동") String representative,
            @Schema(description = "통신판매업 신고번호", example = "제2025-서울강남-1234호")
                    String saleReportNumber,
            @Schema(description = "사업장 우편번호", example = "12345") String businessZipcode,
            @Schema(description = "사업장 주소", example = "서울시 강남구") String businessAddress,
            @Schema(description = "사업장 상세주소", example = "테헤란로 123") String businessAddressDetail) {}

    @Schema(description = "CS 정보")
    public record CsInfo(
            @Schema(description = "CS ID", example = "1") Long id,
            @Schema(description = "고객센터 전화번호", example = "02-1234-5678") String csPhone,
            @Schema(description = "고객센터 휴대폰번호", example = "010-1234-5678") String csMobile,
            @Schema(description = "고객센터 이메일", example = "cs@example.com") String csEmail,
            @Schema(description = "운영 시작 시간", example = "09:00") String operatingStartTime,
            @Schema(description = "운영 종료 시간", example = "18:00") String operatingEndTime,
            @Schema(description = "운영 요일", example = "MON,TUE,WED,THU,FRI") String operatingDays,
            @Schema(description = "카카오 채널 URL", example = "https://pf.kakao.com/xxx")
                    String kakaoChannelUrl) {}

    @Schema(description = "계약 정보")
    public record ContractInfo(
            @Schema(description = "계약 ID", example = "1") Long id,
            @Schema(description = "수수료율", example = "10.5") String commissionRate,
            @Schema(description = "계약 시작일", example = "2025-01-01") String contractStartDate,
            @Schema(description = "계약 종료일", example = "2025-12-31") String contractEndDate,
            @Schema(description = "계약 상태", example = "ACTIVE") String status,
            @Schema(description = "특별 조건", example = "신규 셀러 수수료 할인") String specialTerms,
            @Schema(description = "생성일시 (KST)", example = "2025-01-23 10:30:00") String createdAt,
            @Schema(description = "수정일시 (KST)", example = "2025-01-23 10:30:00")
                    String updatedAt) {}

    @Schema(description = "정산 정보")
    public record SettlementInfo(
            @Schema(description = "정산 ID", example = "1") Long id,
            @Schema(description = "은행 코드", example = "088") String bankCode,
            @Schema(description = "은행명", example = "신한은행") String bankName,
            @Schema(description = "계좌번호", example = "123-456-789") String accountNumber,
            @Schema(description = "예금주명", example = "테스트컴퍼니") String accountHolderName,
            @Schema(description = "정산 주기", example = "MONTHLY") String settlementCycle,
            @Schema(description = "정산일", example = "15") Integer settlementDay,
            @Schema(description = "인증 여부", example = "true") boolean verified,
            @Schema(description = "인증일시 (KST)", example = "2025-01-23 10:30:00") String verifiedAt,
            @Schema(description = "생성일시 (KST)", example = "2025-01-23 10:30:00") String createdAt,
            @Schema(description = "수정일시 (KST)", example = "2025-01-23 10:30:00")
                    String updatedAt) {}
}
