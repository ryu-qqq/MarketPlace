package com.ryuqq.marketplace.adapter.in.rest.seller;

import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.RegisterSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerFullApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.query.SearchSellersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerDetailApiResponse;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerFullCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerPolicyCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Seller API 테스트 Fixtures.
 *
 * <p>Seller REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class SellerApiFixtures {

    private SellerApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_SELLER_NAME = "테스트셀러";
    public static final String DEFAULT_DISPLAY_NAME = "테스트 브랜드";
    public static final String DEFAULT_LOGO_URL = "https://example.com/logo.png";
    public static final String DEFAULT_DESCRIPTION = "테스트 셀러 설명입니다.";

    // ===== RegisterSellerApiRequest =====

    public static RegisterSellerApiRequest registerRequest() {
        return new RegisterSellerApiRequest(defaultSellerInfo(), defaultBusinessInfo());
    }

    public static RegisterSellerApiRequest.SellerInfoRequest defaultSellerInfo() {
        return new RegisterSellerApiRequest.SellerInfoRequest(
                DEFAULT_SELLER_NAME, DEFAULT_DISPLAY_NAME, DEFAULT_LOGO_URL, DEFAULT_DESCRIPTION);
    }

    public static RegisterSellerApiRequest.BusinessInfoRequest defaultBusinessInfo() {
        return new RegisterSellerApiRequest.BusinessInfoRequest(
                "123-45-67890",
                "테스트컴퍼니",
                "홍길동",
                "제2025-서울강남-1234호",
                defaultBusinessAddress(),
                defaultCsContact());
    }

    public static RegisterSellerApiRequest.AddressRequest defaultAddress() {
        return new RegisterSellerApiRequest.AddressRequest("12345", "서울시 강남구", "테헤란로 123");
    }

    public static RegisterSellerApiRequest.AddressRequest defaultBusinessAddress() {
        return new RegisterSellerApiRequest.AddressRequest("54321", "서울시 서초구", "강남대로 456");
    }

    public static RegisterSellerApiRequest.CsContactRequest defaultCsContact() {
        return new RegisterSellerApiRequest.CsContactRequest(
                "02-1234-5678", "cs@example.com", "010-1234-5678");
    }

    // ===== UpdateSellerApiRequest =====

    public static UpdateSellerApiRequest updateRequest() {
        return new UpdateSellerApiRequest(
                "수정된 셀러명",
                "수정된 표시명",
                "https://example.com/new-logo.png",
                "수정된 설명",
                updateAddressRequest(),
                updateCsInfoRequest(),
                updateBusinessInfoRequest());
    }

    public static UpdateSellerApiRequest.AddressRequest updateAddressRequest() {
        return new UpdateSellerApiRequest.AddressRequest("99999", "서울시 종로구", "종로 789");
    }

    public static UpdateSellerApiRequest.CsInfoRequest updateCsInfoRequest() {
        return new UpdateSellerApiRequest.CsInfoRequest(
                "02-9999-8888", "newcs@example.com", "010-9999-8888");
    }

    public static UpdateSellerApiRequest.BusinessInfoRequest updateBusinessInfoRequest() {
        return new UpdateSellerApiRequest.BusinessInfoRequest(
                "999-88-77777",
                "새로운컴퍼니",
                "김철수",
                "제2025-서울종로-9999호",
                updateBusinessAddressRequest());
    }

    public static UpdateSellerApiRequest.AddressRequest updateBusinessAddressRequest() {
        return new UpdateSellerApiRequest.AddressRequest("88888", "서울시 마포구", "마포대로 999");
    }

    // ===== UpdateSellerFullApiRequest =====

    public static UpdateSellerFullApiRequest updateFullRequest() {
        return new UpdateSellerFullApiRequest(
                fullSellerInfo(),
                fullBusinessInfo(),
                fullCsInfo(),
                fullContractInfo(),
                fullSettlementInfo());
    }

    public static UpdateSellerFullApiRequest.SellerInfoRequest fullSellerInfo() {
        return new UpdateSellerFullApiRequest.SellerInfoRequest(
                "전체수정 셀러명", "전체수정 표시명", "https://example.com/full-logo.png", "전체수정 설명");
    }

    public static UpdateSellerFullApiRequest.BusinessInfoRequest fullBusinessInfo() {
        return new UpdateSellerFullApiRequest.BusinessInfoRequest(
                "777-66-55555", "전체수정컴퍼니", "이영희", "제2025-서울강남-7777호", fullBusinessAddress());
    }

    public static UpdateSellerFullApiRequest.AddressRequest fullBusinessAddress() {
        return new UpdateSellerFullApiRequest.AddressRequest("66666", "서울시 강서구", "공항대로 666");
    }

    public static UpdateSellerFullApiRequest.CsInfoRequest fullCsInfo() {
        return new UpdateSellerFullApiRequest.CsInfoRequest(
                "02-7777-6666",
                "fullcs@example.com",
                "010-7777-6666",
                "09:00",
                "18:00",
                "MON,TUE,WED,THU,FRI",
                "https://kakao.com/channel/test");
    }

    public static UpdateSellerFullApiRequest.ContractInfoRequest fullContractInfo() {
        return new UpdateSellerFullApiRequest.ContractInfoRequest(
                15.5, "2025-01-01", "2025-12-31", "특약사항입니다");
    }

    public static UpdateSellerFullApiRequest.SettlementInfoRequest fullSettlementInfo() {
        return new UpdateSellerFullApiRequest.SettlementInfoRequest(fullBankAccount(), "WEEKLY", 5);
    }

    public static UpdateSellerFullApiRequest.BankAccountRequest fullBankAccount() {
        return new UpdateSellerFullApiRequest.BankAccountRequest(
                "004", "KB국민은행", "123-456-789012", "이영희");
    }

    // ===== SearchSellersApiRequest =====

    public static SearchSellersApiRequest searchRequest() {
        return new SearchSellersApiRequest(null, null, null, null, null, 0, 20);
    }

    public static SearchSellersApiRequest searchRequest(
            Boolean active, String searchField, String searchWord, int page, int size) {
        return new SearchSellersApiRequest(
                active, searchField, searchWord, "createdAt", "DESC", page, size);
    }

    // ===== SellerResult (Application) =====

    public static SellerResult sellerResult(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SellerResult(
                id,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                now,
                now);
    }

    public static SellerResult sellerResult(Long id, String sellerName, boolean active) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SellerResult(
                id,
                sellerName,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                active,
                now,
                now);
    }

    public static List<SellerResult> sellerResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> sellerResult((long) i, "셀러_" + i, true))
                .toList();
    }

    public static SellerPageResult pageResult(int count, int page, int size) {
        List<SellerResult> results = sellerResults(count);
        return SellerPageResult.of(results, count, page, size);
    }

    public static SellerPageResult emptyPageResult() {
        return SellerPageResult.of(List.of(), 0, 0, 20);
    }

    // ===== SellerFullCompositeResult =====

    public static SellerFullCompositeResult fullCompositeResult(Long id) {
        return new SellerFullCompositeResult(
                compositeResult(id),
                policyCompositeResult(id),
                contractInfo(id),
                settlementInfo(id));
    }

    public static SellerPolicyCompositeResult policyCompositeResult(Long id) {
        return new SellerPolicyCompositeResult(id, List.of(), List.of());
    }

    public static SellerCompositeResult compositeResult(Long id) {
        return new SellerCompositeResult(sellerInfo(id), businessInfo(id), csInfo(id));
    }

    public static SellerCompositeResult.SellerInfo sellerInfo(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SellerCompositeResult.SellerInfo(
                id,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                now,
                now);
    }

    public static SellerCompositeResult.BusinessInfo businessInfo(Long id) {
        return new SellerCompositeResult.BusinessInfo(
                id,
                "123-45-67890",
                "테스트컴퍼니",
                "홍길동",
                "제2025-서울강남-1234호",
                "54321",
                "서울시 서초구",
                "강남대로 456");
    }

    public static SellerCompositeResult.CsInfo csInfo(Long id) {
        return new SellerCompositeResult.CsInfo(
                id,
                "02-1234-5678",
                "010-1234-5678",
                "cs@example.com",
                "09:00",
                "18:00",
                "MON,TUE,WED,THU,FRI",
                "https://kakao.com/channel/test");
    }

    public static SellerFullCompositeResult.ContractInfo contractInfo(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SellerFullCompositeResult.ContractInfo(
                id,
                BigDecimal.valueOf(15.5),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "ACTIVE",
                "특약사항입니다",
                now,
                now);
    }

    public static SellerFullCompositeResult.SettlementInfo settlementInfo(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SellerFullCompositeResult.SettlementInfo(
                id, "004", "KB국민은행", "123-456-789012", "이영희", "WEEKLY", 5, true, now, now, now);
    }

    // ===== SellerApiResponse =====

    public static SellerApiResponse apiResponse(Long id) {
        return new SellerApiResponse(
                id,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00");
    }

    // ===== SellerDetailApiResponse =====

    public static SellerDetailApiResponse detailApiResponse(Long id) {
        return new SellerDetailApiResponse(
                detailSellerInfo(id),
                detailBusinessInfo(id),
                detailCsInfo(id),
                detailContractInfo(id),
                detailSettlementInfo(id));
    }

    public static SellerDetailApiResponse.SellerInfo detailSellerInfo(Long id) {
        return new SellerDetailApiResponse.SellerInfo(
                id,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00");
    }

    public static SellerDetailApiResponse.BusinessInfo detailBusinessInfo(Long id) {
        return new SellerDetailApiResponse.BusinessInfo(
                id,
                "123-45-67890",
                "테스트컴퍼니",
                "홍길동",
                "제2025-서울강남-1234호",
                "54321",
                "서울시 서초구",
                "강남대로 456");
    }

    public static SellerDetailApiResponse.CsInfo detailCsInfo(Long id) {
        return new SellerDetailApiResponse.CsInfo(
                id,
                "02-1234-5678",
                "010-1234-5678",
                "cs@example.com",
                "09:00",
                "18:00",
                "MON,TUE,WED,THU,FRI",
                "https://kakao.com/channel/test");
    }

    public static SellerDetailApiResponse.ContractInfo detailContractInfo(Long id) {
        return new SellerDetailApiResponse.ContractInfo(
                id,
                "15.5",
                "2025-01-01",
                "2025-12-31",
                "ACTIVE",
                "특약사항입니다",
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00");
    }

    public static SellerDetailApiResponse.SettlementInfo detailSettlementInfo(Long id) {
        return new SellerDetailApiResponse.SettlementInfo(
                id,
                "004",
                "KB국민은행",
                "123-456-789012",
                "이영희",
                "WEEKLY",
                5,
                true,
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00");
    }
}
