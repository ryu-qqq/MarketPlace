package com.ryuqq.marketplace.application.inboundqna.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundqna.port.out.query.InboundQnaQueryPort;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.exception.InboundQnaException;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InboundQnaReadManager 단위 테스트")
class InboundQnaReadManagerTest {

    @InjectMocks private InboundQnaReadManager sut;

    @Mock private InboundQnaQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 InboundQna 단건 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 InboundQna를 조회한다")
        void getById_ExistingId_ReturnsInboundQna() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);
            given(queryPort.findById(1L)).willReturn(Optional.of(inboundQna));

            // when
            InboundQna result = sut.getById(1L);

            // then
            assertThat(result).isEqualTo(inboundQna);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 InboundQnaException이 발생한다")
        void getById_NonExistentId_ThrowsInboundQnaException() {
            // given
            long nonExistentId = 999L;
            given(queryPort.findById(nonExistentId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(nonExistentId))
                    .isInstanceOf(InboundQnaException.class);
        }
    }

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalQnaId() - 채널ID + 외부QnaID 존재 여부 확인")
    class ExistsTest {

        @Test
        @DisplayName("채널ID와 외부QnaID로 존재하는 경우 true를 반환한다")
        void existsBySalesChannelIdAndExternalQnaId_Exists_ReturnsTrue() {
            // given
            long salesChannelId = 1L;
            String externalQnaId = "EXT-QNA-001";
            given(queryPort.existsBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId))
                    .willReturn(true);

            // when
            boolean result = sut.existsBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("채널ID와 외부QnaID로 존재하지 않는 경우 false를 반환한다")
        void existsBySalesChannelIdAndExternalQnaId_NotExists_ReturnsFalse() {
            // given
            long salesChannelId = 1L;
            String externalQnaId = "EXT-QNA-NOT-FOUND";
            given(queryPort.existsBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId))
                    .willReturn(false);

            // when
            boolean result = sut.existsBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByStatus() - 상태로 InboundQna 목록 조회")
    class FindByStatusTest {

        @Test
        @DisplayName("RECEIVED 상태 InboundQna 목록을 반환한다")
        void findByStatus_ReceivedStatus_ReturnsList() {
            // given
            int limit = 10;
            InboundQna qna1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna qna2 = InboundQnaFixtures.receivedInboundQna(2L);
            given(queryPort.findByStatus(InboundQnaStatus.RECEIVED, limit))
                    .willReturn(List.of(qna1, qna2));

            // when
            List<InboundQna> result = sut.findByStatus(InboundQnaStatus.RECEIVED, limit);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findByStatus(InboundQnaStatus.RECEIVED, limit);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 목록을 반환한다")
        void findByStatus_NoResults_ReturnsEmptyList() {
            // given
            int limit = 10;
            given(queryPort.findByStatus(InboundQnaStatus.RECEIVED, limit))
                    .willReturn(List.of());

            // when
            List<InboundQna> result = sut.findByStatus(InboundQnaStatus.RECEIVED, limit);

            // then
            assertThat(result).isEmpty();
        }
    }
}
