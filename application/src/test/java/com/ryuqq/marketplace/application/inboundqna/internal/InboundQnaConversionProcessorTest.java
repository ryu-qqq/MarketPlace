package com.ryuqq.marketplace.application.inboundqna.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/*
 * InboundQnaConversionProcessor.convert() 는 내부적으로 Qna.forNew()로 Qna를 생성한다.
 * qnaCommandManager.persist()는 저장된 Qna의 ID(long)를 반환한다.
 * Mock 환경에서 persist()는 기본값 0L을 반환하므로 정상 흐름이 동작한다.
 *
 * 단위 테스트에서는 다음을 검증한다:
 * - qnaCommandManager.persist() 호출 여부
 * - productIdResolver 상호작용 여부
 * - 예외 전파 차단 동작
 * - FAILED 상태 전환 동작 (qnaCommandManager.persist 자체가 실패할 때)
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InboundQnaConversionProcessor 단위 테스트")
class InboundQnaConversionProcessorTest {

    @InjectMocks private InboundQnaConversionProcessor sut;

    @Mock private InboundProductIdResolver productIdResolver;
    @Mock private QnaCommandManager qnaCommandManager;
    @Mock private InboundQnaCommandManager inboundQnaCommandManager;

    @Nested
    @DisplayName("convert() - InboundQna → Qna 변환")
    class ConvertTest {

        @Test
        @DisplayName("externalProductId 없이 변환 시 qnaCommandManager.persist()를 호출한다")
        void convert_WithoutExternalProductId_CallsQnaCommandManagerPersist() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);

            // when
            sut.convert(inboundQna, null, null);

            // then: qnaCommandManager.persist가 호출된다
            then(qnaCommandManager).should().persist(any(Qna.class));
        }

        @Test
        @DisplayName("externalProductId가 있으면 productIdResolver를 통해 productGroupId를 조회한다")
        void convert_WithExternalProductId_ResolvesProductGroupId() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);
            String externalProductId = "PROD-001";
            given(productIdResolver.resolve(inboundQna.salesChannelId(), externalProductId))
                    .willReturn(ProductGroupId.of(200L));

            // when
            sut.convert(inboundQna, externalProductId, null);

            // then
            then(productIdResolver)
                    .should()
                    .resolve(inboundQna.salesChannelId(), externalProductId);
            then(qnaCommandManager).should().persist(any(Qna.class));
        }

        @Test
        @DisplayName("externalProductId가 빈 문자열이면 productIdResolver를 호출하지 않는다")
        void convert_WithBlankExternalProductId_SkipsProductIdResolution() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);

            // when
            sut.convert(inboundQna, "  ", null);

            // then
            then(productIdResolver).shouldHaveNoInteractions();
            then(qnaCommandManager).should().persist(any(Qna.class));
        }

        @Test
        @DisplayName(
                "productIdResolver 조회 실패 시 productGroupId=0으로 qnaCommandManager.persist()를 호출한다")
        void convert_ProductIdResolutionFails_StillCallsQnaCommandManagerPersist() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);
            String externalProductId = "UNKNOWN-PROD";
            given(productIdResolver.resolve(inboundQna.salesChannelId(), externalProductId))
                    .willThrow(new RuntimeException("상품 조회 실패"));

            // when
            sut.convert(inboundQna, externalProductId, null);

            // then: 역조회 실패 시 productGroupId=0으로 Qna 생성 후 persist 호출
            then(qnaCommandManager).should().persist(any(Qna.class));
        }

        @Test
        @DisplayName("Qna persist 실패 시 InboundQna를 FAILED 상태로 persist한다")
        void convert_QnaPersistFails_MarksFailedAndPersistsInboundQna() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);
            willThrow(new RuntimeException("DB 저장 실패"))
                    .given(qnaCommandManager)
                    .persist(any(Qna.class));

            // when
            sut.convert(inboundQna, null, null);

            // then
            assertThat(inboundQna.status()).isEqualTo(InboundQnaStatus.FAILED);
            then(inboundQnaCommandManager).should().persist(inboundQna);
        }

        @Test
        @DisplayName("변환 중 예외가 발생해도 외부로 예외를 전파하지 않는다")
        void convert_ExceptionOccurs_DoesNotPropagateException() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);
            willThrow(new RuntimeException("예상치 못한 오류"))
                    .given(qnaCommandManager)
                    .persist(any(Qna.class));

            // when & then (예외 전파 없음)
            sut.convert(inboundQna, null, null);
        }

        @Test
        @DisplayName("변환 실패 시 failureReason이 설정된다")
        void convert_QnaPersistFails_SetsFailureReason() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);
            String errorMessage = "DB 저장 실패";
            willThrow(new RuntimeException(errorMessage))
                    .given(qnaCommandManager)
                    .persist(any(Qna.class));

            // when
            sut.convert(inboundQna, null, null);

            // then
            assertThat(inboundQna.failureReason()).isEqualTo(errorMessage);
        }
    }
}
