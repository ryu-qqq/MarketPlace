package com.ryuqq.marketplace.domain.common;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;

/**
 * 공통 Value Object 테스트 Fixtures.
 *
 * <p>테스트에서 공통으로 사용되는 VO 객체들을 생성합니다.
 */
public final class CommonVoFixtures {

    private CommonVoFixtures() {}

    /** 기본 시간 Fixtures */
    public static Instant now() {
        return Instant.now();
    }

    public static Instant yesterday() {
        return Instant.now().minusSeconds(86400);
    }

    public static Instant tomorrow() {
        return Instant.now().plusSeconds(86400);
    }

    /** Money Fixtures */
    public static Money money(int value) {
        return Money.of(value);
    }

    public static Money zeroMoney() {
        return Money.zero();
    }

    public static Money defaultBaseFee() {
        return Money.of(3000);
    }

    public static Money defaultFreeThreshold() {
        return Money.of(50000);
    }

    public static Money defaultExtraFee() {
        return Money.of(3000);
    }

    public static Money defaultReturnFee() {
        return Money.of(3000);
    }

    public static Money defaultExchangeFee() {
        return Money.of(6000);
    }

    /** SellerId Fixtures */
    public static SellerId sellerId(Long value) {
        return SellerId.of(value);
    }

    public static SellerId newSellerId() {
        return SellerId.forNew();
    }

    public static SellerId defaultSellerId() {
        return SellerId.of(1L);
    }

    /** PhoneNumber Fixtures */
    public static PhoneNumber phoneNumber(String value) {
        return PhoneNumber.of(value);
    }

    public static PhoneNumber defaultPhoneNumber() {
        return PhoneNumber.of("010-1234-5678");
    }
}
