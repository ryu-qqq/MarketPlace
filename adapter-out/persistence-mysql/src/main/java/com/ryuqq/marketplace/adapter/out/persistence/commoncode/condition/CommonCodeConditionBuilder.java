package com.ryuqq.marketplace.adapter.out.persistence.commoncode.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.QCommonCodeJpaEntity.commonCodeJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.QCommonCodeTypeJpaEntity.commonCodeTypeJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeConditionBuilder - 공통 코드 QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: ConditionBuilder는 @Component로 등록.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeConditionBuilder {

    /** ID 일치 조건 */
    public BooleanExpression idEq(Long id) {
        return id != null ? commonCodeJpaEntity.id.eq(id) : null;
    }

    /** ID 목록 포함 조건 */
    public BooleanExpression idIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? commonCodeJpaEntity.id.in(ids) : null;
    }

    /** 공통 코드 타입 ID 일치 조건 */
    public BooleanExpression commonCodeTypeIdEq(Long commonCodeTypeId) {
        return commonCodeTypeId != null
                ? commonCodeJpaEntity.commonCodeTypeId.eq(commonCodeTypeId)
                : null;
    }

    /** 코드 일치 조건 */
    public BooleanExpression codeEq(String code) {
        return code != null && !code.isBlank() ? commonCodeJpaEntity.code.eq(code) : null;
    }

    /** 공통 코드 타입 코드 정확 일치 조건 (common_code_types JOIN 기반) */
    public BooleanExpression commonCodeTypeCodeEq(String commonCodeTypeCode) {
        return commonCodeTypeCode != null && !commonCodeTypeCode.isBlank()
                ? commonCodeTypeJpaEntity.code.eq(commonCodeTypeCode)
                : null;
    }

    /** 활성화 상태 일치 조건 */
    public BooleanExpression activeEq(Boolean active) {
        return active != null ? commonCodeJpaEntity.active.eq(active) : null;
    }

    /** 삭제되지 않은 조건 */
    public BooleanExpression notDeleted() {
        return commonCodeJpaEntity.deletedAt.isNull();
    }

    /** 공통 코드 타입 삭제되지 않은 조건 */
    public BooleanExpression commonCodeTypeNotDeleted() {
        return commonCodeTypeJpaEntity.deletedAt.isNull();
    }
}
