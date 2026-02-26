package com.ryuqq.marketplace.domain.seller.vo;

import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;

/** CS 연락처 정보 Value Object. 전화번호, 휴대폰, 이메일 중 최소 하나는 있어야 합니다. */
public record CsContact(PhoneNumber phone, PhoneNumber mobile, Email email) {

    public CsContact {
        if (phone == null && mobile == null && email == null) {
            throw new IllegalArgumentException("CS 연락처는 전화번호, 휴대폰, 이메일 중 최소 하나가 필요합니다");
        }
    }

    public static CsContact of(String phone, String mobile, String email) {
        return new CsContact(
                phone != null && !phone.isBlank() ? PhoneNumber.of(phone) : null,
                mobile != null && !mobile.isBlank() ? PhoneNumber.of(mobile) : null,
                email != null && !email.isBlank() ? Email.of(email) : null);
    }

    public String phoneValue() {
        return phone != null ? phone.value() : null;
    }

    public String mobileValue() {
        return mobile != null ? mobile.value() : null;
    }

    public String emailValue() {
        return email != null ? email.value() : null;
    }

    public boolean hasPhone() {
        return phone != null;
    }

    public boolean hasMobile() {
        return mobile != null;
    }

    public boolean hasEmail() {
        return email != null;
    }
}
