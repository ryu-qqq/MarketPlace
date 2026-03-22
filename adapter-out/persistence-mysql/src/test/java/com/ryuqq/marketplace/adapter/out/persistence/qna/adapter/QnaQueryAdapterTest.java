package com.ryuqq.marketplace.adapter.out.persistence.qna.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.mapper.QnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * QnaQueryAdapterTest - Qna Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository + JpaRepository 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("QnaQueryAdapter 단위 테스트")
class QnaQueryAdapterTest {

    @Mock private QnaQueryDslRepository queryDslRepository;

    @Mock private QnaReplyJpaRepository replyRepository;

    @Mock private QnaJpaEntityMapper mapper;

    @InjectMocks private QnaQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Reply를 포함한 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomainWithReplies() {
            // given
            long id = QnaJpaEntityFixtures.DEFAULT_ID;
            QnaJpaEntity entity = QnaJpaEntityFixtures.answeredEntity(id);
            QnaReplyJpaEntity replyEntity = QnaJpaEntityFixtures.sellerReplyEntity(1L, id);
            Qna domain = QnaFixtures.answeredQna(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(replyRepository.findByQnaId(entity.getId())).willReturn(List.of(replyEntity));
            given(mapper.toDomain(entity, List.of(replyEntity))).willReturn(domain);

            // when
            Optional<Qna> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            long id = 9999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Qna> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Entity가 없을 때 ReplyRepository는 호출되지 않습니다")
        void findById_WhenEntityNotExists_DoesNotCallReplyRepository() {
            // given
            long id = 9999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            queryAdapter.findById(id);

            // then
            then(replyRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Entity가 존재할 때 ReplyRepository와 Mapper가 호출됩니다")
        void findById_WhenEntityExists_CallsReplyRepositoryAndMapper() {
            // given
            long id = QnaJpaEntityFixtures.DEFAULT_ID;
            QnaJpaEntity entity = QnaJpaEntityFixtures.pendingEntity(id);
            Qna domain = QnaFixtures.pendingQna(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(replyRepository.findByQnaId(entity.getId())).willReturn(List.of());
            given(mapper.toDomain(entity, List.of())).willReturn(domain);

            // when
            queryAdapter.findById(id);

            // then
            then(replyRepository).should().findByQnaId(id);
            then(mapper).should().toDomain(entity, List.of());
        }
    }

    // ========================================================================
    // 2. findBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerId 메서드 테스트")
    class FindBySellerIdTest {

        @Test
        @DisplayName("sellerId와 status로 Qna 목록을 조회합니다")
        void findBySellerId_WithSellerIdAndStatus_ReturnsQnaList() {
            // given
            long sellerId = QnaFixtures.DEFAULT_SELLER_ID;
            QnaStatus status = QnaStatus.PENDING;
            int offset = 0;
            int limit = 10;
            QnaJpaEntity entity = QnaJpaEntityFixtures.pendingEntity(1L);
            Qna domain = QnaFixtures.pendingQna(1L);

            given(queryDslRepository.findBySellerIdAndStatus(
                    sellerId, QnaJpaEntity.Status.PENDING, offset, limit))
                    .willReturn(List.of(entity));
            given(replyRepository.findByQnaIdIn(List.of(1L))).willReturn(List.of());
            given(mapper.toDomain(entity, List.of())).willReturn(domain);

            // when
            List<Qna> result = queryAdapter.findBySellerId(sellerId, status, offset, limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("status가 null인 경우 모든 상태를 조회합니다")
        void findBySellerId_WithNullStatus_QueriesAllStatuses() {
            // given
            long sellerId = QnaFixtures.DEFAULT_SELLER_ID;
            int offset = 0;
            int limit = 10;

            given(queryDslRepository.findBySellerIdAndStatus(sellerId, null, offset, limit))
                    .willReturn(List.of());

            // when
            List<Qna> result = queryAdapter.findBySellerId(sellerId, null, offset, limit);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should()
                    .findBySellerIdAndStatus(sellerId, null, offset, limit);
        }

        @Test
        @DisplayName("조회 결과가 없을 때 빈 목록을 반환합니다")
        void findBySellerId_WhenNoResults_ReturnsEmptyList() {
            // given
            long sellerId = 9999L;
            int offset = 0;
            int limit = 10;

            given(queryDslRepository.findBySellerIdAndStatus(sellerId, null, offset, limit))
                    .willReturn(List.of());

            // when
            List<Qna> result = queryAdapter.findBySellerId(sellerId, null, offset, limit);

            // then
            assertThat(result).isEmpty();
            then(replyRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 Qna 조회 시 Reply를 일괄 조회합니다")
        void findBySellerId_WithMultipleQnas_FetchesRepliesInBatch() {
            // given
            long sellerId = QnaFixtures.DEFAULT_SELLER_ID;
            int offset = 0;
            int limit = 10;
            QnaJpaEntity entity1 = QnaJpaEntityFixtures.pendingEntity(1L);
            QnaJpaEntity entity2 = QnaJpaEntityFixtures.answeredEntity(2L);
            QnaReplyJpaEntity replyEntity = QnaJpaEntityFixtures.sellerReplyEntity(1L, 2L);
            Qna domain1 = QnaFixtures.pendingQna(1L);
            Qna domain2 = QnaFixtures.answeredQna(2L);

            given(queryDslRepository.findBySellerIdAndStatus(sellerId, null, offset, limit))
                    .willReturn(List.of(entity1, entity2));
            given(replyRepository.findByQnaIdIn(List.of(1L, 2L)))
                    .willReturn(List.of(replyEntity));
            given(mapper.toDomain(entity1, List.of())).willReturn(domain1);
            given(mapper.toDomain(entity2, List.of(replyEntity))).willReturn(domain2);

            // when
            List<Qna> result = queryAdapter.findBySellerId(sellerId, null, offset, limit);

            // then
            assertThat(result).hasSize(2);
            then(replyRepository).should().findByQnaIdIn(List.of(1L, 2L));
        }
    }

    // ========================================================================
    // 3. countBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("countBySellerId 메서드 테스트")
    class CountBySellerIdTest {

        @Test
        @DisplayName("sellerId와 status로 개수를 조회합니다")
        void countBySellerId_WithSellerIdAndStatus_ReturnsCount() {
            // given
            long sellerId = QnaFixtures.DEFAULT_SELLER_ID;
            QnaStatus status = QnaStatus.PENDING;

            given(queryDslRepository.countBySellerIdAndStatus(
                    sellerId, QnaJpaEntity.Status.PENDING))
                    .willReturn(5L);

            // when
            long count = queryAdapter.countBySellerId(sellerId, status);

            // then
            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("status가 null인 경우 전체 개수를 조회합니다")
        void countBySellerId_WithNullStatus_ReturnsTotal() {
            // given
            long sellerId = QnaFixtures.DEFAULT_SELLER_ID;

            given(queryDslRepository.countBySellerIdAndStatus(sellerId, null)).willReturn(10L);

            // when
            long count = queryAdapter.countBySellerId(sellerId, null);

            // then
            assertThat(count).isEqualTo(10L);
            then(queryDslRepository).should().countBySellerIdAndStatus(sellerId, null);
        }

        @Test
        @DisplayName("데이터가 없을 때 0을 반환합니다")
        void countBySellerId_WhenNoData_ReturnsZero() {
            // given
            long sellerId = 9999L;
            given(queryDslRepository.countBySellerIdAndStatus(sellerId, null)).willReturn(0L);

            // when
            long count = queryAdapter.countBySellerId(sellerId, null);

            // then
            assertThat(count).isEqualTo(0L);
        }
    }
}
