package com.ryuqq.marketplace.application.commoncode.manager;

import com.ryuqq.marketplace.application.commoncode.port.out.command.CommonCodeCommandPort;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * CommonCodeCommandManager - 공통 코드 Command Manager.
 *
 * <p>APP-MGR-001: Manager는 @Component로 등록.
 *
 * <p>APP-MGR-004: CommandManager는 CommandPort만 의존.
 *
 * <p>APP-MGR-005: @Transactional 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@Transactional
public class CommonCodeCommandManager {

    private final CommonCodeCommandPort commandPort;

    public CommonCodeCommandManager(CommonCodeCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 공통 코드 저장.
     *
     * @param commonCode CommonCode 도메인 객체
     * @return 저장된 공통 코드 ID
     */
    public Long persist(CommonCode commonCode) {
        return commandPort.persist(commonCode);
    }

    /**
     * 공통 코드 일괄 저장.
     *
     * @param commonCodes CommonCode 도메인 객체 목록
     */
    public void persistAll(List<CommonCode> commonCodes) {
        commandPort.persistAll(commonCodes);
    }
}
