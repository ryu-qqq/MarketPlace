package com.ryuqq.marketplace.application.saleschannel.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelReadManager;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNameDuplicateException;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNotFoundException;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
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
@DisplayName("SalesChannelValidator 단위 테스트")
class SalesChannelValidatorTest {

    @InjectMocks private SalesChannelValidator sut;

    @Mock private SalesChannelReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재 검증")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 판매채널을 반환한다")
        void findExistingOrThrow_Exists_ReturnsSalesChannel() {
            // given
            SalesChannelId id = SalesChannelId.of(1L);
            SalesChannel expected = SalesChannelFixtures.activeSalesChannel(1L);

            given(readManager.getById(id)).willReturn(expected);

            // when
            SalesChannel result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().getById(id);
        }

        @Test
        @DisplayName("존재하지 않으면 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            SalesChannelId id = SalesChannelId.of(999L);

            given(readManager.getById(id)).willThrow(new SalesChannelNotFoundException(id.value()));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(SalesChannelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateChannelNameNotDuplicate() - 채널명 중복 검증")
    class ValidateChannelNameNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 채널명이면 예외가 발생하지 않는다")
        void validateChannelNameNotDuplicate_NotDuplicate_DoesNotThrow() {
            // given
            String channelName = "새로운 채널";

            given(readManager.existsByChannelName(channelName)).willReturn(false);

            // when & then
            sut.validateChannelNameNotDuplicate(channelName);

            then(readManager).should().existsByChannelName(channelName);
        }

        @Test
        @DisplayName("이미 존재하는 채널명이면 예외가 발생한다")
        void validateChannelNameNotDuplicate_Duplicate_ThrowsException() {
            // given
            String channelName = "테스트 채널";

            given(readManager.existsByChannelName(channelName)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateChannelNameNotDuplicate(channelName))
                    .isInstanceOf(SalesChannelNameDuplicateException.class);
        }
    }
}
