package com.ryuqq.marketplace.domain.inboundsource.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundsource.InboundSourceFixtures;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceUpdateData;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundSource Aggregate 단위 테스트")
class InboundSourceTest {

    @Nested
    @DisplayName("forNew() - 신규 InboundSource 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 InboundSource는 ACTIVE 상태로 생성된다")
        void createNewSourceWithActiveStatus() {
            InboundSource source = InboundSourceFixtures.newInboundSource();

            assertThat(source.status()).isEqualTo(InboundSourceStatus.ACTIVE);
            assertThat(source.isActive()).isTrue();
        }

        @Test
        @DisplayName("신규 생성 시 id.isNew()가 true이다")
        void createNewSourceIsNew() {
            InboundSource source = InboundSourceFixtures.newInboundSource();

            assertThat(source.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 생성 시 기본 정보가 올바르게 설정된다")
        void createNewSourceHasCorrectInfo() {
            InboundSource source = InboundSourceFixtures.newInboundSource();

            assertThat(source.codeValue()).isEqualTo("SETOF");
            assertThat(source.name()).isEqualTo("세토프 레거시");
            assertThat(source.type()).isEqualTo(InboundSourceType.LEGACY);
        }

        @Test
        @DisplayName("다양한 타입으로 신규 소스를 생성한다")
        void createNewSourceWithDifferentTypes() {
            InboundSource crawling =
                    InboundSourceFixtures.newInboundSource(
                            "CRAWL_SRC", "크롤 소스", InboundSourceType.CRAWLING);
            InboundSource partner =
                    InboundSourceFixtures.newInboundSource(
                            "PARTNER_SRC", "파트너 소스", InboundSourceType.PARTNER);

            assertThat(crawling.type()).isEqualTo(InboundSourceType.CRAWLING);
            assertThat(partner.type()).isEqualTo(InboundSourceType.PARTNER);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("ACTIVE 상태로 복원된 소스는 isActive()가 true이다")
        void reconstitutedActiveSourceIsActive() {
            InboundSource source = InboundSourceFixtures.activeInboundSource();

            assertThat(source.isActive()).isTrue();
            assertThat(source.id().isNew()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE 상태로 복원된 소스는 isActive()가 false이다")
        void reconstitutedInactiveSourceIsNotActive() {
            InboundSource source = InboundSourceFixtures.inactiveInboundSource();

            assertThat(source.isActive()).isFalse();
            assertThat(source.status()).isEqualTo(InboundSourceStatus.INACTIVE);
        }

        @Test
        @DisplayName("복원된 소스의 idValue()가 올바르다")
        void reconstitutedSourceHasCorrectIdValue() {
            InboundSource source = InboundSourceFixtures.activeInboundSource(5L);

            assertThat(source.idValue()).isEqualTo(5L);
        }

        @Test
        @DisplayName("크롤링 타입 소스로 복원된다")
        void reconstitutedCrawlingSource() {
            InboundSource source = InboundSourceFixtures.crawlingSource();

            assertThat(source.type()).isEqualTo(InboundSourceType.CRAWLING);
            assertThat(source.codeValue()).isEqualTo("COUPANG_CRAWL");
        }

        @Test
        @DisplayName("파트너 타입 소스로 복원된다")
        void reconstitutedPartnerSource() {
            InboundSource source = InboundSourceFixtures.partnerSource();

            assertThat(source.type()).isEqualTo(InboundSourceType.PARTNER);
        }
    }

    @Nested
    @DisplayName("update() - 소스 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("소스 정보를 수정하면 필드가 업데이트된다")
        void updateSourceUpdatesFields() {
            InboundSource source = InboundSourceFixtures.activeInboundSource();
            InboundSourceUpdateData updateData =
                    InboundSourceUpdateData.of("수정된 소스 이름", "수정된 설명", InboundSourceStatus.INACTIVE);
            Instant now = CommonVoFixtures.now();

            source.update(updateData, now);

            assertThat(source.name()).isEqualTo("수정된 소스 이름");
            assertThat(source.description()).isEqualTo("수정된 설명");
            assertThat(source.status()).isEqualTo(InboundSourceStatus.INACTIVE);
            assertThat(source.isActive()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE로 수정하면 isActive()가 false가 된다")
        void updateToInactiveSetsInactive() {
            InboundSource source = InboundSourceFixtures.activeInboundSource();
            InboundSourceUpdateData updateData =
                    InboundSourceUpdateData.of(
                            source.name(), source.description(), InboundSourceStatus.INACTIVE);

            source.update(updateData, CommonVoFixtures.now());

            assertThat(source.isActive()).isFalse();
        }

        @Test
        @DisplayName("수정 후 updatedAt이 갱신된다")
        void updateRefreshesUpdatedAt() {
            InboundSource source = InboundSourceFixtures.activeInboundSource();
            Instant now = CommonVoFixtures.now();
            InboundSourceUpdateData updateData =
                    InboundSourceUpdateData.of("변경된 이름", "변경된 설명", InboundSourceStatus.ACTIVE);

            source.update(updateData, now);

            assertThat(source.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("code는 수정되지 않는다")
        void updateDoesNotChangeCode() {
            InboundSource source = InboundSourceFixtures.activeInboundSource();
            String originalCode = source.codeValue();
            InboundSourceUpdateData updateData =
                    InboundSourceUpdateData.of("변경", null, InboundSourceStatus.ACTIVE);

            source.update(updateData, CommonVoFixtures.now());

            assertThat(source.codeValue()).isEqualTo(originalCode);
        }

        @Test
        @DisplayName("type은 수정되지 않는다")
        void updateDoesNotChangeType() {
            InboundSource source = InboundSourceFixtures.activeInboundSource();
            InboundSourceType originalType = source.type();
            InboundSourceUpdateData updateData =
                    InboundSourceUpdateData.of("변경", null, InboundSourceStatus.ACTIVE);

            source.update(updateData, CommonVoFixtures.now());

            assertThat(source.type()).isEqualTo(originalType);
        }
    }
}
