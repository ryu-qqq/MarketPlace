package com.ryuqq.marketplace.application.outboundproduct.service.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsShopQueryFactory;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsShopsByOffsetUseCase;
import com.ryuqq.marketplace.application.shop.assembler.ShopAssembler;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** OMS 쇼핑몰 검색 Service (Offset 기반 페이징). */
@Service
public class SearchOmsShopsByOffsetService implements SearchOmsShopsByOffsetUseCase {

    private final ShopReadManager readManager;
    private final OmsShopQueryFactory queryFactory;
    private final ShopAssembler assembler;

    public SearchOmsShopsByOffsetService(
            ShopReadManager readManager,
            OmsShopQueryFactory queryFactory,
            ShopAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public ShopPageResult execute(OmsShopSearchParams params) {
        ShopSearchCriteria criteria = queryFactory.createCriteria(params);

        List<Shop> shops = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(shops, params.page(), params.size(), totalElements);
    }
}
