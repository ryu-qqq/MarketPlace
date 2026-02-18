package com.ryuqq.marketplace.application.selleroption.internal;

import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.application.selleroption.factory.SellerOptionGroupFactory;
import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionGroupReadManager;
import com.ryuqq.marketplace.application.selleroption.validator.SellerOptionGroupValidator;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroupDiff;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroupUpdateData;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerOption Command Coordinator.
 *
 * <p>Factory → Validator → PersistFacade 순서로 옵션 그룹 등록/수정을 조율합니다.
 */
@Component
public class SellerOptionCommandCoordinator {

    private final SellerOptionGroupFactory optionGroupFactory;
    private final SellerOptionGroupValidator validator;
    private final SellerOptionGroupReadManager readManager;
    private final SellerOptionPersistFacade persistFacade;

    public SellerOptionCommandCoordinator(
            SellerOptionGroupFactory optionGroupFactory,
            SellerOptionGroupValidator validator,
            SellerOptionGroupReadManager readManager,
            SellerOptionPersistFacade persistFacade) {
        this.optionGroupFactory = optionGroupFactory;
        this.validator = validator;
        this.readManager = readManager;
        this.persistFacade = persistFacade;
    }

    /**
     * 등록 Command로 OptionGroups 생성 + 검증 + 저장.
     *
     * @param command 셀러 옵션 그룹 등록 Command
     * @return 생성된 SellerOptionValueId 목록
     */
    @Transactional
    public List<SellerOptionValueId> register(RegisterSellerOptionGroupsCommand command) {
        ProductGroupId pgId = ProductGroupId.of(command.productGroupId());
        OptionType optionType = OptionType.valueOf(command.optionType());

        SellerOptionGroups optionGroups =
                optionGroupFactory.createFromRegistration(pgId, command.optionGroups());
        validator.validate(optionGroups, optionType);

        return persistFacade.persistAll(optionGroups.groups());
    }

    /**
     * 수정 Command로 OptionGroups diff 기반 수정.
     *
     * <p>entry의 ID 기반으로 기존 엔티티를 매칭합니다. ID가 null이면 신규, non-null이면 기존으로 처리하여 ID가 보존됩니다.
     *
     * <p>persist 후 신규 SellerOptionValueId가 실제 생성 ID로 치환된 결과를 반환합니다.
     *
     * @param command 셀러 옵션 그룹 수정 Command
     * @return 수정 결과 (resolved 활성 ValueId 목록 + 발생 시각)
     */
    @Transactional
    public SellerOptionUpdateResult update(UpdateSellerOptionGroupsCommand command) {
        ProductGroupId pgId = ProductGroupId.of(command.productGroupId());

        SellerOptionGroupUpdateData updateData =
                optionGroupFactory.toUpdateData(pgId, command.optionGroups());
        validator.validateCanonicalReferences(updateData);

        SellerOptionGroups existing = readManager.getByProductGroupId(pgId);
        SellerOptionGroupDiff diff = existing.update(updateData);

        List<SellerOptionValueId> resolvedIds = persistFacade.persistDiff(diff);
        return new SellerOptionUpdateResult(resolvedIds, diff.occurredAt());
    }
}
