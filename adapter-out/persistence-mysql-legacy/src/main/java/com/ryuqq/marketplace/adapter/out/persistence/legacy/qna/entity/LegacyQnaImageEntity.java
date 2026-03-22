package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyQnaImageEntity - 레거시 QnA 이미지 엔티티.
 *
 * <p>레거시 DB의 qna_image 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "qna_image")
public class LegacyQnaImageEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_image_id")
    private Long id;

    @Column(name = "QNA_ISSUE_TYPE")
    private String qnaIssueType;

    @Column(name = "QNA_ID")
    private Long qnaId;

    @Column(name = "QNA_ANSWER_ID")
    private Long qnaAnswerId;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "DISPLAY_ORDER")
    private Long displayOrder;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyQnaImageEntity() {}

    public static LegacyQnaImageEntity create(
            Long qnaId, Long qnaAnswerId, String issueType, String imageUrl, int displayOrder) {
        LegacyQnaImageEntity entity = new LegacyQnaImageEntity();
        entity.qnaId = qnaId;
        entity.qnaAnswerId = qnaAnswerId;
        entity.qnaIssueType = issueType;
        entity.imageUrl = imageUrl;
        entity.displayOrder = (long) displayOrder;
        entity.deleteYn = "N";
        return entity;
    }

    public void softDelete() {
        this.deleteYn = "Y";
    }

    public Long getId() {
        return id;
    }

    public String getQnaIssueType() {
        return qnaIssueType;
    }

    public Long getQnaId() {
        return qnaId;
    }

    public Long getQnaAnswerId() {
        return qnaAnswerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
