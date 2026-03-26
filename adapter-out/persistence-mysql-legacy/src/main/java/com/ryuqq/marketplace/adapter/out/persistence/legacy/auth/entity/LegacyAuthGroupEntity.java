package com.ryuqq.marketplace.adapter.out.persistence.legacy.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** luxurydb auth_group 테이블 매핑. */
@Entity
@Table(name = "auth_group")
public class LegacyAuthGroupEntity {

    @Id
    @Column(name = "AUTH_GROUP_ID")
    private Long id;

    @Column(name = "AUTH_GROUP_TYPE", nullable = false, length = 50)
    private String authGroupType;

    @Column(name = "GROUP_DESCRIPTION", length = 200)
    private String groupDescription;

    protected LegacyAuthGroupEntity() {}

    public Long getId() {
        return id;
    }

    public String getAuthGroupType() {
        return authGroupType;
    }

    public String getGroupDescription() {
        return groupDescription;
    }
}
