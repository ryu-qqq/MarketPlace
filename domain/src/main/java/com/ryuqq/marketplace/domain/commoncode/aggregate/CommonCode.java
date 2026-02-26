package com.ryuqq.marketplace.domain.commoncode.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncode.vo.CommonCodeDisplayName;
import com.ryuqq.marketplace.domain.commoncode.vo.CommonCodeValue;
import com.ryuqq.marketplace.domain.commoncode.vo.DisplayOrder;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;

/**
 * CommonCode - 공통 코드 Aggregate.
 *
 * <p>결제수단, 배송사, 은행 등 백오피스에서 관리되는 코드를 표현합니다.
 *
 * <p>CommonCodeType 하위에 속하며, CommonCodeTypeId로 참조합니다.
 *
 * <p>주요 불변식:
 *
 * <ul>
 *   <li>commonCodeTypeId와 code의 조합은 유니크해야 함
 *   <li>code는 영문 대문자와 숫자, 언더스코어만 허용
 *   <li>displayOrder는 0 이상이어야 함
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class CommonCode {

    private final CommonCodeId id;
    private final CommonCodeTypeId commonCodeTypeId;
    private final CommonCodeValue code;
    private CommonCodeDisplayName displayName;
    private DisplayOrder displayOrder;
    private boolean active;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private CommonCode(
            CommonCodeId id,
            CommonCodeTypeId commonCodeTypeId,
            CommonCodeValue code,
            CommonCodeDisplayName displayName,
            DisplayOrder displayOrder,
            boolean active,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.commonCodeTypeId = commonCodeTypeId;
        this.code = code;
        this.displayName = displayName;
        this.displayOrder = displayOrder;
        this.active = active;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Factory Methods ==========

    /**
     * 새 공통 코드 생성.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID (필수)
     * @param code 코드값 (필수, 영문 대문자+숫자+언더스코어)
     * @param displayName 표시명 (필수)
     * @param displayOrder 표시 순서 (0 이상)
     * @param now 생성 시각
     * @return 새 CommonCode 인스턴스 (active=true)
     */
    public static CommonCode forNew(
            CommonCodeTypeId commonCodeTypeId,
            String code,
            String displayName,
            int displayOrder,
            Instant now) {
        return new CommonCode(
                CommonCodeId.forNew(),
                commonCodeTypeId,
                CommonCodeValue.of(code),
                CommonCodeDisplayName.of(displayName),
                DisplayOrder.of(displayOrder),
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 영속성 계층에서 엔티티 복원.
     *
     * @param id 식별자
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @param code 코드값
     * @param displayName 표시명
     * @param displayOrder 표시 순서
     * @param active 활성화 여부
     * @param deletedAt 삭제 시각 (null이면 활성 상태)
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     * @return 복원된 CommonCode 인스턴스
     */
    public static CommonCode reconstitute(
            CommonCodeId id,
            CommonCodeTypeId commonCodeTypeId,
            String code,
            String displayName,
            int displayOrder,
            boolean active,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        DeletionStatus status =
                deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
        return new CommonCode(
                id,
                commonCodeTypeId,
                CommonCodeValue.of(code),
                CommonCodeDisplayName.of(displayName),
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
     * 공통 코드 정보 수정.
     *
     * @param data 수정할 데이터 번들
     * @param now 수정 시각
     */
    public void update(CommonCodeUpdateData data, Instant now) {
        this.displayName = data.displayName();
        this.displayOrder = data.displayOrder();
        this.updatedAt = now;
    }

    /** 활성화 */
    public void activate(Instant now) {
        this.active = true;
        this.updatedAt = now;
    }

    /** 비활성화 */
    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    /**
     * 공통 코드 삭제 (Soft Delete).
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 공통 코드 복원.
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // ========== Accessor Methods ==========

    public CommonCodeId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public CommonCodeTypeId commonCodeTypeId() {
        return commonCodeTypeId;
    }

    /** 공통 코드 타입 ID 원시값 반환 (편의 메서드) */
    public Long commonCodeTypeIdValue() {
        return commonCodeTypeId.value();
    }

    /** 코드 VO 반환 */
    public CommonCodeValue code() {
        return code;
    }

    /** 코드 원시값 반환 (편의 메서드) */
    public String codeValue() {
        return code.value();
    }

    /** 표시명 VO 반환 */
    public CommonCodeDisplayName displayName() {
        return displayName;
    }

    /** 표시명 원시값 반환 (편의 메서드) */
    public String displayNameValue() {
        return displayName.value();
    }

    /** 표시 순서 VO 반환 */
    public DisplayOrder displayOrder() {
        return displayOrder;
    }

    /** 표시 순서 원시값 반환 (편의 메서드) */
    public int displayOrderValue() {
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
