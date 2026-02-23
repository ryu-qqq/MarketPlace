package com.ryuqq.marketplace.application.adminmenu.port.in.query;

import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import java.util.List;

/** 역할별 접근 가능한 메뉴 조회 UseCase. */
public interface GetAccessibleMenusUseCase {

    List<AdminMenu> execute(AdminRole userRole);
}
