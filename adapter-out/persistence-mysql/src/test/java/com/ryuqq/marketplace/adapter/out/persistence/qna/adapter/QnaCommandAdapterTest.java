package com.ryuqq.marketplace.adapter.out.persistence.qna.adapter;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.mapper.QnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * QnaCommandAdapterTest - Qna Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository와 ReplyJpaRepository 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("QnaCommandAdapter 단위 테스트")
class QnaCommandAdapterTest {

    @Mock private QnaJpaRepository qnaRepository;

    @Mock private QnaReplyJpaRepository replyRepository;

    @Mock private QnaJpaEntityMapper mapper;

    @InjectMocks private QnaCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Qna와 Reply를 함께 저장합니다")
        void persist_WithQnaAndReplies_SavesBoth() {
            // given
            Qna domain = QnaFixtures.answeredQna();
            QnaJpaEntity qnaEntity = QnaJpaEntityFixtures.answeredEntity(1L);
            QnaReplyJpaEntity replyEntity =
                    QnaJpaEntityFixtures.sellerReplyEntity(1L, 1L);
            List<QnaReplyJpaEntity> replyEntities = List.of(replyEntity);

            given(mapper.toEntity(domain)).willReturn(qnaEntity);
            given(qnaRepository.save(qnaEntity)).willReturn(qnaEntity);
            given(mapper.toReplyEntities(domain.replies(), qnaEntity.getId()))
                    .willReturn(replyEntities);
            given(replyRepository.saveAll(replyEntities)).willReturn(replyEntities);

            // when
            commandAdapter.persist(domain);

            // then
            then(qnaRepository).should().save(qnaEntity);
            then(replyRepository).should().saveAll(replyEntities);
        }

        @Test
        @DisplayName("Reply가 없는 Qna를 저장합니다")
        void persist_WithNoReplies_SavesQnaOnly() {
            // given
            Qna domain = QnaFixtures.pendingQna();
            QnaJpaEntity qnaEntity = QnaJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(qnaEntity);
            given(qnaRepository.save(qnaEntity)).willReturn(qnaEntity);
            given(mapper.toReplyEntities(domain.replies(), qnaEntity.getId()))
                    .willReturn(List.of());
            given(replyRepository.saveAll(List.of())).willReturn(List.of());

            // when
            commandAdapter.persist(domain);

            // then
            then(qnaRepository).should().save(qnaEntity);
            then(replyRepository).should().saveAll(List.of());
        }

        @Test
        @DisplayName("Qna 저장 후 저장된 Entity의 ID로 Reply를 저장합니다")
        void persist_UsesIdFromSavedQnaForReplies() {
            // given
            Qna domain = QnaFixtures.answeredQna();
            QnaJpaEntity qnaEntity = QnaJpaEntityFixtures.answeredEntity(1L);

            given(mapper.toEntity(domain)).willReturn(qnaEntity);
            given(qnaRepository.save(qnaEntity)).willReturn(qnaEntity);
            given(mapper.toReplyEntities(anyList(), anyLong())).willReturn(List.of());
            given(replyRepository.saveAll(anyList())).willReturn(List.of());

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toReplyEntities(domain.replies(), qnaEntity.getId());
        }

        @Test
        @DisplayName("Mapper가 toEntity를 정확히 한 번 호출합니다")
        void persist_CallsToEntityOnce() {
            // given
            Qna domain = QnaFixtures.pendingQna();
            QnaJpaEntity qnaEntity = QnaJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(qnaEntity);
            given(qnaRepository.save(qnaEntity)).willReturn(qnaEntity);
            given(mapper.toReplyEntities(anyList(), anyLong())).willReturn(List.of());
            given(replyRepository.saveAll(anyList())).willReturn(List.of());

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
        }
    }
}
