package com.ryuqq.marketplace.application.saleschannel.validator;

import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelReadManager;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNameDuplicateException;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNotFoundException;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import org.springframework.stereotype.Component;

/**
 * SalesChannel Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class SalesChannelValidator {

    private final SalesChannelReadManager readManager;

    public SalesChannelValidator(SalesChannelReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 판매채널 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 판매채널 ID
     * @return SalesChannel 도메인 객체
     * @throws SalesChannelNotFoundException 존재하지 않는 경우
     */
    public SalesChannel findExistingOrThrow(SalesChannelId id) {
        return readManager.getById(id);
    }

    /**
     * 판매채널명 중복 여부 검증.
     *
     * @param channelName 판매채널명
     * @throws SalesChannelNameDuplicateException 이미 존재하는 경우
     */
    public void validateChannelNameNotDuplicate(String channelName) {
        if (readManager.existsByChannelName(channelName)) {
            throw new SalesChannelNameDuplicateException(channelName);
        }
    }
}
