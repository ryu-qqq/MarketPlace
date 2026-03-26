package com.ryuqq.marketplace.adapter.out.persistence.claim.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.claim.entity.ClaimShipmentJpaEntity;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentMethod;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentStatus;
import com.ryuqq.marketplace.domain.claim.vo.ContactInfo;
import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.claim.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.claim.vo.ShippingFeeInfo;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ClaimShipment JPA Entity Mapper.
 *
 * <p>도메인 객체와 JPA 엔티티 간의 변환을 담당합니다.
 */
@Component
public class ClaimShipmentJpaEntityMapper {

    /**
     * 도메인 객체를 JPA 엔티티로 변환합니다.
     *
     * @param domain ClaimShipment 도메인 객체
     * @return ClaimShipmentJpaEntity
     */
    public ClaimShipmentJpaEntity toEntity(ClaimShipment domain) {
        ClaimShipmentMethod method = domain.method();
        ShippingFeeInfo feeInfo = domain.feeInfo();
        ContactInfo sender = domain.sender();
        ContactInfo receiver = domain.receiver();

        Instant now = Instant.now();

        return ClaimShipmentJpaEntity.create(
                domain.id().value(),
                domain.status().name(),
                method != null ? method.type().name() : null,
                method != null ? method.courierCode() : null,
                method != null ? method.courierName() : null,
                domain.trackingNumber(),
                feeInfo.amount().value(),
                feeInfo.payer().name(),
                feeInfo.includeInPackage(),
                sender != null ? sender.name() : null,
                sender != null ? sender.phone() : null,
                sender != null ? sender.address().line1() : null,
                sender != null ? sender.address().line2() : null,
                sender != null ? sender.address().zipcode() : null,
                receiver != null ? receiver.name() : null,
                receiver != null ? receiver.phone() : null,
                receiver != null ? receiver.address().line1() : null,
                receiver != null ? receiver.address().line2() : null,
                receiver != null ? receiver.address().zipcode() : null,
                domain.shippedAt(),
                domain.receivedAt(),
                now,
                now);
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @param entity ClaimShipmentJpaEntity
     * @return ClaimShipment 도메인 객체
     */
    public ClaimShipment toDomain(ClaimShipmentJpaEntity entity) {
        ClaimShipmentMethod method = resolveMethod(entity);
        ShippingFeeInfo feeInfo = resolveFeeInfo(entity);
        ContactInfo sender =
                resolveContactInfoNullable(
                        entity.getSenderName(),
                        entity.getSenderPhone(),
                        entity.getSenderZipcode(),
                        entity.getSenderAddress(),
                        entity.getSenderAddressDetail());
        ContactInfo receiver =
                resolveContactInfoNullable(
                        entity.getReceiverName(),
                        entity.getReceiverPhone(),
                        entity.getReceiverZipcode(),
                        entity.getReceiverAddress(),
                        entity.getReceiverAddressDetail());

        return ClaimShipment.reconstitute(
                ClaimShipmentId.of(entity.getId()),
                ClaimShipmentStatus.valueOf(entity.getStatus()),
                method,
                entity.getTrackingNumber(),
                feeInfo,
                sender,
                receiver,
                entity.getShippedAt(),
                entity.getReceivedAt());
    }

    private ClaimShipmentMethod resolveMethod(ClaimShipmentJpaEntity entity) {
        if (entity.getMethodType() == null) {
            return null;
        }
        return ClaimShipmentMethod.of(
                ShipmentMethodType.valueOf(entity.getMethodType()),
                entity.getCourierCode(),
                entity.getCourierName());
    }

    private ShippingFeeInfo resolveFeeInfo(ClaimShipmentJpaEntity entity) {
        return ShippingFeeInfo.of(
                Money.of(entity.getFeeAmount()),
                entity.getFeePayer() != null
                        ? FeePayer.valueOf(entity.getFeePayer())
                        : FeePayer.SELLER,
                entity.isFeeIncludeInPackage());
    }

    private ContactInfo resolveContactInfoNullable(
            String name, String phone, String zipcode, String address, String addressDetail) {
        if (name == null && phone == null) {
            return null;
        }
        Address addr = Address.of(zipcode, address, addressDetail);
        return ContactInfo.of(name, phone, addr);
    }
}
