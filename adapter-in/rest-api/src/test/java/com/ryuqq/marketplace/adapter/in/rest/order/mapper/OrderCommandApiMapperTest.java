package com.ryuqq.marketplace.adapter.in.rest.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderApiFixtures;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderCommandApiMapper 단위 테스트")
class OrderCommandApiMapperTest {

    private OrderCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderCommandApiMapper();
    }

    @Nested
    @DisplayName("toAddMemoCommand() - 수기 메모 커맨드 변환")
    class ToAddMemoCommandTest {

        @Test
        @DisplayName("orderItemId, 메모 요청, actorInfo를 AddClaimHistoryMemoCommand로 변환한다")
        void toAddMemoCommand_ConvertsRequest_ReturnsCommand() {
            // given
            String orderItemId = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID;
            AddClaimHistoryMemoApiRequest request = OrderApiFixtures.addMemoRequest();
            long actorId = 100L;
            String actorName = "seller01";
            MarketAccessChecker.ActorInfo actor =
                    new MarketAccessChecker.ActorInfo(actorId, actorName);

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(orderItemId, request, actor);

            // then
            assertThat(command.claimType()).isEqualTo(ClaimType.ORDER);
            assertThat(command.claimId()).isEqualTo(orderItemId);
            assertThat(command.orderItemId()).isEqualTo(orderItemId);
            assertThat(command.message()).isEqualTo("주문 수기 메모 내용입니다.");
            assertThat(command.actorId()).isEqualTo("100");
            assertThat(command.actorName()).isEqualTo(actorName);
        }

        @Test
        @DisplayName("claimId와 orderItemId가 동일한 orderItemId로 설정된다")
        void toAddMemoCommand_ClaimIdAndOrderItemIdAreSame() {
            // given
            String orderItemId = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID;
            AddClaimHistoryMemoApiRequest request = OrderApiFixtures.addMemoRequest();
            MarketAccessChecker.ActorInfo actor =
                    new MarketAccessChecker.ActorInfo(200L, "admin01");

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(orderItemId, request, actor);

            // then
            assertThat(command.claimId()).isEqualTo(command.orderItemId());
            assertThat(command.claimId()).isEqualTo(orderItemId);
        }

        @Test
        @DisplayName("claimType은 항상 ORDER로 설정된다")
        void toAddMemoCommand_ClaimTypeIsAlwaysOrder() {
            // given
            String orderItemId = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID;
            AddClaimHistoryMemoApiRequest request = new AddClaimHistoryMemoApiRequest("다른 메모");
            MarketAccessChecker.ActorInfo actor = new MarketAccessChecker.ActorInfo(1L, "operator");

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(orderItemId, request, actor);

            // then
            assertThat(command.claimType()).isEqualTo(ClaimType.ORDER);
        }

        @Test
        @DisplayName("actorId는 Long을 String으로 변환한다")
        void toAddMemoCommand_ActorIdConvertedToString() {
            // given
            String orderItemId = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID;
            AddClaimHistoryMemoApiRequest request = OrderApiFixtures.addMemoRequest();
            long actorId = 9999L;
            MarketAccessChecker.ActorInfo actor =
                    new MarketAccessChecker.ActorInfo(actorId, "tester");

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(orderItemId, request, actor);

            // then
            assertThat(command.actorId()).isEqualTo("9999");
            assertThat(command.actorName()).isEqualTo("tester");
        }

        @Test
        @DisplayName("메모 내용이 그대로 커맨드의 message로 설정된다")
        void toAddMemoCommand_MessageMappedFromRequest() {
            // given
            String orderItemId = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID;
            String expectedMessage = "특수 메모 내용 - 테스트용";
            AddClaimHistoryMemoApiRequest request =
                    new AddClaimHistoryMemoApiRequest(expectedMessage);
            MarketAccessChecker.ActorInfo actor =
                    new MarketAccessChecker.ActorInfo(100L, "seller01");

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(orderItemId, request, actor);

            // then
            assertThat(command.message()).isEqualTo(expectedMessage);
        }
    }
}
