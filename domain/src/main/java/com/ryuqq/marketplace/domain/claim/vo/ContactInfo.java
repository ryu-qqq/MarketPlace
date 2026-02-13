package com.ryuqq.marketplace.domain.claim.vo;

import com.ryuqq.marketplace.domain.common.vo.Address;

/** 연락처 정보 (발송인/수령인). */
public record ContactInfo(String name, String phone, Address address) {

    public ContactInfo {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("연락처는 필수입니다");
        }
        if (address == null) {
            throw new IllegalArgumentException("주소는 필수입니다");
        }
    }

    public static ContactInfo of(String name, String phone, Address address) {
        return new ContactInfo(name, phone, address);
    }
}
