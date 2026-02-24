package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 고시정보 수정 Coordinator.
 *
 * <p>세토프 PK → 내부 ID 변환 후, NoticeCategory를 해석하고 필드코드 → 필드 ID 매핑을 수행하여
 * ProductNoticeCommandCoordinator에 위임합니다.
 *
 * <p>레거시 시스템은 고정된 필드셋(material, color 등)을 보내지만 카테고리별 고시정보 필드는 다릅니다. 필드 불일치는 다음과 같이 처리합니다:
 *
 * <ul>
 *   <li>레거시가 보내는 필드가 해당 카테고리에 없는 경우 → 무시 (skip)
 *   <li>카테고리의 required 필드를 레거시가 안 보내는 경우 → 기본값("상세설명 참조")으로 채움
 *   <li>카테고리의 optional 필드를 레거시가 안 보내는 경우 → entry 미생성 (skip)
 * </ul>
 */
@Component
public class LegacyNoticeUpdateCoordinator extends LegacyProductUpdateCoordinator {

    private static final String DEFAULT_FIELD_VALUE = "상세설명 참조";

    private final LegacyNoticeCategoryResolver noticeCategoryResolver;
    private final ProductNoticeCommandCoordinator noticeCommandCoordinator;

    public LegacyNoticeUpdateCoordinator(
            LegacyProductIdResolver idResolver,
            LegacyNoticeCategoryResolver noticeCategoryResolver,
            ProductNoticeCommandCoordinator noticeCommandCoordinator) {
        super(idResolver);
        this.noticeCategoryResolver = noticeCategoryResolver;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
    }

    @Transactional
    public void execute(LegacyUpdateNoticeCommand command) {
        long internalId = resolveInternalId(command.setofProductGroupId());

        NoticeCategory noticeCategory = noticeCategoryResolver.resolveByProductGroupId(internalId);
        long noticeCategoryId = noticeCategory.id().value();

        List<UpdateProductNoticeCommand.NoticeEntryCommand> entries =
                buildEntries(command.noticeFields(), noticeCategory.fields());

        UpdateProductNoticeCommand resolvedCommand =
                new UpdateProductNoticeCommand(internalId, noticeCategoryId, entries);
        noticeCommandCoordinator.update(resolvedCommand);
    }

    /**
     * 레거시 필드와 카테고리 필드를 매칭하여 entry 목록을 생성합니다.
     *
     * <p>1. 카테고리 필드 순회: 레거시 값이 있으면 사용, required인데 없으면 기본값 채움, optional이면 skip
     *
     * <p>2. 레거시가 보내는 필드 중 카테고리에 없는 것은 자동으로 무시됨
     */
    private List<UpdateProductNoticeCommand.NoticeEntryCommand> buildEntries(
            Map<String, String> noticeFields, List<NoticeField> categoryFields) {
        List<UpdateProductNoticeCommand.NoticeEntryCommand> entries = new ArrayList<>();

        for (NoticeField field : categoryFields) {
            String legacyValue = noticeFields.get(field.fieldCodeValue());

            if (legacyValue != null && !legacyValue.isBlank()) {
                entries.add(
                        new UpdateProductNoticeCommand.NoticeEntryCommand(
                                field.idValue(), legacyValue));
            } else if (field.isRequired()) {
                entries.add(
                        new UpdateProductNoticeCommand.NoticeEntryCommand(
                                field.idValue(), DEFAULT_FIELD_VALUE));
            }
        }

        return entries;
    }
}
