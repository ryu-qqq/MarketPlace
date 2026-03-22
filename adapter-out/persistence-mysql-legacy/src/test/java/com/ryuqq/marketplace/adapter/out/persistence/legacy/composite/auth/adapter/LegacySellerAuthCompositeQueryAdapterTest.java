package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.dto.LegacySellerAuthQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.mapper.LegacySellerAuthCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.repository.LegacySellerAuthCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacySellerAuthCompositeQueryAdapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacySellerAuthCompositeQueryAdapter 단위 테스트")
class LegacySellerAuthCompositeQueryAdapterTest {

    @Mock private LegacySellerAuthCompositeQueryDslRepository repository;
    @Mock private LegacySellerAuthCompositeMapper mapper;

    @InjectMocks private LegacySellerAuthCompositeQueryAdapter adapter;

    @Nested
    @DisplayName("findByEmail 메서드 테스트")
    class FindByEmailTest {

        @Test
        @DisplayName("존재하는 이메일로 조회 시 인증 결과를 반환합니다")
        void findByEmail_WithExistingEmail_ReturnsAuthResult() {
            // given
            String email = "admin@test.com";
            LegacySellerAuthQueryDto dto =
                    new LegacySellerAuthQueryDto(10L, email, "$2a$10$hash", "MASTER", "APPROVED");
            LegacySellerAuthResult expectedResult =
                    new LegacySellerAuthResult(10L, email, "$2a$10$hash", "MASTER", "APPROVED");

            given(repository.findByEmail(email)).willReturn(Optional.of(dto));
            given(mapper.toResult(dto)).willReturn(expectedResult);

            // when
            Optional<LegacySellerAuthResult> result = adapter.findByEmail(email);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().sellerId()).isEqualTo(10L);
            assertThat(result.get().email()).isEqualTo(email);
            assertThat(result.get().roleType()).isEqualTo("MASTER");
            then(repository).should().findByEmail(email);
            then(mapper).should().toResult(dto);
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional을 반환합니다")
        void findByEmail_WithNonExistingEmail_ReturnsEmpty() {
            // given
            given(repository.findByEmail("unknown@test.com")).willReturn(Optional.empty());

            // when
            Optional<LegacySellerAuthResult> result = adapter.findByEmail("unknown@test.com");

            // then
            assertThat(result).isEmpty();
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
