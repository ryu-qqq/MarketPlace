package com.ryuqq.marketplace.application.adminmenu.port.out.query;

import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import java.util.List;

/** Admin 메뉴 Query Port. */
public interface AdminMenuQueryPort {

    List<AdminMenu> findActiveByMaxRoleLevel(int roleLevel);
}
