package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerJpaEntity.sellerJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerConditionBuilder - 셀러 QueryDSL 조건 빌더.
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerConditionBuilder {

    /**
     * ID 일치 조건.
     *
     * @param id 셀러 ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? sellerJpaEntity.id.eq(id) : null;
    }

    /**
     * ID 목록 일치 조건.
     *
     * @param ids ID 목록
     * @return ID 목록 일치 조건
     */
    public BooleanExpression idIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? sellerJpaEntity.id.in(ids) : null;
    }

    /**
     * ID 제외 조건.
     *
     * @param excludeId 제외할 ID
     * @return ID 제외 조건
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? sellerJpaEntity.id.ne(excludeId) : null;
    }

    /**
     * 셀러명 일치 조건.
     *
     * @param sellerName 셀러명
     * @return 셀러명 일치 조건
     */
    public BooleanExpression sellerNameEq(String sellerName) {
        return sellerName != null ? sellerJpaEntity.sellerName.eq(sellerName) : null;
    }

    /**
     * 셀러명 LIKE 조건.
     *
     * @param sellerName 셀러명 검색어
     * @return 셀러명 LIKE 조건
     */
    public BooleanExpression sellerNameContains(String sellerName) {
        return sellerName != null && !sellerName.isBlank()
                ? sellerJpaEntity.sellerName.containsIgnoreCase(sellerName)
                : null;
    }

    /**
     * 활성화 상태 일치 조건.
     *
     * @param active 활성화 상태 (null이면 전체)
     * @return active 일치 조건
     */
    public BooleanExpression activeEq(Boolean active) {
        return active != null ? sellerJpaEntity.active.eq(active) : null;
    }

    /**
     * 검색 필드 기반 검색 조건.
     *
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return 검색 조건
     */
    public BooleanExpression searchFieldContains(SellerSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return sellerJpaEntity
                    .sellerName
                    .containsIgnoreCase(searchWord)
                    .or(sellerJpaEntity.displayName.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case SELLER_NAME -> sellerJpaEntity.sellerName.containsIgnoreCase(searchWord);
            case REGISTRATION_NUMBER, COMPANY_NAME, REPRESENTATIVE_NAME -> null;
        };
    }

    /**
     * authOrganizationId 일치 조건.
     *
     * @param organizationId 인증 조직 ID
     * @return organizationId 일치 조건
     */
    public BooleanExpression organizationIdEq(String organizationId) {
        return organizationId != null
                ? sellerJpaEntity.authOrganizationId.eq(organizationId)
                : null;
    }

    /**
     * 검색 조건 (Criteria 기반).
     *
     * @param criteria 검색 조건
     * @return 검색 조건
     */
    public BooleanExpression searchCondition(SellerSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        return searchFieldContains(criteria.searchField(), criteria.searchWord());
    }

    /**
     * 활성화 상태 조건 (Criteria 기반).
     *
     * @param criteria 검색 조건
     * @return active 일치 조건
     */
    public BooleanExpression activeEq(SellerSearchCriteria criteria) {
        return criteria.hasActiveFilter() ? sellerJpaEntity.active.eq(criteria.active()) : null;
    }
}
