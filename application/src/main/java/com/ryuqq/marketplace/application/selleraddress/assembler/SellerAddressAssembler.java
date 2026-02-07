package com.ryuqq.marketplace.application.selleraddress.assembler;

import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressResult;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.util.List;
import org.springframework.stereotype.Component;

/** SellerAddress Assembler. Domain → Result 변환. */
@Component
public class SellerAddressAssembler {

    public SellerAddressResult toResult(SellerAddress address) {
        return SellerAddressResult.from(address);
    }

    public List<SellerAddressResult> toResults(List<SellerAddress> addresses) {
        return addresses.stream().map(this::toResult).toList();
    }

    /**
     * 페이징 결과를 생성합니다.
     *
     * @param addresses 셀러 주소 목록
     * @param page 현재 페이지
     * @param size 페이지 크기
     * @param totalElements 전체 개수
     * @return 페이징 Result DTO
     */
    public SellerAddressPageResult toPageResult(
            List<SellerAddress> addresses, int page, int size, long totalElements) {
        List<SellerAddressResult> content = toResults(addresses);
        return SellerAddressPageResult.of(content, page, size, totalElements);
    }
}
