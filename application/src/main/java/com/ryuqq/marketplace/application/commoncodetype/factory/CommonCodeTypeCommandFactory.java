package com.ryuqq.marketplace.application.commoncodetype.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeTypeUpdateData;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeCommandFactory - 공통 코드 타입 Command Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * @author ryu-qqq
 */
@Component
public class CommonCodeTypeCommandFactory {

    private final TimeProvider timeProvider;

    public CommonCodeTypeCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * RegisterCommonCodeTypeCommand로부터 CommonCodeType 생성
     *
     * @param command 등록 Command
     * @return CommonCodeType 도메인 객체
     */
    public CommonCodeType create(RegisterCommonCodeTypeCommand command) {
        Instant now = timeProvider.now();
        return CommonCodeType.forNew(
                command.code(), command.name(), command.description(), command.displayOrder(), now);
    }

    /**
     * UpdateCommonCodeTypeCommand로부터 UpdateContext 생성
     *
     * <p>FAC-008: ID, UpdateData, changedAt을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<CommonCodeTypeId, CommonCodeTypeUpdateData> createUpdateContext(
            UpdateCommonCodeTypeCommand command) {
        CommonCodeTypeId id = CommonCodeTypeId.of(command.id());
        CommonCodeTypeUpdateData updateData =
                CommonCodeTypeUpdateData.of(
                        command.name(), command.description(), command.displayOrder());
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }

    /**
     * ChangeActiveStatusCommand로부터 StatusChangeContext 목록 생성
     *
     * @param command 상태 변경 Command
     * @return StatusChangeContext 목록
     */
    public List<StatusChangeContext<CommonCodeTypeId>> createStatusChangeContexts(
            ChangeActiveStatusCommand command) {
        Instant changedAt = timeProvider.now();
        return command.ids().stream()
                .map(id -> new StatusChangeContext<>(CommonCodeTypeId.of(id), changedAt))
                .toList();
    }
}
