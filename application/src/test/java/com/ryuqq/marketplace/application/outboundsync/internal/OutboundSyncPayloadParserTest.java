package com.ryuqq.marketplace.application.outboundsync.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundSyncPayloadParser 단위 테스트")
class OutboundSyncPayloadParserTest {

    @Nested
    @DisplayName("parseChangedAreas()")
    class ParseChangedAreasTest {

        @Test
        @DisplayName("단일 영역 파싱")
        void parseSingleArea() {
            String payload = "{\"changedAreas\":[\"PRICE\"]}";

            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas(payload);

            assertThat(result).containsExactly(ChangedArea.PRICE);
        }

        @Test
        @DisplayName("복수 영역 파싱")
        void parseMultipleAreas() {
            String payload = "{\"changedAreas\":[\"PRICE\",\"STOCK\",\"IMAGE\"]}";

            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas(payload);

            assertThat(result)
                    .containsExactlyInAnyOrder(
                            ChangedArea.PRICE, ChangedArea.STOCK, ChangedArea.IMAGE);
        }

        @Test
        @DisplayName("빈 JSON이면 빈 Set 반환")
        void emptyJsonReturnsEmptySet() {
            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas("{}");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null이면 빈 Set 반환")
        void nullReturnsEmptySet() {
            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 문자열이면 빈 Set 반환")
        void blankReturnsEmptySet() {
            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas("  ");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("changedAreas 키가 없으면 빈 Set 반환")
        void noChangedAreasKeyReturnsEmptySet() {
            String payload = "{\"someOther\":\"value\"}";

            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas(payload);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("알 수 없는 값은 무시")
        void unknownValueIsIgnored() {
            String payload = "{\"changedAreas\":[\"PRICE\",\"UNKNOWN_AREA\",\"STOCK\"]}";

            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas(payload);

            assertThat(result).containsExactlyInAnyOrder(ChangedArea.PRICE, ChangedArea.STOCK);
        }

        @Test
        @DisplayName("빈 배열이면 빈 Set 반환")
        void emptyArrayReturnsEmptySet() {
            String payload = "{\"changedAreas\":[]}";

            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas(payload);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("모든 영역 파싱")
        void parseAllAreas() {
            String payload =
                    "{\"changedAreas\":[\"BASIC_INFO\",\"DESCRIPTION\",\"IMAGE\",\"NOTICE\",\"OPTION\",\"PRICE\",\"STOCK\",\"STATUS\"]}";

            Set<ChangedArea> result = OutboundSyncPayloadParser.parseChangedAreas(payload);

            assertThat(result).hasSize(ChangedArea.TOTAL_COUNT);
        }
    }
}
