package com.ryuqq.marketplace.application.seller.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.seller.dto.bundle.SellerRegistrationBundle;
import com.ryuqq.marketplace.application.seller.dto.bundle.SellerUpdateBundle;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerBusinessInfoCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfoUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContractUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCsUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlementUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerUpdateData;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.CommissionRate;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.DisplayName;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

/**
 * Seller Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class SellerCommandFactory {

    private final TimeProvider timeProvider;

    public SellerCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 SellerRegistrationBundle 생성.
     *
     * <p>Seller + BusinessInfo + Address를 한번에 묶어서 관리합니다. (모두 1:1 관계) SellerId는 Seller persist 후
     * withSellerId()를 호출하여 설정합니다.
     *
     * @param command 등록 Command
     * @return SellerRegistrationBundle
     */
    public SellerRegistrationBundle createRegistrationBundle(RegisterSellerCommand command) {
        Instant now = timeProvider.now();

        Seller seller = createSeller(command, now);
        SellerBusinessInfo businessInfo = createBusinessInfo(command, now);

        return new SellerRegistrationBundle(seller, businessInfo);
    }

    /**
     * 수정 Command로부터 SellerUpdateBundle 생성.
     *
     * <p>Seller + BusinessInfo + Address + CS + Contract + Settlement 수정 데이터를 한번에 묶어서 관리합니다. (모두
     * 1:1 관계)
     *
     * @param command 수정 Command
     * @return SellerUpdateBundle
     */
    public SellerUpdateBundle createUpdateBundle(UpdateSellerFullCommand command) {
        SellerId sellerId = SellerId.of(command.sellerId());
        Instant changedAt = timeProvider.now();

        SellerUpdateData sellerUpdateData = createSellerUpdateData(command.seller());
        SellerBusinessInfoUpdateData businessInfoUpdateData =
                createBusinessInfoUpdateData(command.businessInfo());
        SellerCsUpdateData csUpdateData = createCsUpdateData(command.csInfo());
        SellerContractUpdateData contractUpdateData =
                createContractUpdateData(command.contractInfo());
        SellerSettlementUpdateData settlementUpdateData =
                createSettlementUpdateData(command.settlementInfo());

        return new SellerUpdateBundle(
                sellerId,
                sellerUpdateData,
                businessInfoUpdateData,
                csUpdateData,
                contractUpdateData,
                settlementUpdateData,
                changedAt);
    }

    /**
     * 등록 Command로부터 Seller 도메인 객체 생성.
     *
     * @param command 등록 Command
     * @return Seller 도메인 객체
     */
    public Seller createSeller(RegisterSellerCommand command) {
        return createSeller(command, timeProvider.now());
    }

    private Seller createSeller(RegisterSellerCommand command, Instant now) {
        RegisterSellerCommand.SellerInfoCommand info = command.seller();
        return Seller.forNew(
                SellerName.of(info.sellerName()),
                DisplayName.of(info.displayName()),
                info.logoUrl() != null ? LogoUrl.of(info.logoUrl()) : null,
                info.description() != null ? Description.of(info.description()) : null,
                now);
    }

    /**
     * 등록 Command로부터 SellerBusinessInfo 생성 (SellerId 없이).
     *
     * <p>SellerId는 나중에 assignSellerId()로 설정합니다.
     */
    private SellerBusinessInfo createBusinessInfo(RegisterSellerCommand command, Instant now) {
        RegisterSellerCommand.SellerBusinessInfoCommand info = command.businessInfo();
        return SellerBusinessInfo.forNew(
                RegistrationNumber.of(info.registrationNumber()),
                CompanyName.of(info.companyName()),
                Representative.of(info.representative()),
                info.saleReportNumber() != null
                        ? SaleReportNumber.of(info.saleReportNumber())
                        : null,
                toAddress(info.businessAddress()),
                now);
    }

    /**
     * UpdateSellerCommand로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (SellerId, SellerUpdateData, changedAt)
     */
    public UpdateContext<SellerId, SellerUpdateData> createUpdateContext(
            UpdateSellerCommand command) {
        SellerId sellerId = SellerId.of(command.sellerId());
        SellerUpdateData updateData =
                SellerUpdateData.of(
                        SellerName.of(command.sellerName()),
                        DisplayName.of(command.displayName()),
                        command.logoUrl() != null ? LogoUrl.of(command.logoUrl()) : null,
                        command.description() != null
                                ? Description.of(command.description())
                                : null);
        return new UpdateContext<>(sellerId, updateData, timeProvider.now());
    }

    /**
     * UpdateSellerCommand.CsInfoCommand로부터 SellerCsUpdateData 생성.
     *
     * @param csCmd CS Command
     * @return SellerCsUpdateData (null이면 null 반환)
     */
    public SellerCsUpdateData createCsUpdateData(UpdateSellerCommand.CsInfoCommand csCmd) {
        if (csCmd == null) {
            return null;
        }
        CsContact csContact = CsContact.of(csCmd.phone(), csCmd.mobile(), csCmd.email());
        return SellerCsUpdateData.of(
                csContact, OperatingHours.businessHours(), "MON,TUE,WED,THU,FRI", null);
    }

    /**
     * UpdateSellerCommand.BusinessInfoCommand로부터 SellerBusinessInfoUpdateData 생성.
     *
     * @param businessCmd 사업자 정보 Command
     * @return SellerBusinessInfoUpdateData (null이면 null 반환)
     */
    public SellerBusinessInfoUpdateData createBusinessInfoUpdateData(
            UpdateSellerCommand.BusinessInfoCommand businessCmd) {
        if (businessCmd == null) {
            return null;
        }
        return SellerBusinessInfoUpdateData.of(
                RegistrationNumber.of(businessCmd.registrationNumber()),
                CompanyName.of(businessCmd.companyName()),
                Representative.of(businessCmd.representative()),
                businessCmd.saleReportNumber() != null
                        ? SaleReportNumber.of(businessCmd.saleReportNumber())
                        : null,
                toAddress(businessCmd.businessAddress()));
    }

    /**
     * 현재 시간을 반환합니다.
     *
     * @return 현재 시간
     */
    public Instant now() {
        return timeProvider.now();
    }

    private Address toAddress(UpdateSellerCommand.AddressCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return Address.of(cmd.zipCode(), cmd.line1(), cmd.line2());
    }

    /**
     * UpdateSellerBusinessInfoCommand로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (SellerId, SellerBusinessInfoUpdateData, changedAt)
     */
    public UpdateContext<SellerId, SellerBusinessInfoUpdateData> createUpdateContext(
            UpdateSellerBusinessInfoCommand command) {
        SellerId sellerId = SellerId.of(command.sellerId());
        SellerBusinessInfoUpdateData updateData =
                SellerBusinessInfoUpdateData.of(
                        RegistrationNumber.of(command.registrationNumber()),
                        CompanyName.of(command.companyName()),
                        Representative.of(command.representative()),
                        command.saleReportNumber() != null
                                ? SaleReportNumber.of(command.saleReportNumber())
                                : null,
                        toAddress(command.businessAddress()));
        return new UpdateContext<>(sellerId, updateData, timeProvider.now());
    }

    /**
     * UpdateSellerFullCommand.SellerInfoCommand로부터 SellerUpdateData 생성.
     *
     * @param info 셀러 정보 Command
     * @return SellerUpdateData
     */
    public SellerUpdateData createSellerUpdateData(UpdateSellerFullCommand.SellerInfoCommand info) {
        return SellerUpdateData.of(
                SellerName.of(info.sellerName()),
                DisplayName.of(info.displayName()),
                info.logoUrl() != null ? LogoUrl.of(info.logoUrl()) : null,
                info.description() != null ? Description.of(info.description()) : null);
    }

    /**
     * UpdateSellerFullCommand.SellerBusinessInfoCommand로부터 SellerBusinessInfoUpdateData 생성.
     *
     * @param info 사업자 정보 Command
     * @return SellerBusinessInfoUpdateData
     */
    public SellerBusinessInfoUpdateData createBusinessInfoUpdateData(
            UpdateSellerFullCommand.SellerBusinessInfoCommand info) {
        return SellerBusinessInfoUpdateData.of(
                RegistrationNumber.of(info.registrationNumber()),
                CompanyName.of(info.companyName()),
                Representative.of(info.representative()),
                info.saleReportNumber() != null
                        ? SaleReportNumber.of(info.saleReportNumber())
                        : null,
                toAddress(info.businessAddress()));
    }

    /**
     * UpdateSellerFullCommand.CsInfoCommand로부터 SellerCsUpdateData 생성.
     *
     * @param csInfo CS 정보 Command
     * @return SellerCsUpdateData
     */
    public SellerCsUpdateData createCsUpdateData(UpdateSellerFullCommand.CsInfoCommand csInfo) {
        CsContact csContact = toCsContact(csInfo.csContact());
        OperatingHours operatingHours = toOperatingHours(csInfo.operatingHours());
        return SellerCsUpdateData.of(
                csContact, operatingHours, csInfo.operatingDays(), csInfo.kakaoChannelUrl());
    }

    /**
     * UpdateSellerFullCommand.ContractInfoCommand로부터 SellerContractUpdateData 생성.
     *
     * @param contractInfo 계약 정보 Command
     * @return SellerContractUpdateData
     */
    public SellerContractUpdateData createContractUpdateData(
            UpdateSellerFullCommand.ContractInfoCommand contractInfo) {
        CommissionRate commissionRate =
                contractInfo.commissionRate() != null
                        ? CommissionRate.of(BigDecimal.valueOf(contractInfo.commissionRate()))
                        : null;
        LocalDate startDate = parseDate(contractInfo.contractStartDate());
        LocalDate endDate = parseDate(contractInfo.contractEndDate());

        return SellerContractUpdateData.of(
                commissionRate, startDate, endDate, contractInfo.specialTerms());
    }

    /**
     * UpdateSellerFullCommand.SettlementInfoCommand로부터 SellerSettlementUpdateData 생성.
     *
     * @param settlementInfo 정산 정보 Command
     * @return SellerSettlementUpdateData
     */
    public SellerSettlementUpdateData createSettlementUpdateData(
            UpdateSellerFullCommand.SettlementInfoCommand settlementInfo) {
        BankAccount bankAccount = toBankAccount(settlementInfo.bankAccount());
        SettlementCycle cycle =
                settlementInfo.settlementCycle() != null
                        ? SettlementCycle.valueOf(settlementInfo.settlementCycle())
                        : null;

        return SellerSettlementUpdateData.of(bankAccount, cycle, settlementInfo.settlementDay());
    }

    private Address toAddress(RegisterSellerCommand.AddressCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return Address.of(cmd.zipCode(), cmd.line1(), cmd.line2());
    }

    private Address toAddress(UpdateSellerBusinessInfoCommand.AddressCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return Address.of(cmd.zipCode(), cmd.line1(), cmd.line2());
    }

    private Address toAddress(UpdateSellerFullCommand.AddressCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return Address.of(cmd.zipCode(), cmd.line1(), cmd.line2());
    }

    private CsContact toCsContact(UpdateSellerFullCommand.CsContactCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return CsContact.of(cmd.phone(), cmd.mobile(), cmd.email());
    }

    private OperatingHours toOperatingHours(UpdateSellerFullCommand.OperatingHoursCommand cmd) {
        if (cmd == null) {
            return OperatingHours.businessHours();
        }
        LocalTime startTime = parseTime(cmd.startTime());
        LocalTime endTime = parseTime(cmd.endTime());
        return OperatingHours.of(startTime, endTime);
    }

    private LocalTime parseTime(String timeString) {
        if (timeString == null || timeString.isBlank()) {
            return null;
        }
        return LocalTime.parse(timeString);
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateString);
    }

    private BankAccount toBankAccount(UpdateSellerFullCommand.BankAccountCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return BankAccount.of(
                cmd.bankCode(), cmd.bankName(), cmd.accountNumber(), cmd.accountHolderName());
    }
}
