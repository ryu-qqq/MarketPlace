package com.ryuqq.marketplace.application.adminmenu.manager;

import com.ryuqq.marketplace.application.adminmenu.port.out.query.AdminMenuQueryPort;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Admin 메뉴 Read Manager. */
@Component
public class AdminMenuReadManager {

    private final AdminMenuQueryPort queryPort;

    public AdminMenuReadManager(AdminMenuQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<AdminMenu> findActiveByMaxRoleLevel(int roleLevel) {
        return queryPort.findActiveByMaxRoleLevel(roleLevel);
    }
}
