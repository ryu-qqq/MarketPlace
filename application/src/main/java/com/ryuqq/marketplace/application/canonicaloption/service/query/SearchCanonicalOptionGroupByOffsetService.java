package com.ryuqq.marketplace.application.canonicaloption.service.query;

import com.ryuqq.marketplace.application.canonicaloption.assembler.CanonicalOptionGroupAssembler;
import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.factory.CanonicalOptionGroupQueryFactory;
import com.ryuqq.marketplace.application.canonicaloption.internal.CanonicalOptionGroupReadFacade;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.SearchCanonicalOptionGroupByOffsetUseCase;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 캐노니컬 옵션 그룹 검색 Service (Offset 기반 페이징). */
@Service
public class SearchCanonicalOptionGroupByOffsetService
        implements SearchCanonicalOptionGroupByOffsetUseCase {

    private final CanonicalOptionGroupReadFacade readFacade;
    private final CanonicalOptionGroupQueryFactory queryFactory;
    private final CanonicalOptionGroupAssembler assembler;

    public SearchCanonicalOptionGroupByOffsetService(
            CanonicalOptionGroupReadFacade readFacade,
            CanonicalOptionGroupQueryFactory queryFactory,
            CanonicalOptionGroupAssembler assembler) {
        this.readFacade = readFacade;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CanonicalOptionGroupPageResult execute(CanonicalOptionGroupSearchParams params) {
        CanonicalOptionGroupSearchCriteria criteria = queryFactory.createCriteria(params);
        List<CanonicalOptionGroupResult> results = readFacade.findByCriteria(criteria);
        long totalElements = readFacade.countByCriteria(criteria);
        return assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements);
    }
}
