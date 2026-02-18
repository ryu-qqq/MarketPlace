package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;

/**
 * SellerOptionGroups 수정 데이터 (ID 기반 entry).
 *
 * <p>각 GroupEntry/ValueEntry의 ID가 null이면 신규, non-null이면 기존 엔티티를 의미합니다. ID 기반 매칭으로 이름 변경 시에도 엔티티 ID가
 * 보존됩니다.
 */
public class SellerOptionGroupUpdateData {

    private final ProductGroupId productGroupId;
    private final List<GroupEntry> groupEntries;
    private final Instant updatedAt;

    private SellerOptionGroupUpdateData(
            ProductGroupId productGroupId, List<GroupEntry> groupEntries, Instant updatedAt) {
        this.productGroupId = productGroupId;
        this.groupEntries = groupEntries;
        this.updatedAt = updatedAt;
    }

    public static SellerOptionGroupUpdateData of(
            ProductGroupId productGroupId, List<GroupEntry> groupEntries, Instant updatedAt) {
        return new SellerOptionGroupUpdateData(
                productGroupId, List.copyOf(groupEntries), updatedAt);
    }

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public List<GroupEntry> groupEntries() {
        return groupEntries;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    /**
     * 옵션 그룹 엔트리.
     *
     * @param sellerOptionGroupId null이면 신규, non-null이면 기존
     * @param optionGroupName 옵션 그룹명
     * @param canonicalOptionGroupId 캐노니컬 옵션 그룹 ID (nullable)
     * @param sortOrder 정렬 순서
     * @param values 하위 옵션 값 엔트리 목록
     */
    public record GroupEntry(
            Long sellerOptionGroupId,
            String optionGroupName,
            Long canonicalOptionGroupId,
            int sortOrder,
            List<ValueEntry> values) {

        public GroupEntry {
            values = List.copyOf(values);
        }
    }

    /**
     * 옵션 값 엔트리.
     *
     * @param sellerOptionValueId null이면 신규, non-null이면 기존
     * @param optionValueName 옵션 값 이름
     * @param canonicalOptionValueId 캐노니컬 옵션 값 ID (nullable)
     * @param sortOrder 정렬 순서
     */
    public record ValueEntry(
            Long sellerOptionValueId,
            String optionValueName,
            Long canonicalOptionValueId,
            int sortOrder) {}
}
