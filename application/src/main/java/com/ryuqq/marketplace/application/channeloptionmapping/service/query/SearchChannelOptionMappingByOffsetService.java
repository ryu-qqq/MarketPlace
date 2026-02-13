package com.ryuqq.marketplace.application.channeloptionmapping.service.query;

import com.ryuqq.marketplace.application.channeloptionmapping.assembler.ChannelOptionMappingAssembler;
import com.ryuqq.marketplace.application.channeloptionmapping.dto.query.ChannelOptionMappingSearchParams;
import com.ryuqq.marketplace.application.channeloptionmapping.dto.response.ChannelOptionMappingPageResult;
import com.ryuqq.marketplace.application.channeloptionmapping.factory.ChannelOptionMappingQueryFactory;
import com.ryuqq.marketplace.application.channeloptionmapping.manager.ChannelOptionMappingReadManager;
import com.ryuqq.marketplace.application.channeloptionmapping.port.in.query.SearchChannelOptionMappingByOffsetUseCase;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 채널 옵션 매핑 검색 Service (Offset 기반 페이징). */
@Service
public class SearchChannelOptionMappingByOffsetService
        implements SearchChannelOptionMappingByOffsetUseCase {

    private final ChannelOptionMappingReadManager readManager;
    private final ChannelOptionMappingQueryFactory queryFactory;
    private final ChannelOptionMappingAssembler assembler;

    public SearchChannelOptionMappingByOffsetService(
            ChannelOptionMappingReadManager readManager,
            ChannelOptionMappingQueryFactory queryFactory,
            ChannelOptionMappingAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public ChannelOptionMappingPageResult execute(ChannelOptionMappingSearchParams params) {
        ChannelOptionMappingSearchCriteria criteria = queryFactory.createCriteria(params);
        List<ChannelOptionMapping> mappings = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(mappings, params.page(), params.size(), totalElements);
    }
}
