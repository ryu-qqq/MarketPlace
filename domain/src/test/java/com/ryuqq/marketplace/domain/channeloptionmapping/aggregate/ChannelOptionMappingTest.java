package com.ryuqq.marketplace.domain.channeloptionmapping.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.ChannelOptionMappingFixtures;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.vo.ExternalOptionCode;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ChannelOptionMapping Aggregate 테스트")
class ChannelOptionMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 채널 옵션 매핑 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 채널 옵션 매핑을 생성한다")
        void createNewChannelOptionMappingWithRequiredFields() {
            // given
            SalesChannelId salesChannelId = ChannelOptionMappingFixtures.defaultSalesChannelId();
            CanonicalOptionValueId canonicalOptionValueId =
                    ChannelOptionMappingFixtures.defaultCanonicalOptionValueId();
            ExternalOptionCode externalOptionCode =
                    ChannelOptionMappingFixtures.defaultExternalOptionCode();
            Instant now = CommonVoFixtures.now();

            // when
            ChannelOptionMapping mapping =
                    ChannelOptionMapping.forNew(
                            salesChannelId, canonicalOptionValueId, externalOptionCode, now);

            // then
            assertThat(mapping.id().isNew()).isTrue();
            assertThat(mapping.salesChannelId()).isEqualTo(salesChannelId);
            assertThat(mapping.canonicalOptionValueId()).isEqualTo(canonicalOptionValueId);
            assertThat(mapping.externalOptionCode()).isEqualTo(externalOptionCode);
            assertThat(mapping.createdAt()).isEqualTo(now);
            assertThat(mapping.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 생성 시 updatedAt과 createdAt이 같다")
        void newMappingHasSameCreatedAndUpdatedAt() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ChannelOptionMapping mapping =
                    ChannelOptionMapping.forNew(
                            ChannelOptionMappingFixtures.defaultSalesChannelId(),
                            ChannelOptionMappingFixtures.defaultCanonicalOptionValueId(),
                            ChannelOptionMappingFixtures.defaultExternalOptionCode(),
                            now);

            // then
            assertThat(mapping.createdAt()).isEqualTo(mapping.updatedAt());
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 채널 옵션 매핑을 복원한다")
        void reconstituteChannelOptionMapping() {
            // given
            ChannelOptionMappingId id =
                    ChannelOptionMappingFixtures.defaultChannelOptionMappingId();
            SalesChannelId salesChannelId = ChannelOptionMappingFixtures.defaultSalesChannelId();
            CanonicalOptionValueId canonicalOptionValueId =
                    ChannelOptionMappingFixtures.defaultCanonicalOptionValueId();
            ExternalOptionCode externalOptionCode =
                    ChannelOptionMappingFixtures.defaultExternalOptionCode();
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            ChannelOptionMapping mapping =
                    ChannelOptionMapping.reconstitute(
                            id,
                            salesChannelId,
                            canonicalOptionValueId,
                            externalOptionCode,
                            createdAt,
                            updatedAt);

            // then
            assertThat(mapping.id()).isEqualTo(id);
            assertThat(mapping.idValue()).isEqualTo(1L);
            assertThat(mapping.salesChannelId()).isEqualTo(salesChannelId);
            assertThat(mapping.canonicalOptionValueId()).isEqualTo(canonicalOptionValueId);
            assertThat(mapping.externalOptionCode()).isEqualTo(externalOptionCode);
            assertThat(mapping.createdAt()).isEqualTo(createdAt);
            assertThat(mapping.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("복원 시 ID가 null이 아니다")
        void reconstituteHasNonNullId() {
            // when
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();

            // then
            assertThat(mapping.id().isNew()).isFalse();
            assertThat(mapping.idValue()).isNotNull();
        }
    }

    @Nested
    @DisplayName("updateExternalOptionCode() - 외부 옵션 코드 수정")
    class UpdateExternalOptionCodeTest {

        @Test
        @DisplayName("외부 옵션 코드를 수정한다")
        void updateExternalOptionCode() {
            // given
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();
            ExternalOptionCode newCode = ExternalOptionCode.of("NEW-CODE-001");
            Instant now = CommonVoFixtures.now();
            Instant oldUpdatedAt = mapping.updatedAt();

            // when
            mapping.updateExternalOptionCode(newCode, now);

            // then
            assertThat(mapping.externalOptionCode()).isEqualTo(newCode);
            assertThat(mapping.externalOptionCodeValue()).isEqualTo("NEW-CODE-001");
        }

        @Test
        @DisplayName("외부 옵션 코드 수정 시 updatedAt이 변경된다")
        void updateExternalOptionCodeUpdatesUpdatedAt() {
            // given
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();
            ExternalOptionCode newCode = ExternalOptionCode.of("NEW-CODE-002");
            Instant now = CommonVoFixtures.now();
            Instant oldUpdatedAt = mapping.updatedAt();

            // when
            mapping.updateExternalOptionCode(newCode, now);

            // then
            assertThat(mapping.updatedAt()).isEqualTo(now);
            assertThat(mapping.updatedAt()).isNotEqualTo(oldUpdatedAt);
        }

        @Test
        @DisplayName("외부 옵션 코드 수정해도 createdAt은 변경되지 않는다")
        void updateExternalOptionCodeDoesNotChangeCreatedAt() {
            // given
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();
            ExternalOptionCode newCode = ExternalOptionCode.of("NEW-CODE-003");
            Instant createdAt = mapping.createdAt();

            // when
            mapping.updateExternalOptionCode(newCode, CommonVoFixtures.now());

            // then
            assertThat(mapping.createdAt()).isEqualTo(createdAt);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(100L);

            // when & then
            assertThat(mapping.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("salesChannelIdValue()는 판매채널 ID 값을 반환한다")
        void salesChannelIdValueReturnsSalesChannelIdValue() {
            // given
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(
                            1L, 50L, 100L, "CODE-001");

            // when & then
            assertThat(mapping.salesChannelIdValue()).isEqualTo(50L);
        }

        @Test
        @DisplayName("canonicalOptionValueIdValue()는 캐노니컬 옵션 값 ID를 반환한다")
        void canonicalOptionValueIdValueReturnsCanonicalOptionValueIdValue() {
            // given
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(
                            1L, 1L, 200L, "CODE-002");

            // when & then
            assertThat(mapping.canonicalOptionValueIdValue()).isEqualTo(200L);
        }

        @Test
        @DisplayName("externalOptionCodeValue()는 외부 옵션 코드 값을 반환한다")
        void externalOptionCodeValueReturnsExternalOptionCodeValue() {
            // given
            ChannelOptionMapping mapping =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(
                            1L, 1L, 100L, "TEST-CODE");

            // when & then
            assertThat(mapping.externalOptionCodeValue()).isEqualTo("TEST-CODE");
        }
    }
}
