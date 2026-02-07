package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.QSellerAdminJpaEntity.sellerAdminJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchField;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAdminConditionBuilder - 셀러 관리자 조회 조건 빌더.
 *
 * <p>QueryDSL BooleanExpression을 생성합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerAdminConditionBuilder {

    public BooleanExpression idEq(String id) {
        return id != null ? sellerAdminJpaEntity.id.eq(id) : null;
    }

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerAdminJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression sellerIdsIn(List<Long> sellerIds) {
        if (sellerIds == null || sellerIds.isEmpty()) {
            return null;
        }
        return sellerAdminJpaEntity.sellerId.in(sellerIds);
    }

    public BooleanExpression idsIn(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return sellerAdminJpaEntity.id.in(ids);
    }

    public BooleanExpression loginIdEq(String loginId) {
        return loginId != null ? sellerAdminJpaEntity.loginId.eq(loginId) : null;
    }

    public BooleanExpression statusIn(List<SellerAdminStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return sellerAdminJpaEntity.status.in(statuses);
    }

    public BooleanExpression searchCondition(SellerAdminSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }

        String searchWord = criteria.searchWord();
        SellerAdminSearchField searchField = criteria.searchField();

        if (searchField == null) {
            return sellerAdminJpaEntity
                    .loginId
                    .containsIgnoreCase(searchWord)
                    .or(sellerAdminJpaEntity.name.containsIgnoreCase(searchWord));
        }

        return switch (searchField) {
            case LOGIN_ID -> sellerAdminJpaEntity.loginId.containsIgnoreCase(searchWord);
            case NAME -> sellerAdminJpaEntity.name.containsIgnoreCase(searchWord);
        };
    }

    public BooleanExpression dateRangeCondition(SellerAdminSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }

        var dateRange = criteria.dateRange();
        LocalDate startDate = dateRange.startDate();
        LocalDate endDate = dateRange.endDate();

        BooleanExpression condition = null;

        if (startDate != null) {
            var startInstant = startDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
            condition = sellerAdminJpaEntity.createdAt.goe(startInstant);
        }

        if (endDate != null) {
            var endInstant =
                    endDate.atTime(LocalTime.MAX).atZone(ZoneId.of("Asia/Seoul")).toInstant();
            BooleanExpression endCondition = sellerAdminJpaEntity.createdAt.loe(endInstant);
            condition = condition != null ? condition.and(endCondition) : endCondition;
        }

        return condition;
    }

    public BooleanExpression notDeleted() {
        return sellerAdminJpaEntity.deletedAt.isNull();
    }
}
