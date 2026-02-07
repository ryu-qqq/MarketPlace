package com.ryuqq.marketplace.adapter.in.rest.seller.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * UpdateSellerFullApiRequest - 셀러 전체정보 수정 요청 DTO.
 *
 * <p>셀러 기본정보 + 사업자정보 + CS정보 + 계약정보 + 정산정보를 한번에 수정합니다.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-003: Validation 어노테이션.
 *
 * <p>API-DTO-006: 복잡한 구조는 중첩 Record로 표현.
 *
 * <p>nullable 허용 안함 - 모든 필드 필수, 더티체킹이 변경사항 판단.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 전체정보 수정 요청 DTO")
public record UpdateSellerFullApiRequest(
        @Schema(description = "셀러 기본 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @Valid
                @NotNull(message = "셀러 기본 정보는 필수입니다")
                SellerInfoRequest seller,
        @Schema(description = "사업자 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @Valid
                @NotNull(message = "사업자 정보는 필수입니다")
                BusinessInfoRequest businessInfo,
        @Schema(description = "CS 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @Valid
                @NotNull(message = "CS 정보는 필수입니다")
                CsInfoRequest csInfo,
        @Schema(description = "계약 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @Valid
                @NotNull(message = "계약 정보는 필수입니다")
                ContractInfoRequest contractInfo,
        @Schema(description = "정산 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @Valid
                @NotNull(message = "정산 정보는 필수입니다")
                SettlementInfoRequest settlementInfo) {

    @Schema(description = "셀러 기본 정보")
    public record SellerInfoRequest(
            @Schema(
                            description = "셀러명",
                            example = "테스트셀러",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "셀러명은 필수입니다")
                    String sellerName,
            @Schema(
                            description = "표시명",
                            example = "테스트 브랜드",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "표시명은 필수입니다")
                    String displayName,
            @Schema(
                            description = "로고 URL",
                            example = "https://example.com/logo.png",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "로고 URL은 필수입니다")
                    String logoUrl,
            @Schema(
                            description = "설명",
                            example = "테스트 셀러 설명입니다.",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "설명은 필수입니다")
                    String description) {}

    @Schema(description = "사업자 정보")
    public record BusinessInfoRequest(
            @Schema(
                            description = "사업자등록번호",
                            example = "123-45-67890",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "사업자등록번호는 필수입니다")
                    String registrationNumber,
            @Schema(
                            description = "회사명",
                            example = "테스트컴퍼니",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "회사명은 필수입니다")
                    String companyName,
            @Schema(
                            description = "대표자명",
                            example = "홍길동",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "대표자명은 필수입니다")
                    String representative,
            @Schema(
                            description = "통신판매업 신고번호",
                            example = "제2025-서울강남-1234호",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "통신판매업 신고번호는 필수입니다")
                    String saleReportNumber,
            @Schema(description = "사업장 주소", requiredMode = Schema.RequiredMode.REQUIRED)
                    @Valid
                    @NotNull(message = "사업장 주소는 필수입니다")
                    AddressRequest businessAddress) {}

    @Schema(description = "CS 정보")
    public record CsInfoRequest(
            @Schema(
                            description = "전화번호",
                            example = "02-1234-5678",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "전화번호는 필수입니다")
                    String phone,
            @Schema(
                            description = "이메일",
                            example = "cs@example.com",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "이메일은 필수입니다")
                    String email,
            @Schema(description = "휴대폰", example = "010-1234-5678") String mobile,
            @Schema(description = "운영 시작 시간", example = "09:00") String operatingStartTime,
            @Schema(description = "운영 종료 시간", example = "18:00") String operatingEndTime,
            @Schema(description = "운영 요일 (쉼표 구분)", example = "MON,TUE,WED,THU,FRI")
                    String operatingDays,
            @Schema(description = "카카오 채널 URL", example = "https://pf.kakao.com/xxx")
                    String kakaoChannelUrl) {}

    @Schema(description = "주소")
    public record AddressRequest(
            @Schema(
                            description = "우편번호",
                            example = "12345",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "우편번호는 필수입니다")
                    String zipCode,
            @Schema(
                            description = "주소",
                            example = "서울시 강남구",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "주소는 필수입니다")
                    String line1,
            @Schema(
                            description = "상세주소",
                            example = "테헤란로 123",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "상세주소는 필수입니다")
                    String line2) {}

    @Schema(description = "계약 정보")
    public record ContractInfoRequest(
            @Schema(
                            description = "수수료율 (0 ~ 100)",
                            example = "10.5",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "수수료율은 필수입니다")
                    Double commissionRate,
            @Schema(
                            description = "계약 시작일 (YYYY-MM-DD)",
                            example = "2025-01-01",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "계약 시작일은 필수입니다")
                    String contractStartDate,
            @Schema(
                            description = "계약 종료일 (YYYY-MM-DD)",
                            example = "2025-12-31",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "계약 종료일은 필수입니다")
                    String contractEndDate,
            @Schema(description = "특약사항", example = "특별 계약 조건입니다.") String specialTerms) {}

    @Schema(description = "정산 정보")
    public record SettlementInfoRequest(
            @Schema(description = "정산 계좌", requiredMode = Schema.RequiredMode.REQUIRED)
                    @Valid
                    @NotNull(message = "정산 계좌는 필수입니다")
                    BankAccountRequest bankAccount,
            @Schema(
                            description = "정산 주기 (WEEKLY, BIWEEKLY, MONTHLY)",
                            example = "MONTHLY",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "정산 주기는 필수입니다")
                    String settlementCycle,
            @Schema(
                            description = "정산일 (1 ~ 31)",
                            example = "15",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "정산일은 필수입니다")
                    Integer settlementDay) {}

    @Schema(description = "정산 계좌")
    public record BankAccountRequest(
            @Schema(description = "은행 코드", example = "004") String bankCode,
            @Schema(
                            description = "은행명",
                            example = "국민은행",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "은행명은 필수입니다")
                    String bankName,
            @Schema(
                            description = "계좌번호 (숫자만)",
                            example = "12345678901234",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "계좌번호는 필수입니다")
                    String accountNumber,
            @Schema(
                            description = "예금주",
                            example = "홍길동",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "예금주는 필수입니다")
                    String accountHolderName) {}
}
