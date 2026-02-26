package com.ryuqq.marketplace.application.seller.assembler;

import com.ryuqq.marketplace.application.seller.dto.response.SellerBusinessInfoResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerCustomerResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Seller Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 */
@Component
public class SellerAssembler {

    /**
     * Domain → SellerResult 변환.
     *
     * @param seller Seller 도메인 객체
     * @return SellerResult
     */
    public SellerResult toResult(Seller seller) {
        return SellerResult.from(seller);
    }

    /**
     * Domain List → SellerResult List 변환.
     *
     * @param sellers Seller 도메인 객체 목록
     * @return SellerResult 목록
     */
    public List<SellerResult> toResults(List<Seller> sellers) {
        return sellers.stream().map(this::toResult).toList();
    }

    /**
     * Domain → SellerBusinessInfoResult 변환.
     *
     * @param businessInfo SellerBusinessInfo 도메인 객체
     * @return SellerBusinessInfoResult
     */
    public SellerBusinessInfoResult toBusinessInfoResult(SellerBusinessInfo businessInfo) {
        return SellerBusinessInfoResult.from(businessInfo);
    }

    /**
     * 고객용 조회 결과 생성.
     *
     * @param seller Seller 도메인 객체
     * @param businessInfo SellerBusinessInfo 도메인 객체
     * @param sellerCs SellerCs 도메인 객체
     * @return SellerCustomerResult
     */
    public SellerCustomerResult toCustomerResult(
            Seller seller, SellerBusinessInfo businessInfo, SellerCs sellerCs) {
        return SellerCustomerResult.of(seller, businessInfo, sellerCs);
    }

    /**
     * 페이지 결과 생성.
     *
     * @param sellers Seller 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalCount 전체 개수
     * @return SellerPageResult
     */
    public SellerPageResult toPageResult(
            List<Seller> sellers, int page, int size, long totalCount) {
        List<SellerResult> results = toResults(sellers);
        return SellerPageResult.of(results, totalCount, page, size);
    }
}
