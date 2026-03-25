package com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * LegacyBaseEntity - 레거시 감사 정보 공통 추상 클래스.
 *
 * <p>레거시 DB의 공통 감사 필드를 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class LegacyBaseEntity {

    @Column(name = "INSERT_OPERATOR")
    private String insertOperator;

    @Column(name = "UPDATE_OPERATOR")
    private String updateOperator;

    @Column(name = "INSERT_DATE")
    private LocalDateTime insertDate;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    protected LegacyBaseEntity() {}

    protected void initAuditFields(String operator) {
        LocalDateTime now = LocalDateTime.now();
        this.insertOperator = operator;
        this.updateOperator = operator;
        this.insertDate = now;
        this.updateDate = now;
    }

    public String getInsertOperator() {
        return insertOperator;
    }

    public String getUpdateOperator() {
        return updateOperator;
    }

    public LocalDateTime getInsertDate() {
        return insertDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }
}
