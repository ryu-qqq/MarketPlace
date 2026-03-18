package com.ryuqq.marketplace.adapter.out.persistence.legacy.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** luxurydb admin_auth_group 테이블 매핑. 복합키(admin_id, auth_group_id). */
@Entity
@Table(name = "admin_auth_group")
@IdClass(LegacyAdminAuthGroupId.class)
public class LegacyAdminAuthGroupEntity {

    @Id
    @Column(name = "ADMIN_ID")
    private Long adminId;

    @Id
    @Column(name = "AUTH_GROUP_ID")
    private Long authGroupId;

    @Column(name = "DELETE_YN", nullable = false, length = 1)
    private String deleteYn;

    protected LegacyAdminAuthGroupEntity() {}

    public Long getAdminId() {
        return adminId;
    }

    public Long getAuthGroupId() {
        return authGroupId;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
