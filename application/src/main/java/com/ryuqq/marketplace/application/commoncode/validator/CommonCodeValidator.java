package com.ryuqq.marketplace.application.commoncode.validator;

import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeReadManager;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeDuplicateException;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeException;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeNotFoundException;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * CommonCodeValidator - 공통 코드 검증기.
 *
 * <p>APP-DEP-004: 자기 도메인 ReadManager만 의존합니다.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeValidator {

    private final CommonCodeReadManager readManager;
    private final CommonCodeTypeReadManager commonCodeTypeReadManager;

    public CommonCodeValidator(
            CommonCodeReadManager readManager,
            CommonCodeTypeReadManager commonCodeTypeReadManager) {
        this.readManager = readManager;
        this.commonCodeTypeReadManager = commonCodeTypeReadManager;
    }

    /**
     * 공통 코드 존재 여부 검증 후 Domain 객체 반환.
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id 공통 코드 ID
     * @return CommonCode 도메인 객체
     * @throws CommonCodeException 존재하지 않는 경우
     */
    public CommonCode findExistingOrThrow(CommonCodeId id) {
        return readManager.getById(id);
    }

    /**
     * 타입 ID + 코드 중복 여부 검증.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @param code 코드값
     * @throws CommonCodeDuplicateException 이미 존재하는 경우
     */
    public void validateNotDuplicate(Long commonCodeTypeId, String code) {
        if (readManager.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code)) {
            throw new CommonCodeDuplicateException(String.valueOf(commonCodeTypeId), code);
        }
    }

    /**
     * 부모 타입(공통 코드 타입) 존재 여부 검증.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @throws com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeException 존재하지 않는
     *     경우
     */
    public void validateCommonCodeTypeExists(Long commonCodeTypeId) {
        CommonCodeTypeId typeId = CommonCodeTypeId.of(commonCodeTypeId);
        commonCodeTypeReadManager.getById(typeId);
    }

    /**
     * ID 목록으로 공통 코드 조회 및 검증.
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체 목록을 반환합니다.
     *
     * <p>최적화: Map 생성 후 단일 순회로 검증 + 순서 보장 반환을 동시 수행.
     *
     * @param ids 공통 코드 ID 목록
     * @return 공통 코드 목록 (ID 순서 보장)
     * @throws CommonCodeNotFoundException 존재하지 않는 ID가 있는 경우
     */
    public List<CommonCode> findAllExistingOrThrow(List<CommonCodeId> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<CommonCodeId, CommonCode> foundMap =
                readManager.getByIds(ids).stream()
                        .collect(Collectors.toMap(CommonCode::id, cc -> cc));

        // 단일 순회: 검증 + 순서 보장 반환 동시 수행
        return ids.stream()
                .map(
                        id -> {
                            CommonCode code = foundMap.get(id);
                            if (code == null) {
                                throw new CommonCodeNotFoundException(id.value());
                            }
                            return code;
                        })
                .toList();
    }
}
