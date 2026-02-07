package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper;

import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.ApplySellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.RejectSellerApplicationApiRequest;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApplySellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApproveSellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.RejectSellerApplicationCommand;
import org.springframework.stereotype.Component;

/**
 * SellerApplicationCommandApiMapper - 셀러 입점 신청 Command API 변환 매퍼.
 *
 * <p>API Request를 Application Command로 변환합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 순수 변환 로직만 포함.
 *
 * <p>API-MAP-003: null-safe 변환 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class SellerApplicationCommandApiMapper {

    /**
     * ApplySellerApplicationApiRequest를 ApplySellerApplicationCommand로 변환.
     *
     * @param request API 요청 DTO
     * @return Application Command
     */
    public ApplySellerApplicationCommand toCommand(ApplySellerApplicationApiRequest request) {
        var sellerInfo = request.sellerInfo();
        var businessInfo = request.businessInfo();
        var csContact = request.csContact();

        ApplySellerApplicationCommand.SellerInfoCommand sellerInfoCmd =
                new ApplySellerApplicationCommand.SellerInfoCommand(
                        sellerInfo.sellerName(),
                        nullSafe(sellerInfo.displayName(), sellerInfo.sellerName()),
                        sellerInfo.logoUrl(),
                        sellerInfo.description());

        ApplySellerApplicationCommand.AddressCommand businessAddressCmd =
                toAddressCommand(businessInfo.businessAddress());

        ApplySellerApplicationCommand.BusinessInfoCommand businessInfoCmd =
                new ApplySellerApplicationCommand.BusinessInfoCommand(
                        businessInfo.registrationNumber(),
                        businessInfo.companyName(),
                        businessInfo.representative(),
                        businessInfo.saleReportNumber(),
                        businessAddressCmd);

        ApplySellerApplicationCommand.CsContactCommand csContactCmd =
                new ApplySellerApplicationCommand.CsContactCommand(
                        csContact.phone(), csContact.email(), csContact.mobile());

        ApplySellerApplicationCommand.SettlementInfoCommand settlementInfoCmd =
                toSettlementInfoCommand(request.settlementInfo());

        ApplySellerApplicationCommand.ContactInfoCommand contactInfoCmd =
                toContactInfoCommand(request.contactInfo());

        return new ApplySellerApplicationCommand(
                sellerInfoCmd, businessInfoCmd, csContactCmd, contactInfoCmd, settlementInfoCmd);
    }

    /**
     * 승인 요청을 ApproveSellerApplicationCommand로 변환.
     *
     * <p>processedBy는 인증 컨텍스트에서 주입 예정 (현재 null).
     *
     * @param applicationId 신청 ID
     * @return Application Command
     */
    public ApproveSellerApplicationCommand toApproveCommand(Long applicationId) {
        return new ApproveSellerApplicationCommand(applicationId, null);
    }

    /**
     * RejectSellerApplicationApiRequest를 RejectSellerApplicationCommand로 변환.
     *
     * <p>processedBy는 인증 컨텍스트에서 주입 예정 (현재 null).
     *
     * @param applicationId 신청 ID
     * @param request API 요청 DTO
     * @return Application Command
     */
    public RejectSellerApplicationCommand toRejectCommand(
            Long applicationId, RejectSellerApplicationApiRequest request) {
        return new RejectSellerApplicationCommand(applicationId, request.rejectionReason(), null);
    }

    private ApplySellerApplicationCommand.AddressCommand toAddressCommand(
            ApplySellerApplicationApiRequest.AddressDetail address) {
        if (address == null) {
            return new ApplySellerApplicationCommand.AddressCommand("", "", "");
        }
        return new ApplySellerApplicationCommand.AddressCommand(
                nullSafe(address.zipCode()), nullSafe(address.line1()), nullSafe(address.line2()));
    }

    private ApplySellerApplicationCommand.ContactInfoCommand toContactInfoCommand(
            ApplySellerApplicationApiRequest.ContactInfo contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        return new ApplySellerApplicationCommand.ContactInfoCommand(
                contactInfo.name(), contactInfo.phone(), contactInfo.email());
    }

    private static final String DEFAULT_SETTLEMENT_CYCLE = "MONTHLY";
    private static final int DEFAULT_SETTLEMENT_DAY = 1;

    private ApplySellerApplicationCommand.SettlementInfoCommand toSettlementInfoCommand(
            ApplySellerApplicationApiRequest.SettlementInfo settlementInfo) {
        String cycle = settlementInfo.settlementCycle();
        if (cycle == null || cycle.isBlank()) {
            cycle = DEFAULT_SETTLEMENT_CYCLE;
        }
        Integer day = settlementInfo.settlementDay();
        if (day == null || day < 1 || day > 31) {
            day = DEFAULT_SETTLEMENT_DAY;
        }
        return new ApplySellerApplicationCommand.SettlementInfoCommand(
                settlementInfo.bankCode(),
                settlementInfo.bankName(),
                settlementInfo.accountNumber(),
                settlementInfo.accountHolderName(),
                cycle,
                day);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String nullSafe(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
