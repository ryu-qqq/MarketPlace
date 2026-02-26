package com.ryuqq.marketplace.adapter.in.rest.adminmenu.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.adminmenu.exception.AdminMenuErrorCode;
import com.ryuqq.marketplace.domain.adminmenu.exception.AdminMenuException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("AdminMenuErrorMapper 단위 테스트")
class AdminMenuErrorMapperTest {

    private final AdminMenuErrorMapper sut = new AdminMenuErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("AdminMenuException을 지원한다")
        void supports_AdminMenuException_ReturnsTrue() {
            // given
            DomainException ex = new AdminMenuException(AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void supports_OtherDomainException_ReturnsFalse() {
            // given
            DomainException ex =
                    new DomainException(
                            new com.ryuqq.marketplace.domain.common.exception.ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "OTHER-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 400;
                                }

                                @Override
                                public String getMessage() {
                                    return "Other error";
                                }
                            }) {};

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("map() - 예외를 MappedError로 변환")
    class MapTest {

        @Test
        @DisplayName("ADMIN_MENU_NOT_FOUND를 404 MappedError로 변환한다")
        void map_NotFound_Returns404() {
            // given
            AdminMenuException ex = new AdminMenuException(AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Admin Menu Error");
            assertThat(result.detail()).contains("관리자 메뉴");
            assertThat(result.type().toString()).startsWith("/errors/admin-menu/");
        }

        @Test
        @DisplayName("커스텀 메시지를 포함한 예외를 변환한다")
        void map_CustomMessage_ReturnsCustomDetail() {
            // given
            AdminMenuException ex =
                    new AdminMenuException(
                            AdminMenuErrorCode.ADMIN_MENU_NOT_FOUND, "ID: 999 메뉴를 찾을 수 없습니다");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.detail()).contains("999");
        }
    }
}
