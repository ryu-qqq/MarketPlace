package com.ryuqq.marketplace.application.saleschannel.service.query;

import com.ryuqq.marketplace.application.saleschannel.assembler.SalesChannelAssembler;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.factory.SalesChannelQueryFactory;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelReadManager;
import com.ryuqq.marketplace.application.saleschannel.port.in.query.SearchSalesChannelByOffsetUseCase;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 판매채널 검색 Service (Offset 기반 페이징). */
@Service
public class SearchSalesChannelByOffsetService implements SearchSalesChannelByOffsetUseCase {

    private final SalesChannelReadManager readManager;
    private final SalesChannelQueryFactory queryFactory;
    private final SalesChannelAssembler assembler;

    public SearchSalesChannelByOffsetService(
            SalesChannelReadManager readManager,
            SalesChannelQueryFactory queryFactory,
            SalesChannelAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public SalesChannelPageResult execute(SalesChannelSearchParams params) {
        SalesChannelSearchCriteria criteria = queryFactory.createCriteria(params);
        List<SalesChannel> salesChannels = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(salesChannels, params.page(), params.size(), totalElements);
    }
}
