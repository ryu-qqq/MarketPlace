package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BankCode enum 단위 테스트")
class BankCodeTest {

    @Nested
    @DisplayName("주요 은행 코드 검증")
    class MajorBankCodeTest {

        @Test
        @DisplayName("KB국민은행 코드는 004이다")
        void kbKookminBankCode() {
            assertThat(BankCode.KB_KOOKMIN.code()).isEqualTo("004");
            assertThat(BankCode.KB_KOOKMIN.displayName()).isEqualTo("KB국민은행");
        }

        @Test
        @DisplayName("신한은행 코드는 088이다")
        void shinhanBankCode() {
            assertThat(BankCode.SHINHAN.code()).isEqualTo("088");
            assertThat(BankCode.SHINHAN.displayName()).isEqualTo("신한은행");
        }

        @Test
        @DisplayName("우리은행 코드는 020이다")
        void wooriBankCode() {
            assertThat(BankCode.WOORI.code()).isEqualTo("020");
            assertThat(BankCode.WOORI.displayName()).isEqualTo("우리은행");
        }

        @Test
        @DisplayName("하나은행 코드는 081이다")
        void hanaBankCode() {
            assertThat(BankCode.HANA.code()).isEqualTo("081");
            assertThat(BankCode.HANA.displayName()).isEqualTo("하나은행");
        }
    }

    @Nested
    @DisplayName("인터넷전문은행 코드 검증")
    class InternetBankCodeTest {

        @Test
        @DisplayName("카카오뱅크 코드는 090이다")
        void kakaoBankCode() {
            assertThat(BankCode.KAKAO_BANK.code()).isEqualTo("090");
            assertThat(BankCode.KAKAO_BANK.displayName()).isEqualTo("카카오뱅크");
        }

        @Test
        @DisplayName("토스뱅크 코드는 092이다")
        void tossBankCode() {
            assertThat(BankCode.TOSS_BANK.code()).isEqualTo("092");
            assertThat(BankCode.TOSS_BANK.displayName()).isEqualTo("토스뱅크");
        }

        @Test
        @DisplayName("케이뱅크 코드는 089이다")
        void kbankCode() {
            assertThat(BankCode.K_BANK.code()).isEqualTo("089");
        }
    }

    @Nested
    @DisplayName("enum 값 개수 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("BankCode는 26가지 값을 가진다")
        void bankCodeHas26Values() {
            assertThat(BankCode.values()).hasSize(26);
        }

        @Test
        @DisplayName("모든 BankCode는 code와 displayName을 가진다")
        void allBankCodesHaveCodeAndDisplayName() {
            for (BankCode bankCode : BankCode.values()) {
                assertThat(bankCode.code()).isNotNull().isNotBlank();
                assertThat(bankCode.displayName()).isNotNull().isNotBlank();
            }
        }
    }
}
