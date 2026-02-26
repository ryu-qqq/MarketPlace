package com.ryuqq.marketplace.domain.order.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;

/** 결제 정보. */
public record PaymentInfo(
        long paymentId,
        String paymentAgencyId,
        PaymentStatus paymentStatus,
        PaymentMethodType paymentMethod,
        Instant paymentDate,
        Instant canceledDate,
        long userId,
        SiteName siteName,
        Money billAmount,
        Money paymentAmount,
        Money usedMileageAmount) {}
