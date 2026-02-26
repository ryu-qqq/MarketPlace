package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.IntelligenceOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.IntelligenceOutboxJpaRepository;
import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * IntelligenceOutboxCommandAdapterTest - Intelligence Pipeline Outbox Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("IntelligenceOutboxCommandAdapter 단위 테스트")
class IntelligenceOutboxCommandAdapterTest {

    @Mock private IntelligenceOutboxJpaRepository repository;

    @Mock private IntelligenceOutboxJpaEntityMapper mapper;

    @InjectMocks private IntelligenceOutboxCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("IntelligenceOutbox를 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidOutbox_SavesAndReturnsId() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.newPendingOutbox();
            IntelligenceOutboxJpaEntity entityToSave =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity();
            IntelligenceOutboxJpaEntity savedEntity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            100L, 100L, "PI:100:1740556800000");

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("기존 PENDING 상태 Outbox를 저장합니다")
        void persist_WithExistingPendingOutbox_Saves() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.existingPendingOutbox();
            IntelligenceOutboxJpaEntity entityToSave =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity();
            IntelligenceOutboxJpaEntity savedEntity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            1L, 100L, "PI:100:1740556800000");

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.newPendingOutbox();
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            1L, 100L, "PI:100:1740556800000");

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("저장 후 반환된 Entity의 ID를 반환합니다")
        void persist_ReturnsIdFromSavedEntity() {
            // given
            Long expectedId = 77L;
            IntelligenceOutbox domain = ProductIntelligenceFixtures.newPendingOutbox();
            IntelligenceOutboxJpaEntity entityToSave =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity();
            IntelligenceOutboxJpaEntity savedEntity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            expectedId, 100L, "PI:100:1740556800000");

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(expectedId);
        }
    }
}
