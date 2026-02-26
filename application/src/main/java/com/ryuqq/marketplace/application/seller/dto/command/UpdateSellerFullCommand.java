package com.ryuqq.marketplace.application.seller.dto.command;

/**
 * 셀러 전체 수정 Command.
 *
 * <p>Seller + BusinessInfo + CS + Contract + Settlement를 한번에 수정합니다. (모두 1:1 관계)
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 수정합니다.
 *
 * @param sellerId 셀러 ID
 * @param seller 셀러 기본 정보
 * @param businessInfo 사업자 정보
 * @param csInfo CS 정보
 * @param contractInfo 계약 정보
 * @param settlementInfo 정산 정보
 */
public record UpdateSellerFullCommand(
        Long sellerId,
        SellerInfoCommand seller,
        SellerBusinessInfoCommand businessInfo,
        CsInfoCommand csInfo,
        ContractInfoCommand contractInfo,
        SettlementInfoCommand settlementInfo) {

    /** 셀러 기본 정보 Command. */
    public record SellerInfoCommand(
            String sellerName, String displayName, String logoUrl, String description) {}

    /** 사업자 정보 Command. */
    public record SellerBusinessInfoCommand(
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            AddressCommand businessAddress,
            CsContactCommand csContact) {}

    /** 공통 주소 Command. */
    public record AddressCommand(String zipCode, String line1, String line2) {}

    /** CS 연락처 Command. */
    public record CsContactCommand(String phone, String email, String mobile) {}

    /**
     * CS 정보 Command.
     *
     * @param csContact CS 연락처
     * @param operatingHours 운영시간 (nullable)
     * @param operatingDays 운영요일 (예: "MON,TUE,WED,THU,FRI")
     * @param kakaoChannelUrl 카카오 채널 URL (선택)
     */
    public record CsInfoCommand(
            CsContactCommand csContact,
            OperatingHoursCommand operatingHours,
            String operatingDays,
            String kakaoChannelUrl) {}

    /**
     * 운영시간 Command.
     *
     * @param startTime 시작 시간 (예: "09:00")
     * @param endTime 종료 시간 (예: "18:00")
     */
    public record OperatingHoursCommand(String startTime, String endTime) {}

    /**
     * 계약 정보 Command.
     *
     * @param commissionRate 수수료율 (0 ~ 100)
     * @param contractStartDate 계약 시작일 (예: "2025-01-01")
     * @param contractEndDate 계약 종료일 (예: "2025-12-31")
     * @param specialTerms 특약사항 (선택)
     */
    public record ContractInfoCommand(
            Double commissionRate,
            String contractStartDate,
            String contractEndDate,
            String specialTerms) {}

    /**
     * 정산 정보 Command.
     *
     * @param bankAccount 정산 계좌 정보
     * @param settlementCycle 정산 주기 (WEEKLY, BIWEEKLY, MONTHLY)
     * @param settlementDay 정산일 (1 ~ 31)
     */
    public record SettlementInfoCommand(
            BankAccountCommand bankAccount, String settlementCycle, Integer settlementDay) {}

    /**
     * 은행 계좌 Command.
     *
     * @param bankCode 은행 코드 (선택)
     * @param bankName 은행명
     * @param accountNumber 계좌번호 (숫자만)
     * @param accountHolderName 예금주
     */
    public record BankAccountCommand(
            String bankCode, String bankName, String accountNumber, String accountHolderName) {}
}
