package com.ryuqq.marketplace.application.commoncodetype;

import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import java.util.List;

/**
 * CommonCodeType Command DTO 테스트 Fixtures.
 *
 * <p>Application Layer 테스트에서 Command DTO 생성에 사용됩니다.
 */
public final class CommonCodeTypeCommandFixtures {

    private CommonCodeTypeCommandFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "PAYMENT_METHOD";
    public static final String DEFAULT_NAME = "결제수단";
    public static final String DEFAULT_DESCRIPTION = "결제수단 공통코드 타입";
    public static final int DEFAULT_DISPLAY_ORDER = 1;

    // ===== RegisterCommonCodeTypeCommand Fixtures =====

    public static RegisterCommonCodeTypeCommand registerCommand() {
        return new RegisterCommonCodeTypeCommand(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_DISPLAY_ORDER);
    }

    public static RegisterCommonCodeTypeCommand registerCommand(String code) {
        return new RegisterCommonCodeTypeCommand(
                code, DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_DISPLAY_ORDER);
    }

    public static RegisterCommonCodeTypeCommand registerCommand(
            String code, String name, String description, int displayOrder) {
        return new RegisterCommonCodeTypeCommand(code, name, description, displayOrder);
    }

    // ===== UpdateCommonCodeTypeCommand Fixtures =====

    public static UpdateCommonCodeTypeCommand updateCommand(Long id) {
        return new UpdateCommonCodeTypeCommand(id, "수정된 이름", "수정된 설명", 2);
    }

    public static UpdateCommonCodeTypeCommand updateCommand(
            Long id, String name, String description, int displayOrder) {
        return new UpdateCommonCodeTypeCommand(id, name, description, displayOrder);
    }

    // ===== ChangeActiveStatusCommand Fixtures =====

    public static ChangeActiveStatusCommand changeStatusCommand(Long id, boolean active) {
        return new ChangeActiveStatusCommand(List.of(id), active);
    }

    public static ChangeActiveStatusCommand changeStatusCommand(List<Long> ids, boolean active) {
        return new ChangeActiveStatusCommand(ids, active);
    }

    public static ChangeActiveStatusCommand activateCommand(Long id) {
        return changeStatusCommand(id, true);
    }

    public static ChangeActiveStatusCommand deactivateCommand(Long id) {
        return changeStatusCommand(id, false);
    }
}
