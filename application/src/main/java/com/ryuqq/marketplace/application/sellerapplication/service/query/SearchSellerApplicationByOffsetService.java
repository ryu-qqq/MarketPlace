package com.ryuqq.marketplace.application.sellerapplication.service.query;

import com.ryuqq.marketplace.application.sellerapplication.assembler.SellerApplicationAssembler;
import com.ryuqq.marketplace.application.sellerapplication.dto.query.SellerApplicationSearchParams;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;
import com.ryuqq.marketplace.application.sellerapplication.factory.SellerApplicationQueryFactory;
import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationReadManager;
import com.ryuqq.marketplace.application.sellerapplication.port.in.query.SearchSellerApplicationByOffsetUseCase;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 셀러 입점 신청 목록 조회 Service (Offset 기반 페이징).
 *
 * <p>APP-UC-001: Offset 페이징은 Search{Domain}ByOffsetService
 *
 * <p>QueryFactory를 통해 Params → Criteria 변환
 *
 * <p>Assembler를 통해 SellerApplicationPageResult 생성
 *
 * @author ryu-qqq
 */
@Service
public class SearchSellerApplicationByOffsetService
        implements SearchSellerApplicationByOffsetUseCase {

    private final SellerApplicationReadManager readManager;
    private final SellerApplicationQueryFactory queryFactory;
    private final SellerApplicationAssembler assembler;

    public SearchSellerApplicationByOffsetService(
            SellerApplicationReadManager readManager,
            SellerApplicationQueryFactory queryFactory,
            SellerApplicationAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public SellerApplicationPageResult execute(SellerApplicationSearchParams params) {
        SellerApplicationSearchCriteria criteria = queryFactory.createCriteria(params);

        List<SellerApplication> applications = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(applications, params.page(), params.size(), totalElements);
    }
}
