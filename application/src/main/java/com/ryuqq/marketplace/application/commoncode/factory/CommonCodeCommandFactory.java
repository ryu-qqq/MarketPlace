package com.ryuqq.marketplace.application.commoncode.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCodeUpdateData;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeCommandFactory - 공통 코드 Command Factory.
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeCommandFactory {

    private final TimeProvider timeProvider;

    public CommonCodeCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * RegisterCommonCodeCommand로부터 CommonCode 생성.
     *
     * @param command 등록 Command
     * @return CommonCode 도메인 객체
     */
    public CommonCode create(RegisterCommonCodeCommand command) {
        Instant now = timeProvider.now();
        CommonCodeTypeId commonCodeTypeId = CommonCodeTypeId.of(command.commonCodeTypeId());
        return CommonCode.forNew(
                commonCodeTypeId,
                command.code(),
                command.displayName(),
                command.displayOrder(),
                now);
    }

    /**
     * UpdateCommonCodeCommand로부터 UpdateContext 생성.
     *
     * <p>FAC-008: ID, UpdateData, changedAt을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<CommonCodeId, CommonCodeUpdateData> createUpdateContext(
            UpdateCommonCodeCommand command) {
        CommonCodeId id = CommonCodeId.of(command.id());
        CommonCodeUpdateData updateData =
                CommonCodeUpdateData.of(command.displayName(), command.displayOrder());
        Instant changedAt = timeProvider.now();
        return new UpdateContext<>(id, updateData, changedAt);
    }

    /**
     * ChangeCommonCodeStatusCommand로부터 StatusChangeContext 목록 생성.
     *
     * @param command 상태 변경 Command
     * @return StatusChangeContext 목록
     */
    public List<StatusChangeContext<CommonCodeId>> createStatusChangeContexts(
            ChangeCommonCodeStatusCommand command) {
        Instant changedAt = timeProvider.now();
        return command.ids().stream()
                .map(id -> new StatusChangeContext<>(CommonCodeId.of(id), changedAt))
                .toList();
    }
}
