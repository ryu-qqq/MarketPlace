package com.ryuqq.marketplace.application.sellerapplication.assembler;

import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult.AgreementResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult.BusinessInfoResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult.ContactInfoResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult.CsContactResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult.SellerInfoResult;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.vo.ContactInfo;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.vo.Agreement;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerApplication Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerApplicationAssembler {

    /**
     * Domain → SellerApplicationResult 변환.
     *
     * @param application SellerApplication 도메인 객체
     * @return SellerApplicationResult
     */
    public SellerApplicationResult toResult(SellerApplication application) {
        return new SellerApplicationResult(
                application.idValue(),
                toSellerInfoResult(application),
                toBusinessInfoResult(application),
                toCsContactResult(application.csContact()),
                toContactInfoResult(application.contactInfo()),
                toAgreementResult(application.agreement()),
                application.status().name(),
                application.appliedAt(),
                application.processedAt(),
                application.processedBy(),
                application.rejectionReason(),
                application.approvedSellerIdValue());
    }

    private SellerInfoResult toSellerInfoResult(SellerApplication application) {
        return new SellerInfoResult(
                application.sellerNameValue(),
                application.displayNameValue(),
                application.logoUrlValue(),
                application.descriptionValue());
    }

    private BusinessInfoResult toBusinessInfoResult(SellerApplication application) {
        return new BusinessInfoResult(
                application.registrationNumberValue(),
                application.companyNameValue(),
                application.representativeValue(),
                application.saleReportNumberValue(),
                toAddressResult(application.businessAddress()));
    }

    private ContactInfoResult toContactInfoResult(ContactInfo contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        return new ContactInfoResult(
                contactInfo.name(), contactInfo.phoneValue(), contactInfo.emailValue());
    }

    private CsContactResult toCsContactResult(CsContact csContact) {
        if (csContact == null) {
            return null;
        }
        return new CsContactResult(
                csContact.phoneValue(), csContact.emailValue(), csContact.mobileValue());
    }

    private SellerApplicationResult.AddressResult toAddressResult(Address address) {
        if (address == null) {
            return null;
        }
        return new SellerApplicationResult.AddressResult(
                address.zipcode(), address.line1(), address.line2());
    }

    private AgreementResult toAgreementResult(Agreement agreement) {
        if (agreement == null) {
            return null;
        }
        return new AgreementResult(
                agreement.agreedAtValue(), agreement.isTermsAgreed(), agreement.isPrivacyAgreed());
    }

    /**
     * Domain List → SellerApplicationResult List 변환.
     *
     * @param applications SellerApplication 도메인 객체 목록
     * @return SellerApplicationResult 목록
     */
    public List<SellerApplicationResult> toResults(List<SellerApplication> applications) {
        return applications.stream().map(this::toResult).toList();
    }

    /**
     * 페이지 결과 생성.
     *
     * @param applications SellerApplication 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalCount 전체 개수
     * @return SellerApplicationPageResult
     */
    public SellerApplicationPageResult toPageResult(
            List<SellerApplication> applications, int page, int size, long totalCount) {
        List<SellerApplicationResult> results = toResults(applications);
        return SellerApplicationPageResult.of(results, totalCount, page, size);
    }
}
