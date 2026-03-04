package com.ryuqq.marketplace.application.sellerapplication.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.sellerapplication.dto.bundle.SellerCreationBundle;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApplySellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.RejectSellerApplicationCommand;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.CommissionRate;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.ContactInfo;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.DisplayName;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SellerApplication Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerApplicationCommandFactory {

    private final TimeProvider timeProvider;

    public SellerApplicationCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 신청 Command로부터 SellerApplication 도메인 객체 생성.
     *
     * @param command 신청 Command
     * @return SellerApplication 도메인 객체
     */
    public SellerApplication create(ApplySellerApplicationCommand command) {
        Instant now = timeProvider.now();
        var seller = command.sellerInfo();
        var biz = command.businessInfo();
        var addr = biz.businessAddress();
        var cs = command.csContact();
        var contact = command.contactInfo();
        var settle = command.settlementInfo();

        return SellerApplication.apply(
                SellerName.of(seller.sellerName()),
                DisplayName.of(seller.displayName()),
                LogoUrl.of(seller.logoUrl()),
                Description.of(seller.description()),
                RegistrationNumber.of(biz.registrationNumber()),
                CompanyName.of(biz.companyName()),
                Representative.of(biz.representative()),
                SaleReportNumber.of(biz.saleReportNumber()),
                Address.of(addr.zipCode(), addr.line1(), addr.line2()),
                CsContact.of(cs.phone(), cs.mobile(), cs.email()),
                ContactInfo.of(contact.name(), contact.phone(), contact.email()),
                BankAccount.of(
                        settle.bankCode(),
                        settle.bankName(),
                        settle.accountNumber(),
                        settle.accountHolderName()),
                SettlementCycle.valueOf(settle.settlementCycle()),
                settle.settlementDay(),
                now);
    }

    /**
     * 거절 Command로부터 StatusChangeContext 생성.
     *
     * @param command 거절 Command
     * @return StatusChangeContext (신청 ID, 변경 시간)
     */
    public StatusChangeContext<SellerApplicationId> createRejectContext(
            RejectSellerApplicationCommand command) {
        return new StatusChangeContext<>(
                SellerApplicationId.of(command.sellerApplicationId()), timeProvider.now());
    }

    /**
     * 입점 신청으로부터 셀러 생성 번들을 생성합니다.
     *
     * <p>승인 시 SellerApplication 정보를 기반으로 Seller 관련 도메인 객체를 생성합니다.
     *
     * @param application 승인된 입점 신청
     * @return SellerCreationBundle
     */
    public SellerCreationBundle createSellerBundle(SellerApplication application) {
        Instant now = timeProvider.now();

        Seller seller =
                Seller.forNew(
                        application.sellerName(),
                        application.displayName(),
                        application.logoUrl(),
                        application.description(),
                        now);

        SellerBusinessInfo businessInfo =
                SellerBusinessInfo.forNew(
                        application.registrationNumber(),
                        application.companyName(),
                        application.representative(),
                        application.saleReportNumber(),
                        application.businessAddress(),
                        now);

        SellerCs sellerCs = SellerCs.defaultCs(null, application.csContact(), now);

        SellerContract sellerContract =
                SellerContract.defaultContract(null, CommissionRate.zero(), now);

        SellerSettlement sellerSettlement =
                SellerSettlement.forNew(
                        application.bankAccount(),
                        application.settlementCycle(),
                        application.settlementDay(),
                        now);

        SellerAuthOutbox authOutbox =
                SellerAuthOutbox.forNew(buildAuthOutboxPayload(application), now);

        return new SellerCreationBundle(
                seller, businessInfo, sellerCs, sellerContract, sellerSettlement, authOutbox, now);
    }

    /**
     * 인증 서버 Outbox payload 생성.
     *
     * <p>인증 서버에 Tenant/Organization 생성 시 필요한 정보를 JSON으로 변환합니다.
     *
     * @param application 입점 신청
     * @return JSON payload
     */
    private String buildAuthOutboxPayload(SellerApplication application) {
        return String.format(
                "{\"tenantName\":\"%s\",\"organizationName\":\"%s\"}",
                application.sellerNameValue(), application.companyNameValue());
    }
}
