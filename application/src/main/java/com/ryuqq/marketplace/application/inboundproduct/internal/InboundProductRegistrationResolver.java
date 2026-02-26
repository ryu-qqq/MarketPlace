package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 인바운드 상품의 배송/환불/고시정보를 해석하는 리졸버.
 *
 * <p>InboundProduct이 아닌 Command 데이터를 기반으로 해석하며, 결과를 {@link ResolvedPolicies}로 반환합니다.
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

    /**
     * InboundProduct에서 직접 정책을 해석하고 적용한다.
     *
     * <p>매핑 완료된 InboundProduct를 기반으로 배송/환불 정책 및 고시정보를 해석하여 적용합니다.
     *
     * @param product 매핑 완료된 인바운드 상품
     * @param now 현재 시각
     */
    public void resolveAndApply(
            com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct product,
            java.time.Instant now) {
        SellerId sellerId = SellerId.of(product.sellerId());
        Long shippingPolicyId = resolveShippingPolicyId(sellerId);
        Long refundPolicyId = resolveRefundPolicyId(sellerId);

        Optional<NoticeCategory> noticeCategoryOpt =
                categoryNoticeResolver.resolve(product.internalCategoryId());
        Long noticeCategoryId = noticeCategoryOpt.map(NoticeCategory::idValue).orElse(null);

        product.applyResolution(shippingPolicyId, refundPolicyId, noticeCategoryId, now);
    }

    /**
     * 셀러의 기본 정책과 카테고리 고시정보를 해석한다.
     *
     * @param sellerId 셀러 ID
     * @param internalCategoryId 내부 카테고리 ID (매핑 완료 후)
     * @param command 수신 커맨드 (고시정보 엔트리 추출용)
     * @return 해석된 정책 정보
     */
    public ResolvedPolicies resolve(
            SellerId sellerId, Long internalCategoryId, ReceiveInboundProductCommand command) {

        Long shippingPolicyId = resolveShippingPolicyId(sellerId);
        Long refundPolicyId = resolveRefundPolicyId(sellerId);
        NoticeResolution noticeResolution = resolveNotice(internalCategoryId, command.notice());

        return new ResolvedPolicies(
                shippingPolicyId,
                refundPolicyId,
                noticeResolution.noticeCategoryId(),
                noticeResolution.resolvedEntries());
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

    private NoticeResolution resolveNotice(
            Long internalCategoryId, ReceiveInboundProductCommand.NoticeCommand notice) {
        Optional<NoticeCategory> noticeCategoryOpt =
                categoryNoticeResolver.resolve(internalCategoryId);

        if (noticeCategoryOpt.isEmpty()) {
            log.warn("카테고리 ID {}에 해당하는 고시정보 카테고리 없음", internalCategoryId);
            return new NoticeResolution(null, List.of());
        }

        NoticeCategory noticeCategory = noticeCategoryOpt.get();
        List<ReceiveInboundProductCommand.NoticeEntryCommand> inboundEntries =
                notice != null && notice.entries() != null ? notice.entries() : List.of();

        List<ResolvedPolicies.ResolvedNoticeEntry> resolvedEntries =
                noticeCategory.fields().stream()
                        .map(
                                field -> {
                                    String value =
                                            findValueByFieldCode(
                                                            inboundEntries, field.fieldCodeValue())
                                                    .orElse(DEFAULT_NOTICE_VALUE);
                                    return new ResolvedPolicies.ResolvedNoticeEntry(
                                            field.idValue(), value);
                                })
                        .toList();

        return new NoticeResolution(noticeCategory.idValue(), resolvedEntries);
    }

    private Optional<String> findValueByFieldCode(
            List<ReceiveInboundProductCommand.NoticeEntryCommand> entries, String fieldCode) {
        return entries.stream()
                .filter(e -> fieldCode.equals(e.fieldCode()))
                .map(ReceiveInboundProductCommand.NoticeEntryCommand::fieldValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }

    private record NoticeResolution(
            Long noticeCategoryId, List<ResolvedPolicies.ResolvedNoticeEntry> resolvedEntries) {}
}
