package com.ryuqq.marketplace.application.canonicaloption.service.query;

import com.ryuqq.marketplace.application.canonicaloption.assembler.CanonicalOptionGroupAssembler;
import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.factory.CanonicalOptionGroupQueryFactory;
import com.ryuqq.marketplace.application.canonicaloption.manager.CanonicalOptionGroupReadManager;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.SearchCanonicalOptionGroupByOffsetUseCase;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 캐노니컬 옵션 그룹 검색 Service (Offset 기반 페이징). */
@Service
public class SearchCanonicalOptionGroupByOffsetService
        implements SearchCanonicalOptionGroupByOffsetUseCase {

    private final CanonicalOptionGroupReadManager readManager;
    private final CanonicalOptionGroupQueryFactory queryFactory;
    private final CanonicalOptionGroupAssembler assembler;

    public SearchCanonicalOptionGroupByOffsetService(
            CanonicalOptionGroupReadManager readManager,
            CanonicalOptionGroupQueryFactory queryFactory,
            CanonicalOptionGroupAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CanonicalOptionGroupPageResult execute(CanonicalOptionGroupSearchParams params) {
        CanonicalOptionGroupSearchCriteria criteria = queryFactory.createCriteria(params);
        List<CanonicalOptionGroup> groups = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        List<CanonicalOptionGroupResult> results =
                groups.stream().map(assembler::toResult).toList();
        return assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements);
    }
}
