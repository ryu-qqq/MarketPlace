package com.ryuqq.marketplace.application.saleschannel.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannel.port.out.query.SalesChannelQueryPort;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNotFoundException;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
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
@DisplayName("SalesChannelReadManager 단위 테스트")
class SalesChannelReadManagerTest {

    @InjectMocks private SalesChannelReadManager sut;

    @Mock private SalesChannelQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 판매채널을 ID로 조회한다")
        void getById_ExistsId_ReturnsSalesChannel() {
            // given
            SalesChannelId id = SalesChannelId.of(1L);
            SalesChannel expected = SalesChannelFixtures.activeSalesChannel(1L);

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            SalesChannel result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void getById_NotExistsId_ThrowsException() {
            // given
            SalesChannelId id = SalesChannelId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(SalesChannelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 판매채널 리스트를 조회한다")
        void findByCriteria_ValidCriteria_ReturnsList() {
            // given
            SalesChannelSearchCriteria criteria = null; // mock 대상
            List<SalesChannel> expected =
                    List.of(
                            SalesChannelFixtures.activeSalesChannel(1L),
                            SalesChannelFixtures.activeSalesChannel(2L));

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<SalesChannel> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 판매채널이 없으면 빈 리스트를 반환한다")
        void findByCriteria_NoMatches_ReturnsEmptyList() {
            // given
            SalesChannelSearchCriteria criteria = null;
            List<SalesChannel> emptyList = List.of();

            given(queryPort.findByCriteria(criteria)).willReturn(emptyList);

            // when
            List<SalesChannel> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 판매채널 개수를 조회한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            SalesChannelSearchCriteria criteria = null;
            long expectedCount = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(queryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 판매채널이 없으면 0을 반환한다")
        void countByCriteria_NoMatches_ReturnsZero() {
            // given
            SalesChannelSearchCriteria criteria = null;

            given(queryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("existsByChannelName() - 채널명 중복 체크")
    class ExistsByChannelNameTest {

        @Test
        @DisplayName("이미 존재하는 채널명이면 true를 반환한다")
        void existsByChannelName_Exists_ReturnsTrue() {
            // given
            String channelName = "테스트 채널";

            given(queryPort.existsByChannelName(channelName)).willReturn(true);

            // when
            boolean result = sut.existsByChannelName(channelName);

            // then
            assertThat(result).isTrue();
            then(queryPort).should().existsByChannelName(channelName);
        }

        @Test
        @DisplayName("존재하지 않는 채널명이면 false를 반환한다")
        void existsByChannelName_NotExists_ReturnsFalse() {
            // given
            String channelName = "새로운 채널";

            given(queryPort.existsByChannelName(channelName)).willReturn(false);

            // when
            boolean result = sut.existsByChannelName(channelName);

            // then
            assertThat(result).isFalse();
        }
    }
}
