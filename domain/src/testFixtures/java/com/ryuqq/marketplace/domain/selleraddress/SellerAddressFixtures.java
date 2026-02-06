package com.ryuqq.marketplace.domain.selleraddress;

import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressName;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.time.Instant;

/**
 * SellerAddress 테스트 Fixtures.
 *
 * <p>테스트에서 SellerAddress 관련 객체들을 생성합니다.
 */
public final class SellerAddressFixtures {

    private SellerAddressFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ADDRESS_NAME = "테스트 주소";
    public static final String DEFAULT_ZIPCODE = "06164";
    public static final String DEFAULT_LINE1 = "서울 강남구 역삼로 123";
    public static final String DEFAULT_LINE2 = "5층";

    // ===== Domain Fixtures =====

    /** 새로운 SHIPPING 주소 생성. */
    public static SellerAddress newShippingAddress(Long sellerId) {
        return SellerAddress.forNew(
                SellerId.of(sellerId),
                AddressType.SHIPPING,
                AddressName.of("본사 창고"),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, DEFAULT_LINE2),
                true,
                Instant.now());
    }

    /** 새로운 RETURN 주소 생성. */
    public static SellerAddress newReturnAddress(Long sellerId) {
        return SellerAddress.forNew(
                SellerId.of(sellerId),
                AddressType.RETURN,
                AddressName.of("반품 센터"),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, DEFAULT_LINE2),
                true,
                Instant.now());
    }

    /** 기본 SHIPPING 주소 생성 (ID 포함). */
    public static SellerAddress defaultShippingAddress(Long id, Long sellerId) {
        return SellerAddress.reconstitute(
                SellerAddressId.of(id),
                SellerId.of(sellerId),
                AddressType.SHIPPING,
                AddressName.of("본사 창고"),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, DEFAULT_LINE2),
                true,
                null,
                Instant.now(),
                Instant.now());
    }

    /** 기본 RETURN 주소 생성 (ID 포함). */
    public static SellerAddress defaultReturnAddress(Long id, Long sellerId) {
        return SellerAddress.reconstitute(
                SellerAddressId.of(id),
                SellerId.of(sellerId),
                AddressType.RETURN,
                AddressName.of("반품 센터"),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, DEFAULT_LINE2),
                true,
                null,
                Instant.now(),
                Instant.now());
    }

    /** 비기본 SHIPPING 주소 생성. */
    public static SellerAddress nonDefaultShippingAddress(
            Long id, Long sellerId, String addressName) {
        return SellerAddress.reconstitute(
                SellerAddressId.of(id),
                SellerId.of(sellerId),
                AddressType.SHIPPING,
                AddressName.of(addressName),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, DEFAULT_LINE2),
                false,
                null,
                Instant.now(),
                Instant.now());
    }

    /** 비기본 RETURN 주소 생성. */
    public static SellerAddress nonDefaultReturnAddress(
            Long id, Long sellerId, String addressName) {
        return SellerAddress.reconstitute(
                SellerAddressId.of(id),
                SellerId.of(sellerId),
                AddressType.RETURN,
                AddressName.of(addressName),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, DEFAULT_LINE2),
                false,
                null,
                Instant.now(),
                Instant.now());
    }

    /** 삭제된 주소 생성. */
    public static SellerAddress deletedAddress(Long id, Long sellerId) {
        Instant now = Instant.now();
        return SellerAddress.reconstitute(
                SellerAddressId.of(id),
                SellerId.of(sellerId),
                AddressType.SHIPPING,
                AddressName.of("삭제된 주소"),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, DEFAULT_LINE2),
                false,
                now,
                now,
                now);
    }

    /** 커스텀 주소 생성. */
    public static SellerAddress customAddress(
            Long id,
            Long sellerId,
            AddressType addressType,
            String addressName,
            String zipcode,
            String line1,
            String line2,
            boolean isDefault) {
        return SellerAddress.reconstitute(
                SellerAddressId.of(id),
                SellerId.of(sellerId),
                addressType,
                AddressName.of(addressName),
                Address.of(zipcode, line1, line2),
                isDefault,
                null,
                Instant.now(),
                Instant.now());
    }

    /** 상세주소가 없는 주소 생성. */
    public static SellerAddress addressWithoutDetail(
            Long id, Long sellerId, AddressType addressType) {
        return SellerAddress.reconstitute(
                SellerAddressId.of(id),
                SellerId.of(sellerId),
                addressType,
                AddressName.of("상세주소 없는 주소"),
                Address.of(DEFAULT_ZIPCODE, DEFAULT_LINE1, null),
                false,
                null,
                Instant.now(),
                Instant.now());
    }
}
