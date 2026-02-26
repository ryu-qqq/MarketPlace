package com.ryuqq.marketplace.domain.shop.aggregate;

import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;

/**
 * Shop 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record ShopUpdateData(String shopName, String accountId, ShopStatus status) {

    public static ShopUpdateData of(String shopName, String accountId, ShopStatus status) {
        return new ShopUpdateData(shopName, accountId, status);
    }
}
