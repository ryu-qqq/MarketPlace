package com.ryuqq.marketplace.domain.notice.aggregate;

import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryName;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 고시정보 카테고리 Aggregate Root. */
public class NoticeCategory {

    private final NoticeCategoryId id;
    private final NoticeCategoryCode code;
    private NoticeCategoryName categoryName;
    private final CategoryGroup targetCategoryGroup;
    private boolean active;
    private final List<NoticeField> fields;
    private final Instant createdAt;
    private Instant updatedAt;

    private NoticeCategory(
            NoticeCategoryId id,
            NoticeCategoryCode code,
            NoticeCategoryName categoryName,
            CategoryGroup targetCategoryGroup,
            boolean active,
            List<NoticeField> fields,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.categoryName = categoryName;
        this.targetCategoryGroup = targetCategoryGroup;
        this.active = active;
        this.fields = new ArrayList<>(fields);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 고시정보 카테고리 생성. */
    public static NoticeCategory forNew(
            NoticeCategoryCode code,
            NoticeCategoryName categoryName,
            CategoryGroup targetCategoryGroup,
            Instant now) {
        return new NoticeCategory(
                NoticeCategoryId.forNew(),
                code,
                categoryName,
                targetCategoryGroup,
                true,
                List.of(),
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static NoticeCategory reconstitute(
            NoticeCategoryId id,
            NoticeCategoryCode code,
            NoticeCategoryName categoryName,
            CategoryGroup targetCategoryGroup,
            boolean active,
            List<NoticeField> fields,
            Instant createdAt,
            Instant updatedAt) {
        return new NoticeCategory(
                id, code, categoryName, targetCategoryGroup, active, fields, createdAt, updatedAt);
    }

    /** 카테고리 이름 수정. */
    public void updateName(NoticeCategoryName categoryName, Instant now) {
        this.categoryName = categoryName;
        this.updatedAt = now;
    }

    /** 필드 추가. */
    public void addField(NoticeField field) {
        this.fields.add(field);
    }

    /** 활성화. */
    public void activate(Instant now) {
        this.active = true;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    public NoticeCategoryId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public NoticeCategoryCode code() {
        return code;
    }

    public String codeValue() {
        return code.value();
    }

    public NoticeCategoryName categoryName() {
        return categoryName;
    }

    public String nameKo() {
        return categoryName.nameKo();
    }

    public String nameEn() {
        return categoryName.nameEn();
    }

    public CategoryGroup targetCategoryGroup() {
        return targetCategoryGroup;
    }

    public boolean isActive() {
        return active;
    }

    public List<NoticeField> fields() {
        return Collections.unmodifiableList(fields);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
