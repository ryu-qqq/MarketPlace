package com.ryuqq.marketplace.domain.commoncodetype.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.commoncodetype.vo.CommonCodeTypeCode;
import com.ryuqq.marketplace.domain.commoncodetype.vo.CommonCodeTypeDescription;
import com.ryuqq.marketplace.domain.commoncodetype.vo.CommonCodeTypeName;
import com.ryuqq.marketplace.domain.commoncodetype.vo.DisplayOrder;
import java.time.Instant;

/**
 * 공통 코드 타입 Aggregate Root.
 *
 * <p>결제수단, 배송사, 은행 등의 공통 코드 타입을 관리합니다.
 */
public class CommonCodeType {

    private final CommonCodeTypeId id;
    private final CommonCodeTypeCode code;
    private CommonCodeTypeName name;
    private CommonCodeTypeDescription description;
    private DisplayOrder displayOrder;
    private boolean active;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private CommonCodeType(
            CommonCodeTypeId id,
            CommonCodeTypeCode code,
            CommonCodeTypeName name,
            CommonCodeTypeDescription description,
            DisplayOrder displayOrder,
            boolean active,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.active = active;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 신규 공통 코드 타입 생성.
     *
     * @param code 코드 (영문 대문자, 언더스코어)
     * @param name 이름
     * @param description 설명 (nullable)
     * @param displayOrder 표시 순서
     * @param now 현재 시간
     * @return 신규 CommonCodeType
     */
    public static CommonCodeType forNew(
            String code, String name, String description, int displayOrder, Instant now) {
        return new CommonCodeType(
                CommonCodeTypeId.forNew(),
                CommonCodeTypeCode.of(code),
                CommonCodeTypeName.of(name),
                CommonCodeTypeDescription.of(description),
                DisplayOrder.of(displayOrder),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 영속성 계층에서 복원.
     *
     * @param id ID
     * @param code 코드
     * @param name 이름
     * @param description 설명
     * @param displayOrder 표시 순서
     * @param active 활성화 여부
     * @param deletedAt 삭제 시각 (null이면 활성 상태)
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     * @return 복원된 CommonCodeType
     */
    public static CommonCodeType reconstitute(
            CommonCodeTypeId id,
            String code,
            String name,
            String description,
            int displayOrder,
            boolean active,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        DeletionStatus status =
                deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
        return new CommonCodeType(
                id,
                CommonCodeTypeCode.of(code),
                CommonCodeTypeName.of(name),
                CommonCodeTypeDescription.of(description),
                DisplayOrder.of(displayOrder),
                active,
                status,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /** 신규 생성 여부 확인 */
    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 공통 코드 타입 정보 수정.
     *
     * @param updateData 수정 데이터
     * @param now 현재 시간
     */
    public void update(CommonCodeTypeUpdateData updateData, Instant now) {
        this.name = updateData.name();
        this.description = updateData.description();
        this.displayOrder = updateData.displayOrder();
        this.updatedAt = now;
    }

    /**
     * 활성화 상태 변경.
     *
     * @param active 활성화 여부
     * @param now 현재 시간
     */
    public void changeActiveStatus(boolean active, Instant now) {
        this.active = active;
        this.updatedAt = now;
    }

    /**
     * 코드 타입 삭제 (Soft Delete).
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 코드 타입 복원.
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // ========== Accessor Methods ==========

    public CommonCodeTypeId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public String code() {
        return code.value();
    }

    public String name() {
        return name.value();
    }

    public String description() {
        return description.value();
    }

    public int displayOrder() {
        return displayOrder.value();
    }

    public boolean isActive() {
        return active;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    /** 삭제 여부 확인 */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    /**
     * 삭제 시각 반환.
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
