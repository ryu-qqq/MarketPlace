package com.ryuqq.marketplace.domain.brand.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Brand Event 단위 테스트
 *
 * <p><strong>테스트 대상 (5개 이벤트)</strong>:</p>
 * <ul>
 *   <li>BrandCreatedEvent</li>
 *   <li>BrandUpdatedEvent</li>
 *   <li>BrandStatusChangedEvent</li>
 *   <li>BrandAliasAddedEvent</li>
 *   <li>BrandAliasConfirmedEvent</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Brand Event 단위 테스트")
@Tag("unit")
@Tag("domain")
@Tag("brand")
@Tag("event")
class BrandEventTest {

    // ==================== BrandCreatedEvent 테스트 ====================

    @Nested
    @DisplayName("BrandCreatedEvent 테스트")
    class BrandCreatedEventTest {

        @Test
        @DisplayName("[성공] of()로 이벤트 생성")
        void of_ValidValues_ShouldCreate() {
            // Given & When
            BrandCreatedEvent event = BrandCreatedEvent.of(1L, "NIKE", "Nike");

            // Then
            assertNotNull(event);
            assertEquals(1L, event.brandId());
            assertEquals("NIKE", event.code());
            assertEquals("Nike", event.canonicalName());
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[성공] brandId가 null이어도 생성 가능 (새 엔티티)")
        void of_NullBrandId_ShouldCreate() {
            // Given & When
            BrandCreatedEvent event = BrandCreatedEvent.of(null, "NIKE", "Nike");

            // Then
            assertNotNull(event);
            assertEquals(null, event.brandId());
        }

        @Test
        @DisplayName("[성공] 직접 생성자로 occurredAt 지정")
        void constructor_WithOccurredAt_ShouldCreate() {
            // Given
            Instant now = Instant.now();

            // When
            BrandCreatedEvent event = new BrandCreatedEvent(1L, "NIKE", "Nike", now);

            // Then
            assertEquals(now, event.occurredAt());
        }

        @Test
        @DisplayName("[성공] occurredAt null이면 자동 설정")
        void constructor_NullOccurredAt_ShouldSetAutomatically() {
            // Given & When
            BrandCreatedEvent event = new BrandCreatedEvent(1L, "NIKE", "Nike", null);

            // Then
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[실패] code가 null이면 예외")
        void of_NullCode_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandCreatedEvent.of(1L, null, "Nike"));
        }

        @Test
        @DisplayName("[실패] code가 빈 문자열이면 예외")
        void of_EmptyCode_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandCreatedEvent.of(1L, "", "Nike"));
            assertThrows(IllegalArgumentException.class,
                () -> BrandCreatedEvent.of(1L, "   ", "Nike"));
        }

        @Test
        @DisplayName("[실패] canonicalName이 null이면 예외")
        void of_NullCanonicalName_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandCreatedEvent.of(1L, "NIKE", null));
        }

        @Test
        @DisplayName("[실패] canonicalName이 빈 문자열이면 예외")
        void of_EmptyCanonicalName_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandCreatedEvent.of(1L, "NIKE", ""));
        }
    }

    // ==================== BrandUpdatedEvent 테스트 ====================

    @Nested
    @DisplayName("BrandUpdatedEvent 테스트")
    class BrandUpdatedEventTest {

        @Test
        @DisplayName("[성공] of()로 이벤트 생성")
        void of_ValidBrandId_ShouldCreate() {
            // Given & When
            BrandUpdatedEvent event = BrandUpdatedEvent.of(1L);

            // Then
            assertNotNull(event);
            assertEquals(1L, event.brandId());
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[성공] 직접 생성자로 occurredAt 지정")
        void constructor_WithOccurredAt_ShouldCreate() {
            // Given
            Instant now = Instant.now();

            // When
            BrandUpdatedEvent event = new BrandUpdatedEvent(1L, now);

            // Then
            assertEquals(now, event.occurredAt());
        }

        @Test
        @DisplayName("[성공] occurredAt null이면 자동 설정")
        void constructor_NullOccurredAt_ShouldSetAutomatically() {
            // Given & When
            BrandUpdatedEvent event = new BrandUpdatedEvent(1L, null);

            // Then
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[실패] brandId가 null이면 예외")
        void of_NullBrandId_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandUpdatedEvent.of(null));
        }
    }

    // ==================== BrandStatusChangedEvent 테스트 ====================

    @Nested
    @DisplayName("BrandStatusChangedEvent 테스트")
    class BrandStatusChangedEventTest {

        @Test
        @DisplayName("[성공] of()로 이벤트 생성")
        void of_ValidValues_ShouldCreate() {
            // Given & When
            BrandStatusChangedEvent event = BrandStatusChangedEvent.of(1L, "ACTIVE", "INACTIVE");

            // Then
            assertNotNull(event);
            assertEquals(1L, event.brandId());
            assertEquals("ACTIVE", event.oldStatus());
            assertEquals("INACTIVE", event.newStatus());
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[성공] 직접 생성자로 occurredAt 지정")
        void constructor_WithOccurredAt_ShouldCreate() {
            // Given
            Instant now = Instant.now();

            // When
            BrandStatusChangedEvent event = new BrandStatusChangedEvent(1L, "ACTIVE", "BLOCKED", now);

            // Then
            assertEquals(now, event.occurredAt());
        }

        @Test
        @DisplayName("[성공] occurredAt null이면 자동 설정")
        void constructor_NullOccurredAt_ShouldSetAutomatically() {
            // Given & When
            BrandStatusChangedEvent event = new BrandStatusChangedEvent(1L, "ACTIVE", "INACTIVE", null);

            // Then
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[실패] brandId가 null이면 예외")
        void of_NullBrandId_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandStatusChangedEvent.of(null, "ACTIVE", "INACTIVE"));
        }

        @Test
        @DisplayName("[실패] oldStatus가 null이면 예외")
        void of_NullOldStatus_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandStatusChangedEvent.of(1L, null, "INACTIVE"));
        }

        @Test
        @DisplayName("[실패] oldStatus가 빈 문자열이면 예외")
        void of_EmptyOldStatus_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandStatusChangedEvent.of(1L, "", "INACTIVE"));
            assertThrows(IllegalArgumentException.class,
                () -> BrandStatusChangedEvent.of(1L, "   ", "INACTIVE"));
        }

        @Test
        @DisplayName("[실패] newStatus가 null이면 예외")
        void of_NullNewStatus_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandStatusChangedEvent.of(1L, "ACTIVE", null));
        }

        @Test
        @DisplayName("[실패] newStatus가 빈 문자열이면 예외")
        void of_EmptyNewStatus_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandStatusChangedEvent.of(1L, "ACTIVE", ""));
        }
    }

    // ==================== BrandAliasAddedEvent 테스트 ====================

    @Nested
    @DisplayName("BrandAliasAddedEvent 테스트")
    class BrandAliasAddedEventTest {

        @Test
        @DisplayName("[성공] of()로 이벤트 생성")
        void of_ValidValues_ShouldCreate() {
            // Given & When
            BrandAliasAddedEvent event = BrandAliasAddedEvent.of(
                1L, 100L, "Nike Korea", "nikekorea", "MANUAL"
            );

            // Then
            assertNotNull(event);
            assertEquals(1L, event.brandId());
            assertEquals(100L, event.aliasId());
            assertEquals("Nike Korea", event.originalAlias());
            assertEquals("nikekorea", event.normalizedAlias());
            assertEquals("MANUAL", event.sourceType());
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[성공] brandId와 aliasId가 null이어도 생성 가능 (새 엔티티)")
        void of_NullIds_ShouldCreate() {
            // Given & When
            BrandAliasAddedEvent event = BrandAliasAddedEvent.of(
                null, null, "Nike Korea", "nikekorea", "MANUAL"
            );

            // Then
            assertNotNull(event);
            assertEquals(null, event.brandId());
            assertEquals(null, event.aliasId());
        }

        @Test
        @DisplayName("[성공] 직접 생성자로 occurredAt 지정")
        void constructor_WithOccurredAt_ShouldCreate() {
            // Given
            Instant now = Instant.now();

            // When
            BrandAliasAddedEvent event = new BrandAliasAddedEvent(
                1L, 100L, "Nike Korea", "nikekorea", "MANUAL", now
            );

            // Then
            assertEquals(now, event.occurredAt());
        }

        @Test
        @DisplayName("[성공] occurredAt null이면 자동 설정")
        void constructor_NullOccurredAt_ShouldSetAutomatically() {
            // Given & When
            BrandAliasAddedEvent event = new BrandAliasAddedEvent(
                1L, 100L, "Nike Korea", "nikekorea", "MANUAL", null
            );

            // Then
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[실패] originalAlias가 null이면 예외")
        void of_NullOriginalAlias_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasAddedEvent.of(1L, 100L, null, "nikekorea", "MANUAL"));
        }

        @Test
        @DisplayName("[실패] originalAlias가 빈 문자열이면 예외")
        void of_EmptyOriginalAlias_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasAddedEvent.of(1L, 100L, "", "nikekorea", "MANUAL"));
        }

        @Test
        @DisplayName("[실패] normalizedAlias가 null이면 예외")
        void of_NullNormalizedAlias_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasAddedEvent.of(1L, 100L, "Nike Korea", null, "MANUAL"));
        }

        @Test
        @DisplayName("[실패] normalizedAlias가 빈 문자열이면 예외")
        void of_EmptyNormalizedAlias_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasAddedEvent.of(1L, 100L, "Nike Korea", "", "MANUAL"));
        }

        @Test
        @DisplayName("[실패] sourceType이 null이면 예외")
        void of_NullSourceType_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasAddedEvent.of(1L, 100L, "Nike Korea", "nikekorea", null));
        }

        @Test
        @DisplayName("[실패] sourceType이 빈 문자열이면 예외")
        void of_EmptySourceType_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasAddedEvent.of(1L, 100L, "Nike Korea", "nikekorea", ""));
        }
    }

    // ==================== BrandAliasConfirmedEvent 테스트 ====================

    @Nested
    @DisplayName("BrandAliasConfirmedEvent 테스트")
    class BrandAliasConfirmedEventTest {

        @Test
        @DisplayName("[성공] of()로 이벤트 생성")
        void of_ValidValues_ShouldCreate() {
            // Given & When
            BrandAliasConfirmedEvent event = BrandAliasConfirmedEvent.of(1L, 100L, "nikekorea");

            // Then
            assertNotNull(event);
            assertEquals(1L, event.brandId());
            assertEquals(100L, event.aliasId());
            assertEquals("nikekorea", event.normalizedAlias());
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[성공] 직접 생성자로 occurredAt 지정")
        void constructor_WithOccurredAt_ShouldCreate() {
            // Given
            Instant now = Instant.now();

            // When
            BrandAliasConfirmedEvent event = new BrandAliasConfirmedEvent(1L, 100L, "nikekorea", now);

            // Then
            assertEquals(now, event.occurredAt());
        }

        @Test
        @DisplayName("[성공] occurredAt null이면 자동 설정")
        void constructor_NullOccurredAt_ShouldSetAutomatically() {
            // Given & When
            BrandAliasConfirmedEvent event = new BrandAliasConfirmedEvent(1L, 100L, "nikekorea", null);

            // Then
            assertNotNull(event.occurredAt());
        }

        @Test
        @DisplayName("[실패] brandId가 null이면 예외")
        void of_NullBrandId_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasConfirmedEvent.of(null, 100L, "nikekorea"));
        }

        @Test
        @DisplayName("[실패] aliasId가 null이면 예외")
        void of_NullAliasId_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasConfirmedEvent.of(1L, null, "nikekorea"));
        }

        @Test
        @DisplayName("[실패] normalizedAlias가 null이면 예외")
        void of_NullNormalizedAlias_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasConfirmedEvent.of(1L, 100L, null));
        }

        @Test
        @DisplayName("[실패] normalizedAlias가 빈 문자열이면 예외")
        void of_EmptyNormalizedAlias_ShouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasConfirmedEvent.of(1L, 100L, ""));
            assertThrows(IllegalArgumentException.class,
                () -> BrandAliasConfirmedEvent.of(1L, 100L, "   "));
        }
    }
}
