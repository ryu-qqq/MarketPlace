package com.ryuqq.marketplace.adapter.out.persistence.legacy.auth.entity;

import java.io.Serializable;
import java.util.Objects;

/** admin_auth_group 복합키. */
public class LegacyAdminAuthGroupId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long adminId;
    private Long authGroupId;

    public LegacyAdminAuthGroupId() {}

    public LegacyAdminAuthGroupId(Long adminId, Long authGroupId) {
        this.adminId = adminId;
        this.authGroupId = authGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LegacyAdminAuthGroupId that)) {
            return false;
        }
        return Objects.equals(adminId, that.adminId)
                && Objects.equals(authGroupId, that.authGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId, authGroupId);
    }
}
