package com.ryuqq.marketplace.application.selleraddress.service.query;

import com.ryuqq.marketplace.application.selleraddress.assembler.SellerAddressAssembler;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressQueryFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.application.selleraddress.port.in.query.SearchSellerAddressUseCase;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 셀러 주소 검색 Service. */
@Service
public class SearchSellerAddressService implements SearchSellerAddressUseCase {

    private final SellerAddressQueryFactory queryFactory;
    private final SellerAddressReadManager readManager;
    private final SellerAddressAssembler assembler;

    public SearchSellerAddressService(
            SellerAddressQueryFactory queryFactory,
            SellerAddressReadManager readManager,
            SellerAddressAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public SellerAddressPageResult execute(SellerAddressSearchParams params) {
        SellerAddressSearchCriteria criteria = queryFactory.createSearchCriteria(params);

        List<SellerAddress> addresses = readManager.search(criteria);
        long totalCount = readManager.count(criteria);

        return assembler.toPageResult(addresses, criteria.page(), criteria.size(), totalCount);
    }
}
