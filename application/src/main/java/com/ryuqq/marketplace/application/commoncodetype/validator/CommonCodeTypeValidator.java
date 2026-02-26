package com.ryuqq.marketplace.application.commoncodetype.validator;

import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeReadManager;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.exception.ActiveCommonCodesExistException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeNotFoundException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.DuplicateCommonCodeTypeCodeException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.DuplicateCommonCodeTypeDisplayOrderException;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeValidator - 공통 코드 타입 검증기
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 *
 * @author ryu-qqq
 */
@Component
public class CommonCodeTypeValidator {

    private final CommonCodeTypeReadManager commonCodeTypeReadManager;
    private final CommonCodeReadManager commonCodeReadManager;

    public CommonCodeTypeValidator(
            CommonCodeTypeReadManager commonCodeTypeReadManager,
            CommonCodeReadManager commonCodeReadManager) {
        this.commonCodeTypeReadManager = commonCodeTypeReadManager;
        this.commonCodeReadManager = commonCodeReadManager;
    }

    /**
     * 공통 코드 타입 존재 여부 검증 후 Domain 객체 반환
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id 공통 코드 타입 ID
     * @return CommonCodeType 도메인 객체
     * @throws CommonCodeTypeException 존재하지 않는 경우
     */
    public CommonCodeType findExistingOrThrow(CommonCodeTypeId id) {
        return commonCodeTypeReadManager.getById(id);
    }

    /**
     * ID 목록으로 공통 코드 타입 조회 및 검증.
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체 목록을 반환합니다.
     *
     * <p>최적화: Map 생성 후 단일 순회로 검증 + 순서 보장 반환을 동시 수행.
     *
     * @param ids 공통 코드 타입 ID 목록
     * @return 공통 코드 타입 목록 (ID 순서 보장)
     * @throws CommonCodeTypeNotFoundException 존재하지 않는 ID가 있는 경우
     */
    public List<CommonCodeType> findAllExistingOrThrow(List<CommonCodeTypeId> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<CommonCodeTypeId, CommonCodeType> foundMap =
                commonCodeTypeReadManager.getByIds(ids).stream()
                        .collect(Collectors.toMap(CommonCodeType::id, cct -> cct));

        return ids.stream()
                .map(
                        id -> {
                            CommonCodeType cct = foundMap.get(id);
                            if (cct == null) {
                                throw new CommonCodeTypeNotFoundException(id.value());
                            }
                            return cct;
                        })
                .toList();
    }

    /**
     * 코드 중복 여부 검증
     *
     * @param code 코드값
     * @throws DuplicateCommonCodeTypeCodeException 이미 존재하는 경우
     */
    public void validateCodeNotDuplicate(String code) {
        if (commonCodeTypeReadManager.existsByCode(code)) {
            throw new DuplicateCommonCodeTypeCodeException(code);
        }
    }

    /**
     * 표시 순서 중복 여부 검증 (자기 자신 제외)
     *
     * @param displayOrder 표시 순서
     * @param excludeId 제외할 ID (수정 시 자기 자신)
     * @throws DuplicateCommonCodeTypeDisplayOrderException 중복인 경우
     */
    public void validateDisplayOrderNotDuplicate(int displayOrder, Long excludeId) {
        if (commonCodeTypeReadManager.existsByDisplayOrderExcludingId(displayOrder, excludeId)) {
            throw new DuplicateCommonCodeTypeDisplayOrderException(displayOrder);
        }
    }

    /**
     * 활성화된 공통 코드 존재 여부 검증 (비활성화 전 호출).
     *
     * <p>해당 공통 코드 타입에 활성화된 공통 코드가 있으면 비활성화할 수 없습니다.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @throws ActiveCommonCodesExistException 활성화된 공통 코드가 존재하는 경우
     */
    public void validateNoActiveCommonCodes(Long commonCodeTypeId) {
        if (commonCodeReadManager.existsActiveByCommonCodeTypeId(commonCodeTypeId)) {
            throw new ActiveCommonCodesExistException(String.valueOf(commonCodeTypeId));
        }
    }
}
