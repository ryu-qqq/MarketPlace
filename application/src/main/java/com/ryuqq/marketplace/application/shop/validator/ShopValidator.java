package com.ryuqq.marketplace.application.shop.validator;

import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.exception.ShopAccountIdDuplicateException;
import com.ryuqq.marketplace.domain.shop.exception.ShopNotFoundException;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import org.springframework.stereotype.Component;

/**
 * Shop Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class ShopValidator {

    private final ShopReadManager readManager;

    public ShopValidator(ShopReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 외부몰 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부몰 ID
     * @return Shop 도메인 객체
     * @throws ShopNotFoundException 존재하지 않는 경우
     */
    public Shop findExistingOrThrow(ShopId id) {
        return readManager.getById(id);
    }

    /**
     * 해당 판매채널에서 계정 ID 중복 여부 검증. (등록 시 사용)
     *
     * @param salesChannelId 판매채널 ID
     * @param accountId 계정 ID
     * @throws ShopAccountIdDuplicateException 이미 존재하는 경우
     */
    public void validateAccountNotDuplicate(Long salesChannelId, String accountId) {
        if (readManager.existsBySalesChannelIdAndAccountId(salesChannelId, accountId)) {
            throw new ShopAccountIdDuplicateException(accountId);
        }
    }

    /**
     * 해당 판매채널에서 계정 ID 중복 여부 검증 (자기 자신 제외). (수정 시 사용)
     *
     * @param salesChannelId 판매채널 ID
     * @param accountId 계정 ID
     * @param excludeId 제외할 외부몰 ID
     * @throws ShopAccountIdDuplicateException 이미 존재하는 경우
     */
    public void validateAccountNotDuplicateExcluding(
            Long salesChannelId, String accountId, ShopId excludeId) {
        if (readManager.existsBySalesChannelIdAndAccountIdExcluding(
                salesChannelId, accountId, excludeId)) {
            throw new ShopAccountIdDuplicateException(accountId);
        }
    }
}
