package com.ryuqq.marketplace.application.commoncode.service.query;

import com.ryuqq.marketplace.application.commoncode.assembler.CommonCodeAssembler;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.factory.CommonCodeQueryFactory;
import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.application.commoncode.port.in.query.SearchCommonCodeUseCase;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 공통 코드 검색 Service.
 *
 * <p>QueryFactory를 통해 Params → Criteria 변환.
 *
 * <p>Assembler를 통해 CommonCodePageResult 생성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class SearchCommonCodeService implements SearchCommonCodeUseCase {

    private final CommonCodeReadManager readManager;
    private final CommonCodeQueryFactory queryFactory;
    private final CommonCodeAssembler assembler;

    public SearchCommonCodeService(
            CommonCodeReadManager readManager,
            CommonCodeQueryFactory queryFactory,
            CommonCodeAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CommonCodePageResult execute(CommonCodeSearchParams params) {
        CommonCodeSearchCriteria criteria = queryFactory.createCriteria(params);

        List<CommonCode> domains = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(domains, params.page(), params.size(), totalElements);
    }
}
