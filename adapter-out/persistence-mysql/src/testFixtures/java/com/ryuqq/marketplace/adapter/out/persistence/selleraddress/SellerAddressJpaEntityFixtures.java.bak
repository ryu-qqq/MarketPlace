package com.ryuqq.marketplace.adapter.out.persistence.selleraddress;

import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import java.time.Instant;

/**
 * SellerAddressJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerAddressJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerAddressJpaEntityFixtures {

    private SellerAddressJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ADDRESS_TYPE_SHIPPING = "SHIPPING";
    public static final String DEFAULT_ADDRESS_TYPE_RETURN = "RETURN";
    public static final String DEFAULT_ADDRESS_NAME = "테스트 주소";
    public static final String DEFAULT_ZIPCODE = "06164";
    public static final String DEFAULT_ADDRESS = "서울 강남구 역삼로 123";
    public static final String DEFAULT_ADDRESS_DETAIL = "5층";

    // ===== Entity Fixtures =====

    /** SHIPPING 주소 Entity 생성. */
    public static SellerAddressJpaEntity shippingEntity(
            Long sellerId, String addressName, boolean isDefault) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                null,
                sellerId,
                DEFAULT_ADDRESS_TYPE_SHIPPING,
                addressName,
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                DEFAULT_ADDRESS_DETAIL,
                isDefault,
                now,
                now,
                null);
    }

    /** RETURN 주소 Entity 생성. */
    public static SellerAddressJpaEntity returnEntity(
            Long sellerId, String addressName, boolean isDefault) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                null,
                sellerId,
                DEFAULT_ADDRESS_TYPE_RETURN,
                addressName,
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                DEFAULT_ADDRESS_DETAIL,
                isDefault,
                now,
                now,
                null);
    }

    /** 삭제된 주소 Entity 생성. */
    public static SellerAddressJpaEntity deletedEntity(Long sellerId) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                null,
                sellerId,
                DEFAULT_ADDRESS_TYPE_SHIPPING,
                "삭제된 주소",
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                DEFAULT_ADDRESS_DETAIL,
                false,
                now,
                now,
                now); // deletedAt 설정
    }

    /** ID를 지정한 SHIPPING 주소 Entity 생성. */
    public static SellerAddressJpaEntity shippingEntityWithId(
            Long id, Long sellerId, String addressName, boolean isDefault) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                id,
                sellerId,
                DEFAULT_ADDRESS_TYPE_SHIPPING,
                addressName,
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                DEFAULT_ADDRESS_DETAIL,
                isDefault,
                now,
                now,
                null);
    }

    /** 커스텀 주소 정보를 가진 Entity 생성. */
    public static SellerAddressJpaEntity customEntity(
            Long sellerId,
            String addressType,
            String addressName,
            String zipcode,
            String address,
            String addressDetail,
            boolean isDefault) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                null,
                sellerId,
                addressType,
                addressName,
                zipcode,
                address,
                addressDetail,
                isDefault,
                now,
                now,
                null);
    }

    /** 기본 SHIPPING 주소 Entity 생성. */
    public static SellerAddressJpaEntity defaultShippingEntity(Long sellerId) {
        return shippingEntity(sellerId, "본사 창고", true);
    }

    /** 기본 RETURN 주소 Entity 생성. */
    public static SellerAddressJpaEntity defaultReturnEntity(Long sellerId) {
        return returnEntity(sellerId, "반품 센터", true);
    }

    /** 비기본 SHIPPING 주소 Entity 생성. */
    public static SellerAddressJpaEntity nonDefaultShippingEntity(
            Long sellerId, String addressName) {
        return shippingEntity(sellerId, addressName, false);
    }

    /** 비기본 RETURN 주소 Entity 생성. */
    public static SellerAddressJpaEntity nonDefaultReturnEntity(Long sellerId, String addressName) {
        return returnEntity(sellerId, addressName, false);
    }

    /** 상세주소가 없는 Entity 생성. */
    public static SellerAddressJpaEntity entityWithoutAddressDetail(
            Long sellerId, String addressType, String addressName, boolean isDefault) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                null,
                sellerId,
                addressType,
                addressName,
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                null,
                isDefault,
                now,
                now,
                null);
    }

    /** ID를 지정한 삭제된 주소 Entity 생성. */
    public static SellerAddressJpaEntity deletedEntityWithId(Long id, Long sellerId) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                id,
                sellerId,
                DEFAULT_ADDRESS_TYPE_SHIPPING,
                "삭제된 주소",
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                DEFAULT_ADDRESS_DETAIL,
                false,
                now,
                now,
                now);
    }

    /** ID를 지정한 비기본 SHIPPING 주소 Entity 생성. */
    public static SellerAddressJpaEntity nonDefaultShippingEntityWithId(
            Long id, Long sellerId, String addressName) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                id,
                sellerId,
                DEFAULT_ADDRESS_TYPE_SHIPPING,
                addressName,
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                DEFAULT_ADDRESS_DETAIL,
                false,
                now,
                now,
                null);
    }

    /** ID를 지정한 상세주소가 없는 Entity 생성. */
    public static SellerAddressJpaEntity entityWithoutAddressDetailWithId(
            Long id, Long sellerId, String addressType, String addressName, boolean isDefault) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                id,
                sellerId,
                addressType,
                addressName,
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                null,
                isDefault,
                now,
                now,
                null);
    }

    /** ID를 지정한 RETURN 주소 Entity 생성. */
    public static SellerAddressJpaEntity returnEntityWithId(
            Long id, Long sellerId, String addressName, boolean isDefault) {
        Instant now = Instant.now();
        return SellerAddressJpaEntity.create(
                id,
                sellerId,
                DEFAULT_ADDRESS_TYPE_RETURN,
                addressName,
                DEFAULT_ZIPCODE,
                DEFAULT_ADDRESS,
                DEFAULT_ADDRESS_DETAIL,
                isDefault,
                now,
                now,
                null);
    }
}
