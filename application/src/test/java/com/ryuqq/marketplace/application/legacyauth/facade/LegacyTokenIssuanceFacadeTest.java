package com.ryuqq.marketplace.application.legacyauth.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenCacheCommandManager;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyTokenIssuanceFacade 테스트")
class LegacyTokenIssuanceFacadeTest {

    @Mock private LegacyTokenManager tokenManager;
    @Mock private LegacyTokenCacheCommandManager tokenCacheCommandManager;

    @InjectMocks private LegacyTokenIssuanceFacade facade;

    @Test
    @DisplayName("토큰 발급 후 리프레시 토큰 캐시에 저장")
    void issueAndCache_GeneratesTokenAndCachesRefresh() {
        String email = "seller@test.com";
        long sellerId = 1L;
        String roleType = "SELLER";

        LegacyTokenResult tokenResult =
                new LegacyTokenResult("access.token", "refresh.token", email, 10800L);
        given(tokenManager.generateToken(email, sellerId, roleType)).willReturn(tokenResult);

        String result = facade.issueAndCache(email, sellerId, roleType);

        assertThat(result).isEqualTo("access.token");
        verify(tokenCacheCommandManager).persist(email, "refresh.token", 10800L);
    }
}
