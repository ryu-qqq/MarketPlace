package com.ryuqq.marketplace.application.selleroption.dto.command;

import java.util.List;

/**
 * UpdateSellerOptionGroupsCommand - 셀러 옵션 그룹 수정 Command.
 *
 * <p>APP-CMD-001: Command Record 패턴
 *
 * <p>APP-CMD-002: Primitive 타입 사용 (DTO 레이어)
 */
public record UpdateSellerOptionGroupsCommand(
        long productGroupId, List<OptionGroupCommand> optionGroups) {

    public record OptionGroupCommand(
            Long sellerOptionGroupId,
            String optionGroupName,
            Long canonicalOptionGroupId,
            List<OptionValueCommand> optionValues) {}

    public record OptionValueCommand(
            Long sellerOptionValueId,
            String optionValueName,
            Long canonicalOptionValueId,
            int sortOrder) {}
}
