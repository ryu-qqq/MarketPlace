package com.ryuqq.marketplace.application.legacyconversion.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.application.legacyconversion.dto.result.SeedLegacyOrderConversionResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxReadManager;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderIdScanPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderScanEntry;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SeedLegacyOrderConversionServiceTest - 레거시 주문 시딩 서비스 단위 테스트.
 *
 * <p>outbox 기반 중복 체크 로직을 검증합니다.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SeedLegacyOrderConversionService 단위 테스트")
class SeedLegacyOrderConversionServiceTest {

    @Mock private LegacyOrderIdScanPort scanPort;

    @Mock private LegacyOrderConversionOutboxReadManager outboxReadManager;

    @Mock private LegacyOrderConversionOutboxCommandManager outboxCommandManager;

    @InjectMocks private SeedLegacyOrderConversionService service;

    @Nested
    @DisplayName("execute 메서드 테스트")
    class ExecuteTest {

        @Test
        @DisplayName("스캔 결과가 없으면 allCompleted를 반환합니다")
        void execute_WithNoEntries_ReturnsAllCompleted() {
            // given
            given(scanPort.findActiveOrderEntries(0L, 500)).willReturn(List.of());

            // when
            SeedLegacyOrderConversionResult result = service.execute(0L, 500);

            // then
            assertThat(result.completed()).isTrue();
            assertThat(result.scanned()).isZero();
            then(outboxCommandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("신규 엔트리에 대해 outbox를 생성합니다")
        void execute_WithNewEntries_CreatesOutboxes() {
            // given
            List<LegacyOrderScanEntry> entries =
                    List.of(
                            new LegacyOrderScanEntry(100L, 200L),
                            new LegacyOrderScanEntry(101L, 201L));

            given(scanPort.findActiveOrderEntries(0L, 500)).willReturn(entries);
            given(outboxReadManager.existsByLegacyOrderId(100L)).willReturn(false);
            given(outboxReadManager.existsByLegacyOrderId(101L)).willReturn(false);
            given(outboxCommandManager.persist(any(LegacyOrderConversionOutbox.class)))
                    .willReturn(1L);

            // when
            SeedLegacyOrderConversionResult result = service.execute(0L, 500);

            // then
            assertThat(result.completed()).isFalse();
            assertThat(result.scanned()).isEqualTo(2);
            assertThat(result.created()).isEqualTo(2);
            assertThat(result.skipped()).isZero();
            assertThat(result.lastCursor()).isEqualTo(101L);
            then(outboxCommandManager).should(times(2)).persist(any());
        }

        @Test
        @DisplayName("이미 outbox에 존재하는 엔트리는 건너뜁니다")
        void execute_WithExistingOutbox_SkipsEntries() {
            // given
            List<LegacyOrderScanEntry> entries =
                    List.of(
                            new LegacyOrderScanEntry(100L, 200L),
                            new LegacyOrderScanEntry(101L, 201L),
                            new LegacyOrderScanEntry(102L, 202L));

            given(scanPort.findActiveOrderEntries(0L, 500)).willReturn(entries);
            given(outboxReadManager.existsByLegacyOrderId(100L)).willReturn(true);
            given(outboxReadManager.existsByLegacyOrderId(101L)).willReturn(false);
            given(outboxReadManager.existsByLegacyOrderId(102L)).willReturn(true);
            given(outboxCommandManager.persist(any(LegacyOrderConversionOutbox.class)))
                    .willReturn(1L);

            // when
            SeedLegacyOrderConversionResult result = service.execute(0L, 500);

            // then
            assertThat(result.scanned()).isEqualTo(3);
            assertThat(result.created()).isEqualTo(1);
            assertThat(result.skipped()).isEqualTo(2);
            assertThat(result.lastCursor()).isEqualTo(102L);
            then(outboxCommandManager).should(times(1)).persist(any());
        }

        @Test
        @DisplayName("모든 엔트리가 이미 존재하면 created=0을 반환합니다")
        void execute_WithAllExisting_ReturnsZeroCreated() {
            // given
            List<LegacyOrderScanEntry> entries =
                    List.of(
                            new LegacyOrderScanEntry(100L, 200L),
                            new LegacyOrderScanEntry(101L, 201L));

            given(scanPort.findActiveOrderEntries(0L, 500)).willReturn(entries);
            given(outboxReadManager.existsByLegacyOrderId(anyLong())).willReturn(true);

            // when
            SeedLegacyOrderConversionResult result = service.execute(0L, 500);

            // then
            assertThat(result.scanned()).isEqualTo(2);
            assertThat(result.created()).isZero();
            assertThat(result.skipped()).isEqualTo(2);
            assertThat(result.lastCursor()).isEqualTo(101L);
            then(outboxCommandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("커서 값을 올바르게 전달합니다")
        void execute_WithCursor_PassesCursorToScanPort() {
            // given
            long cursor = 500L;
            given(scanPort.findActiveOrderEntries(cursor, 100)).willReturn(List.of());

            // when
            service.execute(cursor, 100);

            // then
            then(scanPort).should().findActiveOrderEntries(cursor, 100);
        }
    }
}
