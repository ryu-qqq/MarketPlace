package com.ryuqq.marketplace.application.selleradmin.service.query;

import com.ryuqq.marketplace.application.selleradmin.assembler.SellerAdminAssembler;
import com.ryuqq.marketplace.application.selleradmin.dto.query.SellerAdminApplicationSearchParams;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationPageResult;
import com.ryuqq.marketplace.application.selleradmin.factory.SellerAdminQueryFactory;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.SearchSellerAdminApplicationsUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchSellerAdminApplicationsService - 셀러 관리자 가입 신청 목록 조회 Service.
 *
 * <p>운영자 또는 셀러 관리자가 신청 목록을 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
public class SearchSellerAdminApplicationsService implements SearchSellerAdminApplicationsUseCase {

    private final SellerAdminReadManager readManager;
    private final SellerAdminQueryFactory queryFactory;
    private final SellerAdminAssembler assembler;

    public SearchSellerAdminApplicationsService(
            SellerAdminReadManager readManager,
            SellerAdminQueryFactory queryFactory,
            SellerAdminAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public SellerAdminApplicationPageResult execute(SellerAdminApplicationSearchParams params) {
        SellerAdminSearchCriteria criteria = queryFactory.createCriteria(params);

        List<SellerAdmin> sellerAdmins = readManager.findByCriteria(criteria);
        long totalCount = readManager.countByCriteria(criteria);

        return assembler.toPageResult(sellerAdmins, criteria.page(), criteria.size(), totalCount);
    }
}
