package com.ryuqq.marketplace.application.commoncode.manager;

import com.ryuqq.marketplace.application.commoncode.port.out.query.CommonCodeQueryPort;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeNotFoundException;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * CommonCodeReadManager - 공통 코드 Read Manager.
 *
 * <p>APP-MGR-001: Manager는 @Component로 등록.
 *
 * <p>APP-MGR-002: ReadManager는 QueryPort만 의존.
 *
 * <p>APP-MGR-003: @Transactional(readOnly = true) 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeReadManager {

    private final CommonCodeQueryPort queryPort;

    public CommonCodeReadManager(CommonCodeQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 공통 코드 조회 (필수).
     *
     * @param id 공통 코드 ID
     * @return CommonCode 도메인 객체
     * @throws CommonCodeNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public CommonCode getById(CommonCodeId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new CommonCodeNotFoundException(id.value()));
    }

    /**
     * ID 목록으로 공통 코드 목록 조회.
     *
     * @param ids 공통 코드 ID 목록
     * @return 공통 코드 목록
     */
    @Transactional(readOnly = true)
    public List<CommonCode> getByIds(List<CommonCodeId> ids) {
        return queryPort.findByIds(ids);
    }

    /**
     * 타입 ID + 코드 조합 존재 여부 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @param code 코드값
     * @return 존재하면 true
     */
    @Transactional(readOnly = true)
    public boolean existsByCommonCodeTypeIdAndCode(Long commonCodeTypeId, String code) {
        return queryPort.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code);
    }

    /**
     * 검색 조건으로 공통 코드 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 목록
     */
    @Transactional(readOnly = true)
    public List<CommonCode> findByCriteria(CommonCodeSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    /**
     * 검색 조건으로 공통 코드 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 개수
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommonCodeSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    /**
     * 특정 타입 ID의 활성화된 CommonCode 존재 여부 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @return 활성화된 CommonCode 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsActiveByCommonCodeTypeId(Long commonCodeTypeId) {
        return queryPort.existsActiveByCommonCodeTypeId(commonCodeTypeId);
    }
}
