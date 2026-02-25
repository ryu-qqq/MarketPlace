package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyQnaAnswerEntity - 레거시 QnA 답변 엔티티.
 *
 * <p>레거시 DB의 qna_answer 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "qna_answer")
public class LegacyQnaAnswerEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_answer_id")
    private Long id;

    @Column(name = "QNA_ID")
    private Long qnaId;

    @Column(name = "QNA_PARENT_ID")
    private Long qnaParentId;

    @Column(name = "QNA_WRITER_TYPE")
    private String qnaWriterType;

    @Column(name = "QNA_STATUS")
    private String qnaStatus;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyQnaAnswerEntity() {}

    public Long getId() {
        return id;
    }

    public Long getQnaId() {
        return qnaId;
    }

    public Long getQnaParentId() {
        return qnaParentId;
    }

    public String getQnaWriterType() {
        return qnaWriterType;
    }

    public String getQnaStatus() {
        return qnaStatus;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
