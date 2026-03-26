package com.ryuqq.marketplace.adapter.out.persistence.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.aggregate.QnaReply;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * QnaJpaEntityMapperTest - Qna Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("QnaJpaEntityMapper 단위 테스트")
class QnaJpaEntityMapperTest {

    private QnaJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new QnaJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingDomain_ConvertsCorrectly() {
            // given
            Qna domain = QnaFixtures.pendingQna();

            // when
            QnaJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerId());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupId());
            assertThat(entity.getQnaType()).isEqualTo(domain.qnaType().name());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.source().salesChannelId());
            assertThat(entity.getExternalQnaId()).isEqualTo(domain.source().externalQnaId());
            assertThat(entity.getQuestionContent()).isEqualTo(domain.questionContent());
            assertThat(entity.getQuestionAuthor()).isEqualTo(domain.questionAuthor());
            assertThat(entity.getStatus().name()).isEqualTo(domain.status().name());
        }

        @Test
        @DisplayName("ANSWERED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithAnsweredDomain_ConvertsCorrectly() {
            // given
            Qna domain = QnaFixtures.answeredQna();

            // when
            QnaJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(QnaJpaEntity.Status.ANSWERED);
        }

        @Test
        @DisplayName("신규 Domain(ID null)을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ConvertsCorrectly() {
            // given
            Qna domain = QnaFixtures.newQna();

            // when
            QnaJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getStatus()).isEqualTo(QnaJpaEntity.Status.PENDING);
        }
    }

    // ========================================================================
    // 2. toReplyEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toReplyEntity 메서드 테스트")
    class ToReplyEntityTest {

        @Test
        @DisplayName("QnaReply Domain을 QnaReplyJpaEntity로 변환합니다")
        void toReplyEntity_WithValidReply_ConvertsCorrectly() {
            // given
            QnaReply reply = QnaFixtures.defaultSellerReply();
            long qnaId = 1L;

            // when
            QnaReplyJpaEntity entity = mapper.toReplyEntity(reply, qnaId);

            // then
            assertThat(entity.getId()).isEqualTo(reply.idValue());
            assertThat(entity.getQnaId()).isEqualTo(qnaId);
            assertThat(entity.getParentReplyId()).isEqualTo(reply.parentReplyId());
            assertThat(entity.getContent()).isEqualTo(reply.content());
            assertThat(entity.getAuthorName()).isEqualTo(reply.authorName());
            assertThat(entity.getReplyType()).isEqualTo(reply.replyType().name());
        }

        @Test
        @DisplayName("부모 Reply가 있는 Reply를 변환합니다")
        void toReplyEntity_WithParentReply_ConvertsCorrectly() {
            // given
            QnaReply reply = QnaFixtures.defaultBuyerFollowUp();
            long qnaId = 1L;

            // when
            QnaReplyJpaEntity entity = mapper.toReplyEntity(reply, qnaId);

            // then
            assertThat(entity.getParentReplyId()).isEqualTo(reply.parentReplyId());
            assertThat(entity.getReplyType()).isEqualTo("BUYER_FOLLOW_UP");
        }

        @Test
        @DisplayName("여러 Reply를 Entity 목록으로 변환합니다")
        void toReplyEntities_WithMultipleReplies_ConvertsAll() {
            // given
            QnaReply reply1 = QnaFixtures.defaultSellerReply();
            QnaReply reply2 = QnaFixtures.defaultBuyerFollowUp();
            long qnaId = 1L;

            // when
            List<QnaReplyJpaEntity> entities =
                    mapper.toReplyEntities(List.of(reply1, reply2), qnaId);

            // then
            assertThat(entities).hasSize(2);
            assertThat(entities).allMatch(e -> e.getQnaId() == qnaId);
        }
    }

    // ========================================================================
    // 3. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환합니다 (Reply 없음)")
        void toDomain_WithPendingEntityAndNoReplies_ConvertsCorrectly() {
            // given
            QnaJpaEntity entity = QnaJpaEntityFixtures.pendingEntity(1L);

            // when
            Qna domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerId()).isEqualTo(entity.getSellerId());
            assertThat(domain.productGroupId()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.qnaType().name()).isEqualTo(entity.getQnaType());
            assertThat(domain.source().salesChannelId()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.source().externalQnaId()).isEqualTo(entity.getExternalQnaId());
            assertThat(domain.questionContent()).isEqualTo(entity.getQuestionContent());
            assertThat(domain.questionAuthor()).isEqualTo(entity.getQuestionAuthor());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus().name());
            assertThat(domain.replies()).isEmpty();
        }

        @Test
        @DisplayName("ANSWERED 상태 Entity를 Reply와 함께 Domain으로 변환합니다")
        void toDomain_WithAnsweredEntityAndReplies_ConvertsCorrectly() {
            // given
            QnaJpaEntity entity = QnaJpaEntityFixtures.answeredEntity(1L);
            QnaReplyJpaEntity replyEntity = QnaJpaEntityFixtures.sellerReplyEntity(1L, 1L);

            // when
            Qna domain = mapper.toDomain(entity, List.of(replyEntity));

            // then
            assertThat(domain.status()).isEqualTo(QnaStatus.ANSWERED);
            assertThat(domain.replies()).hasSize(1);
            assertThat(domain.replies().get(0).content()).isEqualTo(replyEntity.getContent());
            assertThat(domain.replies().get(0).authorName()).isEqualTo(replyEntity.getAuthorName());
        }

        @Test
        @DisplayName("여러 Reply가 있는 Entity를 Domain으로 변환합니다")
        void toDomain_WithMultipleReplies_ConvertsAllReplies() {
            // given
            QnaJpaEntity entity = QnaJpaEntityFixtures.answeredEntity(1L);
            QnaReplyJpaEntity replyEntity1 = QnaJpaEntityFixtures.sellerReplyEntity(1L, 1L);
            QnaReplyJpaEntity replyEntity2 =
                    QnaJpaEntityFixtures.buyerFollowUpEntity(2L, 1L, 1L);

            // when
            Qna domain = mapper.toDomain(entity, List.of(replyEntity1, replyEntity2));

            // then
            assertThat(domain.replies()).hasSize(2);
        }
    }

    // ========================================================================
    // 4. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            Qna original = QnaFixtures.pendingQna();

            // when
            QnaJpaEntity entity = mapper.toEntity(original);
            Qna converted = mapper.toDomain(entity, List.of());

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerId()).isEqualTo(original.sellerId());
            assertThat(converted.productGroupId()).isEqualTo(original.productGroupId());
            assertThat(converted.qnaType()).isEqualTo(original.qnaType());
            assertThat(converted.source().salesChannelId())
                    .isEqualTo(original.source().salesChannelId());
            assertThat(converted.source().externalQnaId())
                    .isEqualTo(original.source().externalQnaId());
            assertThat(converted.questionContent()).isEqualTo(original.questionContent());
            assertThat(converted.questionAuthor()).isEqualTo(original.questionAuthor());
            assertThat(converted.status()).isEqualTo(original.status());
        }
    }
}
