package com.ryuqq.marketplace.domain.seller.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.exception.SellerAlreadyActiveException;
import com.ryuqq.marketplace.domain.seller.exception.SellerAlreadyInactiveException;
import com.ryuqq.marketplace.domain.seller.exception.SellerErrorCode;
import com.ryuqq.marketplace.domain.seller.exception.SellerInactiveException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Seller Aggregate 테스트")
class SellerTest {

    @Nested
    @DisplayName("forNew() - 신규 셀러 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 셀러를 생성한다")
        void createNewSellerWithRequiredFields() {
            // given
            SellerName sellerName = SellerFixtures.defaultSellerName();
            var displayName = SellerFixtures.defaultDisplayName();
            LogoUrl logoUrl = SellerFixtures.defaultLogoUrl();
            Description description = SellerFixtures.defaultDescription();
            Instant now = CommonVoFixtures.now();

            // when
            Seller seller = Seller.forNew(sellerName, displayName, logoUrl, description, now);

            // then
            assertThat(seller.isNew()).isTrue();
            assertThat(seller.sellerName()).isEqualTo(sellerName);
            assertThat(seller.displayName()).isEqualTo(displayName);
            assertThat(seller.logoUrl()).isEqualTo(logoUrl);
            assertThat(seller.description()).isEqualTo(description);
            assertThat(seller.isActive()).isTrue();
            assertThat(seller.isDeleted()).isFalse();
            assertThat(seller.createdAt()).isEqualTo(now);
            assertThat(seller.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("nullable 필드 없이 신규 셀러를 생성할 수 있다")
        void createNewSellerWithoutOptionalFields() {
            // given
            SellerName sellerName = SellerFixtures.defaultSellerName();
            var displayName = SellerFixtures.defaultDisplayName();
            LogoUrl logoUrl = SellerFixtures.emptyLogoUrl();
            Description description = SellerFixtures.emptyDescription();
            Instant now = CommonVoFixtures.now();

            // when
            Seller seller = Seller.forNew(sellerName, displayName, logoUrl, description, now);

            // then
            assertThat(seller.logoUrl().isEmpty()).isTrue();
            assertThat(seller.description().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 셀러를 복원한다")
        void reconstituteActiveSeller() {
            // given
            SellerId id = SellerId.of(1L);
            SellerName sellerName = SellerFixtures.defaultSellerName();
            var displayName = SellerFixtures.defaultDisplayName();
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            Seller seller =
                    Seller.reconstitute(
                            id,
                            sellerName,
                            displayName,
                            SellerFixtures.defaultLogoUrl(),
                            SellerFixtures.defaultDescription(),
                            true,
                            null,
                            null,
                            null,
                            createdAt,
                            updatedAt);

            // then
            assertThat(seller.isNew()).isFalse();
            assertThat(seller.id()).isEqualTo(id);
            assertThat(seller.isActive()).isTrue();
            assertThat(seller.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("영속성에서 삭제된 셀러를 복원한다")
        void reconstituteDeletedSeller() {
            // given
            SellerId id = SellerId.of(1L);
            Instant deletedAt = CommonVoFixtures.yesterday();

            // when
            Seller seller =
                    Seller.reconstitute(
                            id,
                            SellerFixtures.defaultSellerName(),
                            SellerFixtures.defaultDisplayName(),
                            SellerFixtures.defaultLogoUrl(),
                            SellerFixtures.defaultDescription(),
                            false,
                            deletedAt,
                            null,
                            null,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(seller.isDeleted()).isTrue();
            assertThat(seller.deletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("update() - 셀러 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("셀러 정보를 수정한다")
        void updateSellerInfo() {
            // given
            Seller seller = SellerFixtures.activeSeller();
            SellerUpdateData updateData = SellerFixtures.sellerUpdateData();
            Instant now = CommonVoFixtures.now();

            // when
            seller.update(updateData, now);

            // then
            assertThat(seller.sellerName()).isEqualTo(updateData.sellerName());
            assertThat(seller.displayName()).isEqualTo(updateData.displayName());
            assertThat(seller.logoUrl()).isEqualTo(updateData.logoUrl());
            assertThat(seller.description()).isEqualTo(updateData.description());
            assertThat(seller.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("activate() / deactivate() - 활성화 상태 변경")
    class ActivationTest {

        @Test
        @DisplayName("비활성 셀러를 활성화한다")
        void activateInactiveSeller() {
            // given
            Seller seller = SellerFixtures.inactiveSeller();
            Instant now = CommonVoFixtures.now();

            // when
            seller.activate(now);

            // then
            assertThat(seller.isActive()).isTrue();
            assertThat(seller.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("이미 활성화된 셀러를 활성화하면 예외가 발생한다")
        void activateAlreadyActiveSeller() {
            // given
            Seller seller = SellerFixtures.activeSeller();

            // when & then
            assertThatThrownBy(() -> seller.activate(CommonVoFixtures.now()))
                    .isInstanceOf(SellerAlreadyActiveException.class)
                    .satisfies(
                            e -> {
                                SellerAlreadyActiveException ex = (SellerAlreadyActiveException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(SellerErrorCode.SELLER_ALREADY_ACTIVE);
                            });
        }

        @Test
        @DisplayName("활성 셀러를 비활성화한다")
        void deactivateActiveSeller() {
            // given
            Seller seller = SellerFixtures.activeSeller();
            Instant now = CommonVoFixtures.now();

            // when
            seller.deactivate(now);

            // then
            assertThat(seller.isActive()).isFalse();
            assertThat(seller.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("이미 비활성화된 셀러를 비활성화하면 예외가 발생한다")
        void deactivateAlreadyInactiveSeller() {
            // given
            Seller seller = SellerFixtures.inactiveSeller();

            // when & then
            assertThatThrownBy(() -> seller.deactivate(CommonVoFixtures.now()))
                    .isInstanceOf(SellerAlreadyInactiveException.class)
                    .satisfies(
                            e -> {
                                SellerAlreadyInactiveException ex =
                                        (SellerAlreadyInactiveException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(SellerErrorCode.SELLER_ALREADY_INACTIVE);
                            });
        }
    }

    @Nested
    @DisplayName("delete() / restore() - 삭제 및 복원")
    class DeletionTest {

        @Test
        @DisplayName("셀러를 삭제(Soft Delete)한다")
        void deleteSeller() {
            // given
            Seller seller = SellerFixtures.activeSeller();
            Instant now = CommonVoFixtures.now();

            // when
            seller.delete(now);

            // then
            assertThat(seller.isDeleted()).isTrue();
            assertThat(seller.deletedAt()).isEqualTo(now);
            assertThat(seller.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("삭제된 셀러를 복원한다")
        void restoreSeller() {
            // given
            Seller seller = SellerFixtures.deletedSeller();
            Instant now = CommonVoFixtures.now();

            // when
            seller.restore(now);

            // then
            assertThat(seller.isDeleted()).isFalse();
            assertThat(seller.deletedAt()).isNull();
            assertThat(seller.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("ensureActive() - 활성 상태 검증")
    class EnsureActiveTest {

        @Test
        @DisplayName("활성 셀러에 대해 ensureActive()를 호출하면 예외가 발생하지 않는다")
        void ensureActiveOnActiveSeller() {
            // given
            Seller seller = SellerFixtures.activeSeller();

            // when & then
            assertThatCode(() -> seller.ensureActive()).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("비활성 셀러에 대해 ensureActive()를 호출하면 예외가 발생한다")
        void ensureActiveOnInactiveSeller() {
            // given
            Seller seller = SellerFixtures.inactiveSeller();

            // when & then
            assertThatThrownBy(() -> seller.ensureActive())
                    .isInstanceOf(SellerInactiveException.class)
                    .satisfies(
                            e -> {
                                SellerInactiveException ex = (SellerInactiveException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(SellerErrorCode.SELLER_INACTIVE);
                            });
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            Seller seller = SellerFixtures.activeSeller(100L);

            // when
            Long idValue = seller.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("sellerNameValue()는 셀러명의 값을 반환한다")
        void sellerNameValueReturnsStringValue() {
            // given
            Seller seller = SellerFixtures.activeSeller();

            // when
            String nameValue = seller.sellerNameValue();

            // then
            assertThat(nameValue).isEqualTo("테스트 셀러");
        }
    }
}
