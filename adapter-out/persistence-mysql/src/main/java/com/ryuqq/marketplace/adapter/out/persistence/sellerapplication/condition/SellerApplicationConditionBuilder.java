package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.QSellerApplicationJpaEntity.sellerApplicationJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchField;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerApplicationConditionBuilder - 입점 신청 QueryDSL 조건 빌더.
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerApplicationConditionBuilder {

    /**
     * ID 일치 조건.
     *
     * @param id 신청 ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? sellerApplicationJpaEntity.id.eq(id) : null;
    }

    /**
     * ID 목록 일치 조건.
     *
     * @param ids ID 목록
     * @return ID 목록 일치 조건
     */
    public BooleanExpression idIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? sellerApplicationJpaEntity.id.in(ids) : null;
    }

    /**
     * 상태 일치 조건.
     *
     * @param status 상태
     * @return 상태 일치 조건
     */
    public BooleanExpression statusEq(ApplicationStatus status) {
        return status != null ? sellerApplicationJpaEntity.status.eq(status) : null;
    }

    /**
     * 사업자등록번호 일치 조건.
     *
     * @param registrationNumber 사업자등록번호
     * @return 일치 조건
     */
    public BooleanExpression registrationNumberEq(String registrationNumber) {
        return registrationNumber != null
                ? sellerApplicationJpaEntity.registrationNumber.eq(registrationNumber)
                : null;
    }

    /**
     * 사업자등록번호로 대기 중인 신청 존재 조건.
     *
     * @param registrationNumber 사업자등록번호
     * @return 조건
     */
    public BooleanExpression pendingByRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null) {
            return null;
        }
        return sellerApplicationJpaEntity
                .registrationNumber
                .eq(registrationNumber)
                .and(sellerApplicationJpaEntity.status.eq(ApplicationStatus.PENDING));
    }

    /**
     * 승인된 셀러 ID 일치 조건.
     *
     * @param approvedSellerId 승인된 셀러 ID
     * @return 일치 조건
     */
    public BooleanExpression approvedSellerIdEq(Long approvedSellerId) {
        return approvedSellerId != null
                ? sellerApplicationJpaEntity.approvedSellerId.eq(approvedSellerId)
                : null;
    }

    /**
     * 회사명 LIKE 조건.
     *
     * @param companyName 회사명 검색어
     * @return LIKE 조건
     */
    public BooleanExpression companyNameContains(String companyName) {
        return companyName != null && !companyName.isBlank()
                ? sellerApplicationJpaEntity.companyName.containsIgnoreCase(companyName)
                : null;
    }

    /**
     * 대표자명 LIKE 조건.
     *
     * @param representative 대표자명 검색어
     * @return LIKE 조건
     */
    public BooleanExpression representativeContains(String representative) {
        return representative != null && !representative.isBlank()
                ? sellerApplicationJpaEntity.representative.containsIgnoreCase(representative)
                : null;
    }

    /**
     * 검색 필드 기반 검색 조건.
     *
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return 검색 조건
     */
    public BooleanExpression searchFieldContains(
            SellerApplicationSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return sellerApplicationJpaEntity
                    .companyName
                    .containsIgnoreCase(searchWord)
                    .or(sellerApplicationJpaEntity.representative.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case COMPANY_NAME ->
                    sellerApplicationJpaEntity.companyName.containsIgnoreCase(searchWord);
            case REPRESENTATIVE_NAME ->
                    sellerApplicationJpaEntity.representative.containsIgnoreCase(searchWord);
        };
    }

    /**
     * 검색 조건 (Criteria 기반).
     *
     * @param criteria 검색 조건
     * @return 검색 조건
     */
    public BooleanExpression searchCondition(SellerApplicationSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        return searchFieldContains(criteria.searchField(), criteria.searchWord());
    }

    /**
     * 상태 조건 (Criteria 기반).
     *
     * @param criteria 검색 조건
     * @return 상태 조건
     */
    public BooleanExpression statusCondition(SellerApplicationSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        return sellerApplicationJpaEntity.status.in(criteria.status());
    }
}
