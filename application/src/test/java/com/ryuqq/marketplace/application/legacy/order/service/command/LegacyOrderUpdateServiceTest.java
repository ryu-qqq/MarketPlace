package com.ryuqq.marketplace.application.legacy.order.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderUpdateResult;
import com.ryuqq.marketplace.application.legacy.order.internal.LegacyOrderMarketRouter;
import com.ryuqq.marketplace.application.legacy.order.resolver.LegacyOrderIdResolver;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegacyOrderUpdateServiceTest {

    @Mock private LegacyOrderIdResolver idResolver;
    @Mock private LegacyOrderMarketRouter marketRouter;

    @InjectMocks private LegacyOrderUpdateService service;

    @Test
    @DisplayName("매핑이 있으면 market 라우터로 위임")
    void routeToMarket() {
        LegacyOrderUpdateCommand command = command("DELIVERY_COMPLETED");
        LegacyOrderIdMapping mapping = LegacyOrderIdMapping.forNew(
                5001L, 9001L, "order-uuid", 1001L, 1L, "SETOF", Instant.now());

        given(idResolver.resolve(5001L)).willReturn(Optional.of(mapping));

        LegacyOrderUpdateResult result = service.execute(command);

        verify(marketRouter).route(command, mapping);
        assertThat(result.orderId()).isEqualTo(5001L);
        assertThat(result.toBeOrderStatus()).isEqualTo("DELIVERY_COMPLETED");
    }

    @Test
    @DisplayName("매핑이 없으면 예외 발생")
    void throwsWhenNoMapping() {
        LegacyOrderUpdateCommand command = command("DELIVERY_COMPLETED");
        given(idResolver.resolve(5001L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException.class);
    }

    private LegacyOrderUpdateCommand command(String orderStatus) {
        return new LegacyOrderUpdateCommand(
                "normalOrder", 5001L, orderStatus, null,
                "사유", "상세", null, null, null);
    }
}
