package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.command.SyncProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.RetrySyncApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncProductsApiResponse;
import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsProductCommandApiMapper 단위 테스트")
class OmsProductCommandApiMapperTest {

    private OmsProductCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OmsProductCommandApiMapper();
    }

    @Nested
    @DisplayName("toRetryResponse() - 연동 재처리 응답 변환")
    class ToRetryResponseTest {

        @Test
        @DisplayName("outboxId를 전달하면 ACCEPTED 상태의 RetrySyncApiResponse를 반환한다")
        void toRetryResponse_GivenOutboxId_ReturnsAcceptedResponse() {
            // given
            Long outboxId = 202L;

            // when
            RetrySyncApiResponse response = mapper.toRetryResponse(outboxId);

            // then
            assertThat(response.outboxId()).isEqualTo(202L);
            assertThat(response.status()).isEqualTo("ACCEPTED");
        }

        @Test
        @DisplayName("outboxId 1이면 id가 그대로 반환된다")
        void toRetryResponse_GivenOutboxIdOne_ReturnsIdOne() {
            // given
            Long outboxId = 1L;

            // when
            RetrySyncApiResponse response = mapper.toRetryResponse(outboxId);

            // then
            assertThat(response.outboxId()).isEqualTo(1L);
            assertThat(response.status()).isEqualTo("ACCEPTED");
        }

        @Test
        @DisplayName("큰 outboxId도 정상적으로 처리된다")
        void toRetryResponse_GivenLargeOutboxId_ReturnsCorrectResponse() {
            // given
            Long outboxId = Long.MAX_VALUE;

            // when
            RetrySyncApiResponse response = mapper.toRetryResponse(outboxId);

            // then
            assertThat(response.outboxId()).isEqualTo(Long.MAX_VALUE);
            assertThat(response.status()).isEqualTo("ACCEPTED");
        }
    }

    @Nested
    @DisplayName("toCommand() - API 요청 → 커맨드 변환")
    class ToCommandTest {

        @Test
        @DisplayName("SyncProductsApiRequest를 ManualSyncProductsCommand로 변환한다")
        void toCommand_MapsFieldsCorrectly() {
            // given
            SyncProductsApiRequest request =
                    new SyncProductsApiRequest(List.of(1L, 2L, 3L), List.of(10L, 20L));

            // when
            ManualSyncProductsCommand command = mapper.toCommand(request);

            // then
            assertThat(command.productGroupIds()).containsExactly(1L, 2L, 3L);
            assertThat(command.shopIds()).containsExactly(10L, 20L);
        }

        @Test
        @DisplayName("단일 항목 리스트도 정상적으로 변환한다")
        void toCommand_SingleItem_MapsCorrectly() {
            // given
            SyncProductsApiRequest request =
                    new SyncProductsApiRequest(List.of(100L), List.of(50L));

            // when
            ManualSyncProductsCommand command = mapper.toCommand(request);

            // then
            assertThat(command.productGroupIds()).containsExactly(100L);
            assertThat(command.shopIds()).containsExactly(50L);
        }
    }

    @Nested
    @DisplayName("toSyncResponse() - 결과 → API 응답 변환")
    class ToSyncResponseTest {

        @Test
        @DisplayName("ManualSyncResult를 SyncProductsApiResponse로 변환한다")
        void toSyncResponse_MapsAllFieldsCorrectly() {
            // given
            ManualSyncResult result = ManualSyncResult.of(3, 2, 1);

            // when
            SyncProductsApiResponse response = mapper.toSyncResponse(result);

            // then
            assertThat(response.createCount()).isEqualTo(3);
            assertThat(response.updateCount()).isEqualTo(2);
            assertThat(response.skippedCount()).isEqualTo(1);
            assertThat(response.status()).isEqualTo("ACCEPTED");
        }

        @Test
        @DisplayName("모든 카운트가 0인 결과도 정상적으로 변환한다")
        void toSyncResponse_ZeroCounts_MapsCorrectly() {
            // given
            ManualSyncResult result = ManualSyncResult.of(0, 0, 0);

            // when
            SyncProductsApiResponse response = mapper.toSyncResponse(result);

            // then
            assertThat(response.createCount()).isZero();
            assertThat(response.updateCount()).isZero();
            assertThat(response.skippedCount()).isZero();
            assertThat(response.status()).isEqualTo("ACCEPTED");
        }
    }
}
