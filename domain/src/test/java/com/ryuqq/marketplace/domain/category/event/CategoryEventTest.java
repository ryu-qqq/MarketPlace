package com.ryuqq.marketplace.domain.category.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Category Event 단위 테스트
 *
 * <p><strong>테스트 대상 (3개 이벤트)</strong>:</p>
 * <ul>
 *   <li>CategoryCreatedEvent</li>
 *   <li>CategoryUpdatedEvent</li>
 *   <li>CategoryStatusChangedEvent</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Category Event 단위 테스트")
@Tag("unit")
@Tag("domain")
@Tag("category")
@Tag("event")
class CategoryEventTest {

    // ==================== CategoryCreatedEvent 테스트 ====================

    @Nested
    @DisplayName("CategoryCreatedEvent 테스트")
    class CategoryCreatedEventTest {

        @Test
        @DisplayName("[성공] 간편 생성자로 이벤트 생성")
        void constructor_WithSimpleParams_ShouldCreate() {
            // Given
            Long categoryId = 1L;
            String code = "FASHION";
            String name = "패션";

            // When
            CategoryCreatedEvent event = new CategoryCreatedEvent(categoryId, code, name);

            // Then
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.code()).isEqualTo(code);
            assertThat(event.name()).isEqualTo(name);
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("[성공] 전체 생성자로 이벤트 생성")
        void constructor_WithAllParams_ShouldCreate() {
            // Given
            Long categoryId = 1L;
            String code = "FASHION";
            String name = "패션";
            Instant occurredAt = Instant.now();

            // When
            CategoryCreatedEvent event = new CategoryCreatedEvent(categoryId, code, name, occurredAt);

            // Then
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.code()).isEqualTo(code);
            assertThat(event.name()).isEqualTo(name);
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("[성공] categoryId가 null이어도 생성 가능 (신규 생성)")
        void constructor_WithNullCategoryId_ShouldCreate() {
            // When
            CategoryCreatedEvent event = new CategoryCreatedEvent(null, "FASHION", "패션");

            // Then
            assertThat(event.categoryId()).isNull();
            assertThat(event.code()).isEqualTo("FASHION");
        }

        @Test
        @DisplayName("[성공] occurredAt 자동 설정 확인")
        void constructor_OccurredAt_ShouldBeSet() {
            // Given
            Instant before = Instant.now();

            // When
            CategoryCreatedEvent event = new CategoryCreatedEvent(1L, "FASHION", "패션");

            // Then
            Instant after = Instant.now();
            assertThat(event.occurredAt())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("[성공] DomainEvent 인터페이스 구현 확인")
        void shouldImplementDomainEvent() {
            // When
            CategoryCreatedEvent event = new CategoryCreatedEvent(1L, "FASHION", "패션");

            // Then
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            Instant now = Instant.now();
            CategoryCreatedEvent event1 = new CategoryCreatedEvent(1L, "FASHION", "패션", now);
            CategoryCreatedEvent event2 = new CategoryCreatedEvent(1L, "FASHION", "패션", now);
            CategoryCreatedEvent event3 = new CategoryCreatedEvent(2L, "BEAUTY", "뷰티", now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1).isNotEqualTo(event3);
        }

        @Test
        @DisplayName("[성공] of() 팩토리 메서드로 이벤트 생성")
        void of_ShouldCreateEvent() {
            // Given
            Long categoryId = 1L;
            String code = "FASHION";
            String name = "패션";
            Instant before = Instant.now();

            // When
            CategoryCreatedEvent event = CategoryCreatedEvent.of(categoryId, code, name);

            // Then
            Instant after = Instant.now();
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.code()).isEqualTo(code);
            assertThat(event.name()).isEqualTo(name);
            assertThat(event.occurredAt())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
        }
    }

    // ==================== CategoryUpdatedEvent 테스트 ====================

    @Nested
    @DisplayName("CategoryUpdatedEvent 테스트")
    class CategoryUpdatedEventTest {

        @Test
        @DisplayName("[성공] 간편 생성자로 이벤트 생성")
        void constructor_WithSimpleParams_ShouldCreate() {
            // Given
            Long categoryId = 1L;

            // When
            CategoryUpdatedEvent event = new CategoryUpdatedEvent(categoryId);

            // Then
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("[성공] 전체 생성자로 이벤트 생성")
        void constructor_WithAllParams_ShouldCreate() {
            // Given
            Long categoryId = 1L;
            Instant occurredAt = Instant.now();

            // When
            CategoryUpdatedEvent event = new CategoryUpdatedEvent(categoryId, occurredAt);

            // Then
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("[성공] occurredAt 자동 설정 확인")
        void constructor_OccurredAt_ShouldBeSet() {
            // Given
            Instant before = Instant.now();

            // When
            CategoryUpdatedEvent event = new CategoryUpdatedEvent(1L);

            // Then
            Instant after = Instant.now();
            assertThat(event.occurredAt())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("[성공] DomainEvent 인터페이스 구현 확인")
        void shouldImplementDomainEvent() {
            // When
            CategoryUpdatedEvent event = new CategoryUpdatedEvent(1L);

            // Then
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            Instant now = Instant.now();
            CategoryUpdatedEvent event1 = new CategoryUpdatedEvent(1L, now);
            CategoryUpdatedEvent event2 = new CategoryUpdatedEvent(1L, now);
            CategoryUpdatedEvent event3 = new CategoryUpdatedEvent(2L, now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1).isNotEqualTo(event3);
        }

        @Test
        @DisplayName("[성공] of() 팩토리 메서드로 이벤트 생성")
        void of_ShouldCreateEvent() {
            // Given
            Long categoryId = 1L;
            Instant before = Instant.now();

            // When
            CategoryUpdatedEvent event = CategoryUpdatedEvent.of(categoryId);

            // Then
            Instant after = Instant.now();
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.occurredAt())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
        }
    }

    // ==================== CategoryStatusChangedEvent 테스트 ====================

    @Nested
    @DisplayName("CategoryStatusChangedEvent 테스트")
    class CategoryStatusChangedEventTest {

        @Test
        @DisplayName("[성공] 간편 생성자로 이벤트 생성")
        void constructor_WithSimpleParams_ShouldCreate() {
            // Given
            Long categoryId = 1L;
            String oldStatus = "ACTIVE";
            String newStatus = "INACTIVE";

            // When
            CategoryStatusChangedEvent event = new CategoryStatusChangedEvent(categoryId, oldStatus, newStatus);

            // Then
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.oldStatus()).isEqualTo(oldStatus);
            assertThat(event.newStatus()).isEqualTo(newStatus);
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("[성공] 전체 생성자로 이벤트 생성")
        void constructor_WithAllParams_ShouldCreate() {
            // Given
            Long categoryId = 1L;
            String oldStatus = "ACTIVE";
            String newStatus = "INACTIVE";
            Instant occurredAt = Instant.now();

            // When
            CategoryStatusChangedEvent event = new CategoryStatusChangedEvent(categoryId, oldStatus, newStatus, occurredAt);

            // Then
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.oldStatus()).isEqualTo(oldStatus);
            assertThat(event.newStatus()).isEqualTo(newStatus);
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        @DisplayName("[성공] ACTIVE -> INACTIVE 상태 변경")
        void constructor_ActiveToInactive_ShouldCreate() {
            // When
            CategoryStatusChangedEvent event = new CategoryStatusChangedEvent(1L, "ACTIVE", "INACTIVE");

            // Then
            assertThat(event.oldStatus()).isEqualTo("ACTIVE");
            assertThat(event.newStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("[성공] ACTIVE -> DEPRECATED 상태 변경")
        void constructor_ActiveToDeprecated_ShouldCreate() {
            // When
            CategoryStatusChangedEvent event = new CategoryStatusChangedEvent(1L, "ACTIVE", "DEPRECATED");

            // Then
            assertThat(event.oldStatus()).isEqualTo("ACTIVE");
            assertThat(event.newStatus()).isEqualTo("DEPRECATED");
        }

        @Test
        @DisplayName("[성공] INACTIVE -> ACTIVE 상태 변경 (재활성화)")
        void constructor_InactiveToActive_ShouldCreate() {
            // When
            CategoryStatusChangedEvent event = new CategoryStatusChangedEvent(1L, "INACTIVE", "ACTIVE");

            // Then
            assertThat(event.oldStatus()).isEqualTo("INACTIVE");
            assertThat(event.newStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("[성공] occurredAt 자동 설정 확인")
        void constructor_OccurredAt_ShouldBeSet() {
            // Given
            Instant before = Instant.now();

            // When
            CategoryStatusChangedEvent event = new CategoryStatusChangedEvent(1L, "ACTIVE", "INACTIVE");

            // Then
            Instant after = Instant.now();
            assertThat(event.occurredAt())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("[성공] DomainEvent 인터페이스 구현 확인")
        void shouldImplementDomainEvent() {
            // When
            CategoryStatusChangedEvent event = new CategoryStatusChangedEvent(1L, "ACTIVE", "INACTIVE");

            // Then
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            Instant now = Instant.now();
            CategoryStatusChangedEvent event1 = new CategoryStatusChangedEvent(1L, "ACTIVE", "INACTIVE", now);
            CategoryStatusChangedEvent event2 = new CategoryStatusChangedEvent(1L, "ACTIVE", "INACTIVE", now);
            CategoryStatusChangedEvent event3 = new CategoryStatusChangedEvent(2L, "ACTIVE", "INACTIVE", now);

            assertThat(event1).isEqualTo(event2);
            assertThat(event1).isNotEqualTo(event3);
        }

        @Test
        @DisplayName("[성공] of() 팩토리 메서드로 이벤트 생성")
        void of_ShouldCreateEvent() {
            // Given
            Long categoryId = 1L;
            String oldStatus = "ACTIVE";
            String newStatus = "INACTIVE";
            Instant before = Instant.now();

            // When
            CategoryStatusChangedEvent event = CategoryStatusChangedEvent.of(categoryId, oldStatus, newStatus);

            // Then
            Instant after = Instant.now();
            assertThat(event.categoryId()).isEqualTo(categoryId);
            assertThat(event.oldStatus()).isEqualTo(oldStatus);
            assertThat(event.newStatus()).isEqualTo(newStatus);
            assertThat(event.occurredAt())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
        }
    }
}
