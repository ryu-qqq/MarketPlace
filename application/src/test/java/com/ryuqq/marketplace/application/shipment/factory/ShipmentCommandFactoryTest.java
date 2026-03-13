package com.ryuqq.marketplace.application.shipment.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.ShipmentCommandFixtures;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory.ShipSingleContext;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.time.Instant;
import java.util.List;
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
@DisplayName("ShipmentCommandFactory 단위 테스트")
class ShipmentCommandFactoryTest {

    @InjectMocks private ShipmentCommandFactory sut;

    @Mock private TimeProvider timeProvider;
    @Mock private com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort idGeneratorPort;

    @Nested
    @DisplayName("createConfirmContexts() - 발주확인 배치 컨텍스트 생성")
    class CreateConfirmContextsTest {

        @Test
        @DisplayName("orderItemIds를 OrderItemId 목록으로 변환하고 changedAt을 설정한다")
        void createConfirmContexts_ValidCommand_ReturnsBulkStatusChangeContext() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            given(timeProvider.now()).willReturn(now);

            ConfirmShipmentBatchCommand command =
                    ShipmentCommandFixtures.confirmBatchCommand(
                            "01940001-0000-7000-8000-000000000001",
                            "01940001-0000-7000-8000-000000000002",
                            "01940001-0000-7000-8000-000000000003");

            // when
            BulkStatusChangeContext<OrderItemId> result = sut.createConfirmContexts(command);

            // then
            assertThat(result.ids()).hasSize(3);
            assertThat(result.ids().get(0).value())
                    .isEqualTo("01940001-0000-7000-8000-000000000001");
            assertThat(result.ids().get(1).value())
                    .isEqualTo("01940001-0000-7000-8000-000000000002");
            assertThat(result.ids().get(2).value())
                    .isEqualTo("01940001-0000-7000-8000-000000000003");
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("빈 목록이면 빈 BulkStatusChangeContext를 반환한다")
        void createConfirmContexts_EmptyList_ReturnsEmptyContext() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            given(timeProvider.now()).willReturn(now);

            ConfirmShipmentBatchCommand command = new ConfirmShipmentBatchCommand(List.of(), null);

            // when
            BulkStatusChangeContext<OrderItemId> result = sut.createConfirmContexts(command);

            // then
            assertThat(result.ids()).isEmpty();
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createShipContexts() - 송장등록 배치 컨텍스트 생성")
    class CreateShipContextsTest {

        @Test
        @DisplayName("배치 항목들을 UpdateContext 목록으로 변환한다")
        void createShipContexts_ValidCommand_ReturnsUpdateContextList() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            given(timeProvider.now()).willReturn(now);

            ShipBatchCommand command = ShipmentCommandFixtures.shipBatchCommand(2);

            // when
            List<UpdateContext<OrderItemId, ShipmentShipData>> result =
                    sut.createShipContexts(command);

            // then
            assertThat(result).hasSize(2);

            UpdateContext<OrderItemId, ShipmentShipData> first = result.get(0);
            assertThat(first.id().value()).isEqualTo("01940001-0000-7000-8000-000000000001");
            assertThat(first.updateData().trackingNumber()).isEqualTo("tracking-1");
            assertThat(first.updateData().method().type()).isEqualTo(ShipmentMethodType.COURIER);
            assertThat(first.changedAt()).isEqualTo(now);

            UpdateContext<OrderItemId, ShipmentShipData> second = result.get(1);
            assertThat(second.id().value()).isEqualTo("01940001-0000-7000-8000-000000000002");
            assertThat(second.updateData().trackingNumber()).isEqualTo("tracking-2");
            assertThat(second.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("빈 배치 목록이면 빈 리스트를 반환한다")
        void createShipContexts_EmptyItems_ReturnsEmptyList() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            given(timeProvider.now()).willReturn(now);

            ShipBatchCommand command = new ShipBatchCommand(List.of());

            // when
            List<UpdateContext<OrderItemId, ShipmentShipData>> result =
                    sut.createShipContexts(command);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("createShipSingleContext() - 단건 송장등록 컨텍스트 생성")
    class CreateShipSingleContextTest {

        @Test
        @DisplayName("ShipSingleCommand를 ShipSingleContext로 변환한다")
        void createShipSingleContext_ValidCommand_ReturnsShipSingleContext() {
            // given
            Instant now = Instant.parse("2026-02-18T10:00:00Z");
            given(timeProvider.now()).willReturn(now);

            ShipSingleCommand command = ShipmentCommandFixtures.shipSingleCommand();

            // when
            ShipSingleContext result = sut.createShipSingleContext(command);

            // then
            assertThat(result.orderItemId().value())
                    .isEqualTo("01940001-0000-7000-8000-000000000001");
            assertThat(result.shipData().trackingNumber()).isEqualTo("1234567890");
            assertThat(result.shipData().method().type()).isEqualTo(ShipmentMethodType.COURIER);
            assertThat(result.shipData().method().courierCode()).isEqualTo("CJ");
            assertThat(result.shipData().method().courierName()).isEqualTo("CJ대한통운");
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createShipmentMethod() - ShipmentMethod 생성")
    class CreateShipmentMethodTest {

        @Test
        @DisplayName("유효한 배송 방법 유형으로 ShipmentMethod를 생성한다")
        void createShipmentMethod_ValidType_ReturnsShipmentMethod() {
            // when
            ShipmentMethod result = sut.createShipmentMethod("QUICK", "QUICK-001", "퀵서비스");

            // then
            assertThat(result.type()).isEqualTo(ShipmentMethodType.QUICK);
            assertThat(result.courierCode()).isEqualTo("QUICK-001");
            assertThat(result.courierName()).isEqualTo("퀵서비스");
        }

        @Test
        @DisplayName("null 유형이면 기본값 COURIER를 사용한다")
        void createShipmentMethod_NullType_DefaultsToCourier() {
            // when
            ShipmentMethod result = sut.createShipmentMethod(null, "CJ", "CJ대한통운");

            // then
            assertThat(result.type()).isEqualTo(ShipmentMethodType.COURIER);
        }

        @Test
        @DisplayName("빈 문자열 유형이면 기본값 COURIER를 사용한다")
        void createShipmentMethod_BlankType_DefaultsToCourier() {
            // when
            ShipmentMethod result = sut.createShipmentMethod("  ", "CJ", "CJ대한통운");

            // then
            assertThat(result.type()).isEqualTo(ShipmentMethodType.COURIER);
        }

        @Test
        @DisplayName("알 수 없는 유형이면 기본값 COURIER를 사용한다")
        void createShipmentMethod_UnknownType_DefaultsToCourier() {
            // when
            ShipmentMethod result = sut.createShipmentMethod("UNKNOWN_TYPE", "CJ", "CJ대한통운");

            // then
            assertThat(result.type()).isEqualTo(ShipmentMethodType.COURIER);
        }

        @Test
        @DisplayName("대소문자 구분 없이 배송 방법 유형을 해석한다")
        void createShipmentMethod_CaseInsensitive_ResolvesCorrectly() {
            // when
            ShipmentMethod result = sut.createShipmentMethod("quick", "Q-001", "퀵");

            // then
            assertThat(result.type()).isEqualTo(ShipmentMethodType.QUICK);
        }
    }
}
