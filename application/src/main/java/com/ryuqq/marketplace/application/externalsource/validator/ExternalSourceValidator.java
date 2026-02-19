package com.ryuqq.marketplace.application.externalsource.validator;

import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.externalsource.port.out.query.ExternalSourceQueryPort;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.exception.ExternalSourceDuplicateException;
import org.springframework.stereotype.Component;

/**
 * ExternalSource Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class ExternalSourceValidator {

    private final ExternalSourceReadManager readManager;
    private final ExternalSourceQueryPort queryPort;

    public ExternalSourceValidator(
            ExternalSourceReadManager readManager, ExternalSourceQueryPort queryPort) {
        this.readManager = readManager;
        this.queryPort = queryPort;
    }

    /**
     * 외부 소스 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부 소스 ID
     * @return ExternalSource 도메인 객체
     * @throws com.ryuqq.marketplace.domain.externalsource.exception.ExternalSourceNotFoundException
     *     존재하지 않는 경우
     */
    public ExternalSource findExistingOrThrow(long id) {
        return readManager.getById(id);
    }

    /**
     * 동일한 code가 이미 존재하는지 검증.
     *
     * @param code 외부 소스 코드
     * @throws ExternalSourceDuplicateException 이미 존재하는 경우
     */
    public void validateCodeNotDuplicate(String code) {
        queryPort
                .findByCode(code)
                .ifPresent(
                        existing -> {
                            throw new ExternalSourceDuplicateException(code);
                        });
    }
}
