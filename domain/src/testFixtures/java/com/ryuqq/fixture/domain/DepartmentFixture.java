package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.Department;

/**
 * Department Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class DepartmentFixture {

    private DepartmentFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 Department Fixture (패션)
     */
    public static Department defaultDepartment() {
        return Department.FASHION;
    }

    /**
     * 뷰티 Department Fixture
     */
    public static Department beautyDepartment() {
        return Department.BEAUTY;
    }

    /**
     * 리빙 Department Fixture
     */
    public static Department livingDepartment() {
        return Department.LIVING;
    }

    /**
     * 디지털 Department Fixture
     */
    public static Department digitalDepartment() {
        return Department.DIGITAL;
    }

    /**
     * 기타 Department Fixture
     */
    public static Department etcDepartment() {
        return Department.ETC;
    }
}
