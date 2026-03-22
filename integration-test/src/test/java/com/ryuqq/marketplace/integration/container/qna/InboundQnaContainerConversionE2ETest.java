package com.ryuqq.marketplace.integration.container.qna;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.InboundQnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.repository.InboundQnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.ConvertInboundQnaUseCase;
import com.ryuqq.marketplace.domain.inboundqna.exception.InboundQnaException;
import com.ryuqq.marketplace.integration.container.ContainerE2ETestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * InboundQna 변환 파이프라인 Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 InboundQna 변환 파이프라인을 검증합니다.
 * H2 기반 InboundQnaConversionE2ETest의 Testcontainers 전환 버전입니다.
 *
 * <p>테스트 대상:
 * <ul>
 *   <li>CV1~CV7: InboundQna → Qna 변환 파이프라인</li>
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("qna")
@Tag("inboundqna")
@DisplayName("InboundQna 변환 파이프라인 Container E2E 테스트")
class InboundQnaContainerConversionE2ETest extends ContainerE2ETestBase {

    @Autowired private InboundQnaJpaRepository inboundQnaRepository;
    @Autowired private QnaJpaRepository qnaRepository;
    @Autowired private QnaReplyJpaRepository qnaReplyRepository;
    @Autowired private QnaOutboxJpaRepository qnaOutboxRepository;
    @Autowired private ConvertInboundQnaUseCase convertInboundQnaUseCase;

    @BeforeEach
    void setUp() {
        qnaReplyRepository.deleteAll();
        qnaOutboxRepository.deleteAll();
        qnaRepository.deleteAll();
        inboundQnaRepository.deleteAll();
    }

    @Nested
    @DisplayName("CV1: RECEIVED InboundQna → 변환 실행 → 정상 완료")
    class ConvertReceivedInboundQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[CV1] RECEIVED 상태 InboundQna 변환 실행 → 예외 없이 완료")
        void convertInboundQna_ReceivedEntity_CompletesWithoutException() {
            var inboundQna = inboundQnaRepository.save(InboundQnaJpaEntityFixtures.receivedEntity());

            convertInboundQnaUseCase.execute(inboundQna.getId());
        }
    }

    @Nested
    @DisplayName("CV2: 변환 성공 후 InboundQna 상태 CONVERTED, internalQnaId 저장 확인")
    class InboundQnaStatusAfterConversionTest {

        @Test
        @Tag("P0")
        @DisplayName("[CV2] 변환 성공 후 InboundQna 상태 CONVERTED 전이, internalQnaId != null 확인")
        void convertInboundQna_AfterSuccess_StatusConvertedAndInternalQnaIdSet() {
            var inboundQna = inboundQnaRepository.save(InboundQnaJpaEntityFixtures.receivedEntity());

            convertInboundQnaUseCase.execute(inboundQna.getId());

            var updated = inboundQnaRepository.findById(inboundQna.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(InboundQnaJpaEntity.Status.CONVERTED);
            assertThat(updated.getInternalQnaId()).isNotNull();
            assertThat(updated.getInternalQnaId()).isGreaterThan(0L);
        }
    }

    @Nested
    @DisplayName("CV3: 변환 성공 후 Qna 레코드 생성 확인")
    class QnaCreatedAfterConversionTest {

        @Test
        @Tag("P0")
        @DisplayName("[CV3] 변환 성공 후 qnas 테이블에 1건 생성, internalQnaId와 qnaId 일치 확인")
        void convertInboundQna_AfterSuccess_QnaRecordCreated() {
            var inboundQna = inboundQnaRepository.save(InboundQnaJpaEntityFixtures.receivedEntity());

            long qnaCountBefore = qnaRepository.count();

            convertInboundQnaUseCase.execute(inboundQna.getId());

            assertThat(qnaRepository.count()).isEqualTo(qnaCountBefore + 1);

            var updatedInbound = inboundQnaRepository.findById(inboundQna.getId()).orElseThrow();
            assertThat(qnaRepository.findById(updatedInbound.getInternalQnaId())).isPresent();
        }
    }

    @Nested
    @DisplayName("CV4: 존재하지 않는 InboundQna ID 변환 시도 → 예외 발생")
    class ConvertNonExistentInboundQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[CV4] 존재하지 않는 InboundQna ID 변환 시도 → InboundQnaException 발생")
        void convertInboundQna_NonExistentId_ThrowsException() {
            assertThatThrownBy(() -> convertInboundQnaUseCase.execute(999999L))
                    .isInstanceOf(InboundQnaException.class);
        }
    }

    @Nested
    @DisplayName("CV5: 서로 다른 externalQnaId InboundQna 2건 순차 변환 → 독립 Qna 2건 생성")
    class ConvertTwoIndependentInboundQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[CV5] 서로 다른 externalQnaId InboundQna 2건 변환 → Qna 각각 독립 생성, 총 2건")
        void convertTwoInboundQnas_DifferentExternalIds_TwoIndependentQnasCreated() {
            var inbound1 = inboundQnaRepository.save(
                    InboundQnaJpaEntityFixtures.receivedEntity(1L, "EXT-CV5-001"));
            var inbound2 = inboundQnaRepository.save(
                    InboundQnaJpaEntityFixtures.receivedEntity(1L, "EXT-CV5-002"));

            convertInboundQnaUseCase.execute(inbound1.getId());
            convertInboundQnaUseCase.execute(inbound2.getId());

            assertThat(qnaRepository.count()).isEqualTo(2);

            var updated1 = inboundQnaRepository.findById(inbound1.getId()).orElseThrow();
            var updated2 = inboundQnaRepository.findById(inbound2.getId()).orElseThrow();

            assertThat(updated1.getStatus()).isEqualTo(InboundQnaJpaEntity.Status.CONVERTED);
            assertThat(updated2.getStatus()).isEqualTo(InboundQnaJpaEntity.Status.CONVERTED);
            assertThat(updated1.getInternalQnaId()).isNotEqualTo(updated2.getInternalQnaId());
        }
    }

    @Nested
    @DisplayName("CV6: RECEIVED 3건 순차 변환 → 각각 CONVERTED + Qna 3건 생성")
    class ConvertMultipleInboundQnaSequentiallyTest {

        @Test
        @Tag("P1")
        @DisplayName("[CV6] RECEIVED 3건 순차 변환 → 3건 모두 CONVERTED, Qna 3건 생성")
        void convertThreeInboundQnas_Sequentially_AllConvertedAndQnasCreated() {
            var inbound1 = inboundQnaRepository.save(InboundQnaJpaEntityFixtures.receivedEntity());
            var inbound2 = inboundQnaRepository.save(InboundQnaJpaEntityFixtures.receivedEntity());
            var inbound3 = inboundQnaRepository.save(InboundQnaJpaEntityFixtures.receivedEntity());

            convertInboundQnaUseCase.execute(inbound1.getId());
            convertInboundQnaUseCase.execute(inbound2.getId());
            convertInboundQnaUseCase.execute(inbound3.getId());

            long convertedCount = inboundQnaRepository.findAll().stream()
                    .filter(e -> e.getStatus() == InboundQnaJpaEntity.Status.CONVERTED)
                    .count();
            assertThat(convertedCount).isEqualTo(3);
            assertThat(qnaRepository.count()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("CV7: qnaType 필드가 변환 후 Qna에 그대로 전파되는지 확인")
    class QnaTypePreservedAfterConversionTest {

        @Test
        @Tag("P1")
        @DisplayName("[CV7] InboundQna qnaType=PRODUCT 변환 → 생성된 Qna qnaType=PRODUCT 확인")
        void convertInboundQna_QnaTypePreserved() {
            var inboundQna = inboundQnaRepository.save(InboundQnaJpaEntityFixtures.receivedEntity());

            convertInboundQnaUseCase.execute(inboundQna.getId());

            var updatedInbound = inboundQnaRepository.findById(inboundQna.getId()).orElseThrow();
            var createdQna = qnaRepository.findById(updatedInbound.getInternalQnaId()).orElseThrow();

            assertThat(createdQna.getQnaType())
                    .isEqualTo(InboundQnaJpaEntityFixtures.DEFAULT_QNA_TYPE);
        }
    }
}
