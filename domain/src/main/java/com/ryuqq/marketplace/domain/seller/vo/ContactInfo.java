package com.ryuqq.marketplace.domain.seller.vo;

import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;

/** 담당자 연락처 정보 Value Object. */
public record ContactInfo(String name, PhoneNumber phone, Email email) {

    private static final int NAME_MAX_LENGTH = 50;

    public ContactInfo {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("담당자명은 필수입니다");
        }
        name = name.trim();
        if (name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("담당자명은 " + NAME_MAX_LENGTH + "자를 초과할 수 없습니다");
        }
        if (phone == null) {
            throw new IllegalArgumentException("담당자 연락처는 필수입니다");
        }
        if (email == null) {
            throw new IllegalArgumentException("담당자 이메일은 필수입니다");
        }
    }

    public static ContactInfo of(String name, String phone, String email) {
        return new ContactInfo(name, PhoneNumber.of(phone), Email.of(email));
    }

    public static ContactInfo of(String name, PhoneNumber phone, Email email) {
        return new ContactInfo(name, phone, email);
    }

    public String phoneValue() {
        return phone.value();
    }

    public String emailValue() {
        return email.value();
    }
}
