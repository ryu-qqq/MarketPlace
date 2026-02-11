package com.ryuqq.marketplace.application.saleschannel.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelResult;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelAssembler 단위 테스트")
class SalesChannelAssemblerTest {

    private SalesChannelAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new SalesChannelAssembler();
    }

    @Nested
    @DisplayName("toResult() - 단일 Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("판매채널을 Result로 변환한다")
        void toResult_ValidSalesChannel_ReturnsResult() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel(1L);

            // when
            SalesChannelResult result = sut.toResult(salesChannel);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(salesChannel.idValue());
            assertThat(result.channelName()).isEqualTo(salesChannel.channelName());
            assertThat(result.status()).isEqualTo(salesChannel.status().name());
            assertThat(result.createdAt()).isEqualTo(salesChannel.createdAt());
            assertThat(result.updatedAt()).isEqualTo(salesChannel.updatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태의 판매채널을 Result로 변환한다")
        void toResult_InactiveSalesChannel_ReturnsResult() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.inactiveSalesChannel(2L);

            // when
            SalesChannelResult result = sut.toResult(salesChannel);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResults() - 리스트 변환")
    class ToResultsTest {

        @Test
        @DisplayName("판매채널 리스트를 Result 리스트로 변환한다")
        void toResults_ValidList_ReturnsResultList() {
            // given
            List<SalesChannel> salesChannels =
                    List.of(
                            SalesChannelFixtures.activeSalesChannel(1L),
                            SalesChannelFixtures.activeSalesChannel(2L));

            // when
            List<SalesChannelResult> results = sut.toResults(salesChannels);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(1).id()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 리스트를 빈 Result 리스트로 변환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<SalesChannel> emptySalesChannels = List.of();

            // when
            List<SalesChannelResult> results = sut.toResults(emptySalesChannels);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 Result 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("판매채널 리스트를 PageResult로 변환한다")
        void toPageResult_ValidList_ReturnsPageResult() {
            // given
            List<SalesChannel> salesChannels =
                    List.of(
                            SalesChannelFixtures.activeSalesChannel(1L),
                            SalesChannelFixtures.activeSalesChannel(2L));
            int page = 0;
            int size = 20;
            long totalElements = 2L;

            // when
            SalesChannelPageResult result = sut.toPageResult(salesChannels, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 리스트로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<SalesChannel> emptyList = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            SalesChannelPageResult result = sut.toPageResult(emptyList, page, size, totalElements);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }
}
