package com.ryuqq.marketplace.adapter.in.rest.seller.mapper;

import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.RegisterSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerFullApiRequest;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;
import org.springframework.stereotype.Component;

/**
 * SellerCommandApiMapper - 셀러 Command API 변환 매퍼.
 *
 * <p>API Request를 Application Command로 변환합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 양방향 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>CQRS 분리: Command 전용 Mapper (QueryApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class SellerCommandApiMapper {

    /**
     * RegisterSellerApiRequest -> RegisterSellerCommand 변환.
     *
     * @param request 등록 요청 DTO
     * @return RegisterSellerCommand
     */
    public RegisterSellerCommand toCommand(RegisterSellerApiRequest request) {
        return new RegisterSellerCommand(
                toSellerInfoCommand(request.seller()),
                toBusinessInfoCommand(request.businessInfo()));
    }

    /**
     * UpdateSellerApiRequest -> UpdateSellerCommand 변환.
     *
     * <p>Seller 기본정보 + CS + BusinessInfo를 포함합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 수정 요청 DTO
     * @return UpdateSellerCommand
     */
    public UpdateSellerCommand toCommand(Long sellerId, UpdateSellerApiRequest request) {
        return new UpdateSellerCommand(
                sellerId,
                request.sellerName(),
                request.displayName(),
                request.logoUrl(),
                request.description(),
                toCsInfoCommand(request.csInfo()),
                toBusinessInfoCommand(request.businessInfo()));
    }

    private UpdateSellerCommand.CsInfoCommand toCsInfoCommand(
            UpdateSellerApiRequest.CsInfoRequest csInfo) {
        if (csInfo == null) {
            return null;
        }
        return new UpdateSellerCommand.CsInfoCommand(
                csInfo.phone(), csInfo.email(), csInfo.mobile());
    }

    private UpdateSellerCommand.BusinessInfoCommand toBusinessInfoCommand(
            UpdateSellerApiRequest.BusinessInfoRequest businessInfo) {
        if (businessInfo == null) {
            return null;
        }
        UpdateSellerCommand.AddressCommand businessAddress = null;
        if (businessInfo.businessAddress() != null) {
            businessAddress =
                    new UpdateSellerCommand.AddressCommand(
                            businessInfo.businessAddress().zipCode(),
                            businessInfo.businessAddress().line1(),
                            businessInfo.businessAddress().line2());
        }
        return new UpdateSellerCommand.BusinessInfoCommand(
                businessInfo.registrationNumber(),
                businessInfo.companyName(),
                businessInfo.representative(),
                businessInfo.saleReportNumber(),
                businessAddress);
    }

    /**
     * UpdateSellerFullApiRequest -> UpdateSellerFullCommand 변환.
     *
     * <p>Seller + BusinessInfo + CS + Contract + Settlement 정보를 모두 포함합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 전체 수정 요청 DTO
     * @return UpdateSellerFullCommand
     */
    public UpdateSellerFullCommand toCommand(Long sellerId, UpdateSellerFullApiRequest request) {
        return new UpdateSellerFullCommand(
                sellerId,
                toSellerInfoCommand(request.seller()),
                toBusinessInfoCommand(request.businessInfo(), request.csInfo()),
                toCsInfoCommand(request.csInfo()),
                toContractInfoCommand(request.contractInfo()),
                toSettlementInfoCommand(request.settlementInfo()));
    }

    // ========================================================================
    // Register Command 변환 메서드
    // ========================================================================

    private RegisterSellerCommand.SellerInfoCommand toSellerInfoCommand(
            RegisterSellerApiRequest.SellerInfoRequest request) {
        return new RegisterSellerCommand.SellerInfoCommand(
                request.sellerName(),
                request.displayName(),
                request.logoUrl(),
                request.description());
    }

    private RegisterSellerCommand.SellerBusinessInfoCommand toBusinessInfoCommand(
            RegisterSellerApiRequest.BusinessInfoRequest request) {
        return new RegisterSellerCommand.SellerBusinessInfoCommand(
                request.registrationNumber(),
                request.companyName(),
                request.representative(),
                request.saleReportNumber(),
                toAddressCommand(request.businessAddress()),
                toCsContactCommand(request.csContact()));
    }

    private RegisterSellerCommand.AddressCommand toAddressCommand(
            RegisterSellerApiRequest.AddressRequest request) {
        return new RegisterSellerCommand.AddressCommand(
                request.zipCode(), request.line1(), request.line2());
    }

    private RegisterSellerCommand.CsContactCommand toCsContactCommand(
            RegisterSellerApiRequest.CsContactRequest request) {
        return new RegisterSellerCommand.CsContactCommand(
                request.phone(), request.email(), request.mobile());
    }

    // ========================================================================
    // UpdateFull Command 변환 메서드
    // ========================================================================

    private UpdateSellerFullCommand.SellerInfoCommand toSellerInfoCommand(
            UpdateSellerFullApiRequest.SellerInfoRequest request) {
        return new UpdateSellerFullCommand.SellerInfoCommand(
                request.sellerName(),
                request.displayName(),
                request.logoUrl(),
                request.description());
    }

    private UpdateSellerFullCommand.SellerBusinessInfoCommand toBusinessInfoCommand(
            UpdateSellerFullApiRequest.BusinessInfoRequest request,
            UpdateSellerFullApiRequest.CsInfoRequest csInfo) {
        return new UpdateSellerFullCommand.SellerBusinessInfoCommand(
                request.registrationNumber(),
                request.companyName(),
                request.representative(),
                request.saleReportNumber(),
                toAddressCommand(request.businessAddress()),
                toCsContactCommand(csInfo));
    }

    private UpdateSellerFullCommand.AddressCommand toAddressCommand(
            UpdateSellerFullApiRequest.AddressRequest request) {
        return new UpdateSellerFullCommand.AddressCommand(
                request.zipCode(), request.line1(), request.line2());
    }

    private UpdateSellerFullCommand.CsContactCommand toCsContactCommand(
            UpdateSellerFullApiRequest.CsInfoRequest csInfo) {
        return new UpdateSellerFullCommand.CsContactCommand(
                csInfo.phone(), csInfo.email(), csInfo.mobile());
    }

    private UpdateSellerFullCommand.CsInfoCommand toCsInfoCommand(
            UpdateSellerFullApiRequest.CsInfoRequest csInfo) {
        UpdateSellerFullCommand.CsContactCommand csContact =
                new UpdateSellerFullCommand.CsContactCommand(
                        csInfo.phone(), csInfo.email(), csInfo.mobile());

        UpdateSellerFullCommand.OperatingHoursCommand operatingHours = null;
        if (csInfo.operatingStartTime() != null && csInfo.operatingEndTime() != null) {
            operatingHours =
                    new UpdateSellerFullCommand.OperatingHoursCommand(
                            csInfo.operatingStartTime(), csInfo.operatingEndTime());
        }

        return new UpdateSellerFullCommand.CsInfoCommand(
                csContact, operatingHours, csInfo.operatingDays(), csInfo.kakaoChannelUrl());
    }

    private UpdateSellerFullCommand.ContractInfoCommand toContractInfoCommand(
            UpdateSellerFullApiRequest.ContractInfoRequest contractInfo) {
        return new UpdateSellerFullCommand.ContractInfoCommand(
                contractInfo.commissionRate(),
                contractInfo.contractStartDate(),
                contractInfo.contractEndDate(),
                contractInfo.specialTerms());
    }

    private UpdateSellerFullCommand.SettlementInfoCommand toSettlementInfoCommand(
            UpdateSellerFullApiRequest.SettlementInfoRequest settlementInfo) {
        return new UpdateSellerFullCommand.SettlementInfoCommand(
                toBankAccountCommand(settlementInfo.bankAccount()),
                settlementInfo.settlementCycle(),
                settlementInfo.settlementDay());
    }

    private UpdateSellerFullCommand.BankAccountCommand toBankAccountCommand(
            UpdateSellerFullApiRequest.BankAccountRequest bankAccount) {
        return new UpdateSellerFullCommand.BankAccountCommand(
                bankAccount.bankCode(),
                bankAccount.bankName(),
                bankAccount.accountNumber(),
                bankAccount.accountHolderName());
    }
}
