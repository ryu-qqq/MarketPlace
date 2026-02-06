package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.QCommonCodeJpaEntity.commonCodeJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.QCommonCodeTypeJpaEntity.commonCodeTypeJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchField;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeConditionBuilder - 공통 코드 타입 QueryDSL 조건 빌더.
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeTypeConditionBuilder {

    /**
     * ID 일치 조건.
     *
     * @param id 공통 코드 타입 ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? commonCodeTypeJpaEntity.id.eq(id) : null;
    }

    /**
     * 미삭제(notDeleted) 조건.
     *
     * <p>deletedAt이 null인 데이터만 조회합니다.
     *
     * @return deletedAt is null 조건
     */
    public BooleanExpression notDeleted() {
        return commonCodeTypeJpaEntity.deletedAt.isNull();
    }

    /**
     * ID 목록 일치 조건.
     *
     * @param ids ID 목록
     * @return ID 목록 일치 조건
     */
    public BooleanExpression idIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? commonCodeTypeJpaEntity.id.in(ids) : null;
    }

    /**
     * ID 제외 조건.
     *
     * @param excludeId 제외할 ID
     * @return ID 제외 조건
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? commonCodeTypeJpaEntity.id.ne(excludeId) : null;
    }

    /**
     * 코드 일치 조건.
     *
     * @param code 코드
     * @return 코드 일치 조건
     */
    public BooleanExpression codeEq(String code) {
        return code != null ? commonCodeTypeJpaEntity.code.eq(code) : null;
    }

    /**
     * 표시 순서 일치 조건.
     *
     * @param displayOrder 표시 순서
     * @return 표시 순서 일치 조건
     */
    public BooleanExpression displayOrderEq(Integer displayOrder) {
        return displayOrder != null ? commonCodeTypeJpaEntity.displayOrder.eq(displayOrder) : null;
    }

    /**
     * 활성화 상태 일치 조건.
     *
     * @param active 활성화 상태 (null이면 전체)
     * @return active 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression activeEq(Boolean active) {
        return active != null ? commonCodeTypeJpaEntity.active.eq(active) : null;
    }

    /**
     * 활성화 상태 조건 (Criteria 기반).
     *
     * @param criteria 검색 조건
     * @return active 일치 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression activeEq(CommonCodeTypeSearchCriteria criteria) {
        return criteria.hasActiveFilter()
                ? commonCodeTypeJpaEntity.active.eq(criteria.active())
                : null;
    }

    /**
     * 검색 필드 기반 검색 조건.
     *
     * @param searchField 검색 필드 (null이면 코드, 이름, 설명 전체 검색)
     * @param searchWord 검색어
     * @return 검색 조건
     */
    public BooleanExpression searchFieldContains(
            CommonCodeTypeSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return commonCodeTypeJpaEntity
                    .code
                    .containsIgnoreCase(searchWord)
                    .or(commonCodeTypeJpaEntity.name.containsIgnoreCase(searchWord))
                    .or(commonCodeTypeJpaEntity.description.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case CODE -> commonCodeTypeJpaEntity.code.containsIgnoreCase(searchWord);
            case NAME -> commonCodeTypeJpaEntity.name.containsIgnoreCase(searchWord);
        };
    }

    /**
     * 검색 조건 (Criteria 기반).
     *
     * @param criteria 검색 조건
     * @return 검색 조건 (검색어가 없으면 null 반환)
     */
    public BooleanExpression searchCondition(CommonCodeTypeSearchCriteria criteria) {
        if (!criteria.hasSearchWord()) {
            return null;
        }
        return searchFieldContains(criteria.searchField(), criteria.searchWord());
    }

    /**
     * 공통 코드 값(CommonCodeValue) 필터 - 해당 값을 가진 공통코드를 갖는 타입만 조회.
     *
     * @param type 공통 코드 값 (null 또는 빈 문자열이면 null 반환)
     * @return 타입 필터 조건
     */
    public BooleanExpression typeHasCommonCodeValue(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        String normalizedType = type.trim().toUpperCase(Locale.ROOT);
        return commonCodeTypeJpaEntity.id.in(
                JPAExpressions.select(commonCodeJpaEntity.commonCodeTypeId)
                        .from(commonCodeJpaEntity)
                        .where(commonCodeJpaEntity.code.eq(normalizedType)));
    }

    /**
     * 공통 코드 값 필터 (Criteria 기반).
     *
     * @param criteria 검색 조건
     * @return 타입 필터 조건 (type이 없으면 null 반환)
     */
    public BooleanExpression typeHasCommonCodeValue(CommonCodeTypeSearchCriteria criteria) {
        return criteria.hasType() ? typeHasCommonCodeValue(criteria.type()) : null;
    }
}
