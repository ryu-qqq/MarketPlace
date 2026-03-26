package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyQnaEntity - 레거시 QnA 엔티티.
 *
 * <p>레거시 DB의 qna 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "qna")
public class LegacyQnaEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id")
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "PRIVATE_YN")
    private String privateYn;

    @Column(name = "QNA_STATUS")
    private String qnaStatus;

    @Column(name = "QNA_TYPE")
    private String qnaType;

    @Column(name = "QNA_DETAIL_TYPE")
    private String qnaDetailType;

    @Column(name = "SELLER_ID")
    private Long sellerId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_TYPE")
    private String userType;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyQnaEntity() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPrivateYn() {
        return privateYn;
    }

    public String getQnaStatus() {
        return qnaStatus;
    }

    public String getQnaType() {
        return qnaType;
    }

    public String getQnaDetailType() {
        return qnaDetailType;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public String getDeleteYn() {
        return deleteYn;
    }

    public void updateQnaStatus(String qnaStatus) {
        this.qnaStatus = qnaStatus;
    }
}
