package com.ryuqq.marketplace.application.selleroption.dto.command;

import java.util.List;

/**
 * RegisterSellerOptionGroupsCommand - 셀러 옵션 그룹 등록 Command.
 *
 * <p>APP-CMD-001: Command Record 패턴
 *
 * <p>APP-CMD-002: Primitive 타입 사용 (DTO 레이어)
 */
public record RegisterSellerOptionGroupsCommand(
        long productGroupId, String optionType, List<OptionGroupCommand> optionGroups) {

    public record OptionGroupCommand(
            String optionGroupName,
            Long canonicalOptionGroupId,
            String inputType,
            List<OptionValueCommand> optionValues) {}

    public record OptionValueCommand(
            String optionValueName, Long canonicalOptionValueId, int sortOrder) {}
}
