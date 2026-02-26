package com.ryuqq.marketplace.domain.outboundproduct;

import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundproduct.id.OutboundProductId;
import com.ryuqq.marketplace.domain.outboundproduct.vo.OutboundProductStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.time.Instant;

/**
 * OutboundProduct 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 OutboundProduct 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class OutboundProductFixtures {

    private OutboundProductFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 10L;
    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";

    // ===== Aggregate Fixtures =====

    /** PENDING_REGISTRATION 상태의 신규 OutboundProduct (ID null). */
    public static OutboundProduct newPendingProduct() {
        return OutboundProduct.forNew(
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                Instant.now());
    }

    /** 외부 상품 ID를 알고 있는 신규 OutboundProduct (ID null, REGISTERED 상태). */
    public static OutboundProduct newRegisteredProduct() {
        return OutboundProduct.forNewWithExternalId(
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_EXTERNAL_PRODUCT_ID,
                Instant.now());
    }

    /** PENDING_REGISTRATION 상태로 영속화된 OutboundProduct. */
    public static OutboundProduct pendingProduct() {
        Instant now = Instant.now();
        return OutboundProduct.reconstitute(
                OutboundProductId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                null,
                OutboundProductStatus.PENDING_REGISTRATION,
                now,
                now);
    }

    /** REGISTERED 상태로 영속화된 OutboundProduct. */
    public static OutboundProduct registeredProduct() {
        Instant now = Instant.now();
        return OutboundProduct.reconstitute(
                OutboundProductId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_EXTERNAL_PRODUCT_ID,
                OutboundProductStatus.REGISTERED,
                now,
                now);
    }

    /** REGISTRATION_FAILED 상태로 영속화된 OutboundProduct. */
    public static OutboundProduct failedProduct() {
        Instant now = Instant.now();
        return OutboundProduct.reconstitute(
                OutboundProductId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                null,
                OutboundProductStatus.REGISTRATION_FAILED,
                now,
                now);
    }

    /** ID를 지정한 REGISTERED 상태 OutboundProduct. */
    public static OutboundProduct registeredProduct(Long id) {
        Instant now = Instant.now();
        return OutboundProduct.reconstitute(
                OutboundProductId.of(id),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_EXTERNAL_PRODUCT_ID,
                OutboundProductStatus.REGISTERED,
                now,
                now);
    }

    /** productGroupId와 salesChannelId를 지정한 PENDING 상태 OutboundProduct. */
    public static OutboundProduct pendingProduct(Long productGroupId, Long salesChannelId) {
        Instant now = Instant.now();
        return OutboundProduct.reconstitute(
                OutboundProductId.forNew(),
                ProductGroupId.of(productGroupId),
                SalesChannelId.of(salesChannelId),
                null,
                OutboundProductStatus.PENDING_REGISTRATION,
                now,
                now);
    }
}
