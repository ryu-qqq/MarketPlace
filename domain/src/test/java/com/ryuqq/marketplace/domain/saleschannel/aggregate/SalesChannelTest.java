package com.ryuqq.marketplace.domain.saleschannel.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannel Aggregate 단위 테스트")
class SalesChannelTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {
        @Test
        @DisplayName("필수 필드로 새 SalesChannel을 생성한다")
        void createNewSalesChannelWithRequiredFields() {
            // given
            String channelName = "새 채널";
            Instant now = CommonVoFixtures.now();

            // when
            SalesChannel salesChannel = SalesChannel.forNew(channelName, now);

            // then
            assertThat(salesChannel).isNotNull();
            assertThat(salesChannel.channelName()).isEqualTo(channelName);
            assertThat(salesChannel.status()).isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(salesChannel.isActive()).isTrue();
            assertThat(salesChannel.createdAt()).isEqualTo(now);
            assertThat(salesChannel.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("새로 생성된 SalesChannel의 기본 상태는 ACTIVE다")
        void newSalesChannelDefaultStatusIsActive() {
            // given & when
            SalesChannel salesChannel = SalesChannelFixtures.newSalesChannel();

            // then
            assertThat(salesChannel.status()).isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(salesChannel.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {
        @Test
        @DisplayName("영속성에서 활성 상태의 SalesChannel을 복원한다")
        void reconstituteActiveSalesChannel() {
            // given
            SalesChannelId id = SalesChannelId.of(1L);
            String channelName = "복원 채널";
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            SalesChannel salesChannel =
                    SalesChannel.reconstitute(
                            id, channelName, SalesChannelStatus.ACTIVE, createdAt, updatedAt);

            // then
            assertThat(salesChannel.id()).isEqualTo(id);
            assertThat(salesChannel.channelName()).isEqualTo(channelName);
            assertThat(salesChannel.status()).isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(salesChannel.isActive()).isTrue();
            assertThat(salesChannel.createdAt()).isEqualTo(createdAt);
            assertThat(salesChannel.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 SalesChannel을 복원한다")
        void reconstituteInactiveSalesChannel() {
            // given
            SalesChannelId id = SalesChannelId.of(2L);
            String channelName = "비활성 채널";

            // when
            SalesChannel salesChannel =
                    SalesChannel.reconstitute(
                            id,
                            channelName,
                            SalesChannelStatus.INACTIVE,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(salesChannel.status()).isEqualTo(SalesChannelStatus.INACTIVE);
            assertThat(salesChannel.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("상태 변경 메서드 테스트")
    class StateChangeTest {
        @Test
        @DisplayName("활성 상태의 SalesChannel을 비활성화한다")
        void deactivateActiveSalesChannel() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel();
            Instant now = CommonVoFixtures.now();

            // when
            salesChannel.deactivate(now);

            // then
            assertThat(salesChannel.status()).isEqualTo(SalesChannelStatus.INACTIVE);
            assertThat(salesChannel.isActive()).isFalse();
            assertThat(salesChannel.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("비활성 상태의 SalesChannel을 활성화한다")
        void activateInactiveSalesChannel() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.inactiveSalesChannel();
            Instant now = CommonVoFixtures.now();

            // when
            salesChannel.activate(now);

            // then
            assertThat(salesChannel.status()).isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(salesChannel.isActive()).isTrue();
            assertThat(salesChannel.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성화된 SalesChannel을 활성화해도 상태는 유지된다")
        void activateAlreadyActiveSalesChannel() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel();
            Instant now = CommonVoFixtures.now();

            // when
            salesChannel.activate(now);

            // then
            assertThat(salesChannel.isActive()).isTrue();
            assertThat(salesChannel.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("update 메서드 테스트")
    class UpdateTest {
        @Test
        @DisplayName("SalesChannel 정보를 수정한다")
        void updateSalesChannelInfo() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel();
            SalesChannelUpdateData updateData =
                    SalesChannelFixtures.salesChannelUpdateData(
                            "수정된 채널", SalesChannelStatus.INACTIVE);
            Instant now = CommonVoFixtures.now();

            // when
            salesChannel.update(updateData, now);

            // then
            assertThat(salesChannel.channelName()).isEqualTo("수정된 채널");
            assertThat(salesChannel.status()).isEqualTo(SalesChannelStatus.INACTIVE);
            assertThat(salesChannel.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("채널명만 수정하고 상태는 유지할 수 있다")
        void updateOnlyChannelName() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel();
            SalesChannelUpdateData updateData =
                    SalesChannelFixtures.salesChannelUpdateData("새 채널명", SalesChannelStatus.ACTIVE);
            Instant now = CommonVoFixtures.now();

            // when
            salesChannel.update(updateData, now);

            // then
            assertThat(salesChannel.channelName()).isEqualTo("새 채널명");
            assertThat(salesChannel.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {
        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel(100L);

            // when
            Long idValue = salesChannel.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("channelName()은 채널명 값을 반환한다")
        void channelNameReturnsStringValue() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel(1L, "쿠팡");

            // when
            String name = salesChannel.channelName();

            // then
            assertThat(name).isEqualTo("쿠팡");
        }

        @Test
        @DisplayName("isActive()는 활성 상태 여부를 반환한다")
        void isActiveReturnsStatusFlag() {
            // given
            SalesChannel active = SalesChannelFixtures.activeSalesChannel();
            SalesChannel inactive = SalesChannelFixtures.inactiveSalesChannel();

            // when & then
            assertThat(active.isActive()).isTrue();
            assertThat(inactive.isActive()).isFalse();
        }
    }
}
