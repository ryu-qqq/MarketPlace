package com.ryuqq.marketplace.application.commoncode;

import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import java.util.List;

/**
 * CommonCode Command 테스트 Fixtures.
 *
 * <p>CommonCode 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class CommonCodeCommandFixtures {

    private CommonCodeCommandFixtures() {}

    // ===== RegisterCommonCodeCommand =====

    public static RegisterCommonCodeCommand registerCommand(Long commonCodeTypeId) {
        return new RegisterCommonCodeCommand(commonCodeTypeId, "CREDIT_CARD", "신용카드", 1);
    }

    public static RegisterCommonCodeCommand registerCommand(
            Long commonCodeTypeId, String code, String displayName) {
        return new RegisterCommonCodeCommand(commonCodeTypeId, code, displayName, 1);
    }

    public static RegisterCommonCodeCommand registerCommand(
            Long commonCodeTypeId, String code, String displayName, int displayOrder) {
        return new RegisterCommonCodeCommand(commonCodeTypeId, code, displayName, displayOrder);
    }

    // ===== UpdateCommonCodeCommand =====

    public static UpdateCommonCodeCommand updateCommand(Long id) {
        return new UpdateCommonCodeCommand(id, "수정된 표시명", 2);
    }

    public static UpdateCommonCodeCommand updateCommand(
            Long id, String displayName, int displayOrder) {
        return new UpdateCommonCodeCommand(id, displayName, displayOrder);
    }

    // ===== ChangeCommonCodeStatusCommand =====

    public static ChangeCommonCodeStatusCommand activateCommand(Long... ids) {
        return new ChangeCommonCodeStatusCommand(List.of(ids), true);
    }

    public static ChangeCommonCodeStatusCommand deactivateCommand(Long... ids) {
        return new ChangeCommonCodeStatusCommand(List.of(ids), false);
    }
}
