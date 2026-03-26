package com.ryuqq.marketplace.adapter.in.rest.sellerapplication;

import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.ApplySellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.RejectSellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.query.SearchSellerApplicationsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response.SellerApplicationApiResponse;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * SellerApplication API 테스트 Fixtures.
 *
 * <p>SellerApplication REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class SellerApplicationApiFixtures {

    private SellerApplicationApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_SELLER_NAME = "테스트셀러";
    public static final String DEFAULT_DISPLAY_NAME = "테스트 브랜드";
    public static final String DEFAULT_LOGO_URL = "https://example.com/logo.png";
    public static final String DEFAULT_DESCRIPTION = "테스트 셀러 설명입니다.";
    public static final String DEFAULT_REGISTRATION_NUMBER = "123-45-67890";
    public static final String DEFAULT_COMPANY_NAME = "테스트컴퍼니";
    public static final String DEFAULT_REPRESENTATIVE = "홍길동";
    public static final String DEFAULT_SALE_REPORT_NUMBER = "제2025-서울강남-1234호";
    public static final String DEFAULT_PHONE = "02-1234-5678";
    public static final String DEFAULT_EMAIL = "cs@example.com";
    public static final String DEFAULT_MOBILE = "010-1234-5678";
    public static final String DEFAULT_ZIP_CODE = "12345";
    public static final String DEFAULT_LINE1 = "서울시 강남구";
    public static final String DEFAULT_LINE2 = "테헤란로 123";
    public static final String DEFAULT_CONTACT_NAME = "김담당";
    public static final String DEFAULT_CONTACT_PHONE = "010-9876-5432";
    public static final String DEFAULT_CONTACT_EMAIL = "contact@example.com";
    public static final String DEFAULT_BANK_CODE = "088";
    public static final String DEFAULT_BANK_NAME = "신한은행";
    public static final String DEFAULT_ACCOUNT_NUMBER = "110123456789";
    public static final String DEFAULT_ACCOUNT_HOLDER = "홍길동";
    public static final String DEFAULT_SETTLEMENT_CYCLE = "MONTHLY";
    public static final int DEFAULT_SETTLEMENT_DAY = 15;
    public static final String DEFAULT_REJECTION_REASON = "서류 미비";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_ISO_DATE = "2025-01-23 10:30:00";

    // ===== ApplySellerApplicationApiRequest =====

    public static ApplySellerApplicationApiRequest applyRequest() {
        return new ApplySellerApplicationApiRequest(
                defaultSellerInfo(),
                defaultBusinessInfo(),
                defaultCsContact(),
                defaultContactInfo(),
                defaultSettlementInfo());
    }

    public static ApplySellerApplicationApiRequest.SellerInfo defaultSellerInfo() {
        return new ApplySellerApplicationApiRequest.SellerInfo(
                DEFAULT_SELLER_NAME, DEFAULT_DISPLAY_NAME, DEFAULT_LOGO_URL, DEFAULT_DESCRIPTION);
    }

    public static ApplySellerApplicationApiRequest.SellerInfo sellerInfoWithBlankDisplayName() {
        return new ApplySellerApplicationApiRequest.SellerInfo(
                DEFAULT_SELLER_NAME, "", DEFAULT_LOGO_URL, DEFAULT_DESCRIPTION);
    }

    public static ApplySellerApplicationApiRequest.SellerInfo sellerInfoWithNullDisplayName() {
        return new ApplySellerApplicationApiRequest.SellerInfo(
                DEFAULT_SELLER_NAME, null, DEFAULT_LOGO_URL, DEFAULT_DESCRIPTION);
    }

    public static ApplySellerApplicationApiRequest.BusinessInfo defaultBusinessInfo() {
        return new ApplySellerApplicationApiRequest.BusinessInfo(
                DEFAULT_REGISTRATION_NUMBER,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                defaultAddressDetail());
    }

    public static ApplySellerApplicationApiRequest.CsContactInfo defaultCsContact() {
        return new ApplySellerApplicationApiRequest.CsContactInfo(
                DEFAULT_PHONE, DEFAULT_EMAIL, DEFAULT_MOBILE);
    }

    public static ApplySellerApplicationApiRequest.AddressDetail defaultAddressDetail() {
        return new ApplySellerApplicationApiRequest.AddressDetail(
                DEFAULT_ZIP_CODE, DEFAULT_LINE1, DEFAULT_LINE2);
    }

    public static ApplySellerApplicationApiRequest.ContactInfo defaultContactInfo() {
        return new ApplySellerApplicationApiRequest.ContactInfo(
                DEFAULT_CONTACT_NAME, DEFAULT_CONTACT_PHONE, DEFAULT_CONTACT_EMAIL);
    }

    public static ApplySellerApplicationApiRequest.SettlementInfo defaultSettlementInfo() {
        return new ApplySellerApplicationApiRequest.SettlementInfo(
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY);
    }

    /** 정산 주기/정산일 미입력(선택) 시나리오용. */
    public static ApplySellerApplicationApiRequest.SettlementInfo
            settlementInfoWithNullCycleAndDay() {
        return new ApplySellerApplicationApiRequest.SettlementInfo(
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER,
                null,
                null);
    }

    // ===== RejectSellerApplicationApiRequest =====

    public static RejectSellerApplicationApiRequest rejectRequest() {
        return new RejectSellerApplicationApiRequest(DEFAULT_REJECTION_REASON);
    }

    // ===== SearchSellerApplicationsApiRequest =====

    public static SearchSellerApplicationsApiRequest searchRequest() {
        return new SearchSellerApplicationsApiRequest(null, null, null, null, null, null, null);
    }

    public static SearchSellerApplicationsApiRequest searchRequest(
            List<String> status, String searchField, String searchWord, int page, int size) {
        return new SearchSellerApplicationsApiRequest(
                status, searchField, searchWord, "appliedAt", "DESC", page, size);
    }

    public static SearchSellerApplicationsApiRequest searchRequestWithStatus(List<String> status) {
        return new SearchSellerApplicationsApiRequest(
                status, null, null, "appliedAt", "DESC", 0, 20);
    }

    // ===== SellerApplicationResult (Application) =====

    public static SellerApplicationResult applicationResult(Long id) {
        return applicationResult(id, "PENDING");
    }

    public static SellerApplicationResult applicationResult(Long id, String status) {
        return new SellerApplicationResult(
                id,
                defaultSellerInfoResult(),
                defaultBusinessInfoResult(),
                defaultCsContactResult(),
                defaultContactInfoResult(),
                defaultAgreementResult(),
                status,
                DEFAULT_INSTANT,
                null,
                null,
                null,
                null);
    }

    public static SellerApplicationResult approvedResult(Long id, Long sellerId) {
        return new SellerApplicationResult(
                id,
                defaultSellerInfoResult(),
                defaultBusinessInfoResult(),
                defaultCsContactResult(),
                defaultContactInfoResult(),
                defaultAgreementResult(),
                "APPROVED",
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                "admin@example.com",
                null,
                sellerId);
    }

    public static SellerApplicationResult rejectedResult(Long id) {
        return new SellerApplicationResult(
                id,
                defaultSellerInfoResult(),
                defaultBusinessInfoResult(),
                defaultCsContactResult(),
                defaultContactInfoResult(),
                defaultAgreementResult(),
                "REJECTED",
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                "admin@example.com",
                DEFAULT_REJECTION_REASON,
                null);
    }

    public static SellerApplicationResult resultWithNullSubObjects(Long id) {
        return new SellerApplicationResult(
                id,
                null,
                null,
                null,
                null,
                null,
                "PENDING",
                DEFAULT_INSTANT,
                null,
                null,
                null,
                null);
    }

    public static SellerApplicationResult.SellerInfoResult defaultSellerInfoResult() {
        return new SellerApplicationResult.SellerInfoResult(
                DEFAULT_SELLER_NAME, DEFAULT_DISPLAY_NAME, DEFAULT_LOGO_URL, DEFAULT_DESCRIPTION);
    }

    public static SellerApplicationResult.BusinessInfoResult defaultBusinessInfoResult() {
        return new SellerApplicationResult.BusinessInfoResult(
                DEFAULT_REGISTRATION_NUMBER,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                defaultAddressResult());
    }

    public static SellerApplicationResult.CsContactResult defaultCsContactResult() {
        return new SellerApplicationResult.CsContactResult(
                DEFAULT_PHONE, DEFAULT_EMAIL, DEFAULT_MOBILE);
    }

    public static SellerApplicationResult.AddressResult defaultAddressResult() {
        return new SellerApplicationResult.AddressResult(
                DEFAULT_ZIP_CODE, DEFAULT_LINE1, DEFAULT_LINE2);
    }

    public static SellerApplicationResult.ContactInfoResult defaultContactInfoResult() {
        return new SellerApplicationResult.ContactInfoResult(
                DEFAULT_CONTACT_NAME, DEFAULT_CONTACT_PHONE, DEFAULT_CONTACT_EMAIL);
    }

    public static SellerApplicationResult.AgreementResult defaultAgreementResult() {
        return new SellerApplicationResult.AgreementResult(DEFAULT_INSTANT, true, true);
    }

    public static List<SellerApplicationResult> applicationResults(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> applicationResult((long) i)).toList();
    }

    public static SellerApplicationPageResult pageResult(int count, int page, int size) {
        List<SellerApplicationResult> results = applicationResults(count);
        return SellerApplicationPageResult.of(results, count, page, size);
    }

    public static SellerApplicationPageResult emptyPageResult() {
        return SellerApplicationPageResult.of(List.of(), 0, 0, 20);
    }

    // ===== SellerApplicationApiResponse =====

    public static SellerApplicationApiResponse apiResponse(Long id) {
        return new SellerApplicationApiResponse(
                id,
                new SellerApplicationApiResponse.SellerInfo(
                        DEFAULT_SELLER_NAME,
                        DEFAULT_DISPLAY_NAME,
                        DEFAULT_LOGO_URL,
                        DEFAULT_DESCRIPTION),
                new SellerApplicationApiResponse.BusinessInfo(
                        DEFAULT_REGISTRATION_NUMBER,
                        DEFAULT_COMPANY_NAME,
                        DEFAULT_REPRESENTATIVE,
                        DEFAULT_SALE_REPORT_NUMBER,
                        new SellerApplicationApiResponse.AddressDetail(
                                DEFAULT_ZIP_CODE, DEFAULT_LINE1, DEFAULT_LINE2)),
                new SellerApplicationApiResponse.CsContactInfo(
                        DEFAULT_PHONE, DEFAULT_EMAIL, DEFAULT_MOBILE),
                new SellerApplicationApiResponse.ContactInfo(
                        DEFAULT_CONTACT_NAME, DEFAULT_CONTACT_PHONE, DEFAULT_CONTACT_EMAIL),
                new SellerApplicationApiResponse.AgreementInfo(DEFAULT_ISO_DATE, true, true),
                "PENDING",
                DEFAULT_ISO_DATE,
                null,
                null,
                null,
                null);
    }

    public static List<SellerApplicationApiResponse> apiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> apiResponse((long) i)).toList();
    }
}
