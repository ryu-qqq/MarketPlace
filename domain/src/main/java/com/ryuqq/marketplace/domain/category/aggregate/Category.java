package com.ryuqq.marketplace.domain.category.aggregate;

import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import java.time.Instant;

/** 카테고리 Aggregate Root. */
public class Category {

    private final CategoryId id;
    private final CategoryCode code;
    private CategoryName categoryName;
    private final Long parentId;
    private CategoryDepth depth;
    private CategoryPath path;
    private SortOrder sortOrder;
    private boolean leaf;
    private CategoryStatus status;
    private Department department;
    private CategoryGroup categoryGroup;
    private String displayPath;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private Category(
            CategoryId id,
            CategoryCode code,
            CategoryName categoryName,
            Long parentId,
            CategoryDepth depth,
            CategoryPath path,
            SortOrder sortOrder,
            boolean leaf,
            CategoryStatus status,
            Department department,
            CategoryGroup categoryGroup,
            String displayPath,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.categoryName = categoryName;
        this.parentId = parentId;
        this.depth = depth;
        this.path = path;
        this.sortOrder = sortOrder;
        this.leaf = leaf;
        this.status = status;
        this.department = department;
        this.categoryGroup = categoryGroup != null ? categoryGroup : CategoryGroup.ETC;
        this.displayPath = displayPath;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Category forNew(
            CategoryCode code,
            CategoryName categoryName,
            Long parentId,
            CategoryDepth depth,
            CategoryPath path,
            SortOrder sortOrder,
            Department department,
            CategoryGroup categoryGroup,
            String displayPath,
            Instant now) {
        return new Category(
                CategoryId.forNew(),
                code,
                categoryName,
                parentId,
                depth,
                path,
                sortOrder,
                true,
                CategoryStatus.ACTIVE,
                department,
                categoryGroup,
                displayPath,
                DeletionStatus.active(),
                now,
                now);
    }

    public static Category reconstitute(
            CategoryId id,
            CategoryCode code,
            CategoryName categoryName,
            Long parentId,
            CategoryDepth depth,
            CategoryPath path,
            SortOrder sortOrder,
            boolean leaf,
            CategoryStatus status,
            Department department,
            CategoryGroup categoryGroup,
            String displayPath,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        DeletionStatus deletion =
                deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
        return new Category(
                id,
                code,
                categoryName,
                parentId,
                depth,
                path,
                sortOrder,
                leaf,
                status,
                department,
                categoryGroup,
                displayPath,
                deletion,
                createdAt,
                updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 카테고리 정보 수정.
     *
     * @param updateData 수정 데이터
     * @param now 현재 시간
     */
    public void update(CategoryUpdateData updateData, Instant now) {
        this.categoryName = updateData.categoryName();
        this.sortOrder = updateData.sortOrder();
        this.status = updateData.status();
        this.department = updateData.department();
        this.categoryGroup = updateData.categoryGroup();
        this.updatedAt = now;
    }

    /**
     * 카테고리 이동 (부모 변경 시 경로/깊이 갱신).
     *
     * @param newPath 새 경로
     * @param newDepth 새 깊이
     * @param now 현재 시간
     */
    public void move(CategoryPath newPath, CategoryDepth newDepth, Instant now) {
        this.path = newPath;
        this.depth = newDepth;
        this.updatedAt = now;
    }

    /** 리프 노드 해제 (자식이 추가될 때). */
    public void markAsNonLeaf(Instant now) {
        this.leaf = false;
        this.updatedAt = now;
    }

    /** 리프 노드 설정 (자식이 모두 삭제될 때). */
    public void markAsLeaf(Instant now) {
        this.leaf = true;
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        this.status = CategoryStatus.ACTIVE;
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.status = CategoryStatus.INACTIVE;
        this.updatedAt = now;
    }

    /**
     * 카테고리 삭제 (Soft Delete).
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 카테고리 복원.
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    public boolean isRoot() {
        return parentId == null;
    }

    // Getters
    public CategoryId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public CategoryCode code() {
        return code;
    }

    public String codeValue() {
        return code.value();
    }

    public CategoryName categoryName() {
        return categoryName;
    }

    public String nameKo() {
        return categoryName.nameKo();
    }

    public String nameEn() {
        return categoryName.nameEn();
    }

    public Long parentId() {
        return parentId;
    }

    public CategoryDepth depth() {
        return depth;
    }

    public int depthValue() {
        return depth.value();
    }

    public CategoryPath path() {
        return path;
    }

    public String pathValue() {
        return path.value();
    }

    public SortOrder sortOrder() {
        return sortOrder;
    }

    public int sortOrderValue() {
        return sortOrder.value();
    }

    public boolean isLeaf() {
        return leaf;
    }

    public CategoryStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public Department department() {
        return department;
    }

    public CategoryGroup categoryGroup() {
        return categoryGroup;
    }

    public String displayPath() {
        return displayPath;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

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
