package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ApplySellerApplicationApiRequest - 셀러 입점 신청 요청 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: Jakarta Validation 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 입점 신청 요청 DTO")
public record ApplySellerApplicationApiRequest(
        @Schema(description = "셀러 기본 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                @Valid
                SellerInfo sellerInfo,
        @Schema(description = "사업자 정보", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull @Valid
                BusinessInfo businessInfo,
        @Schema(description = "CS 연락처 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                @Valid
                CsContactInfo csContact,
        @Schema(description = "담당자 연락처", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                @Valid
                ContactInfo contactInfo,
        @Schema(description = "정산 정보", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull @Valid
                SettlementInfo settlementInfo) {

    @Schema(description = "셀러 기본 정보")
    public record SellerInfo(
            @Schema(
                            description = "셀러명",
                            example = "테스트셀러",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "셀러명은 필수입니다.")
                    @Size(max = 100, message = "셀러명은 100자 이하여야 합니다.")
                    String sellerName,
            @Schema(description = "표시명", example = "테스트 브랜드")
                    @Size(max = 100, message = "표시명은 100자 이하여야 합니다.")
                    String displayName,
            @Schema(description = "로고 URL", example = "https://example.com/logo.png")
                    @Size(max = 500, message = "로고 URL은 500자 이하여야 합니다.")
                    String logoUrl,
            @Schema(description = "설명", example = "테스트 셀러 설명입니다.")
                    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다.")
                    String description) {}

    @Schema(description = "사업자 정보")
    public record BusinessInfo(
            @Schema(
                            description = "사업자등록번호",
                            example = "123-45-67890",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "사업자등록번호는 필수입니다.")
                    @Size(max = 20, message = "사업자등록번호는 20자 이하여야 합니다.")
                    String registrationNumber,
            @Schema(
                            description = "회사명",
                            example = "테스트컴퍼니",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "회사명은 필수입니다.")
                    @Size(max = 100, message = "회사명은 100자 이하여야 합니다.")
                    String companyName,
            @Schema(
                            description = "대표자명",
                            example = "홍길동",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "대표자명은 필수입니다.")
                    @Size(max = 50, message = "대표자명은 50자 이하여야 합니다.")
                    String representative,
            @Schema(description = "통신판매업 신고번호", example = "제2025-서울강남-1234호")
                    @Size(max = 50, message = "통신판매업 신고번호는 50자 이하여야 합니다.")
                    String saleReportNumber,
            @Schema(description = "사업장 주소 정보", requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull
                    @Valid
                    AddressDetail businessAddress) {}

    @Schema(description = "CS 연락처 정보")
    public record CsContactInfo(
            @Schema(
                            description = "전화번호",
                            example = "02-1234-5678",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "전화번호는 필수입니다.")
                    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
                    String phone,
            @Schema(
                            description = "이메일",
                            example = "cs@example.com",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "이메일은 필수입니다.")
                    @Email(message = "올바른 이메일 형식이 아닙니다.")
                    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
                    String email,
            @Schema(description = "휴대폰번호", example = "010-1234-5678")
                    @Size(max = 20, message = "휴대폰번호는 20자 이하여야 합니다.")
                    String mobile) {}

    @Schema(description = "주소 상세")
    public record AddressDetail(
            @Schema(
                            description = "우편번호",
                            example = "12345",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "우편번호는 필수입니다.")
                    @Size(max = 10, message = "우편번호는 10자 이하여야 합니다.")
                    String zipCode,
            @Schema(
                            description = "주소1",
                            example = "서울시 강남구",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "주소는 필수입니다.")
                    @Size(max = 200, message = "주소는 200자 이하여야 합니다.")
                    String line1,
            @Schema(description = "주소2", example = "테헤란로 123")
                    @Size(max = 200, message = "상세주소는 200자 이하여야 합니다.")
                    String line2) {}

    @Schema(description = "담당자 연락처")
    public record ContactInfo(
            @Schema(description = "담당자명", example = "김담당")
                    @Size(max = 50, message = "담당자명은 50자 이하여야 합니다.")
                    String name,
            @Schema(description = "연락처", example = "010-9876-5432")
                    @Size(max = 20, message = "연락처는 20자 이하여야 합니다.")
                    String phone,
            @Schema(description = "이메일", example = "contact@example.com")
                    @Email(message = "올바른 이메일 형식이 아닙니다.")
                    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
                    String email) {}

    @Schema(description = "정산 정보")
    public record SettlementInfo(
            @Schema(description = "은행 코드", example = "088")
                    @Size(max = 15, message = "은행 코드는 15자 이하여야 합니다.")
                    String bankCode,
            @Schema(
                            description = "은행명",
                            example = "신한은행",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "은행명은 필수입니다.")
                    @Size(max = 50, message = "은행명은 50자 이하여야 합니다.")
                    String bankName,
            @Schema(
                            description = "계좌번호",
                            example = "110123456789",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "계좌번호는 필수입니다.")
                    @Size(max = 30, message = "계좌번호는 30자 이하여야 합니다.")
                    @Pattern(regexp = "^[0-9]+$", message = "계좌번호는 숫자만 입력 가능합니다.")
                    String accountNumber,
            @Schema(
                            description = "예금주",
                            example = "홍길동",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "예금주는 필수입니다.")
                    @Size(max = 50, message = "예금주는 50자 이하여야 합니다.")
                    String accountHolderName,
            @Schema(description = "정산 주기 (미입력 시 MONTHLY)", example = "MONTHLY")
                    @Pattern(
                            regexp = "^(WEEKLY|BIWEEKLY|MONTHLY)$",
                            message = "정산 주기는 WEEKLY, BIWEEKLY, MONTHLY 중 하나여야 합니다.")
                    String settlementCycle,
            @Schema(description = "정산일 1-31 (미입력 시 1)", example = "1")
                    @Min(value = 1, message = "정산일은 1 이상이어야 합니다.")
                    @Max(value = 31, message = "정산일은 31 이하여야 합니다.")
                    Integer settlementDay) {}
}
