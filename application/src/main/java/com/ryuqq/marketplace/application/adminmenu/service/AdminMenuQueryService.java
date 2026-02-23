package com.ryuqq.marketplace.application.adminmenu.service;

import com.ryuqq.marketplace.application.adminmenu.manager.AdminMenuReadManager;
import com.ryuqq.marketplace.application.adminmenu.port.in.query.GetAccessibleMenusUseCase;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import java.util.List;
import org.springframework.stereotype.Service;

/** Admin 메뉴 Query Service. */
@Service
public class AdminMenuQueryService implements GetAccessibleMenusUseCase {

    private final AdminMenuReadManager readManager;

    public AdminMenuQueryService(AdminMenuReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    public List<AdminMenu> execute(AdminRole userRole) {
        return readManager.findActiveByMaxRoleLevel(userRole.level());
    }
}
