package com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.repository.LegacyProductNoticeJpaRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductNoticeCommandAdapterTest - 레거시 상품 고시정보 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductNoticeCommandAdapter 단위 테스트")
class LegacyProductNoticeCommandAdapterTest {

    @Mock private LegacyProductNoticeJpaRepository repository;

    @InjectMocks private LegacyProductNoticeCommandAdapter commandAdapter;

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("고시정보를 저장합니다")
        void persist_WithValidNotice_SavesSuccessfully() {
            // given
            long productGroupId = 1L;
            Map<String, String> flatFields =
                    Map.of(
                            "material", "면 100%",
                            "color", "블랙",
                            "size", "M",
                            "manufacturer", "삼성",
                            "made_in", "한국",
                            "wash_care", "물세탁",
                            "release_date", "2024-01-01",
                            "quality_assurance", "KC인증",
                            "cs_info", "1588-1234");

            // when
            commandAdapter.persist(productGroupId, flatFields);

            // then
            then(repository).should().save(any());
        }
    }
}
