package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Outbox payload에서 변경 영역 정보를 파싱합니다.
 *
 * <p>payload 형식: {@code {"changedAreas":["PRICE","STOCK"]}}
 *
 * <p>파싱 실패 시 빈 Set을 반환하여 전체 수정으로 폴백합니다.
 */
public final class OutboundSyncPayloadParser {

    private static final Pattern AREAS_PATTERN =
            Pattern.compile("\"changedAreas\"\\s*:\\s*\\[([^]]*)]");
    private static final Pattern VALUE_PATTERN = Pattern.compile("\"(\\w+)\"");

    private OutboundSyncPayloadParser() {}

    /**
     * payload JSON에서 changedAreas를 파싱합니다.
     *
     * @param payload Outbox payload (nullable)
     * @return 변경 영역 집합 (빈 Set이면 전체 수정으로 간주)
     */
    public static Set<ChangedArea> parseChangedAreas(String payload) {
        if (payload == null || payload.isBlank() || "{}".equals(payload.trim())) {
            return Set.of();
        }

        Matcher arrayMatcher = AREAS_PATTERN.matcher(payload);
        if (!arrayMatcher.find()) {
            return Set.of();
        }

        String arrayContent = arrayMatcher.group(1);
        Matcher valueMatcher = VALUE_PATTERN.matcher(arrayContent);
        Set<ChangedArea> areas = EnumSet.noneOf(ChangedArea.class);

        while (valueMatcher.find()) {
            try {
                areas.add(ChangedArea.valueOf(valueMatcher.group(1)));
            } catch (IllegalArgumentException ignored) {
                // 알 수 없는 값은 무시
            }
        }

        return areas;
    }
}
