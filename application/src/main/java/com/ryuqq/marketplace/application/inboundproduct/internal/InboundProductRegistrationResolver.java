package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundNoticeEntry;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 수신 시점에 배송/환불/고시정보를 해석하여 InboundProduct에 적용하는 리졸버.
 *
 * <p>매핑 완료(MAPPED) 상태의 InboundProduct에 대해 셀러 배송/환불 정책을 검증하고, 카테고리 고시정보 필드를 해석하여 누락 필드에 기본값을 채웁니다.
 */
@Component
public class InboundProductRegistrationResolver {

    private static final Logger log =
            LoggerFactory.getLogger(InboundProductRegistrationResolver.class);
    private static final String DEFAULT_NOTICE_VALUE = "상세설명 참고";

    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;
    private final CategoryNoticeResolver categoryNoticeResolver;

    public InboundProductRegistrationResolver(
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager,
            CategoryNoticeResolver categoryNoticeResolver) {
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
        this.categoryNoticeResolver = categoryNoticeResolver;
    }

    /** 매핑 완료된 InboundProduct의 배송/환불/고시정보를 해석하고 적용한다. */
    public void resolveAndApply(InboundProduct product, Instant now) {
        SellerId sellerId = SellerId.of(product.sellerId());

        Long shippingPolicyId = resolveShippingPolicyId(sellerId);
        Long refundPolicyId = resolveRefundPolicyId(sellerId);
        NoticeResolutionResult noticeResult =
                resolveNotice(product.payload(), product.internalCategoryId());

        InboundProductPayload resolvedPayload =
                rebuildPayloadWithResolvedNotice(product.payload(), noticeResult.resolvedEntries());

        product.applyResolution(
                shippingPolicyId,
                refundPolicyId,
                noticeResult.noticeCategoryId(),
                resolvedPayload,
                now);

        log.info(
                "인바운드 상품 해석 완료: inboundProductId={}, shippingPolicyId={}, "
                        + "refundPolicyId={}, noticeCategoryId={}",
                product.idValue(),
                shippingPolicyId,
                refundPolicyId,
                noticeResult.noticeCategoryId());
    }

    private Long resolveShippingPolicyId(SellerId sellerId) {
        return shippingPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(ShippingPolicy::id)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "셀러 ID " + sellerId.value() + "의 기본 배송 정책이 없습니다"))
                .value();
    }

    private Long resolveRefundPolicyId(SellerId sellerId) {
        return refundPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(RefundPolicy::id)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "셀러 ID " + sellerId.value() + "의 기본 환불 정책이 없습니다"))
                .value();
    }

    private NoticeResolutionResult resolveNotice(
            InboundProductPayload payload, Long internalCategoryId) {
        Optional<NoticeCategory> noticeCategoryOpt =
                categoryNoticeResolver.resolve(internalCategoryId);

        if (noticeCategoryOpt.isEmpty()) {
            log.warn("카테고리 ID {}에 해당하는 고시정보 카테고리 없음", internalCategoryId);
            return new NoticeResolutionResult(null, payload.noticeEntries());
        }

        NoticeCategory noticeCategory = noticeCategoryOpt.get();
        List<InboundNoticeEntry> resolvedEntries =
                resolveNoticeEntries(payload.noticeEntries(), noticeCategory);
        return new NoticeResolutionResult(noticeCategory.idValue(), resolvedEntries);
    }

    /** 카테고리 고시정보 필드 기준으로 인바운드 엔트리를 매핑하고 누락 필드에 기본값을 채운다. */
    private List<InboundNoticeEntry> resolveNoticeEntries(
            List<InboundNoticeEntry> inboundEntries, NoticeCategory noticeCategory) {
        return noticeCategory.fields().stream()
                .map(
                        field -> {
                            String value =
                                    findValueByFieldCode(inboundEntries, field.fieldCodeValue())
                                            .orElse(DEFAULT_NOTICE_VALUE);
                            return InboundNoticeEntry.ofResolved(
                                    field.fieldCodeValue(), value, field.idValue());
                        })
                .toList();
    }

    private Optional<String> findValueByFieldCode(
            List<InboundNoticeEntry> entries, String fieldCode) {
        if (entries == null || entries.isEmpty()) {
            return Optional.empty();
        }
        return entries.stream()
                .filter(e -> fieldCode.equals(e.fieldCode()))
                .map(InboundNoticeEntry::fieldValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }

    private InboundProductPayload rebuildPayloadWithResolvedNotice(
            InboundProductPayload original, List<InboundNoticeEntry> resolvedEntries) {
        return new InboundProductPayload(
                original.images(), original.optionGroups(), original.products(), resolvedEntries);
    }

    private record NoticeResolutionResult(
            Long noticeCategoryId, List<InboundNoticeEntry> resolvedEntries) {}
}
