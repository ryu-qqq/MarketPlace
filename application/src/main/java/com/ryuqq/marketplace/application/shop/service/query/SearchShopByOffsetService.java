package com.ryuqq.marketplace.application.shop.service.query;

import com.ryuqq.marketplace.application.shop.assembler.ShopAssembler;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.factory.ShopQueryFactory;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.application.shop.port.in.query.SearchShopByOffsetUseCase;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** Shop 검색 Service (Offset 기반 페이징). */
@Service
public class SearchShopByOffsetService implements SearchShopByOffsetUseCase {

    private final ShopReadManager readManager;
    private final ShopQueryFactory queryFactory;
    private final ShopAssembler assembler;

    public SearchShopByOffsetService(
            ShopReadManager readManager, ShopQueryFactory queryFactory, ShopAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public ShopPageResult execute(ShopSearchParams params) {
        ShopSearchCriteria criteria = queryFactory.createCriteria(params);
        List<Shop> shops = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(shops, params.page(), params.size(), totalElements);
    }
}
