package com.ryuqq.marketplace.application.shipment.factory;

import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import org.springframework.stereotype.Component;

/**
 * Shipment Command Factory.
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 */
@Component
public class ShipmentCommandFactory {

    /**
     * ShipmentMethod 생성.
     *
     * @param shipmentMethodType 배송 방법 유형 문자열
     * @param courierCode 택배사 코드
     * @param courierName 택배사명
     * @return ShipmentMethod
     */
    public ShipmentMethod createShipmentMethod(
            String shipmentMethodType, String courierCode, String courierName) {
        ShipmentMethodType type = resolveMethodType(shipmentMethodType);
        return ShipmentMethod.of(type, courierCode, courierName);
    }

    private ShipmentMethodType resolveMethodType(String typeString) {
        if (typeString == null || typeString.isBlank()) {
            return ShipmentMethodType.COURIER;
        }

        for (ShipmentMethodType type : ShipmentMethodType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                return type;
            }
        }

        return ShipmentMethodType.COURIER;
    }
}
