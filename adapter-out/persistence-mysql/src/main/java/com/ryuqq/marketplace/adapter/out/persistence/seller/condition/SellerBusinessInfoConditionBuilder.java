package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerBusinessInfoJpaEntity.sellerBusinessInfoJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchField;
import org.springframework.stereotype.Component;

/**
 * SellerBusinessInfoConditionBuilder - 셀러 사업자 정보 QueryDSL 조건 빌더.
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerBusinessInfoConditionBuilder {

    /**
     * ID 일치 조건.
     *
     * @param id 사업자 정보 ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? sellerBusinessInfoJpaEntity.id.eq(id) : null;
    }

    /**
     * 셀러 ID 일치 조건.
     *
     * @param sellerId 셀러 ID
     * @return 셀러 ID 일치 조건
     */
    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerBusinessInfoJpaEntity.sellerId.eq(sellerId) : null;
    }

    /**
     * 셀러 ID 제외 조건.
     *
     * @param excludeSellerId 제외할 셀러 ID
     * @return 셀러 ID 제외 조건
     */
    public BooleanExpression sellerIdNe(Long excludeSellerId) {
        return excludeSellerId != null
                ? sellerBusinessInfoJpaEntity.sellerId.ne(excludeSellerId)
                : null;
    }

    /**
     * 사업자등록번호 일치 조건.
     *
     * @param registrationNumber 사업자등록번호
     * @return 사업자등록번호 일치 조건
     */
    public BooleanExpression registrationNumberEq(String registrationNumber) {
        return registrationNumber != null
                ? sellerBusinessInfoJpaEntity.registrationNumber.eq(registrationNumber)
                : null;
    }

    /**
     * 사업자등록번호 LIKE 조건.
     *
     * @param registrationNumber 사업자등록번호 검색어
     * @return 사업자등록번호 LIKE 조건
     */
    public BooleanExpression registrationNumberContains(String registrationNumber) {
        return registrationNumber != null && !registrationNumber.isBlank()
                ? sellerBusinessInfoJpaEntity.registrationNumber.containsIgnoreCase(
                        registrationNumber)
                : null;
    }

    /**
     * 회사명 LIKE 조건.
     *
     * @param companyName 회사명 검색어
     * @return 회사명 LIKE 조건
     */
    public BooleanExpression companyNameContains(String companyName) {
        return companyName != null && !companyName.isBlank()
                ? sellerBusinessInfoJpaEntity.companyName.containsIgnoreCase(companyName)
                : null;
    }

    /**
     * 대표자명 LIKE 조건.
     *
     * @param representative 대표자명 검색어
     * @return 대표자명 LIKE 조건
     */
    public BooleanExpression representativeContains(String representative) {
        return representative != null && !representative.isBlank()
                ? sellerBusinessInfoJpaEntity.representative.containsIgnoreCase(representative)
                : null;
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
            return sellerBusinessInfoJpaEntity
                    .registrationNumber
                    .containsIgnoreCase(searchWord)
                    .or(sellerBusinessInfoJpaEntity.companyName.containsIgnoreCase(searchWord))
                    .or(sellerBusinessInfoJpaEntity.representative.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case REGISTRATION_NUMBER -> registrationNumberContains(searchWord);
            case COMPANY_NAME -> companyNameContains(searchWord);
            case REPRESENTATIVE_NAME -> representativeContains(searchWord);
            case SELLER_NAME -> null;
        };
    }
}
