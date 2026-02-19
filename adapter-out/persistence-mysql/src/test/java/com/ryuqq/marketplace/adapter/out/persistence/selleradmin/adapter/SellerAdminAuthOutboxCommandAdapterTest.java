package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminAuthOutboxJpaRepository;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SellerAdminAuthOutboxCommandAdapterTest - 셀러 관리자 인증 Outbox 명령 어댑터 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerAdminAuthOutboxCommandAdapter 단위 테스트")
class SellerAdminAuthOutboxCommandAdapterTest {

    @InjectMocks private SellerAdminAuthOutboxCommandAdapter sut;

    @Mock private SellerAdminAuthOutboxJpaRepository repository;
    @Mock private SellerAdminAuthOutboxJpaEntityMapper mapper;

    @Nested
    @DisplayName("persist() - Outbox 저장")
    class PersistTest {

        @Test
        @DisplayName("새 SellerAdminAuthOutbox를 저장하고 ID를 반환한다")
        void persist_NewOutbox_ReturnsId() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.newSellerAdminAuthOutbox();
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.newPendingEntity();
            SellerAdminAuthOutboxJpaEntity savedEntity =
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("기존 SellerAdminAuthOutbox를 업데이트하고 ID를 반환한다")
        void persist_ExistingOutbox_ReturnsId() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.pendingSellerAdminAuthOutboxWithId();
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("PROCESSING 상태 Outbox를 저장한다")
        void persist_ProcessingOutbox() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.processingEntity();
            Long expectedId = SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("COMPLETED 상태 Outbox를 저장한다")
        void persist_CompletedOutbox() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.completedSellerAdminAuthOutbox();
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.completedEntity();
            Long expectedId = SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("FAILED 상태 Outbox를 저장한다")
        void persist_FailedOutbox() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.failedSellerAdminAuthOutbox();
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.failedEntity();
            Long expectedId = SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }
    }
}
