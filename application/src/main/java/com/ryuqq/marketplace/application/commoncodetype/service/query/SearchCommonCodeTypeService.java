package com.ryuqq.marketplace.application.commoncodetype.service.query;

import com.ryuqq.marketplace.application.commoncodetype.assembler.CommonCodeTypeAssembler;
import com.ryuqq.marketplace.application.commoncodetype.dto.query.CommonCodeTypeSearchParams;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeQueryFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeReadManager;
import com.ryuqq.marketplace.application.commoncodetype.port.in.query.SearchCommonCodeTypeUseCase;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 공통 코드 타입 검색 Service.
 *
 * <p>QueryFactory를 통해 Params → Criteria 변환
 *
 * <p>Assembler를 통해 CommonCodeTypePageResult 생성
 */
@Service
public class SearchCommonCodeTypeService implements SearchCommonCodeTypeUseCase {

    private final CommonCodeTypeReadManager readManager;
    private final CommonCodeTypeQueryFactory queryFactory;
    private final CommonCodeTypeAssembler assembler;

    public SearchCommonCodeTypeService(
            CommonCodeTypeReadManager readManager,
            CommonCodeTypeQueryFactory queryFactory,
            CommonCodeTypeAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CommonCodeTypePageResult execute(CommonCodeTypeSearchParams params) {
        CommonCodeTypeSearchCriteria criteria = queryFactory.createCriteria(params);

        List<CommonCodeType> domains = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(domains, params.page(), params.size(), totalElements);
    }
}
